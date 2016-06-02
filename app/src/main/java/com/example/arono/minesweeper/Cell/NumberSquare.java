package com.example.arono.minesweeper.Cell;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by arono on 15/11/2015.
 */
public class NumberSquare  extends Square {

    private int num;

    public NumberSquare(Context context, int x, int y)
    {
        super(context, x, y);
        //setTextSize(10);
    }

    public int getNum(){
        return num;
    }

    public void setNum(int num){
        this.num = num;
    }

    public void exposeMe(){
        switch(num){
            case 1:
                setTextColor(Color.BLUE);
                break;
            case 2:
                setTextColor(Color.LTGRAY);
                break;
            case 3:
                setTextColor(Color.RED);
                break;
            case 4:
                setTextColor(Color.DKGRAY);
                break;
            case 5:
                setTextColor(Color.MAGENTA);
                break;
            case 6:
                setTextColor(Color.CYAN);
                break;
            case 7:
                setTextColor(Color.BLACK);
                break;
            case 8:
                setTextColor(Color.GREEN);
                break;

        }
        setText(""+num);
        setIsPressed(true);
    }
}
