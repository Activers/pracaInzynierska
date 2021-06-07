package com.example.pracainzynierska;


public class Model {

    String Nick, Username;
    String Game; // Myprofile
    String Rank, Mic; // Players
    int Visibility = 8; // MyProfile Edit

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

    // MyProfile
    public String getGame() {
        return Game;
    }
    // End MyProfile

    // Players
    public String getRank() { return Rank; }

    public String getMic() { return Mic; }
    public void setMic(String mic) { this.Mic = mic; }
    // End Players

    // Visibility MyProfile Edit
    public int getVisibility() {return Visibility; }
    public void setVisibility(int visibility) {this.Visibility = visibility; }
    // End of Visibility
}