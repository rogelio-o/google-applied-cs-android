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

package com.google.engedu.touringmusician;


import android.graphics.Point;
import android.util.Log;

import java.util.Iterator;

public class CircularLinkedList implements Iterable<Point> {

    private class Node {
        Point point;
        Node prev, next;

        public Node(Point point) {
            this.point = point;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public Node getNext() {
            return next;
        }
    }

    Node head;

    public void insertBeginning(Point p) {
        Node node = new Node(p);

        if(head != null) {
            node.setNext(head);
            node.setPrev(head.getPrev());
            head.getPrev().setNext(node);
            head.setPrev(node);
        } else {
            node.setNext(node);
            node.setPrev(node);
        }

        head = node;
    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        float total = 0;
        Point prevP = null;
        for(Point p : this) {
            if(prevP != null) {
                total += distanceBetween(prevP, p);
            }
            prevP = p;
        }
        if(prevP != null && head != null) {
            total += distanceBetween(prevP, head.point);
        }
        return total;
    }

    public void insertNearest(Point p) {
        Node selectedNode = null;
        Float minDistance = null;
        Node iNode = head;
        while(iNode != null) {
            float dist = distanceBetween(iNode.point, p);
            if(selectedNode == null || minDistance == null || minDistance > dist) {
                minDistance = dist;
                selectedNode = iNode;
            }

            iNode = !iNode.getNext().equals(head) ? iNode.getNext() : null;
        }

        addBefore(selectedNode, p);
    }

    public void insertSmallest(Point p) {
        Node selectedNode = null;
        float totalDistance = totalDistance();
        Float minDistance = null;
        Node iNode = head;
        while(iNode != null) {
            float oldPartialDist = distanceBetween(iNode.point, iNode.next.point);
            float dist = (totalDistance - oldPartialDist) + distanceBetween(iNode.point, p) + distanceBetween(p, iNode.next.point);
            if(selectedNode == null || minDistance == null || minDistance > dist) {
                minDistance = dist;
                selectedNode = iNode;
            }

            iNode = !iNode.getNext().equals(head) ? iNode.getNext() : null;
        }

        addBefore(selectedNode, p);
    }

    private void addBefore(Node selectedNode, Point p) {
        Node newNode = new Node(p);
        if(selectedNode != null) {
            newNode.setNext(selectedNode.getNext());
            selectedNode.getNext().setPrev(newNode);
            selectedNode.setNext(newNode);
            newNode.setPrev(selectedNode);
        } else {
            newNode.setPrev(newNode);
            newNode.setNext(newNode);
            head = newNode;
        }
    }

    public void reset() {
        head = null;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }


}
