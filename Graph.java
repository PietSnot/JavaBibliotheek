/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javabibliotheek;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Sylvia
 */
public class Graph {
    
    //==============================================
    // members
    //==============================================
    private final boolean isUndirected;
    private final int size;
    private final Map<Integer, Set<Integer>> adjacencyMap;
    
    //==============================================
    // constructors
    //==============================================
    public Graph(int size, boolean isUndirected) {
        this.size = size;
        this.isUndirected = isUndirected;
        adjacencyMap = new HashMap<>();
    }
    
    //==============================================
    // nested classes
    //==============================================
    private static class EdgeGeneral<E> {
        private final E e1, e2;
        EdgeGeneral(E e1, E e2) {
            this.e1 = e1;
            this.e2 = e2;
        }
        public E getFirstVertex() {
            return e1;
        }
        public E getSecondVertex() {
            return e2;
        }
        @Override
        public String toString() {
            return String.format("(%s, %s)", e1, e2);
        }
    }
    
    //--------------------------------------
    public class Edge extends EdgeGeneral<Integer> {
        Edge(int v1, int v2) {
            super(v1, v2);
        }
    }
    
    //==============================================
    // public methods
    //==============================================
    public void addEdge(int v1, int v2) {
        if (!isVertex(v1, v2))
            throw new IllegalArgumentException("parameter(s) out of bound");
        adjacencyMap.computeIfAbsent(v1, HashSet::new).add(v2);
        if (isUndirected) adjacencyMap.computeIfAbsent(v2, HashSet::new).add(v1);
    }
    
    //--------------------------------------
    public void addEdges(int... v) {
        if (v.length % 2 != 0 || !isVertex(v)) 
            throw new IllegalArgumentException("nr of parameters must be even!");
        for (int i = 0; i < v.length; i += 2) addEdge(v[i], v[i + 1]);
    }
    
    //--------------------------------------
    public void removeEdge(int v1, int v2) {
        if (!isVertex(v1, v2))
            throw new IllegalArgumentException("parameter(s) out of bound!");
        Set<Integer> keys = adjacencyMap.keySet();
        if (keys.contains(v1)) adjacencyMap.get(v1).remove(v2);
        if (isUndirected) adjacencyMap.get(v2).remove(v1);
        if (adjacencyMap.get(v1).isEmpty()) adjacencyMap.remove(v1);
        if (adjacencyMap.get(v2).isEmpty()) adjacencyMap.remove(v2);
    }
    
    //--------------------------------------
    public int[][] getAdjacencyMatrix() {
        int[][] result = new int[size][size];
        adjacencyMap.keySet().forEach(i -> adjacencyMap.get(i).forEach(j -> result[i][j] = 1));
        return result;
    }
    
    //--------------------------------------
    public List<Edge> getListOfEdges() {
        return adjacencyMap.keySet().stream()
                .flatMap(i -> adjacencyMap.get(i).stream().map(j -> new Edge(i, j)))
                .collect(Collectors.toList());
    }
    
    //--------------------------------------
    public boolean isConnected() {
        if (!isUndirected) 
            throw new RuntimeException("only for undirected graphs!");
        LinkedList<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(0);
        visited.add(0);
        while (!queue.isEmpty()) {
            System.out.format("queue size = %d%n", queue.size());
            Set<Integer> neighbors = getNeighbors(queue.removeFirst());
            neighbors.removeAll(visited);
            visited.addAll(neighbors);
            queue.addAll(neighbors);
        }
        return visited.size() == size;
    }
    
    //--------------------------------------
    public static Graph getRandomGraph(int size, boolean undirected, double chance) {
        Random r = new Random();
        Graph g = new Graph(size, undirected);
//        IntStream.range(0, size - 1).forEach(i -> 
//                        IntStream.range(i + 1, size)
//                            .filter(s -> r.nextDouble() < chance)
//                            .forEach(j -> g.addEdge(i, j))
//        );
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (i % 1000 == 0 && j % 1000 == 0) System.out.println("i, j = " + i + "," + j);
                if (r.nextDouble() < chance) g.addEdge(i, j);
            }
        }
        return g;
    }
    
    //--------------------------------------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("size: ").append(size).append("\n");
        sb.append("undirectd: ").append(isUndirected);
        sb.append("list of Edges: \n").append(this.getListOfEdges());
        return sb.toString();
    }
    
    //==============================================
    // private methods
    //==============================================
    private boolean isVertex(int... v) {
        return Arrays.stream(v).allMatch(i -> i >= 0 && i < size);
    }
    
    //--------------------------------------
    private Set<Integer> getNeighbors(int v) {
        return adjacencyMap.getOrDefault(v, new HashSet<>());
    }
}

//=============================================================================
class GraphTester {
    public static void main(String... args) {
        Graph g = new Graph(6, true);
        g.addEdges(0, 1, 2, 3, 2, 4, 4, 5);
        System.out.println(g);
        System.out.println("g is connected: " + g.isConnected());
        System.out.println("*****************************");
        Graph f = Graph.getRandomGraph(12_000, true, .02);
//        System.out.println(f);
        System.out.println("f is connected: " + f.isConnected());
    }
}