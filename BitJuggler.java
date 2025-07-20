/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;

/**
 *
 * @author Sylvia
 */
public final class BitJuggler {
    
    //================================================
    // members
    //================================================
    private final long value;
    private static final LongBinaryOperator SET = (x, i) -> x | (1L << i);
    private static final LongBinaryOperator CLEAR = (x, i) -> x & ~(1L << i);
    private static final LongBinaryOperator TOGGLE = (x, i) -> x ^ (1L << i);
    
    //================================================
    // constructors
    //================================================
    private BitJuggler(long v) {
        value = v;
    }
    
    //================================================
    // public methods
    //================================================
    public static BitJuggler initialize(long v) {
        return new BitJuggler(v);
    }
    
    //----------------------------
    public BitJuggler setBits(long... bits) {
        return reduce(SET, bits);
    }
    
    //----------------------------
    public BitJuggler clearBits(long... bits) {
        return reduce(CLEAR, bits);
    }
    
    //----------------------------
    public BitJuggler toggleBits(long... bits) {
        return reduce(TOGGLE, bits);
    }
    
    //----------------------------
    public long getValue() {
        return value;
    }
    
    //----------------------------
    public static void main(String... args) {
        byte b = -127;
        System.out.println("input:  "+ Long.toBinaryString(b));
        long c = BitJuggler.initialize(b).clearBits(1, 2).setBits(3).toggleBits(5, 6).getValue();
        System.out.println("output: "+ Long.toBinaryString(c));
        byte d = (byte) c;
        System.out.format("%o%n", d);
    }
    
    //================================================
    // private methods
    //================================================
    private BitJuggler reduce(LongBinaryOperator lbo, long... bits) {
        return new BitJuggler(Arrays.stream(bits).reduce(value, lbo));
    }

    //================================================
    // end of class
    //================================================
}
