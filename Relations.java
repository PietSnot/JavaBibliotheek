/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;

/**
 * This class has N elements of type T, initially unrelated. With 
 * the comand union(n, m) the integers n and m will be related.
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
 * The Hague, Holland, 10th august 2018
 * @param <T> the generic class type
 */

public class Relations<T> {
    
    //==============================================
    // members
    //==============================================
    final int size;
    final private Map<T, Object> map = new HashMap<>();
    
    //==============================================
    // constructors
    //==============================================
    public <S extends Iterable<? extends T>> Relations(S collection) {
        collection.forEach(c -> map.put(c, new Object()));
        size = map.size();
    }
    
    //--------------------------------------
    public Relations(T[] array) {
        for (T t: array) map.put(t, new Object());
        size = map.size();
    }
    
    //--------------------------------------
    public Relations(Map<T, List<T>> adjacencyMap) {
        adjacencyMap.entrySet().stream()
            .flatMap(e -> Stream.concat(Stream.of(e.getKey()), e.getValue().stream()))
            .collect(toSet())
            .forEach(t -> map.put(t, new Object())
        );
        size = map.size();
        adjacencyMap.forEach(
            (k, v) -> v.stream().forEach(t -> relate(k, t))
        );
    }
    
    //==============================================
    // public methods
    //==============================================
    public boolean areRelated(T a, T b) {
        if (!allAreInMap(a, b)) return false;
        return map.get(a) == map.get(b);
    }
    
    //--------------------------------------
    public boolean relate(T a, T b) {
        if (!allAreInMap(a, b)) return false;
        var o = map.get(a);
        getGroup(b).forEach(s -> map.put(s, o));
        return true;
    }
    
    //--------------------------------------
    public Set<T> getElements() {
        return map.keySet();
    }
    
    //--------------------------------------
    public long getNrOfRelatedGroups() {
        return map.values().stream().distinct().count();
    }
    
    //--------------------------------------
    public boolean isConnected() {
        return getNrOfRelatedGroups() == 1;
    }
    
    //--------------------------------------
    @Override
    public String toString() {
        Map<Object, List<T>> temp = map.entrySet().stream().collect(
            groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toList()))
        );
        return temp.values().toString();
    }
    
    //==============================================
    // private methods
    //==============================================
    private Set<T> getGroup(T a) {
        final Object o = map.get(a);
        return map.keySet().stream().filter(s -> map.get(s) == o).collect(Collectors.toSet());
    }
    
    //--------------------------------------
    private boolean allAreInMap(T... a) {
        return map.keySet().containsAll(Arrays.asList(a));
    }
    
    //--------------------------------------
    
    
    //==============================================
    // end of class
    //==============================================
}

//=============================================================================
class RelationsTester {
    public static void main(String... args) {
        Graph g = new Graph(6, true);
        g.addEdges(0, 1, 2, 3, 2, 4, 2, 5);
        // connected?
        Relations<Integer> r = new Relations<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        for (Graph.Edge edge: g.getListOfEdges()) {
            r.relate(edge.getFirstVertex(), edge.getSecondVertex());
            System.out.println(r);
        }
        System.out.println("is graph connected: " + (r.getNrOfRelatedGroups() == 1));
    }
}
