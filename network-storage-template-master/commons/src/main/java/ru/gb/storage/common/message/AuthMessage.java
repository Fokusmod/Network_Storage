package ru.gb.storage.common.message;

public class AuthMessage extends Message{

    private String login;
    private String password;

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

    @Override
    public String toString() {
        return "AuthMessage{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
