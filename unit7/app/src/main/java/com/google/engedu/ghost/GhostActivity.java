/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String STATE_TEXT = "playingWord";
    private static final String STATE_LABEL = "gameStatus";
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        TextView text = (TextView) findViewById(R.id.ghostText);
        savedInstanceState.putCharSequence(STATE_TEXT, text.getText());
        TextView label = (TextView) findViewById(R.id.gameStatus);
        savedInstanceState.putCharSequence(STATE_LABEL, label.getText());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        if (savedInstanceState != null) {
            TextView text = (TextView) findViewById(R.id.ghostText);
            text.setText(savedInstanceState.getCharSequence(STATE_TEXT));
            TextView label = (TextView) findViewById(R.id.gameStatus);
            label.setText(savedInstanceState.getCharSequence(STATE_LABEL));
        } else {
            AssetManager assetManager = getAssets();
            try {
                dictionary = new FastDictionary(assetManager.open("words.txt"));
            } catch (IOException e) {
                AlertDialog show = new AlertDialog.Builder(this).create();
                show.setTitle("Error");
                show.setMessage("The dictionary can not be loaded.");
                show.show();
            }
            onStart(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public boolean onChallenge(View view) {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);
        String word = (String) text.getText();
        if(dictionary.isWord(word)) {
            label.setText("User WINS");
        } else {
            String possibleWord = dictionary.getAnyWordStartingWith(word);
            if(possibleWord != null) {
                label.setText("Computer WINS (" + possibleWord + ")");
            } else {
                label.setText("User WINS");
            }
        }

        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);
        String word = (String) text.getText();
        if(dictionary.isWord(word)) {
            label.setText("Computer WINS");
        } else {
            String possibleLongerWord = dictionary.getGoodWordStartingWith(word);
            if(possibleLongerWord == null) {
                label.setText("Computer WINS");
            } else {
                text.setText(possibleLongerWord.substring(0, word.length() + 1));
                userTurn = true;
                label.setText(USER_TURN);
            }
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if(Character.isLetter(c)) {
            TextView text = (TextView) findViewById(R.id.ghostText);
            String newWord = text.getText().toString() + c;
            text.setText(newWord);

            computerTurn();
        }
        return super.onKeyUp(keyCode, event);
    }
}
