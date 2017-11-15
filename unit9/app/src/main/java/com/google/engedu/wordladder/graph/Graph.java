package com.google.engedu.wordladder.graph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rogelio on 5/11/17.
 */

public class Graph {

    private HashMap<String, Vertex> vertices;
    private HashMap<Integer, Edge> edges;

    public Graph() {
        this.vertices = new HashMap<String, Vertex>();
        this.edges = new HashMap<Integer, Edge>();
    }

    public boolean addEdge(Vertex one, Vertex two) {
        if(one.equals(two)){
            return false;
        }

        //ensures the Edge is not in the Graph
        Edge e = new Edge(one, two);
        if(edges.containsKey(e.hashCode())) {
            return false;
        }

        //and that the Edge isn't already incident to one of the vertices
        else if(one.containsNeighbor(e) || two.containsNeighbor(e)) {
            return false;
        }

        edges.put(e.hashCode(), e);
        one.addNeighbor(e);
        two.addNeighbor(e);
        return true;
    }


    public void addWord(String word) {
        Vertex vertex = new Vertex(word);
        vertices.put(word, vertex);
        for(Vertex possibleNeighborhood : vertices.values()) {
            if(isOneWordPermutation(word, possibleNeighborhood.getLabel())) {
                addEdge(vertex, possibleNeighborhood);
            }
        }
    }

    private boolean isOneWordPermutation(String word1, String word2) {
        if(word1.length() != word2.length()) {
            return false;
        }

        char[] chars1 = word1.toCharArray();
        char[] chars2 = word2.toCharArray();
        boolean oneChange = false;

        for(int i = 0; i < chars1.length; i++) {
            char c1 = chars1[i];
            char c2 = chars2[i];

            if(c1 != c2) {
                if(oneChange) {
                    return  false;
                }
                oneChange = true;
            }
        }

        return true;
    }

    public ArrayList<String> getNeighboursWords(String word) {
        Vertex vertex = vertices.get(word);
        ArrayList<Edge> edges = vertex.getNeighbors();
        ArrayList<String> neighborHoodWords = new ArrayList<>();
        for(Edge edge : edges) {
            neighborHoodWords.add(edge.getNeighbor(vertex).getLabel());
        }
        return neighborHoodWords;
    }

}
