/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Piet
 */
public class PietsVector {
    final private List<Double> coordinates = new ArrayList<>();

    //**********************************************************
    //  constructors
    //**********************************************************
    public PietsVector(int n) {
        coordinates.addAll(Collections.nCopies(n, 0.0));
    }
    
    public PietsVector(double... doubles) {
        if (doubles.length == 0) {
            throw new RuntimeException("number of coordinates is 0!!!");
        }
        for (double d: doubles) coordinates.add(d);
    }
    
    //==============================================
    public PietsVector(List<Double> list) {
        if (list.isEmpty()) {
            throw new RuntimeException("number of coordinates is 0!!!");
        }
        coordinates.addAll(list);
    }
    
    public PietsVector(Point2D p) {
        this(p.getX(), p.getY());
    }
    
    //**********************************************************
    //  public methods
    //**********************************************************
    @Override
    public boolean equals(Object p) {
        if (p == null || !(p instanceof PietsVector)) return false;
        return coordinates.equals(((PietsVector) p).coordinates);
    }

    //==============================================
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.coordinates);
        return hash;
    }
    
    //==============================================
    /**
     * Gets the dimension of this PietsVector
     * @return the dimension
     */
    public int getDimension() {
        return coordinates.size();
    }
    
    //==============================================
    /**
     * gives the classical dotproduct of this PietsVector and the 
     * argument.
     * @param p the other PietsVector
     * @return the dotproduct, or a RuntimeException if the dimensions 
     * are unequal
     */
    public double dotProduct(PietsVector p) {
        if (!isSameDimension(p)) {
            throw new RuntimeException("Dimensions are unequal!!!");
        }
        double sum  = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            sum += coordinates.get(i) * p.coordinates.get(i);
        }
        return sum;
    }
    
    //==============================================
    /**
     * Gives the length of this PietsVector
     * @return the length
     */
    public double getLength() {
        return Math.sqrt(dotProduct(this));
    }
    
    //==============================================
    /**
     * Creates a new PietsVector in the same direction, but with length
     * equal to the argument.
     * @param the required length
     * @return the new PietsVecor of the required length
     */
    public PietsVector setLengthTo(double length) {
        double factor = length / getLength();
        List<Double> list = new ArrayList<>();
        for (int i = 1; i <= this.getDimension(); i++) {
            list.add(getCoordinate(i) * factor);
        }
        return new PietsVector(list);
    }
    
    //==============================================
    /**
     * Returns a new PietsVector that points in the same direction,
     * having unit length (i.e. length 1)
     * @return the unit PietsVector
     */
    public PietsVector getUnitVector() {
        return setLengthTo(1);
    }
    
    //==============================================
    /**
     * checks if this is the Nulvector, with all coordinates equal to 0.0
     * @return true if this Vector = 0, else false
     */
    public boolean isNulvector() {
        return getLength() == 0.0;
    }
    
    //==============================================
    /**
     * Gives the crossproduct of this PietsVector and the argument.
     * This is a vector perpendicular to both this vector and the argument,
     * according to a right hand coordinate system.
     * @param p the other PietsVector
     * @return the normal PietsVector to this vector and the argument, 
     * can be the nul vecotr
     */
    public PietsVector crossProduct(PietsVector p) {
        if (this.getDimension() != 3 || p.getDimension() != 3) {
            throw new RuntimeException("Dimensions must be 3!!");
        }
        double x = getCoordinate(2) * p.getCoordinate(3) - getCoordinate(3) * p.getCoordinate(2);
        double y = getCoordinate(3) * p.getCoordinate(1) - getCoordinate(1) * p.getCoordinate(3);
        double z = getCoordinate(1) * p.getCoordinate(2) - getCoordinate(2) * p.getCoordinate(1);
        return new PietsVector(x, y, z);
    }
    
    //==============================================
    /**
     * Gets the coordinate with dimension equal to the argument
     * Note that the legal values for the argument are those of Mthematics,
     * i.e. 1 <= dimensionIndex <= dimension
     * So, if this = (3, 2, 4, 5) then 1 <= index <= 4
     * @param dimensionIndex the index of the required dimension 
     * @return the coordinate belonging to this index
     */
    public double getCoordinate(int dimensionIndex) {
        if (dimensionIndex < 1 || dimensionIndex > coordinates.size()) {
            throw new RuntimeException("Wrong dimension!!!");
        }
        return coordinates.get(dimensionIndex - 1);
    }
    
    //==============================================
    /**
     * adds this PietsVector and the argument, and returns the sum
     * @param p the other PietsVector
     * @return the sum of this PietsVector and the argument
     */
    public PietsVector add(PietsVector p) {
        if (getDimension() != p.getDimension()) {
            throw new RuntimeException("Dimensions must be equal for sum");
        }
        List<Double> list = new ArrayList<>();
        for (int i = 1; i <= getDimension(); i++) {
            list.add(getCoordinate(i) + p.getCoordinate(i));
        }
        return new PietsVector(list);
    }
    
    //==============================================
    /**
     * Subtracts the argument from this PietsVector
     * @param p the PietsVector to subtract
     * @return 
     */
    public PietsVector sub(PietsVector p) {
        if (getDimension() != p.getDimension()) {
            throw new RuntimeException("Dimensions must be equal for sum");
        }
        List<Double> list = new ArrayList<>();
        for (int i = 1; i <= getDimension(); i++) {
            list.add(getCoordinate(i) - p.getCoordinate(i));
        }
        return new PietsVector(list);
    }
    
    //==============================================
    /**
     * scales this vector by the argument
     * @param d the scaling factor
     * @return the scaled PietsVector
     */
    public PietsVector scale(double d) {
        List<Double> list = new ArrayList<>();
        for (int i = 1; i <= getDimension(); i++) {
            list.add(getCoordinate(i) * d);
        }
        return new PietsVector(list);
    }
    
    //==============================================
    /**
     * Gives the angle in radians between this vector and the argument
     * @param p the other PietsVector
     * @return 
     */
    public double getAngleInRadians(PietsVector p) {
        PietsVector a = this.setLengthTo(1);
        PietsVector b = p.setLengthTo(1);
        return a.dotProduct(b);
    }
    
    //==============================================
    /**
     * returns the ngle in degrees between thi vector and the argument
     * @param p th other PietsVector
     * @return 
     */
    public double getAngleInDegrees(PietsVector p) {
        return Math.toDegrees(getAngleInRadians(p));
    }
    
    //==============================================
    public static PietsVector addAll(List<PietsVector> list) {
        if (list == null || list.isEmpty()) return null;
        int dimension = list.get(0).getDimension();
        return list.stream().reduce(new PietsVector(dimension), (a, b) -> a.add(b));
    }
    
    //==============================================
    public static PietsVector addAll(PietsVector... p) {
        if (p == null || p.length == 0) return null;
        return addAll(Arrays.asList(p));
    }
    //==============================================
    public static PietsVector averageVector(PietsVector... p) {
        if (p == null || p.length == 0) return null;
        PietsVector result = addAll(p);
        return result.scale(1 / p.length);
    }
    
    //==============================================
    public static PietsVector averageVector(List<PietsVector> list) {
        if (list == null || list.isEmpty()) return null;
        PietsVector result = addAll(list);
        return result.scale(1 / list.size());
    }
    
    //**********************************************************
    // private methods
    //**********************************************************
    //==============================================
    public boolean isSameDimension(PietsVector other) {
        return coordinates.size() == other.getDimension();
    }
    
}   // end PietsVector
