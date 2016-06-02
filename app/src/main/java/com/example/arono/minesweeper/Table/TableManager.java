package com.example.arono.minesweeper.Table;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

public class TableManager {

    private final int NUM_OF_SCORES = 10;
    private final String FILE_NAME = "Scores";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private ArrayList<Score>  scores = new ArrayList<>(NUM_OF_SCORES);
    private Context context;

    public TableManager(Context context){
        this.context = context;
    }

    public void addScore(Score s){

        if(scores.size() == NUM_OF_SCORES) {
            Collections.sort(scores);
            if(s.compareTo(scores.get(NUM_OF_SCORES-1)) == -1) {
                scores.remove(NUM_OF_SCORES - 1);
                scores.add(s);
            }
        }
        else
            scores.add(s);
    }

    public ArrayList<Score> getScores() {
        return scores;
    }

    public void saveScore(){

         this.sharedPreferences = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
         this.spEditor = sharedPreferences.edit();

        int size = this.getScores().size();

        this.spEditor.putInt("Size", size);

        for (int j = 0; j < size; j++) {

            this.spEditor.putString("Name_" + j, this.getScores().get(j).getName());
            this.spEditor.putInt("Time_" + j, this.getScores().get(j).getTime());
            this.spEditor.putString("Lat_" + j, String.valueOf(this.getScores().get(j).getPosition().latitude));
            this.spEditor.putString("Lon_" + j,String.valueOf(this.getScores().get(j).getPosition().longitude));

        }
        this.spEditor.apply();
    }

    public void loadScores(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME,context.MODE_PRIVATE);
        int size;
        size = sharedPreferences.getInt("Size",0);
        for(int i = 0 ; i < size ; i++) {
            String name = sharedPreferences.getString("Name_" + i, null);
            int time = sharedPreferences.getInt("Time_" + i, 0);
            String lat = sharedPreferences.getString("Lat_" + i, null);
            String lon = sharedPreferences.getString("Lon_" + i, null);
            double lat1 = Double.parseDouble(lat);
            double lon1 = Double.parseDouble(lon);
            LatLng latLng = new LatLng(lat1,lon1);
            Score s = new Score(name, time, latLng);
            this.getScores().add(s);
        }
        Collections.sort(this.getScores());
    }

    public boolean checkIfScoreInTopTen(int time){
        if(getScores().size() < 10)
            return true;

        if(getScores().get(getScores().size()-1).getTime() > time)
            return true;

        return false;
    }

}
