package com.example.arono.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.arono.minesweeper.Table.FragmentListTable;
import com.example.arono.minesweeper.Table.FragmentMapTable;
import com.example.arono.minesweeper.Table.TableActivity;

public class GameScreen extends AppCompatActivity {


    Button btnStart,btnScores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnScores = (Button)findViewById(R.id.btnScores);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(getBaseContext(),LevelScreen.class);
                startActivity(start);
            }
        });

        btnScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent topScores = new Intent(getBaseContext(), TableActivity.class);
                startActivity(topScores);
            }
        });
    }
}
