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

package com.google.engedu.continentaldivide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class ContinentMap extends View {
    public static final int MAX_HEIGHT = 255;
    public static final int NOISE = 10;
    private Cell[] map;
    private int boardSize;
    private Random random = new Random();
    private int maxHeight = 0, minHeight = 0;

    private Integer[] DEFAULT_MAP = {
            50, 50, 50, 50, 60,
            50, 22, 26, 70, 50,
            50, 24, 30, 30, 29,
            50, 28, 28, 29, 22,
            60, 50, 50, 50, 50,
    };

    public ContinentMap(Context context) {
        super(context);

        boardSize = (int) (Math.sqrt(DEFAULT_MAP.length));
        map = new Cell[boardSize * boardSize];
        for (int i = 0; i < boardSize * boardSize; i++) {
            map[i] = new Cell();
            map[i].height = DEFAULT_MAP[i];
        }
        maxHeight = Collections.max(Arrays.asList(DEFAULT_MAP));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    private class Cell {
        protected int height = 0;
        protected boolean flowsNW = false;
        protected boolean flowsSE = false;
        protected boolean basin = false;
        protected boolean processing = false;
    }

    private Cell getMap(int x, int y) {
        if (x >=0 && x < boardSize && y >= 0 && y < boardSize)
            return map[x + boardSize * y];
        else
            return null;
    }

    public void clearContinentalDivide() {
        for (int i = 0; i < boardSize * boardSize; i++) {
            map[i].flowsNW = false;
            map[i].flowsSE = false;
            map[i].basin = false;
            map[i].processing = false;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float tileSize = (float) canvas.getWidth() / boardSize;
        for(int row = 0; row < boardSize; row++) {
            for(int col = 0; col < boardSize; col++) {
                Cell cell = getMap(col, row);
                float left = col * tileSize;
                float top = row * tileSize;
                float right = left + tileSize;
                float bottom = top + tileSize;

                Paint paint = new Paint();
                paint.setARGB((int) Math.round(cell.height * 2 * (100D / (maxHeight * 2))), cell.flowsNW ? 256 : 128, cell.flowsSE ? 256 : 128, 128);
                canvas.drawRect(left, top, right, bottom, paint);

                Rect rect = new Rect();
                String text = String.valueOf(cell.height);
                Paint paintText = new Paint();
                paintText.setTextAlign(Paint.Align.CENTER);
                paintText.setTextSize(50);
                paintText.getTextBounds(text, 0, text.length(), rect);
                canvas.drawText(text, left + (tileSize / 2), top + (tileSize / 2) + ((rect.bottom - rect.top) / 2), paintText);
            }
        }
    }

    public void buildUpContinentalDivide(boolean oneStep) {
        if (!oneStep)
            clearContinentalDivide();
        boolean iterated = false;
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                Cell cell = getMap(x, y);
                if ((x == 0 || y == 0 || x == boardSize - 1 || y == boardSize - 1)) {
                    buildUpContinentalDivideRecursively(
                            x, y, x == 0 || y == 0, x == boardSize - 1 || y == boardSize - 1, -1);
                    if (oneStep) {
                        iterated = true;
                        break;
                    }
                }
            }
            if (iterated && oneStep)
                break;
        }
        invalidate();
    }

    private void buildUpContinentalDivideRecursively(
            int x, int y, boolean flowsNW, boolean flowsSE, int previousHeight) {
        if(x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
            Cell cell = getMap(x, y);
            if(cell.height >= previousHeight) {
                cell.flowsNW |= flowsNW;
                cell.flowsSE |= flowsSE;
                if(!cell.processing) {
                    cell.processing = true;
                    int[][] coords = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
                    for (int index = 0; index < coords.length; index++) {
                        int[] coord = coords[index];
                        buildUpContinentalDivideRecursively(x + coord[0], y + coord[1], flowsNW, flowsSE, cell.height);
                    }
                }
            }
        }
    }

    public void buildDownContinentalDivide(boolean oneStep) {
        if (!oneStep)
            clearContinentalDivide();
        while (true) {
            int maxUnprocessedX = -1, maxUnprocessedY = -1, foundMaxHeight = -1;
            for (int y = 0; y < boardSize; y++) {
                for (int x = 0; x < boardSize; x++) {
                    Cell cell = getMap(x, y);
                    if (!(cell.flowsNW || cell.flowsSE || cell.basin) && cell.height > foundMaxHeight) {
                        maxUnprocessedX = x;
                        maxUnprocessedY = y;
                        foundMaxHeight = cell.height;
                    }
                }
            }
            if (maxUnprocessedX != -1) {
                buildDownContinentalDivideRecursively(maxUnprocessedX, maxUnprocessedY, foundMaxHeight + 1);
                if (oneStep) {
                    break;
                }
            } else {
                break;
            }
        }
        invalidate();
    }

    private Cell buildDownContinentalDivideRecursively(int x, int y, int previousHeight) {
        Cell workingCell = new Cell();

        if(x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
            workingCell = getMap(x, y);
            if(!workingCell.processing && workingCell.height <= previousHeight) {
                workingCell.processing = true;
                workingCell.flowsSE = x == (boardSize - 1) || y == (boardSize - 1);
                workingCell.flowsNW = x == 0 || y == 0;

                int[][] coords = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
                Cell[] cells = new Cell[4];
                for (int index = 0; index < coords.length; index++) {
                    int[] coord = coords[index];
                    cells[index] = buildDownContinentalDivideRecursively(x + coord[0], y + coord[1], workingCell.height);
                }

                for (int index = 0; index < cells.length; index++) {
                    workingCell.flowsSE |= cells[index].flowsSE;
                    workingCell.flowsNW |= cells[index].flowsNW;
                }

                workingCell.basin = !workingCell.flowsSE && !workingCell.flowsNW;

                for (int index = 0; index < cells.length; index++) {
                    if(cells[index].height == workingCell.height) {
                        cells[index].flowsSE |= workingCell.flowsSE;
                        cells[index].flowsNW |= workingCell.flowsNW;
                    }
                }
            }
        } else {
            workingCell.height = previousHeight;
        }

        return workingCell;
    }

    public void generateTerrain(int detail) {
        int newBoardSize = (int) (Math.pow(2, detail) + 1);
        if (newBoardSize != boardSize * boardSize) {
            boardSize = newBoardSize;
            map = new Cell[boardSize * boardSize];
            for (int i = 0; i < boardSize * boardSize; i++) {
                map[i] = new Cell();
                map[i].height = 0;
            }
        }

        // SET CORNERS
        getMap(0, 0).height = random.nextInt(MAX_HEIGHT);
        getMap(boardSize - 1, 0).height = random.nextInt(MAX_HEIGHT);
        getMap(0, boardSize - 1).height = random.nextInt(MAX_HEIGHT);
        getMap(boardSize - 1, boardSize - 1).height = random.nextInt(MAX_HEIGHT);

        generateTerrain(0, 0, boardSize - 1, boardSize - 1);
        makeMapHeightsGreaterThanZero();

        maxHeight = 0;
        for(Cell c : map) {
            if(c.height > maxHeight) {
                maxHeight = c.height;
            }
        }

        invalidate();
    }

    private void makeMapHeightsGreaterThanZero() {
        int minValue = 0;
        for(Cell c : map) {
            if(c.height < minValue) {
                minValue = c.height;
            }
        }

        if(minValue < 0) {
            int sum = -minValue;

            for(Cell c : map) {
                c.height += sum;
            }
        }
    }

    private int getNoise() {
        return random.nextInt(NOISE * 2) - NOISE;
    }

    private void generateTerrain(int initX, int initY, int endX, int endY) {
        int middleX = initX + (int) Math.floor((endX + 1 - initX) / 2);
        int middleY = initY + (int) Math.floor((endY + 1 - initY) / 2);
        int middleValue = Math.round((getMap(initX, initY).height + getMap(endX, initY).height + getMap(initX, endY).height + getMap(endX, endY).height) / 4);
        middleValue += getNoise();
        getMap(middleX, middleY).height = middleValue;

        int leftValue = Math.round((getMap(initX, initY).height + getMap(initX, endY).height) / 2);
        leftValue += getNoise();
        getMap(initX, middleY).height = leftValue;

        int rightValue = Math.round((getMap(endX, initY).height + getMap(endX, endY).height) / 2);
        rightValue += getNoise();
        getMap(endX, middleY).height = rightValue;

        int topValue = Math.round((getMap(initX, initY).height + getMap(endX, initY).height) / 2);
        topValue += getNoise();
        getMap(middleX, initY).height = topValue;

        int bottomValue = Math.round((getMap(initX, endY).height + getMap(endX, endY).height) / 2);
        bottomValue += getNoise();
        getMap(middleX, endY).height = bottomValue;

        int size = endX - initX;
        if(size > 3) {
            generateTerrain(initX, initY, middleX, middleY);
            generateTerrain(middleX, middleY, endX, endY);
            generateTerrain(initX, middleY, middleX, endY);
            generateTerrain(middleX, initY, endX, middleY);
        }
    }
}
