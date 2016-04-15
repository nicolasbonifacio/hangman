package com.nick.hangman;

/**
 * Created by Nick on 2016-04-14.
 */
public class Player {

    private int id;
    private String descrName;
    private int score;
    private int lastUsed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescrName() {
        return descrName;
    }

    public void setDescrName(String descrName) {
        this.descrName = descrName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(int lastUsed) {
        this.lastUsed = lastUsed;
    }
}
