package com.nick.hangman.Objects;

/**
 * Created by Nick on 2016-04-19.
 */
public class TaleScoreCategory {

    private int id;
    private int playerId;
    private int categoryId;
    private int score;
    private String descrCategory;
    private int enabled;
    private int enableScore;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDescrCategory() {
        return descrCategory;
    }

    public void setDescrCategory(String descrCategory) {
        this.descrCategory = descrCategory;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getEnableScore() {
        return enableScore;
    }

    public void setEnableScore(int enableScore) {
        this.enableScore = enableScore;
    }
}
