package ru.mikhailantonov.taskmanager.core;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Пользователь системы
 */


public class People {

    private int userId;
    private String login;
    private String name;
    private String password;
    private String email;
    private Calendar regDate;
    private People currentUser;
    private ArrayList<Task> tasksAuthor = new ArrayList<>();
    private ArrayList<Task> tasksAppointer = new ArrayList<>();

    //пользователь-админ (может все)
    private boolean isAdmin;

    //пользователь - поддержка (принимают задачи в работу)
    private boolean isSupport;
    //пользователь активен?
    private boolean isActive;

    public People() {
    }

    public People(String login) {
        this.login = login;
        this.isActive = true;
        regDate = Calendar.getInstance();
        this.isAdmin = false;
        this.isSupport = false;
    }

    public static People createUser(String login) {
        People user = new People();
        user.login = login;
        user.isActive = true;
        return user;
    }

    public void disableUserActivity() {
        this.isActive = false;
    }

    public String getLogin() {
        return login;
    }

    public void changeUserType(String type) {
        if (type.equals("admin")) {
            this.makeUserAdmin();
        }
        if (type.equals("support")) {
            this.makeUserSupport();
        }

    }

    public People getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(People user) {
        this.currentUser = user;
    }

    //сделать пользователя админом
    private void makeUserAdmin() {
        isAdmin = true;
    }

    // сделать пользователя саппортом
    private void makeUserSupport() {
        isSupport = true;
    }

    boolean checkUserIsAdmin() {
        return isAdmin;
    }

    boolean checkUserIsSupport() {
        return isSupport;
    }
}
