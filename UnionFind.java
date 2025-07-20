/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.IntStream;

/**
 * This class has N integers 0, 1, ..., N-1, initially unrelated.
 * With the comand union(n, m) the integers n and m will be related.
 * You can ask if two integers are related with: boolean areRelated(n, m)
 * If n and m are related, and m and p are related, then so are n and p.
 * So, this class effectively groups the elements into groups of related
 * integers. You can ask for an overview of these groups with the command
 * getGroups().
 * This structure can be used to partition a Graph into related vertices. The
 * relation is if there is a path between two vertices.
 * The graph must be undirected.
 * 
 * @author Piet
 */


public class UnionFind {
    
    //==============================================
    // members
    //==============================================
    final private int size;
    final private Map<Integer, Object> map = new HashMap<>();
    final private int startValue;
    
    //==============================================
    // constructors
    //==============================================
    public UnionFind(int size) {
        this(size, 0);
    }
    
    public UnionFind(int size, int startValue) {
        this.size = size;
        this.startValue = startValue;
        IntStream.range(startValue, startValue + size).forEach(i -> map.put(i, new Object()));
    }
    
    //==============================================
    // public methods
    //==============================================
    public boolean areRelated(int a, int b) {
        if (!allAreInMap(a, b)) return false;
        return map.get(a) == map.get(b);
    }
    
    //--------------------------------------
    public boolean union(int a, int b) {
        if (!allAreInMap(a, b)) return false;
        if (areRelated(a, b)) return true;
        Object o = map.get(a);
        getGroup(b).forEach(s -> map.put(s, o));
        return true;
    }
    
    //--------------------------------------
    public Set<Integer> getElements() {
        return map.keySet();
    }
    
    //--------------------------------------
    public Collection<List<Integer>> getAllGroups() {
        Map<Object, List<Integer>> temp = map.entrySet().stream().collect(
            groupingBy(Entry::getValue, mapping(Entry::getKey, toList()))
        );
        return temp.values();
    }
    
    //--------------------------------------
    @Override
    public String toString() {
        return getAllGroups().toString();
    }
    
    //--------------------------------------
    public Set<Integer> getAllRelatedTo(int a) {
        return getGroup(a);
    }
    
    //==============================================
    // private methods
    //==============================================
    private Set<Integer> getGroup(int a) {
        final Object o = map.get(a);
        return map.keySet().stream()
                .filter(s -> map.get(s) == o)
                .collect(toSet());
    }
    
    //--------------------------------------
    private boolean allAreInMap(int... a) {
        return Arrays.stream(a).allMatch(i -> i >= startValue && i < startValue + size);
    }
    
    //==============================================
    // end of class
    //==============================================
}

