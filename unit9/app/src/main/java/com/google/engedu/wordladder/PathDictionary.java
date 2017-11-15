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

package com.google.engedu.wordladder;

import android.util.Log;

import com.google.engedu.wordladder.graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

public class PathDictionary {
    private static final int MAX_WORD_LENGTH = 4;
    private static HashSet<String> words = new HashSet<>();
    private static Graph graph = new Graph();
    private static final int MAX_PATH_SIZE = 8;

    public PathDictionary(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        Log.i("Word ladder", "Loading dict");
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        Log.i("Word ladder", "Loading dict");
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() > MAX_WORD_LENGTH) {
                continue;
            }
            words.add(word);
            graph.addWord(word);
        }
    }

    public boolean isWord(String word) {
        return words.contains(word.toLowerCase());
    }

    private ArrayList<String> neighbours(String word) {
        return graph.getNeighboursWords(word);
    }

    public String[] findPath(String start, String end) {
        PriorityQueue<ArrayList<String>> queue = new PriorityQueue<>(1000, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return o1.size() - o2.size();
            }
        });

        ArrayList<String> initialPath = new ArrayList<>();
        initialPath.add(start);
        queue.add(initialPath);

        HashMap<String, Integer> reachedWords = new HashMap<>();
        while (!queue.isEmpty()) {
            ArrayList<String> path = queue.poll();
            ArrayList<String> neighbours = neighbours(path.get(path.size() - 1));
            if(neighbours.contains(end)) {
                path.add(end);
                return path.toArray(new String[path.size()]);
            } else if(path.size() < MAX_PATH_SIZE) {
                for(String neighbor : neighbours) {
                    Integer reachedWord = reachedWords.get(neighbor);
                    if(!path.contains(neighbor) && (reachedWord == null || reachedWord > (path.size() + 1))) {
                        ArrayList<String> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        queue.add(newPath);
                        reachedWords.put(neighbor, newPath.size());
                    }
                }
            }
        }

        return null;
    }
}
