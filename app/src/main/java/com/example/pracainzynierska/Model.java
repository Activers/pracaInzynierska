package com.example.pracainzynierska;

/**
 * Created by ankit on 22/1/18.
 */

public class Model {

    String mGame, mUser;


    public Model(){

    }

    public Model(String mGame, String mUser) {

        this.mGame = mGame;
        this.mUser = mUser;
    }

    public void changeGameText(String text) {
        this.mGame = text;
    }

    public String getmGame() {
        return mGame;
    }

    public void setmGame(String mGame) {
        this.mGame = mGame;
    }

    public String getmUser() {
        return mUser;
    }

    public void setmUser(String mUser) {
        this.mUser = mUser;
    }
}