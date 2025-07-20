/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javabibliotheek;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 *
 * @author Piet
 */
public class Statistics {
    
    public static void main(String... args) {
//        List<Integer> x = List.of(95, 85, 80, 70, 60);
//        List<Integer> y = List.of(85, 95, 70, 65, 70);
//        var pair = linearRegression(x, y);
//        System.out.format("a = %.3f, b = %.3f%n", pair.k, pair.v);
//        System.out.format("%.3f%n", pair.v * 80 + pair.k);
        int n = 100;
        double alpha = 10. / n;
        System.out.format("%,d%n",LongStream.range(1, (int) Math.ceil(1. / alpha))
            .mapToObj(i -> nOverK(n, i))
            .reduce(BigInteger.ZERO, (a, b) -> a.add(b))
        );
    }
    
    public static double average(double[] x) {
        return Arrays.stream(x).average().getAsDouble();
    }
    
    public static <S extends Number, T extends Collection<S>> double average(T x) {
        return x.stream().mapToDouble(i -> i.doubleValue()).average().getAsDouble();
    }
    
    public static double variance(double[] x) {
        // as if x was the whole population
        double EX2 = Arrays.stream(x).map(i -> i * i).average().getAsDouble();
        double meanX = average(x);
        return EX2 - meanX * meanX;
    }
    
    public static <S extends Number, T extends Collection<S>> double variance(T x) {
        double EX2 = x.stream().mapToDouble(i -> Math.pow(i.doubleValue(), 2)).average().getAsDouble();
        double meanX = x.stream().mapToDouble(i -> i.doubleValue()).average().getAsDouble();
        return EX2 - meanX * meanX;
    }
    
    public static double stdev(double[] x) {
        return Math.sqrt(variance(x));
    }
    
    public static <S extends Number, T extends Collection<S>> double stdev(T x) {
        return Math.sqrt(variance(x));
    }
    
    public static BigInteger nOverK(long N, long K) {
        if (K == 0 || K == N) return BigInteger.ONE;
        if (K == 1 || K == N - 1) return new BigInteger("" + N);
//        return faculty(N).divide(faculty(K).multiply(faculty(N - K)));
        long max = Math.max(K, N - K);
        return LongStream.rangeClosed(max + 1, N)
            .mapToObj(i -> new BigInteger("" + i))
            .reduce(BigInteger.ONE, (a, b) -> a.multiply(b))
            .divide(faculty(N - max))
        ;
    }
    
    public static BigInteger faculty(long n) {
        if (n == 0 || n == 1) return BigInteger.ONE;
        return LongStream.rangeClosed(2, n)
            .mapToObj(i -> new BigInteger("" + i))
            .reduce(BigInteger.ONE, (a, b) -> a.multiply(b))
        ;
    }
    
    public static double binom(int N, double p, int K) {
        double q = 1 - p;
        return Math.pow(p, K) * Math.pow(q, N - K) * nOverK(N, K).doubleValue();
    }
    
    public static double poisson(double lambda, long N) {
        return Math.pow(Math.E, -lambda) * Math.pow(lambda, N) / faculty(N).doubleValue();
    }
    
    public static double correlationCoefficient(double[] x, double[] y) {
        // de formule is wel heel simpel: stel er zijn N waarnemingen van X en Y,
        // dan is mx = E[X], my = E[Y], SomXY = som(xi*yi) / n, sx = standaard
        // deviatie alsof de steekproef de gehele populatie betrof, idem voor sy
        // de formule is dan:
        // (SomXY / n - mx * my) / (sx * sy)
        // gaan we
        double somXY = IntStream.range(0, x.length).mapToDouble(i -> x[i] * y[i]).sum();
        double mx = average(x);
        double my = average(y);
        double sx = stdev(x);
        double sy = stdev(y);
        return (somXY / x.length - mx * my) / (sx * sy);
    }
    
    public static <S extends Number, T extends Collection<S>, U extends Collection<S>> 
        double correlationCoefficient(T x, U y) {
            Iterator<S> itX = x.iterator();
            Iterator<S> itY = y.iterator();
            double somXY = IntStream.range(0, x.size())
                .mapToDouble(i -> itX.next().doubleValue() * itY.next().doubleValue()).sum();
            double mx = average(x);
            double my = average(y);
            double sx = stdev(x);
            double sy = stdev(y);
            return (somXY / x.size() - mx * my) / (sx * sy);
    }
    
    public static <S extends Number, T extends Collection<S>> List<Integer> getRankList(T list) {
        Iterator<Integer> iter = IntStream.rangeClosed(1, list.size()).boxed().collect(toList()).iterator();
        var map = list.stream()
            .sorted()
            .collect(toMap(i -> i, i -> iter.next()))
        ;
        var result = list.stream().map(map::get).collect(toList());
        return result;
    }
    
    public static int[] getRankList(double[] a) {
        List<Integer> list = getRankList(Arrays.stream(a).boxed().collect(toList()));
        return list.stream().mapToInt(i -> i).toArray();
    }
    
    public static double SpearmanRankCorrelationCoefficient(double[] x, double[] y) {
        List<Integer> rankX = getRankList(Arrays.stream(x).boxed().collect(toList()));
        List<Integer> rankY = getRankList(Arrays.stream(y).boxed().collect(toList()));
        return correlationCoefficient(rankX, rankY);
    }
    
   private static double srcc(double[] x, double[] y) {
       int[] xL = getRankList(x);
       int[] yL = getRankList(y);
       double som = 0;
       for (int i = 0; i < x.length; i++) {
           som += Math.pow(xL[i] - yL[i], 2);
       }
       var result = 1 - 6 * som / (xL.length * (xL.length * xL.length - 1));
       return result;
   }
   
public static <S1 extends Number, S2 extends Number, T extends Collection<S1>, U extends Collection<S2>> 
             Pair<Double, Double> linearRegression(T x, U y) {
       double meanX = average(x);
       double meanY = average(y);
       double somX = meanX * x.size();
       double somY = meanY * x.size();
       Iterator<S1> iterT = x.iterator();
       Iterator<S2> iterU = y.iterator();
       double somXY = IntStream.range(0, x.size())
           .mapToDouble(i -> iterT.next().doubleValue() * iterU.next().doubleValue())
           .sum()
       ;
       double somX2 = x.stream()
           .mapToDouble(i -> {double d = i.doubleValue(); return d * d;})
           .sum()
       ;
       double b = (x.size() * somXY - somX * somY) / (x.size() * somX2 - somX * somX);
       double a = meanY - b * meanX;
       return new Pair<>(a, b);
   }
}