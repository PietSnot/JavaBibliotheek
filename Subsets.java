/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javabibliotheek;

import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.max;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;

/**
 *
 * @author Piet
 */

public class Subsets {
    
    public static void main(String... args) {
        var list = Arrays.asList(5, 15, 21, 8, 7);
        var piet = getSubsets(list);
        piet.stream().forEach(System.out::println);
        var map = piet.stream().collect(groupingBy(List::size));
        map.entrySet().forEach(System.out::println);
        var map2 = piet.stream().collect(groupingBy(List::size, counting()));
        map2.entrySet().forEach(System.out::println);
    }
    
    public static <T> List<List<T>> getSubsets(List<T> list) {
        Map<Integer, List<List<Integer>>> map = new HashMap<>();
        getSubsets(map, list.size(), list.size());
        return mapIndicesToT(list, map);
    }
    
    private static Stream<List<Integer>> getNewSubsets(List<Integer> list, int size) {
        return range(list.isEmpty() ? 0 : max(list) + 1, size)
            .mapToObj(i -> {var temp = new ArrayList<>(list); temp.add(i); return temp;})
        ;
    }
    
    private static void getSubsets(Map<Integer, List<List<Integer>>> map, int level, int size) {
        if (level == 0) map.computeIfAbsent(0, ArrayList::new).add(new ArrayList<>());
        else {
            getSubsets(map, level - 1, size);
            var temp = map.get(level - 1).stream().flatMap(list -> getNewSubsets(list, size)); 
            map.put(level, temp.collect(toList()));
        }
    }
    
    private static <T> List<T> mapIndexToReal(List<T> list, List<Integer> index) {
        var result = index.stream().map(list::get).collect(toList());
        return result;
    }
    
    private static <T> List<List<T>> mapIndicesToT(List<T> list, Map<Integer, List<List<Integer>>> map) {
        return map.values().stream()
            .flatMap(ll -> ll.stream().map(l -> mapIndexToReal(list, l)))
            .collect(toList())
        ;        
    }
    
}