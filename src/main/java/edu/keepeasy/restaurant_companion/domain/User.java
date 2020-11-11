package edu.keepeasy.restaurant_companion.domain;

public class User extends Entity{
    private String name;
    private String login;
    private String password;
    private UserType userType;

    public User(long id, String name, String login, String password, UserType userType) {
        super(id);
        this.name = name;
        this.login = login;
        this.password = password;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
