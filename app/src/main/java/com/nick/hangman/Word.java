package com.nick.hangman;

/**
 * Created by Nick on 2016-04-15.
 */
public class Word {

    private int id;
    private int languageId;
    private int categoryId;
    private int levelId;
    private String word;
    private int wordUsed;

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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getWordUsed() {
        return wordUsed;
    }

    public void setWordUsed(int wordUsed) {
        this.wordUsed = wordUsed;
    }
}
