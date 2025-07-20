/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Sylvia
 */
public class ListUtilities {
    
    public static void main(String... args) {
        List<Integer> first = Arrays.asList(1, 3, 5, 7, 4);
        List<Integer> second = Arrays.asList(2, 4, 6, 8, 10, 12);
        System.out.println(mergeTwoListsIntoOne(first, second));
    }
    
    public static <T extends Comparable<T>> List<T> mergeTwoListsIntoOne(List<T> first, List<T> second) {
        int firstIndex = 0, secondIndex = 0;
        List<T> result = new ArrayList<>();
        while (firstIndex < first.size() && secondIndex < second.size()) {
            if (first.get(firstIndex).compareTo(second.get(secondIndex)) <= 0) {
                result.add(first.get(firstIndex));
                firstIndex++;
            }
            else {
                result.add(second.get(secondIndex));
                secondIndex++;
            }
        }
        if (firstIndex < first.size()) {
            result.addAll(first.subList(firstIndex, first.size()));
        }  
        else {
            result.addAll(second.subList(secondIndex, second.size()));
        }
        return result;
    }
}
