package project1_1;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ColEdge {
    int u;
    int v;
}

public class ReadGraph {

    public static ColEdge[] e; // Static field to hold edges
    public static int n;       // Static field to hold number of vertices

    public final static boolean DEBUG = true;
    public final static String COMMENT = "//";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error! No filename specified.");
            System.exit(0);
        }

        String inputfile = args[0];
        boolean seen[] = null;
        n = -1;  // Number of vertices
        int m = -1; // Number of edges

        try {
            FileReader fr = new FileReader(inputfile);
            BufferedReader br = new BufferedReader(fr);

            String record = new String();

            while ((record = br.readLine()) != null) {
                if (record.startsWith(COMMENT)) continue;
                break;
            }

            if (record.startsWith("VERTICES = ")) {
                n = Integer.parseInt(record.substring(11));
                if (DEBUG) System.out.println(COMMENT + " Number of vertices = " + n);
            }

            seen = new boolean[n + 1];
            record = br.readLine();

            if (record.startsWith("EDGES = ")) {
                m = Integer.parseInt(record.substring(8));
                if (DEBUG) System.out.println(COMMENT + " Expected number of edges = " + m);
            }

            e = new ColEdge[m];  // Initialize edge array
            for (int d = 0; d < m; d++) {
                if (DEBUG) System.out.println(COMMENT + " Reading edge " + (d + 1));
                record = br.readLine();
                String data[] = record.split(" ");
                if (data.length != 2) {
                    System.out.println("Error! Malformed edge line: " + record);
                    System.exit(0);
                }
                e[d] = new ColEdge();
                e[d].u = Integer.parseInt(data[0]);
                e[d].v = Integer.parseInt(data[1]);

                seen[e[d].u] = true;
                seen[e[d].v] = true;

                if (DEBUG) System.out.println(COMMENT + " Edge: " + e[d].u + " " + e[d].v);
            }

            String surplus = br.readLine();
            if (surplus != null) {
                if (surplus.length() >= 2)
                    if (DEBUG) System.out.println(COMMENT + " Warning: extra data after last edge: '" + surplus + "'");
            }

        } catch (IOException ex) {
            System.out.println("Error! Problem reading file " + inputfile);
            System.exit(0);
        }

        for (int x = 1; x <= n; x++) {
            if (!seen[x]) {
                if (DEBUG) System.out.println(COMMENT + " Warning: vertex " + x + " didn't appear in any edge");
            }
        }

        GraphColoring.runUpperBound();
        GraphColoring.runLowerBound();
    }

    static class GraphColoring {
            
            // Convert edge list to adjacency list
            public static List<List<Integer>> createAdjacencyList(ColEdge[] edges, int numVertices) {
                List<List<Integer>> adjacencyList = new ArrayList<>();
    
                // Initialize adjacency list with empty lists for each vertex
                for (int i = 0; i < numVertices; i++) {
                    adjacencyList.add(new ArrayList<>());
                }
    
                // Populate adjacency list with edges
                for (ColEdge edge : edges) {
                    int u = edge.u - 1; // Adjust for 0-based index
                    int v = edge.v - 1;
                    adjacencyList.get(u).add(v);
                    adjacencyList.get(v).add(u);
                }
    
                return adjacencyList;
            }
    
            // Greedy coloring algorithm for upper bound using adjacency list
            public static int computeUpperBound(List<List<Integer>> graph) {
                int n = graph.size();
                int[] colors = new int[n];
                Arrays.fill(colors, -1);  // Initialize all vertices with no color
    
                for (int v = 0; v < n; v++) {
                    Set<Integer> usedColors = new HashSet<>();
    
                    // Check neighbors and mark their colors as used
                    for (int neighbor : graph.get(v)) {
                        if (colors[neighbor] != -1) {
                            usedColors.add(colors[neighbor]);
                        }
                    }
    
                    // Find the smallest available color
                    int color;
                    for (color = 1; ; color++) {
                        if (!usedColors.contains(color)) {
                            colors[v] = color;
                            break;
                        }
                    }
                }
    
                // Find the maximum color used (chromatic number)
                int chromaticNumber = 0;
                for (int color : colors) {
                    chromaticNumber = Math.max(chromaticNumber, color);
                }
    
                return chromaticNumber;  // Return the maximum color used as the upper bound
            }
    
            public static void runUpperBound() {
                // Create the adjacency list using the edges from ReadGraph
                List<List<Integer>> adjacencyList = createAdjacencyList(ReadGraph.e, ReadGraph.n);
    
                // Compute the upper bound using the greedy coloring algorithm
                int upperBound = computeUpperBound(adjacencyList);
                System.out.println("Upper Bound (Greedy Coloring): " + upperBound);
            }

            // Method to compute the lower bound (maximum clique size)
            public static int computeLowerBound(List<List<Integer>> adjacencyList) {
                int n = adjacencyList.size();
                int maxCliqueSize = 1;

                // Generate all subsets of vertices to find the largest clique
                for (int subset = 0; subset < (1 << n); subset++) {
                    List<Integer> currentSubset = new ArrayList<>();

                    // Include vertices in the subset
                    for (int i = 0; i < n; i++) {
                        if ((subset & (1 << i)) != 0) {
                            currentSubset.add(i);
                        }
                    }

                    // Check if the current subset forms a clique
                    if (isClique(adjacencyList, currentSubset)) {
                        maxCliqueSize = Math.max(maxCliqueSize, currentSubset.size());
                    }
                }

                return maxCliqueSize;
            }

            // Helper method to check if a subset of vertices forms a clique
            private static boolean isClique(List<List<Integer>> adjacencyList, List<Integer> subset) {
                for (int i = 0; i < subset.size(); i++) {
                    for (int j = i + 1; j < subset.size(); j++) {
                        int u = subset.get(i);
                        int v = subset.get(j);

                        // Check if there is an edge between u and v
                        if (!adjacencyList.get(u).contains(v)) {
                            return false;
                        }
                    }
                }
                return true;
            }

            public static void runLowerBound() {
                // Create the adjacency list using the edges from ReadGraph
                List<List<Integer>> adjacencyList = createAdjacencyList(ReadGraph.e, ReadGraph.n);
    
                // Compute the upper bound using the greedy coloring algorithm
                int lowerBound = computeLowerBound(adjacencyList);
                System.out.println("Lower Bound (Maximum Clique Size): " + lowerBound);
            }
    
            // Backtracking to check if the graph can be colored with `numColors`
            public static boolean canColorGraph(List<List<Integer>> graph, int[] colors, int vertex, int numColors) {
                // If all vertices are colored, return true
                if (vertex == graph.size()) {
                    return true;
                }
    
                // Try assigning all colors (1 through numColors)
                for (int color = 1; color <= numColors; color++) {
                    if (isSafeToColor(graph, colors, vertex, color)) {
                        colors[vertex] = color;
    
                        // Recur to assign colors to the rest of the vertices
                        if (canColorGraph(graph, colors, vertex + 1, numColors)) {
                            return true;
                        }
    
                        // Backtrack (undo the coloring)
                        colors[vertex] = -1;
                    }
                }
    
                return false;
            }
    
            // Helper method to check if it's safe to assign `color` to `vertex`
            public static boolean isSafeToColor(List<List<Integer>> graph, int[] colors, int vertex, int color) {
                // Check the adjacent vertices
                for (int neighbor : graph.get(vertex)) {
                    if (colors[neighbor] == color) {
                        return false; // Conflict found
                    }
                }
                return true;
            }
    
            // Function to compute the exact chromatic number using backtracking
            public static int computeExactChromaticNumber(List<List<Integer>> graph) {
                int n = graph.size();
                int[] colors = new int[n];
                Arrays.fill(colors, -1); // Initially, all vertices are uncolored (-1)
    
                // Try coloring the graph with an increasing number of colors
                for (int numColors = 1; numColors <= n; numColors++) {
                    if (canColorGraph(graph, colors, 0, numColors)) {
                        return numColors; // Found the minimum number of colors required
                    }
                }
    
                return n; // Worst case scenario: each vertex gets its own color
            }
    
            public static void runExactChromaticNumber() {
                // Create the adjacency list using the edges from ReadGraph
                List<List<Integer>> adjacencyList = createAdjacencyList(ReadGraph.e, ReadGraph.n);
    
                // Compute the upper bound using the greedy coloring algorithm
                int exactChromaticNumber = computeExactChromaticNumber(adjacencyList);
                System.out.println("Exact Chromatic Number (Backtracking): " + exactChromaticNumber);
            }
        }
}