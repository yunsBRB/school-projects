import json,pathlib,subprocess,hashlib,os

root=pathlib.Path('.')
files=json.loads((root/'migration/files.json').read_text())
cache={}
for f in files:
    key=(f['repository'],f['commit'])
    if key not in cache:
        p=pathlib.Path('/tmp/github-category-import')/f['repository']
        subprocess.run(['git','clone','--quiet','https://github.com/yunsBRB/'+f['repository']+'.git',str(p)],check=True)
        subprocess.run(['git','-C',str(p),'checkout','--quiet',f['commit']],check=True)
        cache[key]=p
    src=cache[key]/f['source']
    data=src.read_bytes()
    sha=hashlib.sha1(b'blob '+str(len(data)).encode()+b'\0'+data).hexdigest()
    if sha!=f['sha']:raise ValueError('Source mismatch: '+f['source'])
    dest=root/f['destination']
    if dest.exists():raise ValueError('Destination exists: '+str(dest))
    dest.parent.mkdir(parents=True,exist_ok=True)
    dest.write_bytes(data)
    if f['mode']=='100755':dest.chmod(0o755)
(root/'MIGRATION.json').write_text(json.dumps(files,ensure_ascii=False,indent=2)+'\n')
print('Imported and verified',len(files),'files')
