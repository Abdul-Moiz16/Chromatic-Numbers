package project1_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class Graph {
    private final int numVertices;
    private final int numEdges;
    private final List<List<Integer>> adjacencyList;
    public final List<Integer> colors;

    // Constructor to create a Graph object
    public Graph(int numVertices, int numEdges, List<List<Integer>> adjacencyList) {
        this.numVertices = numVertices;
        this.numEdges = numEdges;
        this.adjacencyList = adjacencyList;
        this.colors = new ArrayList<>(numVertices);

        for(int i = 0; i < numVertices; i++){
            colors.add(-1);
        }
    }

    // Constructor for randomly generated graph.
    public Graph(int numVertices, int numEdges) {
        this.numVertices = numVertices;
        this.numEdges = numEdges;
        this.adjacencyList = new ArrayList<>(numVertices);
        this.colors = new ArrayList<>(numVertices);
        
        // Initialize adjacency list
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
            colors.add(-1);
        }
        
        // Generate random edges
        generateRandomEdges();
    }

    public int getColor(int vertex){
        return colors.get(vertex);
    }

    private void generateRandomEdges() {
        Random random = new Random();
        int edgesAdded = 0;

        while (edgesAdded < numEdges) {
            int from = random.nextInt(numVertices);
            int to = random.nextInt(numVertices);

            // Avoid self-loops and duplicate edges
            if (from != to && !adjacencyList.get(from).contains(to)) {
                adjacencyList.get(from).add(to);
                adjacencyList.get(to).add(from); // Undirected graph
                edgesAdded++;
            }
        }
    }

    public void addEdge(int u, int v) {
        if (!adjacencyList.get(u).contains(v)) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u); // For undirected graph
        }
    }

    public List<List<Integer>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getVertices() {
        return numVertices;
    }

    public int getEdges() {
        return numEdges;
    }

    public void setColor(int vertexIndex, int colorHash) {
        colors.set(vertexIndex, colorHash);
    }

    public void removeColor(int vertexIndex) {
        colors.set(vertexIndex, -1);
    }

    public int countUsedColors() {
        
        LinkedHashSet<Integer> s = new LinkedHashSet<Integer>();
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i) != -1) {
                System.out.println("Current Color: " + colors.get(i));
                s.add(colors.get(i));
                System.out.println("Current Set: " + s);
            }
        }
        System.out.println(
                "Number of colors used: " + s.size() + " " + "Colors used: " + s.toString() + "\n"
        );
        return s.size();
    }

    // Static method to create a Graph object from a text file
    public static Graph fromFile(String filePath) throws IOException {
        int numVertices = 0;
        int numEdges = 0;
        List<List<Integer>> adjacencyList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("VERTICES = ")) {
                    numVertices = Integer.parseInt(line.substring(11).trim());
                    // Initialize adjacency list
                    for (int i = 0; i < numVertices; i++) {
                        adjacencyList.add(new ArrayList<>());
                    }
                } else if (line.startsWith("EDGES = ")) {
                    numEdges = Integer.parseInt(line.substring(8).trim());
                } else {
                    // Read edges
                    String[] parts = line.trim().split(" ");
                    if (parts.length == 2) {
                        int u = Integer.parseInt(parts[0]) - 1; // Convert to 0-based index
                        int v = Integer.parseInt(parts[1]) - 1; // Convert to 0-based index

                        // Add the edge to the adjacency list
                        adjacencyList.get(u).add(v);
                        adjacencyList.get(v).add(u);
                    }
                }
            }
        }

        return new Graph(numVertices, numEdges, adjacencyList);
    }
}
