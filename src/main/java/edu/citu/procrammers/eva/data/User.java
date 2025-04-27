package edu.citu.procrammers.eva.data;

public class User {

    private int uid;
    public String username;
    public String hashedPassword;

    public User(int uid) {
        username = null;
        hashedPassword = null;
    }

    public int getID() {
        return uid;
    }
}
