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

package com.google.engedu.palindromes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private HashMap<Range, PalindromeGroup> findings = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onFindPalindromes(View view) {
        findings.clear();
        EditText editText = (EditText) findViewById(R.id.editText);
        TextView textView = (TextView) findViewById(R.id.textView);
        String text = editText.getText().toString();
        text = text.replaceAll(" ", "");
        text = text.replaceAll("'", "");
        char[] textAsChars = text.toCharArray();
        if (isPalindrome(textAsChars, 0, text.length())) {
          textView.setText(text + " is already a palindrome!");
        } else {
            PalindromeGroup palindromes = breakIntoPalindromes(text.toCharArray(), 0, text.length());
            textView.setText(palindromes.toString());
        }
        return true;
    }

    private boolean isPalindrome(char[] text, int start, int end) {
        int size = end - start;
        if(size > 1) {
            int middle = (int) Math.ceil(size / 2D);
            for (int i = 0; i <= middle; i++) {
                if (text[start + i] != text[end - 1 - i]) {
                    return false;
                }
            }
        }
        return true;
    }

    private PalindromeGroup greedyBreakIntoPalindromes(char[] text, int start, int end) {
        int newEnd = start + 1;
        PalindromeGroup bestGroup = null;
        while(newEnd <= text.length && isPalindrome(text, start, newEnd)) {
            bestGroup = new PalindromeGroup(text, start, newEnd);
            newEnd++;
        };

        newEnd--;

        if(newEnd < end) {
            bestGroup.append(breakIntoPalindromes(text, newEnd, end));
        }

        return bestGroup;
    }

    private PalindromeGroup recursiveBreakIntoPalindromes(char[] text, int start, int end) {
        PalindromeGroup bestGroup = null;

        int newEnd = start + 1;
        while(newEnd <= text.length) {
            if(isPalindrome(text, start, newEnd)) {
                PalindromeGroup auxGroup = new PalindromeGroup(text, start, newEnd);
                if (newEnd < end) {
                    auxGroup.append(recursiveBreakIntoPalindromes(text, newEnd, end));
                }

                if (bestGroup == null || bestGroup.length() > auxGroup.length()) {
                    bestGroup = auxGroup;
                }
            }

            newEnd++;
        };

        return bestGroup;
    }

    private PalindromeGroup dynamicProgrammingBreakIntoPalindromesInternal(char[] text, int start, int end) {
        PalindromeGroup bestGroup = null;
        Range range = new Range(start, end);

        if(findings.containsKey(range)) {
            bestGroup = findings.get(range);
        } else {
            int newEnd = start + 1;
            while (newEnd <= text.length) {
                if (isPalindrome(text, start, newEnd)) {
                    PalindromeGroup auxGroup = new PalindromeGroup(text, start, newEnd);
                    if (newEnd < end) {
                        auxGroup.append(dynamicProgrammingBreakIntoPalindromesInternal(text, newEnd, end));
                    }

                    if (bestGroup == null || bestGroup.length() > auxGroup.length()) {
                        bestGroup = auxGroup;
                    }
                }

                newEnd++;
            }

            findings.put(range, bestGroup);
        }

        return bestGroup;
    }

    private PalindromeGroup dynamicProgrammingBreakIntoPalindromes(char[] text, int start, int end) {
        findings = new HashMap<>();
        return dynamicProgrammingBreakIntoPalindromesInternal(text, start, end);
    }

    private PalindromeGroup breakIntoPalindromes(char[] text, int start, int end) {
        long startTime = System.currentTimeMillis();
        PalindromeGroup result = dynamicProgrammingBreakIntoPalindromes(text, start, end);
        long endTime = System.currentTimeMillis();
        Log.d("times", "breakIntoPalindromes: " + (endTime - startTime) + "ms");
        return result;
    }
}
