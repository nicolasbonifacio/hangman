package com.nick.hangman;

/**
 * Created by Nick on 2016-04-14.
 */
public class Category {

    private int id;
    private int languageId;
    private String descrCategory;

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

    public String getDescrCategory() {
        return descrCategory;
    }

    public void setDescrCategory(String descrCategory) {
        this.descrCategory = descrCategory;
    }
}
