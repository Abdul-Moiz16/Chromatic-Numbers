package project1_1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class GraphGenerator {

    public static List<List<Integer>> generateGraph(int numVertices, int numEdges) {
        if (numVertices < 1) {
            throw new IllegalArgumentException("Number of vertices must be at least 1.");
        }
        if (numEdges < numVertices - 1) {
            throw new IllegalArgumentException("Not enough edges to connect all vertices.");
        }
        if (numEdges > numVertices * (numVertices - 1) / 2) {
            throw new IllegalArgumentException("Too many edges for a simple undirected graph.");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        Set<String> existingEdges = new HashSet<>();
        Random rand = new Random();

        // Step 1: Create a base of connected vertices (spanning tree)
        for (int i = 1; i < numVertices; i++) {
            int u = i; // Current vertex
            int v = rand.nextInt(i); // Connect to one of the previous vertices
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
            existingEdges.add(u + "-" + v);
            existingEdges.add(v + "-" + u);
        }

        // Step 2: Add remaining edges randomly until reaching the desired edge count
        while (existingEdges.size() / 2 < numEdges) { // Each edge is counted twice
            int u = rand.nextInt(numVertices);
            int v = rand.nextInt(numVertices);

            // Ensure no self-loops and no duplicate edges
            if (u != v && !existingEdges.contains(u + "-" + v) && !existingEdges.contains(v + "-" + u)) {
                adjacencyList.get(u).add(v);
                adjacencyList.get(v).add(u);
                existingEdges.add(u + "-" + v);
                existingEdges.add(v + "-" + u);
            }
        }

        return adjacencyList;
    }

    public static void main(String[] args) {
        int vertices = 0;
        int edges = 0;

        Scanner input = new Scanner(System.in);

        System.out.println("Give the number of vertices:");
        vertices = input.nextInt();

        System.out.println("Give the number of edges:");
        edges = input.nextInt();

        List<List<Integer>> adjacencyList = generateGraph(vertices, edges);
        System.out.println('\n' + "Generated graph with " + vertices + " vertices and " + edges + " edges:");

        // Print the adjacency list
        for (int i = 0; i < adjacencyList.size(); i++) {
            System.out.println("Vertex " + (i + 1) + ": " + adjacencyList.get(i));
        }
    }

    // public static void saveGraphToFile (List<List<Integer>> graph) {
        
    // }

}
