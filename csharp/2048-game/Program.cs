int[,] grid = new int[4, 4];
Random rand = new Random();

InitGrid();
while (true)
{
    Console.Clear();
    PrintGrid();
    ConsoleKey key = Console.ReadKey(true).Key;
    bool moved = false;
    switch (key)
    {
        case ConsoleKey.LeftArrow:
            moved = MoveLeft();
            break;
        case ConsoleKey.RightArrow:
            RotateGrid(); RotateGrid();
            moved = MoveLeft();
            RotateGrid(); RotateGrid();
            break;
        case ConsoleKey.UpArrow:
            RotateGrid(); RotateGrid(); RotateGrid();
            moved = MoveLeft();
            RotateGrid();
            break;
        case ConsoleKey.DownArrow:
            RotateGrid();
            moved = MoveLeft();
            RotateGrid(); RotateGrid(); RotateGrid();
            break;
    }
    if (moved)
        AddRandomTile();
}
void InitGrid()
{
    AddRandomTile();
    AddRandomTile();
}
void PrintGrid()
{
    for (int i = 0; i < 4; i++)
    {
        for (int j = 0; j < 4; j++)
        {
            Console.Write((grid[i, j] == 0 ? "." : grid[i, j].ToString()) + "\t");
        }
        Console.WriteLine();
    }
}
void AddRandomTile()
{
    int x, y;
    do
    {
        x = rand.Next(4);
        y = rand.Next(4);
    } while (grid[x, y] != 0);
    grid[x, y] = rand.Next(10) < 9 ? 2 : 4;
}
bool MoveLeft()
{
    bool moved = false;
    for (int i = 0; i < 4; i++)
    {
        int[] line = new int[4];
        int index = 0;
        for (int j = 0; j < 4; j++)
        {
            if (grid[i, j] != 0)
                line[index++] = grid[i, j];
        }

        for (int j = 0; j < 3; j++)
        {
            if (line[j] != 0 && line[j] == line[j + 1])
            {
                line[j] *= 2;
                line[j + 1] = 0;
                moved = true;
            }
        }
        int[] newLine = new int[4];
        index = 0;
        for (int j = 0; j < 4; j++)
        {
            if (line[j] != 0)
                newLine[index++] = line[j];
        }
        for (int j = 0; j < 4; j++)
        {
            if (grid[i, j] != newLine[j])
            {
                grid[i, j] = newLine[j];
                moved = true;
            }
        }
    }
    return moved;
}
void RotateGrid()
{
    int[,] newGrid = new int[4, 4];
    for (int i = 0; i < 4; i++)
        for (int j = 0; j < 4; j++)
            newGrid[j, 3 - i] = grid[i, j];
    grid = newGrid;
}