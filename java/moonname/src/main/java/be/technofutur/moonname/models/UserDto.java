package be.technofutur.moonname.models;

public record UserDto(Long id, String username) {
    public static UserDto fromEntity(User u) {
        return new UserDto(u.getId(), u.getUsername());
    }
}