package com.nick.hangman.Objects;

/**
 * Created by Nick on 2016-04-15.
 */
public class Level {

    private int id;
    private int languageId;
    private String descrLevel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getDescrLevel() {
        return descrLevel;
    }

    public void setDescrLevel(String descrLevel) {
        this.descrLevel = descrLevel;
    }
}
