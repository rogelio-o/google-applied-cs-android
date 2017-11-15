package com.rogelio.training.unit3.scarnesdice;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String SCORE_TEXT = "Your score: %d computer score: %d %s turn score: %d";

    private static final int COMPUTER_MAX_TURN_SCORE = 20;

    private int userOverallScore = 0;

    private int userTurnScore = 0;

    private int computerOverallScore = 0;

    private int computerTurnScore = 0;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickRollHandler(View view) {
        int dice = random.nextInt(6) + 1;
        if(!roll(dice, PlayerType.USER)) {
            computerTurn();
        }
    }

    public void clickHoldHandler(View view) {
        hold(PlayerType.USER);
        computerTurn();
    }

    public void clickResetHandler(View view) {
        userOverallScore = 0;
        userTurnScore = 0;
        computerOverallScore = 0;
        computerTurnScore = 0;
        updateScoreText(PlayerType.USER);
    }

    private void computerTurn() {
        ((Button) findViewById(R.id.roll)).setEnabled(false);
        ((Button) findViewById(R.id.hold)).setEnabled(false);

        computerTurnPlay();
    }

    private void computerTurnPlay() {
        int dice = random.nextInt(6) + 1;
        final boolean result = roll(dice, PlayerType.COMPUTER);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(computerTurnScore < COMPUTER_MAX_TURN_SCORE && result) {
                    computerTurnPlay();
                } else {
                    computerTurnEnd(result);
                }
            }
        }, 1000);
    }

    private void computerTurnEnd(boolean result) {
        if(result) {
            hold(PlayerType.COMPUTER);
            ((TextView) findViewById(R.id.score)).setText("Computer holds");
        } else {
            ((TextView) findViewById(R.id.score)).setText("Computer rolled a one");
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                updateScoreText(PlayerType.USER);
                ((Button) findViewById(R.id.roll)).setEnabled(true);
                ((Button) findViewById(R.id.hold)).setEnabled(true);
            }
        }, 1000);
    }

    private boolean roll(int dice, PlayerType type) {
        boolean result;

        updateImage(dice);
        if(dice == 1) {
            clearTurnScore(type);
            result = false;
        } else {
            addTurnScore(dice, type);
            result = true;
        }
        updateScoreText(type);

        return result;
    }

    private void hold(PlayerType type) {
        if(type == PlayerType.USER) {
            userOverallScore += userTurnScore;
            userTurnScore = 0;
        } else {
            computerOverallScore += computerTurnScore;
            computerTurnScore = 0;
        }
        updateScoreText(type);
    }

    private void addTurnScore(int dice, PlayerType type) {
        if(type == PlayerType.USER) {
            userTurnScore += dice;
        } else {
            computerTurnScore += dice;
        }
    }

    private void clearTurnScore(PlayerType type) {
        if(type == PlayerType.USER) {
            userTurnScore = 0;
        } else {
            computerTurnScore = 0;
        }
    }

    private void updateImage(int dice) {
        ((ImageView) findViewById(R.id.dice)).setImageDrawable(getResources().getDrawable(getDiceDrawable(dice)));
    }

    private void updateScoreText(PlayerType type) {
        String typeText = type == PlayerType.COMPUTER ? "computer" : "your";
        int turnScore = type == PlayerType.COMPUTER ? computerTurnScore : userTurnScore;
        ((TextView) findViewById(R.id.score)).setText(String.format(SCORE_TEXT, userOverallScore, computerOverallScore, typeText, turnScore));
    }

    private int getDiceDrawable(int dice) {
        switch(dice) {
            case 1:
                return R.drawable.dice1;
            case 2:
                return R.drawable.dice2;
            case 3:
                return R.drawable.dice3;
            case 4:
                return R.drawable.dice4;
            case 5:
                return R.drawable.dice5;
            case 6:
                return R.drawable.dice6;
            default:
                return R.drawable.dice1;
        }
    }

    private enum PlayerType {
        COMPUTER, USER
    }

}
