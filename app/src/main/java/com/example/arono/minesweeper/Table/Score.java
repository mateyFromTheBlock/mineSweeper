package com.example.arono.minesweeper.Table;


import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

public class Score implements Serializable,Comparable<Score>{

    String name;
    int time;
    LatLng position;

    public Score(String name,int time,LatLng position){
        setName(name);
        setTime(time);
        setPosition(position);
    }

    public void setName(String name){
        this.name = name;
    }

    public void setTime(int time){
        this.time = time;
    }

    public void setPosition(LatLng position){
        this.position = position;
    }

    public String getName(){
        return name;
    }
    public int getTime() {
        return time;
    }
    public LatLng getPosition(){
        return position;
    }

    @Override
    public int compareTo(Score score) {
        if(score.getTime() < time)
            return 1;
        else
            return -1;
    }
}
