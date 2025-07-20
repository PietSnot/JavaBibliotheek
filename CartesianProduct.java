/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javabibliotheek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 *
 * @author Piet
 */
public class CartesianProduct {
    
    public static void main(String... args) {
        List<String> a = Arrays.asList("black", "blue");
        List<String> b = Arrays.asList("apple", "orange", "grapes");
        List<String> c = Arrays.asList("dog", "lion", "snake");
        List<Integer> d = Arrays.asList(1, 2, 3);
        System.out.println(cartesianProductString(a, b, c, d));
    }

    private static <T, R> Stream<String> oneToN(T t, List<R> list) {
        return list.stream().map(r -> t.toString() + "-" + r);
    }

    private static <T, R> List<String> nToN(List<T> listT, List<R> listR) {
        return listT.stream().flatMap(t -> oneToN(t, listR)).collect(toList());
    }

    public static List<String> cartesianProductString(List... lists) {
        return Arrays.stream(lists).reduce(Arrays.asList(""), CartesianProduct::nToN);
    }
    
    private static <T> Stream<List<T>> mapper(T t, List<List<T>> lists) {
        return lists.stream()
            .map(list -> {List<T> temp = new ArrayList<>(list); temp.add(t); return temp;})
        ;
    }
    
    private static <T> List<List<T>> flatMapper(List<List<T>> lists, List<T> list) {
        return list.stream().flatMap(t -> mapper(t, lists)).collect(Collectors.toList());
    }
    
    public static <T> List<List<T>> cartesianProductWithStreams(List<List<T>> lists) {
        List<List<T>> start = new ArrayList<>();
        start.add(new ArrayList<>());
        return lists.stream().reduce(start, (a, b) -> flatMapper(a, b), (a, b) -> a);
    }
    
    private static <T> List<List<T>> recursiveHelp(int whichList, List<List<T>> sofar, List<List<T>> original) {
        if (whichList >= original.size()) return sofar;
        List<List<T>> newList = new ArrayList<>();
        for (List<T> list: sofar) {
            for (T t: original.get(whichList)) {
                List<T> temp = new ArrayList<>(list);
                temp.add(t);
                newList.add(temp);
            }
        }
        return recursiveHelp(whichList + 1, newList, original);
    }
    
    public static <T> List<List<T>> cartesianProductRecursiveWay(List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        return recursiveHelp(0, result, lists);
    }
    
    public static <T> List<List<T>> cartesianProducrIterativeWay(List<List<T>> lists) {
        List<List<T>> currentLists = new ArrayList<>();
        currentLists.add(new ArrayList<>());
        for (List<T> originalList: lists) {
            List<List<T>> copy = new ArrayList<>();
            for (List<T> current: currentLists) {
                for (T t: originalList) {
                    List<T> temp = new ArrayList<>(current);
                    temp.add(t);
                    copy.add(temp);
                }
            }
            currentLists = new ArrayList<>(copy);
        }
        return currentLists;
    }
}