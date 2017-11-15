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

package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int steps;
    private PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles = new ArrayList<>();
        Bitmap initialBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        int division = (int) Math.floor(parentWidth / 3d);
        int tileNumber = 0;
        for(int e = 0; e < NUM_TILES; e++) {
            for(int i = 0; i < NUM_TILES; i++) {
                if(i == NUM_TILES - 1 && e == NUM_TILES - 1) {
                    tiles.add(null);
                } else {
                    Bitmap tileBitmap = Bitmap.createBitmap(initialBitmap, i * division, e * division, division, division);
                    PuzzleTile tile = new PuzzleTile(tileBitmap, tileNumber);
                    tiles.add(tile);
                }
                tileNumber++;
            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        previousBoard = null;
        steps = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    private Coordinate indexToXY(int index) {
        int y = (int) Math.floor((double) index / NUM_TILES);
        int x = index - (y * NUM_TILES);
        return new Coordinate(x, y);
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private int getEmptyTileIndex() {
        for(int i = 0; i < tiles.size(); i++) {
            if(tiles.get(i) == null) {
                return i;
            }
        }

        return -1;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        int emptyTileIndex = getEmptyTileIndex();
        ArrayList<PuzzleBoard> result = new ArrayList<>();

        if(emptyTileIndex != -1) {
            Coordinate emptyTileCoordinate = indexToXY(emptyTileIndex);

            for(int i = 0; i < NEIGHBOUR_COORDS.length; i++) {
                Coordinate coordinate = emptyTileCoordinate.move(NEIGHBOUR_COORDS[i]);
                if(coordinate.isValid()) {
                    int index = XYtoIndex(coordinate.getX(), coordinate.getY());
                    PuzzleBoard newPuzzleBoard = new PuzzleBoard(this);
                    newPuzzleBoard.swapTiles(emptyTileIndex, index);
                    result.add(newPuzzleBoard);
                }
            }
        }

        return result;
    }

    public ArrayList<PuzzleBoard> filteredNeighbours() {
        ArrayList<PuzzleBoard> result = new ArrayList<>();

        for(PuzzleBoard board : neighbours()) {
            if(board != previousBoard) {
                result.add(board);
            }
        }

        return result;
    }

    private int getManhattanDistance() {
        int result = 0;

        for(int i = 0; i < tiles.size(); i++) {
            PuzzleTile tile = tiles.get(i);
            if(tile != null) {
                Coordinate c1 = indexToXY(i);
                Coordinate c2 = indexToXY(tile.getNumber());
                result += Math.abs(c1.getX() - c2.getX()) + Math.abs(c1.getY() - c2.getY());
            }
        }

        return result;
    }

    public int priority() {
        return getManhattanDistance() + steps;
    }

    public ArrayList<PuzzleBoard> getSolution() {
        ArrayList<PuzzleBoard> result = new ArrayList<>();
        result.add(this);
        if(previousBoard != null) {
            result.addAll(previousBoard.getSolution());
        }
        return result;
    }

    @Override
    public String toString() {
        return "[board = " + tiles.toString() + ", steps = " + steps + ", priority = " + priority() + "]";
    }

    public class Coordinate {

        private int x;

        private int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Coordinate move(int[] movement) {
            int newX = x + movement[0];
            int newY = y + movement[1];

            return new Coordinate(newX, newY);
        }

        public boolean isValid() {
            return x >= 0 && y >= 0 && x < NUM_TILES && y < NUM_TILES;
        }

    }

}
