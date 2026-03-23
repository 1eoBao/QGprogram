package org.example;

import java.util.Date;

public class User {
    private Integer id;
    private Integer password;
    private Integer role;
    private String problem;
    private Integer room;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPassword() {
        return password;
    }

    public void setPassword(Integer password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password=" + password +
                ", role=" + role +
                ", problem='" + problem + '\'' +
                ", room=" + room +
                '}';
    }
}