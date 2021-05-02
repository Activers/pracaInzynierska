package com.example.pracainzynierska;

import android.view.View;

/**
 * Created by ankit on 22/1/18.
 */

public class Model {

    String Nick, Username;
    String Game; // Myprofile
    String Rank, Avatar, Mic; // Players

    int Visibility = 4; // MyProfile Edit

    public Model(){

    }

    public Model(String Game, String Nick) {

        this.Game = Game;
        this.Nick = Nick;
    }

    public Model(String Username, String Nick, String Rank, String Mic) {
        this.Username = Username;
        this.Nick = Nick;
        this.Rank = Rank;
        this.Mic = Mic;
    }


    public String getNick() {
        return Nick;
    }
    public void setNick(String nick) { this.Nick = nick; }

    public String getUsername() { return Username; }
    public void setUsername(String username) { Username = username; }

    public void changeGameText(String text) {
        this.Game = text;
    }


    // MyProfile
    public String getGame() {
        return Game;
    }
    public void setGame(String game) {
        this.Game = game;
    }
    // End MyProfile


    // Players
    public String getRank() { return Rank; }
    public void setRank(String rank) { this.Rank = rank; }

    public String getAvatar() { return Avatar; }
    public void setAvatar(String avatar) { this.Avatar = avatar; }

    public String getMic() { return Mic; }
    public void setMic(String mic) { this.Mic = mic; }
    // End Players

    // Visibility MyProfile Edit
    public int getVisibility() {return Visibility; }
    public void setVisibility(int visibility) {this.Visibility = visibility; }
    // End of Visibility
}