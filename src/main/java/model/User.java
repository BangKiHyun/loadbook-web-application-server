package model;

public class User {
    private String Id;
    private String password;
    private String name;
    private String email;

    public User(String id, String password, String name, String email) {
        Id = id;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return Id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id='" + Id + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
