/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javabibliotheek;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author Piet
 */
public class Permutaties2 {
    
    public static void main(String... args) {
        var list = List.of(1, 2, 3);
        var result = permutaties(list);
        result.forEach(out::println);
        //********************8
        var lijst = IntStream.range(0, 10).boxed().toList();
        var start = System.currentTimeMillis();
        permutaties(lijst);
        var eind = System.currentTimeMillis();
        System.out.format("duurde: %f seconden%n", (eind - start) / 1000.);
    }
    
    public static <T> List<List<T>> permutaties(List<T> list) {
        var result = new ArrayList<List<T>>();
        if (list.size() == 1) result.add(list);
        else {
            IntStream.range(0, list.size()).forEach(i -> {
                var temp = permutaties(remove(list, i));
                temp.stream().forEach(d -> d.add(list.get(i)));
                result.addAll(temp);
            });
        }
        return result;
    }

    public static <T> List<T> remove(List<T> list, int index) {
        var result = new ArrayList<>(list.subList(0, index));
        result.addAll(list.subList(index + 1, list.size()));
        return result;
    }
}
