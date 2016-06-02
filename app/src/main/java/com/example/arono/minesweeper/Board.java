package com.example.arono.minesweeper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import com.example.arono.minesweeper.Cell.EmptySquare;
import com.example.arono.minesweeper.Cell.MineSquare;
import com.example.arono.minesweeper.Cell.NumberSquare;
import com.example.arono.minesweeper.Cell.Square;
import java.util.ArrayList;
import java.util.Random;


public class Board {

    private final float HEIGHT_BTN = 0.052f;
    private final int NUM_OF_BUTTON_IN_ROW = 10;
    private Context context;
    private int numOfMines,cols,rows,flagCounter;
    private LinearLayout lHoriz[];
    private Random r;
    private LinearLayout.LayoutParams btnParam;
    private DisplayMetrics metrics;
    private ArrayList<Square> squares;

    public Board(int cols,int rows,int numOfMines,Context c,DisplayMetrics metrics){
        this.context = c;
        this.cols = cols;
        this.rows = rows;
        this.numOfMines = numOfMines;
        lHoriz = new LinearLayout[rows];
        this.flagCounter = 0;
        this.metrics = metrics;
        squares = new ArrayList<>(rows*cols);
    }

    public void setBoard(LinearLayout lVertical) {

        LinearLayout.LayoutParams lHorizParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,0.1f);
        btnParam = setBtnParams();

        for (int j = 0; j < lHoriz.length ; j++) {
            lHoriz[j] = new LinearLayout(context);
            lHoriz[j].setOrientation(LinearLayout.HORIZONTAL);
            lHoriz[j].setId(0);
            for (int i = 0; i < NUM_OF_BUTTON_IN_ROW ; i++) {
                Square square = new Square(context,j,i);
                square.setId(0);
                lHoriz[j].addView(square, btnParam);
            }
            lVertical.addView(lHoriz[j], lHorizParam);
        }
    }

    public void resetBoard(LinearLayout lVertical){
        lVertical.removeAllViews();
    }

    public void setFlagCounter(int flagCounter){
        this.flagCounter = flagCounter;
    }

    public int getFlagCounter(){
        return flagCounter;
    }

    public void setNumOfMines(int numOfMines){
        this.numOfMines = numOfMines;
    }

    public int getNumOfMines(){
        return numOfMines;
    }

    public void setMines(){
        int row,col;

        btnParam = setBtnParams();
        r = new Random();

        row = r.nextInt(NUM_OF_BUTTON_IN_ROW);
        col = r.nextInt(NUM_OF_BUTTON_IN_ROW);

        changeSquareToMine(row,col,btnParam);

        for(int i = 0 ; i < numOfMines-1 ; i++){
            do {
                row = r.nextInt(NUM_OF_BUTTON_IN_ROW);
                col = r.nextInt(NUM_OF_BUTTON_IN_ROW);
            }while(checkMinesInTheSameSpot(row,col));

            changeSquareToMine(row,col,btnParam);
        }
    }

    public boolean checkMinesInTheSameSpot(int row, int col){
        return (lHoriz[row].getChildAt(col) instanceof MineSquare);
    }

    public void changeSquareToMine(int row, int col,LinearLayout.LayoutParams btnParam){
        MineSquare ms = new MineSquare(context, row, col);
        lHoriz[row].removeViewAt(col);
        lHoriz[row].addView(ms, col, btnParam);
    }

    public void setNumbers(){
        btnParam = setBtnParams();

        for(int row = 0 ; row < this.rows ; row++){
            for(int col = 0 ; col < this.cols ; col++){
                if(!(lHoriz[row].getChildAt(col) instanceof MineSquare)) {
                    int count = countMineAroundSquare(row, col);
                    if(count != 0){
                        NumberSquare ns = new NumberSquare(context,row,col);
                        ns.setNum(count);
                        lHoriz[row].removeViewAt(col);
                        lHoriz[row].addView(ns, col, btnParam);
                    }
                    else{
                        EmptySquare ns = new EmptySquare(context,row,col);
                        lHoriz[row].removeViewAt(col);
                        lHoriz[row].addView(ns,col,btnParam);
                    }
                }

            }
        }
    }

    public int countMineAroundSquare(int row,int col){
        int count = 0;

          for(int i = row-1 ; i < row+2 ; i++ ){
              for(int j = col-1 ; j < col+2 ; j++){
                  if( isInRange(i,j)){
                    if(i != row || j != col){
                      if(lHoriz[i].getChildAt(j) instanceof MineSquare)
                          count++;
                  }
              }
          }
        }
        return count;
    }

    public boolean isInRange(int row,int col) {
        return row  >= 0 && row < this.rows && col >=0 && col < this.cols;
    }

    public LinearLayout.LayoutParams setBtnParams(){
        int heightPixels = metrics.heightPixels;
        int btnDimension = (int) (heightPixels * HEIGHT_BTN);
        //LinearLayout.LayoutParams btnParams =

        return  new LinearLayout.LayoutParams(btnDimension,btnDimension, 0.1f);
    }

    /*public void insertSquaresIntoArray(){
        for(int i = 0 ; i < rows ; i++){
            for(int j = 0 ; j < cols ; j++){
                squares.add((Square)lHoriz[i].getChildAt(j));
            }
        }
    }*/

    public void addMine() {

        boolean mineAdded = false;
        r = new Random();
        int i = r.nextInt(NUM_OF_BUTTON_IN_ROW);
        int j = r.nextInt(NUM_OF_BUTTON_IN_ROW);
            while (!mineAdded) {
                Square s = (Square) lHoriz[i].getChildAt(j);
                if (!(s instanceof MineSquare) && !s.getIsFlagged()) {
                    MineSquare mineSquare = new MineSquare(context, i, j);
                    btnParam = setBtnParams();
                    lHoriz[i].removeViewAt(j);
                    lHoriz[i].addView(mineSquare, j, btnParam);
                    mineAdded = true;
                    setNumOfMines(getNumOfMines() + 1);
                }

                i = r.nextInt(NUM_OF_BUTTON_IN_ROW);
                j = r.nextInt(NUM_OF_BUTTON_IN_ROW);
            }

    }
    public boolean checkIfThereIsMoreEmptySquare(){
        for(int i = 0 ; i < 10 ; i ++){
            for(int j = 0 ; j < 10 ; j++){
                Square s = (Square)lHoriz[i].getChildAt(j);
                if(s instanceof EmptySquare || s instanceof NumberSquare)
                    if(!s.getIsPressed())
                        return true;
            }
        }
        return false;
    }

    public void resetBoardAfterAddingMine() {
        for(Square s : squares) {
            int i = s.getSquareX();
            int j = s.getSquareY();
            lHoriz[i].addView(s,j);
        }

        setNumbersAfterAngleChanged();

    }

    public void setNumbersAfterAngleChanged() {
        btnParam = setBtnParams();
        for(int row = 0 ; row < this.rows ; row++){
            for(int col = 0 ; col < this.cols ; col++){
                if(!(lHoriz[row].getChildAt(col) instanceof MineSquare)) {
                    int count = countMineAroundSquare(row, col);
                    if(count != 0){

                        NumberSquare ns = new NumberSquare(context,row,col);
                        if(((Square)lHoriz[row].getChildAt(col)).getIsPressed()) {
                            ns.setNum(count);
                            ns.exposeMe();
                        }
                        else if(((Square)lHoriz[row].getChildAt(col)).getIsFlagged()) {
                            ns.setNum(count);
                            ns.setIsFlagged(true);
                        }
                        ns.setNum(count);
                        lHoriz[row].removeViewAt(col);
                        lHoriz[row].addView(ns, col, btnParam);

                    }

                }

            }
        }
    }
}
