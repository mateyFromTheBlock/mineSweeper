package com.example.arono.minesweeper.Cell;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.arono.minesweeper.R;

/**
 * Created by arono on 15/11/2015.
 */
public class Square extends Button {

    private int x,y;
    private boolean isPressed,isFlagged,isAnimated;
    private DisplayMetrics metrics;

    public Square(Context context,int x,int y) {
        super(context);
        this.x = x;
        this.y = y;
        this.isPressed = false;
        this.isFlagged = false;
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(2, Color.BLACK);
        gd.setCornerRadius(20f);
        gd.setColor(Color.WHITE);
        setBackground(gd);
        setTextSize(10);
        setIsAnimated(false);

    }

    public boolean getIsPressed(){
        return isPressed;
    }
    public boolean getIsFlagged(){
        return isFlagged;
    }

    public void setIsPressed(boolean isPressed){
        this.isPressed = isPressed;
        setEnabled(false);
        //getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(2, Color.BLACK);
        gd.setCornerRadius(20f);
        gd.setColor(Color.GREEN);
        setBackground(gd);
        //C0B8E3  FFB419 red
    }

    public void setIsFlagged(boolean isFlagged){
        this.isFlagged = isFlagged;
        if(isFlagged)
            setBackgroundResource(R.drawable.flag2);
        else{
            GradientDrawable gd = new GradientDrawable();
            gd.setStroke(2, Color.BLACK);
            gd.setCornerRadius(20f);
            gd.setColor(Color.WHITE);
            setBackground(gd);
        }
    }

    public int getSquareX(){
        return x;
    }
    public int getSquareY(){
        return y;
    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
        setIsAnimated(true);
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        setVisibility(INVISIBLE);
    }

    public void setIsAnimated(boolean isAnimated){
        this.isAnimated = isAnimated;
    }
    public boolean getIsAnimated(){
        return isAnimated;
    }
}
