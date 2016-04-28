package com.nick.hangman.Objects;

/**
 * Created by Nick on 2016-04-27.
 */
public class TaleOverall {

    private int id;
    private int playerId;
    private int categryId;
    private int wordId;
    private int numStars;
    private float numStarsAverage;

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

    public int getCategryId() {
        return categryId;
    }

    public void setCategryId(int categryId) {
        this.categryId = categryId;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getNumStars() {
        return numStars;
    }

    public void setNumStars(int numStars) {
        this.numStars = numStars;
    }

    public float getNumStarsAverage() {
        return numStarsAverage;
    }

    public void setNumStarsAverage(float numStarsAverage) {
        this.numStarsAverage = numStarsAverage;
    }
}
