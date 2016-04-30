package com.nick.hangman;

import java.io.Serializable;

/**
 * Created by Nick on 2016-04-15.
 */
public class ParametersSelected implements Serializable {

    private int player1Id;
    private String player1DescrName;
    private int player1Score;

    private int player2Id;
    private String player2DescrName;
    private int player2Score;

    private int languageId;
    private String languageDescrLanguage;

    private int categoryId;
    private String categoryDescrCategory;

    private int levelId;
    private int levelPercCompleted;
    private String levelDescrLevel;

    private int taleScoreCategoryId;
    private int taleScoreCategoryScore;
    private int taleScoreCategoryEnableScore;
    private int taleScoreCategoryPathOrder;
    private int taleScoreCategoryIdNextCategory;

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1DescrName() {
        return player1DescrName;
    }

    public void setPlayer1DescrName(String player1DescrName) {
        this.player1DescrName = player1DescrName;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2DescrName() {
        return player2DescrName;
    }

    public void setPlayer2DescrName(String player2DescrName) {
        this.player2DescrName = player2DescrName;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLanguageDescrLanguage() {
        return languageDescrLanguage;
    }

    public void setLanguageDescrLanguage(String languageDescrLanguage) {
        this.languageDescrLanguage = languageDescrLanguage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryDescrCategory() {
        return categoryDescrCategory;
    }

    public void setCategoryDescrCategory(String categoryDescrCategory) {
        this.categoryDescrCategory = categoryDescrCategory;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getLevelPercCompleted() {
        return levelPercCompleted;
    }

    public void setLevelPercCompleted(int levelPercCompleted) {
        this.levelPercCompleted = levelPercCompleted;
    }

    public String getLevelDescrLevel() {
        return levelDescrLevel;
    }

    public void setLevelDescrLevel(String levelDescrLevel) {
        this.levelDescrLevel = levelDescrLevel;
    }

    public int getTaleScoreCategoryId() {
        return taleScoreCategoryId;
    }

    public void setTaleScoreCategoryId(int taleScoreCategoryId) {
        this.taleScoreCategoryId = taleScoreCategoryId;
    }

    public int getTaleScoreCategoryScore() {
        return taleScoreCategoryScore;
    }

    public void setTaleScoreCategoryScore(int taleScoreCategoryScore) {
        this.taleScoreCategoryScore = taleScoreCategoryScore;
    }

    public int getTaleScoreCategoryEnableScore() {
        return taleScoreCategoryEnableScore;
    }

    public void setTaleScoreCategoryEnableScore(int taleScoreCategoryEnableScore) {
        this.taleScoreCategoryEnableScore = taleScoreCategoryEnableScore;
    }

    public int getTaleScoreCategoryPathOrder() {
        return taleScoreCategoryPathOrder;
    }

    public void setTaleScoreCategoryPathOrder(int taleScoreCategoryPathOrder) {
        this.taleScoreCategoryPathOrder = taleScoreCategoryPathOrder;
    }

    public int getTaleScoreCategoryIdNextCategory() {
        return taleScoreCategoryIdNextCategory;
    }

    public void setTaleScoreCategoryIdNextCategory(int taleScoreCategoryIdNextCategory) {
        this.taleScoreCategoryIdNextCategory = taleScoreCategoryIdNextCategory;
    }
}
