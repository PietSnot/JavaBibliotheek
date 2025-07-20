/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javabibliotheek;

import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Piet
 */
public class GrootsteGemeneDeler {
    
    public static void main(String... args) {
        System.out.println(ggd(10_000, 15_000));
    }
    
    public static int ggd(int x, int y) {
        var start = Duo.of(Math.min(x, y), Math.max(x, y));
        var result = Stream.iterate(start, p -> Duo.of(p.y % p.x, p.x)).filter(p -> p.y % p.x == 0).findFirst();
        return result.get().x;
    }
}

class Duo {
    final int x, y;
    private Duo(int a, int b) {
        x = a;
        y = b;
    }
    static Duo of(int a, int b) {
        return new Duo(a, b);
    }  
}
