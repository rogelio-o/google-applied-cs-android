package com.google.engedu.wordladder.graph;

/**
 * Created by rogelio on 5/11/17.
 */

public class Edge {

    private Vertex one, two;

    public Edge(Vertex one, Vertex two){
        this.one = (one.getLabel().compareTo(two.getLabel()) <= 0) ? one : two;
        this.two = (this.one == one) ? two : one;
    }

    public Vertex getNeighbor(Vertex current){
        if(!(current.equals(one) || current.equals(two))){
            return null;
        }

        return (current.equals(one)) ? two : one;
    }

    public Vertex getOne(){
        return this.one;
    }

    public Vertex getTwo(){
        return this.two;
    }

    @Override
    public String toString(){
        return "({" + one + ", " + two + "})";
    }

    @Override
    public int hashCode(){
        return (one.getLabel() + two.getLabel()).hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Edge)){
            return false;
        }

        Edge e = (Edge)other;

        return e.one.equals(this.one) && e.two.equals(this.two);
    }

}
