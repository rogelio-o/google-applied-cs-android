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

package com.google.engedu.bstguesser;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class TreeNode {
    private static final int SIZE = 60;
    private static final int MARGIN = 20;
    private int value, height;
    protected TreeNode left, right;
    private boolean showValue;
    private int x, y;
    private int color = Color.rgb(150, 150, 250);

    public TreeNode(int value) {
        this.value = value;
        this.height = 0;
        showValue = false;
        left = null;
        right = null;
    }

    private void calcHeight() {
        int heightLeft, heightRight;
        if(left == null) {
            heightLeft = 0;
        } else {
            left.calcHeight();
            heightLeft = left.height + 1;
        }
        if(right == null) {
            heightRight = 0;
        } else {
            right.calcHeight();
            heightRight = right.height + 1;
        }

        height = Math.max(heightLeft, heightRight);
    }

    private int getBalanceFactor() {
        int a = left != null ? left.height : -1;
        int b = right != null ? right.height : -1;
        return a - b;
    }

    private TreeNode getInPathNode(int valueToInsert) {
        if(valueToInsert < value) {
            return left.value != valueToInsert ? left : null;
        } else {
            return right.value != valueToInsert ? right : null;
        }
    }

    private void replaceChildeNode(TreeNode oldNode, TreeNode newNode) {
        if(oldNode.equals(left)) {
            left = newNode;
        } else {
            right = newNode;
        }
    }

    public void insert(int valueToInsert, TreeNode parent, BinarySearchTree searchTree) {
        TreeNode c = null;

        if(valueToInsert < value) {
            if(left == null) {
                left = new TreeNode(valueToInsert);
            } else {
                left.insert(valueToInsert, this, searchTree);
                c = left;
            }
        } else {
            if(right == null) {
                right = new TreeNode(valueToInsert);
            } else {
                right.insert(valueToInsert, this, searchTree);
                c = right;
            }
        }

        calcHeight();
        if(c != null) {
            int balanceFactor = this.getBalanceFactor();
            if (balanceFactor != 0) {
                TreeNode b = c.getInPathNode(valueToInsert);
                if (b != null) {
                    if (isLeftLeftCase(this, c, b)) {
                        doLeftLeftCase(parent, searchTree, this, c, b);
                    } else if (isLeftRightCase(this, c, b)) {
                        doLeftRightCase(parent, searchTree, this, c, b);
                    } else if (isRightRightCase(this, c, b)) {
                        doRightRightCase(parent, searchTree, this, c, b);
                    } else if (isRightLeftCase(this, c, b)) {
                        doRightLeftCase(parent, searchTree, this, c, b);
                    }

                    calcHeight();
                }
            }
        }
    }

    public boolean isLeftLeftCase(TreeNode d, TreeNode c, TreeNode b) {
        return c.equals(d.left) && b.equals(c.left);
    }

    public boolean isLeftRightCase(TreeNode d, TreeNode c, TreeNode b) {
        return c.equals(d.left) && b.equals(c.right);
    }

    public boolean isRightRightCase(TreeNode d, TreeNode c, TreeNode b) {
        return c.equals(d.right) && b.equals(c.right);
    }

    public boolean isRightLeftCase(TreeNode d, TreeNode c, TreeNode b) {
        return c.equals(d.right) && b.equals(c.left);
    }

    public void doLeftLeftCase(TreeNode parent, BinarySearchTree searchTree, TreeNode z, TreeNode y, TreeNode x) {
        z.left = y.right;
        y.right = z;
        if(parent != null) {
            parent.replaceChildeNode(z, y);
        } else {
            searchTree.setRoot(y);
        }
    }

    public void doLeftRightCase(TreeNode parent, BinarySearchTree searchTree, TreeNode z, TreeNode y, TreeNode x) {
        y.right = x.left;
        x.left = y;
        z.left = x;

        z.left = x.right;
        x.right = z;
        if(parent != null) {
            parent.replaceChildeNode(z, x);
        } else {
            searchTree.setRoot(x);
        }
    }

    public void doRightRightCase(TreeNode parent, BinarySearchTree searchTree, TreeNode z, TreeNode y, TreeNode x) {
        z.right = y.left;
        y.left = z;
        if(parent != null) {
            parent.replaceChildeNode(z, y);
        } else {
            searchTree.setRoot(y);
        }
    }

    public void doRightLeftCase(TreeNode parent, BinarySearchTree searchTree, TreeNode z, TreeNode y, TreeNode x) {
        z.right = x;
        y.left = x.right;
        x.right = y;

        z.right = x.left;
        x.left = z;
        if(parent != null) {
            parent.replaceChildeNode(z, x);
        } else {
            searchTree.setRoot(x);
        }
    }

    public int getValue() {
        return value;
    }

    public void positionSelf(int x0, int x1, int y) {
        this.y = y;
        x = (x0 + x1) / 2;

        if(left != null) {
            left.positionSelf(x0, right == null ? x1 - 2 * MARGIN : x, y + SIZE + MARGIN);
        }
        if (right != null) {
            right.positionSelf(left == null ? x0 + 2 * MARGIN : x, x1, y + SIZE + MARGIN);
        }
    }

    public void draw(Canvas c) {
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.GRAY);
        if (left != null)
            c.drawLine(x, y + SIZE/2, left.x, left.y + SIZE/2, linePaint);
        if (right != null)
            c.drawLine(x, y + SIZE/2, right.x, right.y + SIZE/2, linePaint);

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        c.drawRect(x-SIZE/2, y, x+SIZE/2, y+SIZE, fillPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(SIZE * 2/3);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(/*showValue ? */String.valueOf(value)/* : "?"*/, x, y + SIZE * 3/4, paint);

        if (height > 0) {
            Paint heightPaint = new Paint();
            heightPaint.setColor(Color.MAGENTA);
            heightPaint.setTextSize(SIZE * 2 / 3);
            heightPaint.setTextAlign(Paint.Align.LEFT);
            c.drawText(String.valueOf(height), x + SIZE / 2 + 10, y + SIZE * 3 / 4, heightPaint);
        }

        if (left != null)
            left.draw(c);
        if (right != null)
            right.draw(c);
    }

    public int click(float clickX, float clickY, int target) {
        int hit = -1;
        if (Math.abs(x - clickX) <= (SIZE / 2) && y <= clickY && clickY <= y + SIZE) {
            if (!showValue) {
                if (target != value) {
                    color = Color.RED;
                } else {
                    color = Color.GREEN;
                }
            }
            showValue = true;
            hit = value;
        }
        if (left != null && hit == -1)
            hit = left.click(clickX, clickY, target);
        if (right != null && hit == -1)
            hit = right.click(clickX, clickY, target);
        return hit;
    }

    public void invalidate() {
        color = Color.CYAN;
        showValue = true;
    }
}
