/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javabibliotheek;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Piet
 * @param <T>  generic type of the elements
 */
public class SummaryStats<T> {
    
    private final List<T> list = new ArrayList<>();
    private Function <T, Double> transform;
    
    public static void main(String... args) {
        var list = List.of(5, 1, 2, 3, 1, 4, 5);
        var stats = new SummaryStats(list, i -> i);
        System.out.println(stats.max());
        System.out.println(stats.min());
        System.out.println(stats.average());
    }
    
    public SummaryStats(Function<T, Double> f) {
        transform = f;
    }
    public SummaryStats(List<T> input, Function<T, Double> f) {
        list.addAll(input);
        transform = f;
    }
    
    public void setFunction(Function<T, Double> f) {
        transform = f;
    }
    
    public void add(T number) {
        list.add(number);
    }
    
    public double average() {
        if (list.isEmpty()) throw new RuntimeException("no data!");
        return list.stream().mapToDouble(transform::apply).average().getAsDouble();
    }
    
    public double variance() {
        if (list.size() < 2) throw new RuntimeException("too little data!!!");
        var avg = average();
        var EX2 = list.stream()
            .mapToDouble(i -> {var t = transform.apply(i); return t * t;})
            .average()
            .getAsDouble()
        ;
        return EX2 - avg * avg;
    }
    
    public double stdev() {
        return Math.sqrt(variance());
    }
    
    public int getNrOfObservations() {
        return list.size();
    }
    
    public List<T> getData() {
        return new ArrayList<>(list);
    }
    
    private Map.Entry<Double, List<T>> extreme(Comparator<Double> comp) { 
        if (list.isEmpty()) throw new RuntimeException("No data available!!!");
        var map = list.stream()
            .collect(
                groupingBy(transform::apply, 
                           () -> new TreeMap<>(comp),
                           mapping(s -> s, toList())
                          )
            )
        ;
        return map.firstEntry();                               
    }
    
    public Map.Entry<Double, List<T>> max() {
        if (list.isEmpty()) throw new RuntimeException("No data available!!!");
        return extreme(Comparator.reverseOrder());
    }
    
    public Map.Entry<Double, List<T>> min() {
        if (list.isEmpty()) throw new RuntimeException("No data available!!!");
        return extreme(Comparator.naturalOrder());
    }
}