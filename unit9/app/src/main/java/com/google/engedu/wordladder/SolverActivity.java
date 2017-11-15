package com.google.engedu.wordladder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.engedu.worldladder.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SolverActivity extends AppCompatActivity {

    private String[] words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_solver);

        words = getIntent().getStringArrayExtra("words");

        TextView startText = (TextView) findViewById(R.id.startTextView);
        startText.setText(words[0]);
        TextView endText = (TextView) findViewById(R.id.endTextView);
        endText.setText(words[words.length - 1]);
        Log.d("words", Arrays.toString(words));
        LinearLayout layout = (LinearLayout) findViewById(R.id.inputsLayout);
        for(int i = 0;  i < words.length - 2; i++) {
            final String word = words[i + 1];
            final EditText et = new EditText(this);
            et.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().equals(word)) {
                        et.setTextColor(getResources().getColor(R.color.colorGreen));
                    } else {
                        et.setTextColor(getResources().getColor(R.color.colorRed));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}

            });
            et.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(et);
        }
    }

    public boolean onSolve(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.inputsLayout);
        final int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            EditText et = (EditText) layout.getChildAt(i);
            et.setText(words[i + 1]);
        }
        return true;
    }
}
