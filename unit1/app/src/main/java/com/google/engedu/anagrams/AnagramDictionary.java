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

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AnagramDictionary {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    private List<String> wordList = new ArrayList<>();
    private Set<String> wordSet = new HashSet<>();
    private Map<String, List<String>> lettersToWord = new HashMap<>();
    private Map<Integer, List<String>> sizeToWords = new HashMap<>();
    private int wordLength = DEFAULT_WORD_LENGTH;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            String sortedWord = sortLetters(word);
            if(lettersToWord.containsKey(sortedWord)) {
                lettersToWord.get(sortedWord).add(word);
            } else {
                List<String> words = new ArrayList<>();
                words.add(word);
                lettersToWord.put(sortedWord, words);
            }
            if(sizeToWords.containsKey(word.length())) {
                sizeToWords.get(word.length()).add(word);
            } else {
                List<String> words = new ArrayList<>();
                words.add(word);
                sizeToWords.put(word.length(), words);
            }
            wordList.add(word);
            wordSet.add(word);
        }
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        String inSortedLetters = sortLetters(targetWord);
        for(String word : wordList) {
            if(word.length() == targetWord.length()) {
                String wordSortedLetters = sortLetters(word);
                if (inSortedLetters.equals(wordSortedLetters)) {
                    result.add(word);
                }
            }
        }
        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for(char letter : ALPHABET) {
            String newInSortedLetters = sortLetters(word + letter);
            if(lettersToWord.containsKey(newInSortedLetters)) {
                result.addAll(lettersToWord.get(newInSortedLetters));
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        List<String> wordList = sizeToWords.get(wordLength);
        int offset = random.nextInt(wordList.size() - 1);
        String result = null;
        for(int i = 0; i < wordList.size(); i++) {
            int index = (i + offset) % wordList.size();
            String word = wordList.get(index);
            List<String> anagrams = getAnagramsWithOneMoreLetter(word);
            if(anagrams.size() > MIN_NUM_ANAGRAMS) {
                result = word;
                break;
            }
        }
        if(wordLength < MAX_WORD_LENGTH) {
            wordLength++;
        }
        return result;
    }

    public void resetGame() {
        wordLength = DEFAULT_WORD_LENGTH;
    }

    private String sortLetters(String in) {
        char[] result = in.toCharArray();
        Arrays.sort(result);
        return String.valueOf(result);
    }
}
