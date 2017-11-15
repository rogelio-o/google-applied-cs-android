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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private Random random = new Random();
    private BinarySearcher searcher;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
        searcher = new BinarySearcher(words);
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix.isEmpty()) {
            return words.get(random.nextInt(words.size() - 1));
        } else {
            int index = searcher.search(prefix);
            if(index >= 0) {
                return words.get(index);
            } else {
                return null;
            }
        }
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;
        BinarySearcher.Range range = searcher.getRange(prefix);
        if(range != null) {
            boolean isEven = prefix.length() % 2 == 0;
            List<String> possibilities = new ArrayList<>();
            for(int index = range.getStart(); index <= range.getEnd(); index++) {
                String word = words.get(index);
                boolean isWordEven = word.length() % 2 == 0;
                if((isEven && isWordEven) || (!isEven && !isWordEven)) {
                    possibilities.add(word);
                }
            }

            if(!possibilities.isEmpty()){
                selected = possibilities.get(random.nextInt(possibilities.size()));
            }
        }
        return selected;
    }
}
