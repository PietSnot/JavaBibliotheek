/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Sylvia
 */
public class PollardRho {

    private final static BigInteger ZERO = new BigInteger("0");
    private final static BigInteger ONE = new BigInteger("1");
    private final static BigInteger TWO = new BigInteger("2");
    private final static SecureRandom random = new SecureRandom();

    public static BigInteger rho(BigInteger N) {
        BigInteger divisor;
        BigInteger c = new BigInteger(N.bitLength(), random);
        BigInteger x = new BigInteger(N.bitLength(), random);
        BigInteger xx = x;

        // check divisibility by 2
        if (N.mod(TWO).compareTo(ZERO) == 0) {
            return TWO;
        }

        do {
            x = x.multiply(x).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            divisor = x.subtract(xx).gcd(N);
        } while ((divisor.compareTo(ONE)) == 0);

        return divisor;
    }

    public static void factor(BigInteger N, List<BigInteger> list) {
        if (N.compareTo(ONE) == 0) {
            return;
        }
        if (N.isProbablePrime(20)) {
            list.add(N);
            return;
        }
        BigInteger divisor = rho(N);
        factor(divisor, list);
        factor(N.divide(divisor), list);
    }

    public static void main(String[] args) {
//        BigInteger N = new BigInteger("" + (2 * 2 * 3 * 3 * 5 * 7 * 11));
        BigInteger N = new BigInteger( 2 * 2 * 2 * 3 * 5 * 7 * 11 + "");
        List<BigInteger> list = new ArrayList<>();
        factor(N, list);
        System.out.println("N: " + N);
        System.out.println(list);
        BigInteger b = list.stream().reduce((b1, b2) -> b1.multiply(b2)).orElseGet(() -> BigInteger.ZERO);
        System.out.println(b);
        Map<BigInteger, Long> map = list.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        long nrOfDivisors = map.values().stream().reduce(1L, (sofar, next) -> sofar * (next + 1));
        System.out.println("number of divisors: " + nrOfDivisors);
    }
}
