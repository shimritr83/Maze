package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MazePoint {
    private double x ;
    private double y ;
    private boolean white ;

    public MazePoint(){}

    public MazePoint(double x,double Y,boolean white){
        this.x = x;
        this.y=y;
        this.white=white;

    }

    public double getX() {
        return this.x;
    }
    public double getY(){
        return this.y;

    }
    public boolean getWhite (){
        return this.white;
    }
}
