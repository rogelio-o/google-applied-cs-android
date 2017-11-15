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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        String key = s.substring(0, 1);
        String newS = s.length() == 1 ? null : s.substring(1);
        TrieNode child = children.get(key);
        if(child == null) {
            child = new TrieNode();
            children.put(key, child);
        }
        if(newS == null) {
            child.isWord = true;
        } else {
            child.add(newS);
        }
    }

    public boolean isWord(String s) {
        if(s.isEmpty()) {
            return false;
        } else {
            String key = s.substring(0, 1);
            String newS = s.length() == 1 ? null : s.substring(1);
            TrieNode child = children.get(key);
            if (child == null) {
                return false;
            } else if (newS == null) {
                return child.isWord;
            } else {
                return child.isWord(newS);
            }
        }
    }

    public String getAnyWordStartingWith(String s) {
        if(s.isEmpty()) {
            return getRandomFullWord();
        } else {
            String key = s.substring(0, 1);
            String newS = s.length() == 1 ? null : s.substring(1);
            TrieNode child = children.get(key);
            if (child == null) {
                return null;
            } else if (newS == null) {
                if (child.isWord) {
                    return key;
                } else {
                    String append = child.getRandomFullWord();
                    if(append == null) {
                        return null;
                    } else {
                        return key + append;
                    }
                }
            } else {
                String append = child.getAnyWordStartingWith(newS);
                if(append == null) {
                    return null;
                } else {
                    return key + append;
                }
            }
        }
    }

    private String getRandomFullWord() {
        Set<String> keys = children.keySet();
        if(keys.isEmpty()) {
            return "";
        } else {
            Random random = new Random();
            String key = keys.toArray(new String[keys.size()])[random.nextInt(keys.size())];
            TrieNode child = children.get(key);
            if (child.isWord) {
                return key;
            } else {
                return key + child.getRandomFullWord();
            }
        }
    }

    public String getGoodWordStartingWith(String s) {
        if(s.isEmpty()) {
            return getRandomFullWord();
        } else {
            String key = s.substring(0, 1);
            String newS = s.length() == 1 ? null : s.substring(1);
            TrieNode child = children.get(key);
            if (child == null) {
                return null;
            } else if (newS == null) {
                if (child.isWord) {
                    return key;
                } else {
                    String append = child.getNotAWordFullWord();
                    if(append == null) {
                        return null;
                    } else {
                        return key + append;
                    }
                }
            } else {
                String append = child.getGoodWordStartingWith(newS);
                if(append == null) {
                    return null;
                } else {
                    return key + append;
                }
            }
        }
    }

    private String getNotAWordFullWord() {
        String selected = null;
        for(Map.Entry<String, TrieNode> entry : children.entrySet()) {
            if(!entry.getValue().isWord) {
                selected = entry.getKey() + entry.getValue().getRandomFullWord();
                break;
            }
        }
        if(selected == null) {
            selected = getRandomFullWord();
        }

        return selected;
    }
}
