/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javabibliotheek;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;

/**
 *
 * @author Piet
 */
public class Permutaties {
    
    /**
     * use: Permutaties.getPermutations(List<T> list)
     * 
     * Piet Souris, Den Haag, 2018-05-30
     * @param args
     */
    
    public static void main(String... args) {
//        List<String> list = Arrays.asList("aap", "noot", "mies");
//        List<List<String>> perms = getPermutations(list);
//        perms.forEach(System.out::println);
//        
//        var money = new ArrayList<>(List.of(1, 2, 5, 10));
//        int sum = 8;
//        var result = differentWaysOfChange(sum, money);
//        System.out.println("*****************");
//        Comparator<List<?>> c = Comparator.comparingInt(List::size);
//        result.sort(c);
//        result.forEach(System.out::println);
//        var list = new ArrayList<>(List.of(1,2));
//        var result = permutaties2(list);
//        result.forEach(System.out::println);
        var lijst = IntStream.range(0, 11).boxed().toList();
        var start = System.currentTimeMillis();
        getPermutations(lijst);
        var eind = System.currentTimeMillis();
        System.out.format("duurde: %f seconden%n", (eind - start) / 1000.);
    }
    
    //****************************************************************
    // public methods
    //****************************************************************
    public static <T> List<List<T>> getPermutations(List<T> list) {
        return permutations(list);
    }
    
    //****************************************************************
    // private methods
    //****************************************************************
    private static Set<Set<Integer>> permutaties(int N) {
        Set<Integer> original = IntStream.range(0, N).boxed().collect(toCollection(HashSet::new));
        Set<Set<Integer>> result = new HashSet<>();
        permutaties(result, original, new HashSet<>());
        return result;
    }
    
    private static void permutaties(Set<Set<Integer>> total, Set<Integer> original, Set<Integer> currentSet) {
        if (currentSet.size() == original.size()) total.add(new HashSet<>(currentSet));
        else original.stream()
            .filter(i -> !currentSet.contains(i))
            .forEach(i -> {
                currentSet.add(i); 
                permutaties(total, original, currentSet);
                currentSet.remove(i);
        });
    }
    
    public static <T> List<List<T>> permutations(List<T> list) {
        return permutaties(list.size()).stream().map(set -> permutationsHelper(list, set)).collect(toList());
    }
    
    private static <T> List<T> permutationsHelper(List<T> list, Set<Integer> set) {
        return set.stream().map(list::get).toList();
    }
    
    public static List<List<Integer>> differentWaysOfChange(int sum, List<Integer> money) {
        var result = new ArrayList<List<Integer>>();
        var currentList = new ArrayList<Integer>();
        differentWaysOfChange(sum, money, result, currentList);
        return result;
    }
    
    private static void differentWaysOfChange(int amount, List<Integer> money, List<List<Integer>> result, List<Integer> sofar) {
        if (amount < 0 || money.isEmpty()) return;
        if (amount == 0) {
            result.add(new ArrayList<>(sofar));
            return;
        }
        int coin = money.get(0);
        sofar.add(coin);
        differentWaysOfChange(amount - coin, money, result, sofar);
        sofar.remove(sofar.size() - 1);
        differentWaysOfChange(amount, money.subList(1, money.size()), result, sofar);
    }
    
    public static <T> List<List<T>> permutaties2(List<T> list) {
        var result = new ArrayList<List<T>>();
        var current = new ArrayList<List<T>>();
        result.add(new ArrayList<>());
        permutaties2Helper(result, current, list);
        return result;
    }
    
    private static <T> void permutaties2Helper(List<List<T>> result, List<List<T>> sofar, List<T> original) {
        if (original.size() == 1) sofar.forEach(list -> list.add(original.get(0)));
        else {
            for (int i = 0; i < original.size(); i++) {
                var a = original.get(i);
                permutaties2Helper(result, sofar, remove(original, i));
                sofar.forEach(list -> list.add(a));
                result.addAll(sofar);
            }
        }
    }
    
    private static <T> List<T> remove(List<T> list, int index) {
        return IntStream.range(0, list.size())
            .filter(i -> i != index)
            .mapToObj(list::get)
            .collect(toCollection(ArrayList::new))
        ;       
    }
}