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

package com.google.engedu.blackhole;

import android.util.Pair;

import org.junit.Test;
import static org.junit.Assert.*;

public class BlackHoleBoardTest {
    @Test
    public void testCoordsToIndex() {
        BlackHoleBoard b = new BlackHoleBoard();
        assertEquals(0, b.coordsToIndex(0, 0));
        assertEquals(1, b.coordsToIndex(0, 1));
        assertEquals(2, b.coordsToIndex(1, 1));
        assertEquals(3, b.coordsToIndex(0, 2));
        assertEquals(4, b.coordsToIndex(1, 2));
        assertEquals(5, b.coordsToIndex(2, 2));
    }

    @Test
    public void testIndexToCoords() {
        BlackHoleBoard b = new BlackHoleBoard();
        Coordinates coords = b.indexToCoords(0);
        assertEquals(0, coords.x);
        assertEquals(0, coords.y);
        coords = b.indexToCoords(1);
        assertEquals(0, coords.x);
        assertEquals(1, coords.y);
        for (int i = 0; i < b.BOARD_SIZE; i++) {
            coords = b.indexToCoords(i);
            assertEquals(i, b.coordsToIndex(coords.x, coords.y));
        }
    }

    @Test
    public void testGetScore() {
        BlackHoleBoard board1 = new BlackHoleBoard();
        for(int i = 0; i < 21; i++) {
            if(i != 4) {

                board1.setValue(i);
            }
        }
        int computerScore = 1 + 3 + 8;
        int userScore = 2 + 5 + 7;
        assertEquals(userScore - computerScore, board1.getScore());
    }
}
