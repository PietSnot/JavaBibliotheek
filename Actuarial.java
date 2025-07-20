/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Piet
 */
public class Actuarial {
    
    public static void main(String... args) {
//        List<Double> cf = Arrays.asList(1000., 1000., 1000., -1000., 1000., 1000.);
//        System.out.println("cashflows: " + cf);
//        
//        //----------------------------------------------------------------
//        double interest = 0.0;
//        double reserve = presentValue(interest, cf, false);
//        System.out.format("pv tegen %.8f peruun = %,.2f%n", interest, reserve);
//        double i = findInterest(cf, reserve, 0.0000001, false);
//        System.out.format("berekende interest: %.8f peruun%n", i);
//        
//        /*
//        output:
//            cashflows: [1000.0, 1000.0, 1000.0, -1000.0, 1000.0, 1000.0]
//            pv tegen 0,00000000 peruun = 4.000,00
//            berekende interest: 0,00000000 peruun
//        */
//        
//        interest = 0.1234567;
//        reserve = presentValue(interest, cf, false);
//        System.out.format("pv tegen %.8f peruun = %,.2f%n", interest, reserve);
//        i = findInterest(cf, reserve, 0.0000001, false);
//        System.out.format("berekende interest: %.8f peruun%n", i);
//        
//        /*
//        output:
//            pv tegen 0,12345670 peruun = 2.816,00
//            berekende interest: 0,12345665 peruun
//        */
//        
//        interest = 0.2;
//        reserve = presentValue(interest, cf, false);
//        System.out.format("pv tegen %.8f peruun = %,.2f%n", interest, reserve);
//        i = findInterest(cf, reserve, 0.0000001, false);
//        System.out.format("berekende interest: %.8f peruun%n", i);
//        
//        /*
//        output:
//            pv tegen 0,20000000 peruun = 2.361,00
//            berekende interest: 0,19999990 peruun
//        */
//        
//        interest = 0.25;
//        reserve = presentValue(interest, cf, false);
//        System.out.format("pv tegen %.8f peruun = %,.2f%n", interest, reserve);
//        i = findInterest(cf, reserve, 0.0000001, false);
//        System.out.format("berekende interest: %.8f peruun%n", i);
//        
//        /*
//        output:
//            pv tegen 0,25000000 peruun = 2.132,22
//            Exception in thread "main" java.lang.IllegalArgumentException: Cannot find the required interest
//	        at pietsLibrary.Actuarial.lambda$findInterest$4(Actuarial.java:92)
//        */
        System.out.format("%8.4f%n", a(30, .04, false));
        System.out.format("%8.4f%n", a3(30, .04, true));
    }  // einde main
    
    //----------------------------------------------------------------------
    public static List<Double> discontoFactors(double i_peruun, int max_duration, boolean prenumerando) {
        double v = 1 / (1 + i_peruun);
        return Stream
            .iterate(prenumerando ? 1.0 : v, a -> a * v)
            .limit(max_duration)
            .collect(toList())
        ;
    }
    
    //----------------------------------------------------------------------
    public static double presentValue(double i_peruun, List<Double> cf, boolean prenumerando) {
        List<Double> df = discontoFactors(i_peruun, cf.size(), prenumerando);
        return zip(df, cf)
            .stream()
            .mapToDouble(e -> e.first * e.second)
            .sum()
        ;
    }
    
    //----------------------------------------------------------------------
    public static OptionalDouble findRightPerunage(int maxInterestPercentage, double reserve, List<Double> cf, boolean prenumerando) {
        double teken = Math.signum(reserve - presentValue(0, cf, prenumerando));
        return IntStream
            .rangeClosed(0, maxInterestPercentage)
            .mapToDouble(e -> e / 100.)
            .filter(e -> Math.signum(reserve - presentValue(e, cf, prenumerando)) != teken)
            .findFirst()
        ;
    }
    
    //----------------------------------------------------------------------
    public static double findInterest(List<Double> cf, double reserve, double accuracy, boolean prenumerando) {
        double right = findRightPerunage(20, reserve, cf, prenumerando)
            .orElseThrow( () -> new IllegalArgumentException("Cannot find the required interest"))
        ;
        double left = 0.;
        double signumLeft = Math.signum(reserve - presentValue(left, cf, prenumerando));
        while (right - left > accuracy) {
            double half = (right + left) / 2;
            if (Math.signum(reserve - presentValue(half, cf, prenumerando)) != signumLeft) right = half;
            else left = half;
        }
        return left;
    }
    
    //----------------------------------------------------------------------
    public static <K, V> List<Tweetal<K, V>> zip(List<K> listK, List<V> listV) {
        return IntStream
            .range(0, Math.min(listK.size(), listV.size()))
            .mapToObj(e -> new Tweetal<>(listK.get(e), listV.get(e)))
            .collect(Collectors.toList())
        ;
    }
    
    //----------------------------------------------------------------------
    public static double A(int n, double r) {
        return Math.pow(1 / (1 + r), n);
    }
    
    //----------------------------------------------------------------------
    public static double a(int n, double r, boolean prenumerando) {
        return r == 0.0 ? n : 
           prenumerando ? (1  + r) * (1 - A(n, r)) / r :
                          (1 - A(n, r)) / r;
    }
    
    //----------------------------------------------------------------------
    public static double a(int n, double r, boolean prenumerando, int periodsPerYear) {
        return a(n * periodsPerYear, r / periodsPerYear, prenumerando);
    }
    
    //----------------------------------------------------------------------
    public static double a2(int n, double r, boolean prenumerando) {
        double v = 1 / (1 + r);
        return DoubleStream.iterate(0, d -> (prenumerando ? 1 : v) + v * d)
            .skip(n)
            .findFirst()
            .getAsDouble();
    }
    
    //----------------------------------------------------------------------
    public static double a3(int n, double r, boolean prenumerando) {
        if (n == 0) return 0;
        double v = 1 / (1 + r);
        double constant = prenumerando ? 1 : v;
        return IntStream.range(0, n)
            .mapToDouble(i -> i)
            .reduce(0, (d, i) -> v * d + constant)
        ;
    }
}

/*****************************************************************************/

class Tweetal<K, V> {
    K first;
    V second;
    Tweetal(K k, V v) {
        first = k;
        second = v;
    }
}
