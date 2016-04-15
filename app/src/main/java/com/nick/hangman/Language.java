package com.nick.hangman;

/**
 * Created by Nick on 2016-04-14.
 */
public class Language {

    private int id;
    private String descrLanguage;
    private int lastUsed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescrLanguage() {
        return descrLanguage;
    }

    public void setDescrLanguage(String descrLanguage) {
        this.descrLanguage = descrLanguage;
    }

    public int getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(int lastUsed) {
        this.lastUsed = lastUsed;
    }
}
