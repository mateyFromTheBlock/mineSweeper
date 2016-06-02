package com.example.arono.minesweeper;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.arono.minesweeper.Cell.MineSquare;
import com.example.arono.minesweeper.Cell.NumberSquare;
import com.example.arono.minesweeper.Cell.Square;
import com.example.arono.minesweeper.Service.MyService;
import com.example.arono.minesweeper.Table.Score;
import com.example.arono.minesweeper.Table.TableManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;


public class GamePanel extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final int ROW = 10;
    private final int COL = 10;

    int animationCounter = 0;
    private Vibrator v;
    private double latitude;
    private double longitude;
    private Board board;
    private Thread timer, timerAnimation;
    private Handler handler, handlerAnimation;
    private ImageView ivSmile;
    private int mine, time = 0;
    private TableManager table;
    private DisplayMetrics metrics;
    private LinearLayout lVertical;
    private RelativeLayout rl;
    private LocationManager locationManger;
    private LocationListener locationListener;
    private Animation anim, animWon;
    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private TextView tvFlagCounter, tvTimer, tvDanger;
    private boolean isWinnerOrLooser = false, isAngleChanged = true, registerService = false, running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_panel);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initialize();
        createBoard();
        setClick();
        textViewInitialize();

        ivSmile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restart();
            }
        });

        table.loadScores();

        if(!displayGpsStatus())
            alertbox();
        getYourPosition();
    }

    public void initialize() {
        Intent mineIntent = getIntent();
        mine = mineIntent.getIntExtra(LevelScreen.mines, 0);
        serviceIntent = new Intent(this, MyService.class);
        rl = (RelativeLayout) findViewById(R.id.rl);
        lVertical = (LinearLayout) findViewById(R.id.lVertical);
        tvFlagCounter = (TextView) findViewById(R.id.tvFlagCounter);
        ivSmile = (ImageView) findViewById(R.id.ivSmile);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvDanger = (TextView) findViewById(R.id.tvDanger);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        table = new TableManager(getApplicationContext());
        animWon = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        locationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void createBoard() {
        board = new Board(ROW, COL, mine, getApplicationContext(), metrics);
        board.setBoard(lVertical);
        board.setMines();
        board.setNumbers();
    }

    public void textViewInitialize() {
        ivSmile.setImageResource(R.drawable.smile2);
        tvDanger.setVisibility(View.INVISIBLE);
        tvFlagCounter.setText("" + (board.getNumOfMines()-board.getFlagCounter()));
    }

    public void restart() {
        board.resetBoard(lVertical);

        createBoard();
        setClick();

        isWinnerOrLooser = false;
        running = false;
        time = 0;
        animationCounter = 0;
        textViewInitialize();
        tvTimer.setText("" + time);

        if (registerService) {
            unregisterReceiver(broadcastReceiver);
            registerService = false;
        }

        if(!displayGpsStatus())
            alertbox();

        Toast.makeText(getApplicationContext(), "New Game", Toast.LENGTH_SHORT).show();
    }

    public void timer() {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (running && !isWinnerOrLooser) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            time++;
                            tvTimer.setText("" + time);
                        }
                    });
                }
            }
        };
        timer = new Thread(runnable);
    }

    public void setClick() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                ((Square) ((LinearLayout) lVertical.getChildAt(i)).getChildAt(j)).setOnClickListener(this);
                ((Square) ((LinearLayout) lVertical.getChildAt(i)).getChildAt(j)).setOnLongClickListener(this);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        Square s = (Square) view;

        if (!running) {
            timer();
            timer.start();
            running = true;
            bindAndRegisterService();
        }

        if (!isWinnerOrLooser) {
            if (!s.getIsPressed()) {
                if (!s.getIsFlagged())
                    markFlag(s);
                else
                    removeFlag(s);
            }

            if (checkIfAllMinesAreFlagged()) {
                wonGame();
            }
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        Square s = (Square) view;

        if (!running) {
            timer();
            timer.start();
            running = true;
            bindAndRegisterService();
        }
        if (!isWinnerOrLooser) {
            if (!s.getIsFlagged()) {
                if (s instanceof MineSquare) {
                    loseGame();
                } else if (s instanceof NumberSquare) {
                    ((NumberSquare) s).exposeMe();
                } else
                    exposeTillNumber(s);
            }
            if (checkIfAllNumbersExposed() || checkIfAllMinesAreFlagged()) {
                wonGame();
            }
        }

    }


    public boolean checkIfAllMinesAreFlagged() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                Square s = ((Square) ((LinearLayout) lVertical.getChildAt(i)).getChildAt(j));
                if (s instanceof MineSquare && !s.getIsFlagged())
                    return false;
            }
        }
        return true;
    }

    public boolean checkIfAllNumbersExposed() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                Square s = ((Square) ((LinearLayout) lVertical.getChildAt(i)).getChildAt(j));
                if (s instanceof NumberSquare) {
                    if (!s.getIsPressed())
                        return false;
                }
            }
        }
        return true;
    }

    public void exposeMines() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                Square s = ((Square) ((LinearLayout) lVertical.getChildAt(i)).getChildAt(j));
                if (s instanceof MineSquare) {
                    s.setIsPressed(true);
                    s.setBackgroundResource(R.drawable.mine2);
                }
            }
        }
    }

    public void exposeTillNumber(Square s) {
        if (s.getIsFlagged())
            return;
        if (s instanceof NumberSquare) {
            ((NumberSquare) s).exposeMe();
            return;
        }

        s.setIsPressed(true);

        for (int i = s.getSquareX() - 1; i < s.getSquareX() + 2; i++) {
            for (int j = s.getSquareY() - 1; j < s.getSquareY() + 2; j++) {
                if (board.isInRange(i, j)) {
                    if ((i != s.getSquareX() || j != s.getSquareY())
                            && !((Square) ((LinearLayout) lVertical.getChildAt(i)).getChildAt(j)).getIsPressed()) {
                        exposeTillNumber(((Square) ((LinearLayout) lVertical.getChildAt(i)).getChildAt(j)));
                    }
                }
            }
        }
    }

    public void markFlag(Square s) {
        if (board.getFlagCounter() < board.getNumOfMines()) {
            board.setFlagCounter(board.getFlagCounter() + 1);
            s.setIsFlagged(true);
            tvFlagCounter.setText("" + (board.getNumOfMines() - board.getFlagCounter()));
        }
    }

    public void removeFlag(Square s) {
        s.setIsFlagged(false);
        board.setFlagCounter(board.getFlagCounter() - 1);
        tvFlagCounter.setText("" + (board.getNumOfMines() - board.getFlagCounter()));
    }


    /*******Animation****Start***/
    public void animationTheard() {
        handlerAnimation = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (isWinnerOrLooser && animationCounter < 99) {
                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handlerAnimation.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isWinnerOrLooser) {

                                anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.drop);
                                Square s = (Square) ((LinearLayout) lVertical.getChildAt(animationCounter / 10)).getChildAt(animationCounter % 10);
                                s.startAnimation(anim);
                                animationCounter++;

                            }
                        }
                    });
                }
            }
        };
        timerAnimation = new Thread(runnable);
    }

    public void animation() {
        rl.setClipChildren(false);
        lVertical.setClipChildren(false);
        for (int i = 0; i < 10; i++) {
            ((LinearLayout) lVertical.getChildAt(i)).setClipChildren(false);
        }

    }

    /*******Animation****End***/

    /*******PopUp And Game Status****Start***/
    public void loseGame() {
        isWinnerOrLooser = true;
        exposeMines();
        animation();
        animationTheard();
        timerAnimation.start();
        Dialog dialog = new Dialog(this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                restart();
            }
        });
        popUpImage(dialog, R.drawable.faild2);
        ivSmile.setImageResource(R.drawable.cry1);
        if (registerService) {
            unregisterReceiver(broadcastReceiver);
            registerService = false;
        }
    }
    public void wonGame() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                restart();
            }
        });
        popUpImage(dialog, R.drawable.winner1);
        if (table.checkIfScoreInTopTen(time)) {
            newRecordDialog();
        }
        ivSmile.setImageResource(R.drawable.smilesun);
        isWinnerOrLooser = true;
        ivSmile.startAnimation(animWon);
        if (registerService) {
            unregisterReceiver(broadcastReceiver);
            registerService = false;
        }

    }

    public void getYourPosition(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };
        try{
            locationManger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, locationListener);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }
    private void popUpImage(Dialog dialog, int imageId) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(imageId);
        imageView.setBackground(null);
        dialog.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        dialog.show();
    }

    public void newRecordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("New Record").setIcon(R.drawable.smilesun);

        final EditText etName = new EditText(this);

        alertDialog.setView(etName);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String name = etName.getText().toString();
                int time = Integer.parseInt(tvTimer.getText().toString());
                LatLng latlng = new LatLng(latitude,longitude);
                Score score = new Score(name, time, latlng);
                saveScore(score);

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void saveScore(Score score){
        table.addScore(score);
        table.saveScore();
    }

    /*******PopUp And Game Status****End***/

    /*******Service ****Start***/
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder localBinder = (MyService.LocalBinder)iBinder;
            MyService service = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void bindAndRegisterService(){
        //bindService(serviceIntent,sc,Context.BIND_AUTO_CREATE);
        if(!registerService) {
            registerReceiver(broadcastReceiver, new IntentFilter(MyService.SENSOR_SERVICE_BROADCAST_ACTION));
            registerService = true;
        }

    }

    public void CreateBroadcastAndRegisterReciver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MyService.SENSOR_SERVICE_BROADCAST_ACTION)) {
                    isAngleChanged = intent.getBooleanExtra(MyService.SENSOR_SERVICE_VALUES_KEY,false);
                }

                if(isAngleChanged) {
                    tvDanger.setVisibility(View.VISIBLE);
                    vibrate();
                    AngleChangedActions();
                }
                else
                    tvDanger.setVisibility(View.INVISIBLE);
            }
        };
        bindService(serviceIntent, sc, Context.BIND_AUTO_CREATE);
    }

    /*******Service ****End***/

    public void AngleChangedActions() {
        boolean moreMine = board.checkIfThereIsMoreEmptySquare();
       if(moreMine) {
           board.addMine();
           tvFlagCounter.setText("" + (board.getNumOfMines() -board.getFlagCounter()));
           board.resetBoardAfterAddingMine();
           setClick();
        }
        else{
           loseGame();
       }
    }


    public void vibrate(){
        v.vibrate(200);
    }

    /*******LifeCycle ****Start***/
    @Override
    protected void onStart() {
        super.onStart();
        CreateBroadcastAndRegisterReciver();
        if(running && !registerService)
            bindAndRegisterService();
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(registerService) {
            unregisterReceiver(broadcastReceiver);
            registerService  = false;
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(registerService) {
            unregisterReceiver(broadcastReceiver);
        }
        unbindService(sc);
    }

    /*******LifeCycle ****End***/


    private boolean displayGpsStatus() {
        return  locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    protected void alertbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("Gps Status")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                                onBackPressed();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }



}
