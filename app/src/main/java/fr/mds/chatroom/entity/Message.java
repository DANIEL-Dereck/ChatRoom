package fr.mds.chatroom.entity;

import java.io.Serializable;

public class Message implements Serializable {
    private String login;
    private String message;

    public Message() {

    }

    public Message(String login, String messsage) {
        this.login = login;
        this.message = messsage;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
