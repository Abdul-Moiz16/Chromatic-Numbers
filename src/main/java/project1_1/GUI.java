package project1_1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GUI extends Application {
    private static final int VERTEX_RADIUS = 20;
    private static final int PADDING = 100;
    private static final int GRAPH_SIZE = 500;
    private List<Graph> loadedGraphs = new ArrayList<>();
    private Pane graphPane = new Pane();
    private Label timerLabel;
    private Timeline timeline;
    private int secondsElapsed = 0;
    private int score = 1000;
    private ColorPicker colorPicker;
    public Graph currentGraph;
    private boolean gameInProgress = true;
    private String currentGameMode = "";
    private Circle[] vertexCircles;
    public ArrayList<Label> indexLabels = new ArrayList<>();
    private ComboBox<String> fileDropdown = new ComboBox<>();
    public String temp;
    private Clip audioClip;
    private boolean isMuted = false;
    public Text t1 = new Text();
    public Text t2 = new Text();
    public int randomOrderIndex;



    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        VBox inputPane = new VBox(10);
        inputPane.setPadding(new Insets(10));
        
        inputPane.setAlignment(Pos.CENTER);
        graphPane.setPrefSize(400, 600);

        Label verticesLabel = new Label("Number of vertices:");
        TextField verticesField = new TextField();
        Label edgesLabel = new Label("Number of edges:");
        TextField edgesField = new TextField();

        verticesField.setMaxWidth(150);
        verticesField.setMaxHeight(20);
        edgesField.setMaxWidth(150);
        edgesField.setMaxHeight(20);

        Button submitButton = new Button("Generate Graph");
        Button loadFileButton = new Button("Load Graph File");
        fileDropdown.setPromptText("Select a file");
        colorPicker = new ColorPicker();
        Button bitterEndGameButton = new Button("Start Game: To the Bitter End");
        Button randomOrderGameButton = new Button("Start Game: Random Order");
        Button iChangeMyMindButton = new Button("Start Game: I Change My Mind");
        Button undoButton = new Button("Undo");
        Button startButton = new Button("Start");
        Button hintButton = new Button("Hint");
        Button mute = new Button("Mute");
        Button quit = new Button("Quit");
        Button goBack = new Button("Return");
        Button chooseGameMode = new Button("Choose GameMode");
        Button colorGreedyButton = new Button("Color (Greedy)");
        Button colorDSaturButton = new Button("Color (DSatur)");
        t1.setText("Color the graph in this order: ");

        Image logo = new Image(getClass().getResource("/project1_1/logo.png").toExternalForm());

        ImageView imageView = new ImageView(logo);
        imageView.setFitWidth(500);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);

        startButton.setId("big-buttons");
        quit.setId("big-buttons");
        mute.setId("big-buttons");

        timerLabel = new Label("00:00");
        timerLabel.setStyle("-fx-font-size: 20px; -fx-padding: 10px; -fx-text-fill: black;");
        inputPane.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, null)));
        root.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, null)));

        inputPane.getChildren().addAll(imageView, startButton, mute, quit);

        // Music
        try {
             File audioFile = new File("BackOnTrack.wav");
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
             audioClip = AudioSystem.getClip();
             audioClip.open(audioStream);
             audioClip.loop(Clip.LOOP_CONTINUOUSLY);
             audioClip.start();

         } catch (Exception e) {
            System.err.println("Error loading audio: " + e.getMessage());
             e.printStackTrace();
        }

        mute.setOnMouseClicked(e -> {

            if (isMuted == false) {
                audioClip.stop(); // Resume the audio
                mute.setText("Unmute");
                isMuted = true;
            } else {
                audioClip.start(); // Pause the audio
                mute.setText("Mute");
                isMuted = false;
            }
        });

        quit.setOnMouseClicked(e -> {
           primaryStage.close();
        });

        startButton.setOnMouseClicked(e ->{
            inputPane.getChildren().removeAll(startButton, mute, quit);
            inputPane.getChildren().addAll(verticesLabel, verticesField, edgesLabel, edgesField, fileDropdown, submitButton, loadFileButton, chooseGameMode, quit);
            quit.setId(null);

            imageView.setFitWidth(400);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            inputPane.prefWidthProperty().unbind(); // Remove the binding
            inputPane.setPrefWidth(300);
            inputPane.setAlignment(Pos.CENTER_LEFT);
            inputPane.setPadding(new Insets(0, 0, 0, 40));
            root.setLeft(inputPane);
            root.setRight(graphPane);
        });

        chooseGameMode.setOnMouseClicked(e -> {
            if(currentGraph == null){
                showAlert("No Graph", "Please load or generate a graph first.", Alert.AlertType.ERROR);
                return;
            } else {
                inputPane.getChildren().removeAll(verticesLabel, verticesField, edgesLabel, edgesField, submitButton, loadFileButton, chooseGameMode, fileDropdown, quit);
                inputPane.getChildren().addAll(bitterEndGameButton, randomOrderGameButton, iChangeMyMindButton, colorGreedyButton, colorDSaturButton, goBack, quit);
            }
        });

        goBack.setOnMouseClicked(e -> {
            inputPane.getChildren().removeAll(bitterEndGameButton, randomOrderGameButton, iChangeMyMindButton, colorGreedyButton, colorDSaturButton, goBack, colorPicker, hintButton, quit, t1, t2);
            graphPane.getChildren().remove(undoButton);
            for(Label label : indexLabels){
                graphPane.getChildren().remove(label);
            }
            inputPane.getChildren().addAll(verticesLabel, verticesField, edgesLabel, edgesField, fileDropdown, submitButton, loadFileButton, chooseGameMode, quit);
            quit.setId(null);
            stopTimer();
            unColorGraph();
        });

        fileDropdown.setOnAction(e -> {
            String selectedFile = fileDropdown.getValue();
            if (selectedFile != null) {
                int index = fileDropdown.getItems().indexOf(selectedFile);
                if (index >= 0 && index < loadedGraphs.size()) {
                    Graph selectedGraph = loadedGraphs.get(index);
                    this.currentGraph = selectedGraph;
                    displayGraph(selectedGraph, graphPane);
                }
            }
        });

        bitterEndGameButton.setOnAction(e -> {
            inputPane.getChildren().removeAll(fileDropdown, randomOrderGameButton, bitterEndGameButton, iChangeMyMindButton, goBack, quit);
            inputPane.getChildren().addAll(colorPicker, hintButton, goBack, quit);
            startGameBitterEnd();
            graphPane.getChildren().remove(undoButton);
        });

        randomOrderGameButton.setOnAction(e -> {
            inputPane.getChildren().removeAll(fileDropdown, randomOrderGameButton, bitterEndGameButton, iChangeMyMindButton, goBack, quit);
            inputPane.getChildren().addAll(colorPicker, hintButton, goBack, quit);  
            graphPane.getChildren().remove(undoButton);
            startRandomOrderGame();
        });
        root.setLeft(inputPane);
        root.setCenter(graphPane);
        root.setBottom(timerLabel);

        // Submit button for random graph generation
        submitButton.setOnAction(e -> {
            int numVertices;
            int numEdges;
            
            numVertices = Integer.parseInt(verticesField.getText());
            numEdges = Integer.parseInt(edgesField.getText());

            if (numVertices < 1 || numEdges < 1) {
                showAlert("Invalid Input", "The number of Vertices or Edges is smaller than 1!", Alert.AlertType.ERROR);                 
            } else if (numEdges > numVertices * (numVertices - 1) / 2){
                showAlert("Invalid Input", "Too few Vertices for " + numEdges + " Edges", Alert.AlertType.ERROR);
            } else {
                Graph graph = new Graph(numVertices, numEdges);
                this.currentGraph = graph;
                displayGraph(graph, graphPane);
                loadedGraphs.add(graph);
            }

            // Generate a random graph and display it
            //startTimer();
        });

        iChangeMyMindButton.setOnAction(e ->{
            System.out.println("Trying to set colorPicker and Hint");
            inputPane.getChildren().removeAll(randomOrderGameButton, bitterEndGameButton, iChangeMyMindButton, goBack, quit);
            inputPane.getChildren().addAll(colorPicker, hintButton, goBack, quit);
            System.out.println("Got here after setting them");
            displayGraph(currentGraph, graphPane);
            System.out.println("Got here after displaying the graph");
            iChangeMyMindGame();
            System.out.println("Got here after starting the game");
        });

        colorGreedyButton.setOnMouseClicked(e -> {
            if(isCycle() == true){
                algorithmIsCycle();
                if(currentGraph.getVertices() % 2 == 0){
                    int exactChromaticNumberEstimate = 2;
                    int upperBound = 2;
                    int lowerBound = 2;
                    showAlert("Done! The graph is a Cycle graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
                } else {
                    int exactChromaticNumberEstimate = 3;
                    int upperBound = 3;
                    int lowerBound = 3;
                    showAlert("Done! The graph is a Cycle graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
                }
            } else if(completeGraph() == true){
                colorCompleteGraph();
                int exactChromaticNumberEstimate = currentGraph.getVertices();
                int upperBound = currentGraph.getVertices();
                int lowerBound = currentGraph.getVertices();
                showAlert("Done! The graph is a Complete Graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
            } else if(isTree() == true){ 
                algorithmIsTree();
                int exactChromaticNumberEstimate = 2;
                int upperBound = 2;
                int lowerBound = 2;
                showAlert("Done! The graph is a Tree Graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + " \n Upper bound: " + upperBound + " \n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);                                                         
            } else{
                colorGreedy();
                int upperBound = ReadGraph.GraphColoring.computeUpperBound(currentGraph.getAdjacencyList());
                int lowerBound = ReadGraph.GraphColoring.computeLowerBound(currentGraph.getAdjacencyList());
                int exactChromaticNumberEstimate = (ReadGraph.GraphColoring.computeUpperBound(currentGraph.getAdjacencyList()) + ReadGraph.GraphColoring.computeLowerBound(currentGraph.getAdjacencyList())) / 2;
                showAlert("Done!", "Exact Chromatic number estimate: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
            }
        });

        colorDSaturButton.setOnMouseClicked(e -> {
            if(isCycle() == true){
                algorithmIsCycle();
                if(currentGraph.getVertices() % 2 == 0){
                    int exactChromaticNumberEstimate = 2;
                    int upperBound = 2;
                    int lowerBound = 2;
                    showAlert("Done! The graph is a Cycle graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
                } else {
                    int exactChromaticNumberEstimate = 3;
                    int upperBound = 3;
                    int lowerBound = 3;
                    showAlert("Done! The graph is a Cycle graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
                }
            } else if(completeGraph() == true){
                colorCompleteGraph();
                int exactChromaticNumberEstimate = currentGraph.getVertices();
                int upperBound = currentGraph.getVertices();
                int lowerBound = currentGraph.getVertices();
                showAlert("Done! The graph is a Complete Graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
            } else if(isTree() == true){ 
                algorithmIsTree();
                int exactChromaticNumberEstimate = 2;
                int upperBound = 2;
                int lowerBound = 2;
                showAlert("Done! The graph is a Tree Graph!", "Exact Chromatic number: " + exactChromaticNumberEstimate + " \n Upper bound: " + upperBound + " \n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);  
            }else{    
                colorDSatur();
                int upperBound = ReadGraph.GraphColoring.computeUpperBound(currentGraph.getAdjacencyList());
                int lowerBound = ReadGraph.GraphColoring.computeLowerBound(currentGraph.getAdjacencyList());
                int exactChromaticNumberEstimate = (ReadGraph.GraphColoring.computeUpperBound(currentGraph.getAdjacencyList()) + ReadGraph.GraphColoring.computeLowerBound(currentGraph.getAdjacencyList())) / 2;
                showAlert("Done!", "Exact Chromatic number estimate: " + exactChromaticNumberEstimate + "\n Upper bound: " + upperBound + "\n Lower bound: " + lowerBound, Alert.AlertType.INFORMATION);
            }
        });

        hintButton.setOnAction(e ->{
            hint(currentGraph);
        });
        // Load file button
        loadFileButton.setOnAction(e -> loadGraphFromFile(primaryStage));

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        inputPane.prefWidthProperty().bind(scene.widthProperty().divide(1));
        primaryStage.setTitle("Graph Input and Display");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGameBitterEnd(){
        if(currentGraph == null){
            showAlert("No Graph", "Please load or generate a graph first.", Alert.AlertType.ERROR);
            return;
        }

        gameInProgress = true;
        currentGameMode = "To the Bitter End";
        startTimer();
        
        graphPane.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof Circle){
                Circle clickedVertex = (Circle) e.getTarget();
                int vertexIndex = Integer.parseInt(clickedVertex.getId());
                Color selectedColor = colorPicker.getValue();
                
                if(isValidColoring(vertexIndex, selectedColor.hashCode())){
                    clickedVertex.setFill(selectedColor);
                    currentGraph.setColor(vertexIndex, selectedColor.hashCode());
                    checkGameStatus();
                } else {
                    showAlert("Invalid Color", "This vertex cannot be colored the same as its neighbours.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private boolean isValidColoring(int vertexIndex, int color){
        List<Integer> neighbours = currentGraph.getAdjacencyList().get(vertexIndex);
        for(int neighbor : neighbours){
            if(currentGraph.getColor(neighbor) != -1 && currentGraph.getColor(neighbor) == color){
                return false;
            }
        }
        return true;
    }

    private List<Integer> randomOrder; // To store the randomized vertex order
    private int currentVertexIndex; // To track the current vertex to color

    private void startRandomOrderGame() {
        randomOrder = new ArrayList<>();
            for (int i = 0; i < currentGraph.getVertices(); i++) {
                randomOrder.add(i);
            }
            Collections.shuffle(randomOrder); // Shuffle the vertices
        

            for (int i = 0; i < vertexCircles.length; i++) {
                Circle circle = vertexCircles[i];
                int shuffledIndex = randomOrder.get(i);
                System.out.println("Random Order got: " + shuffledIndex);
                Label indexLabel  = new Label(String.valueOf(shuffledIndex));
                indexLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                indexLabels.add(indexLabel);
                graphPane.getChildren().add(indexLabel);

                indexLabel.layoutXProperty().bind(circle.centerXProperty().subtract(indexLabel.widthProperty().divide(2)));
                indexLabel.layoutYProperty().bind(circle.centerYProperty().subtract(indexLabel.heightProperty().divide(2)));

                // Update the circle ID to match the shuffled order
                circle.setId(String.valueOf(shuffledIndex));
            }

        
            currentVertexIndex = 0;
            gameInProgress = true;
            startTimer();
            

        
            graphPane.setOnMouseClicked(e -> {
                if (e.getTarget() instanceof Circle) {
                    Circle clickedVertex = (Circle) e.getTarget();
                    int vertexIndex = Integer.parseInt(clickedVertex.getId());
                
                    // Ensure the player is coloring the correct vertex
                    System.out.println("Current Vertex Index: " + currentVertexIndex);
                    System.out.println("Vertex Index selected: " + vertexIndex);
                    if (vertexIndex == currentVertexIndex) {
                        Color selectedColor = colorPicker.getValue();
                        if (isValidColoring(vertexIndex, selectedColor.hashCode())) {
                            clickedVertex.setFill(selectedColor);
                            currentGraph.setColor(vertexIndex, selectedColor.hashCode());
                            currentVertexIndex++;
                            checkGameStatus();                        
                    } else {
                        showAlert("Invalid Color", "This vertex cannot be colored the same as its neighbours.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Wrong Vertex", "Please color the vertices in the specified order.", Alert.AlertType.ERROR);
                }
            }
        });
    }


    // Add a stack to track the coloring history
    private Stack<Integer> coloringHistory = new Stack<>();

    private void iChangeMyMindGame() {
        randomOrder = new ArrayList<>();
            for (int i = 0; i < currentGraph.getVertices(); i++) {
                randomOrder.add(i);
            }
            Collections.shuffle(randomOrder); // Shuffle the vertices
        

            for (int i = 0; i < vertexCircles.length; i++) {
                Circle circle = vertexCircles[i];
                int shuffledIndex = randomOrder.get(i);
                System.out.println("Random Order got: " + shuffledIndex);
                Label indexLabel  = new Label(String.valueOf(shuffledIndex));
                indexLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                indexLabels.add(indexLabel);
                graphPane.getChildren().add(indexLabel);

                indexLabel.layoutXProperty().bind(circle.centerXProperty().subtract(indexLabel.widthProperty().divide(2)));
                indexLabel.layoutYProperty().bind(circle.centerYProperty().subtract(indexLabel.heightProperty().divide(2)));

                // Update the circle ID to match the shuffled order
                circle.setId(String.valueOf(shuffledIndex));
            }

        
            currentVertexIndex = 0;
            gameInProgress = true;
            startTimer();
            

        
            graphPane.setOnMouseClicked(e -> {
                if (e.getTarget() instanceof Circle) {
                    Circle clickedVertex = (Circle) e.getTarget();
                    int vertexIndex = Integer.parseInt(clickedVertex.getId());
                
                    // Ensure the player is coloring the correct vertex
                    System.out.println("Current Vertex Index: " + currentVertexIndex);
                    System.out.println("Vertex Index selected: " + vertexIndex);
                    if (vertexIndex == currentVertexIndex) {
                        Color selectedColor = colorPicker.getValue();
                        if (isValidColoring(vertexIndex, selectedColor.hashCode())) {
                            clickedVertex.setFill(selectedColor);
                            currentGraph.setColor(vertexIndex, selectedColor.hashCode());
                            currentVertexIndex++;
                            checkGameStatus();                        
                    } else {
                        showAlert("Invalid Color", "This vertex cannot be colored the same as its neighbours.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Wrong Vertex", "Please color the vertices in the specified order.", Alert.AlertType.ERROR);
                }
            }
        });
        
        // Add undo functionality
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> {
            System.out.println("Coloring history before: " + coloringHistory);
            if (!coloringHistory.isEmpty()) {
                int lastVertex = coloringHistory.pop();
                Circle circle = vertexCircles[lastVertex];
                circle.setFill(Color.WHITE); // Reset the circle's color
                currentGraph.setColor(lastVertex, -1); // Reset the graph's vertex color
                currentVertexIndex--;
            } else {
                showAlert("No Actions to Undo", "There are no colorings to undo.", Alert.AlertType.WARNING);
            }
            System.out.println("Coloring history after: " + coloringHistory);

        });
        graphPane.getChildren().add(undoButton);
        
    }
    
    private void colorGreedy() {
        //TODO: Implement
        List<List<Integer>> adjacencyList = currentGraph.getAdjacencyList();
        //List<Integer> colors = currentGraph.colors;
        // currentGraph.setColor(0, 1000);
        //         Circle p = vertexCircles[0];
        //         p.setFill(intToColor(1000));
        //For loop until all the vetices have been colored.
        int color = 100;
        boolean o;
        for (int x = 0; x < currentGraph.getVertices(); x++) {
            o = true;
            while(o){
            if(isValidColoring(x, color)){
                currentGraph.setColor(x, color);
                Circle circle = vertexCircles[x];
                circle.setFill(intToColor(color));
                o = false;
                }
            else{
                color += 12345678;
            }
        }   
        color = 100;
    }

        
            // List<Integer> colors = currentGraph.colors;
            // int tempColor = 60;
            // int tempVertexIndex = 0;
            // ArrayList<Integer> usedColors = new ArrayList<>();
            // //find the first empty vertice

            // for (int i = 0; i < colors.size(); i++){
            //     if(colors.get(i) == -1){
            //         tempVertexIndex = i;
            //         break;
            //     }      
            // }
            // //finds all the colors the vertice is connected to.
            // for (int i = 0; i < adjacencyList.get(tempVertexIndex).size(); i++){
            //     usedColors.add(colors.get(i));
            // }
            // //compares the colors and sees if there is a color left that we can use
            
            // for (int i = 0; i < colors.size(); i++ ){
            //     if((colors.get(i) != -1)  && (!usedColors.contains(colors.get(i)))){
            //         tempColor = colors.get(i);

            //     }
           
            // }
            // if(tempColor == -1){
            //     int t = (int)(Math.random() *Integer.MAX_VALUE);
            //     currentGraph.setColor(tempVertexIndex, t);
            //     Circle circle = vertexCircles[tempVertexIndex];
            //     circle.setFill(intToColor(t));         
            // }
            // else{
            //     if(isValidColoring(tempVertexIndex, tempColor)){
            //     Circle circle = vertexCircles[tempVertexIndex];
            //     circle.setFill(intToColor(tempColor));
            //     currentGraph.setColor(tempVertexIndex, tempColor);
                
            //     }
            //     else{
            //         int d = (int) (Math.random() * Integer.MAX_VALUE);
            //         currentGraph.setColor(tempVertexIndex, d);
                     
          
            //         Circle circle = vertexCircles[tempVertexIndex];
            //         circle.setFill(intToColor(d));
                 
            //         }
            // }



            // Circle circle = vertexCircles[x];
            // circle.setFill(Color.BLACK);
            // currentGraph.setColor(x, Color.BLACK.hashCode());
        }
    

    private void colorDSatur() {
        int numVertices = currentGraph.getVertices();
        boolean[] usedColors = new boolean[numVertices];
        int[] saturationDegree = new int[numVertices];
        boolean[] isColored = new boolean[numVertices];

        for (int i = 0; i < numVertices; i++) {
            saturationDegree[i] = 0;
            isColored[i] = false;
        }

        for (int step = 0; step < numVertices; step++) {
            int maxSaturationVertex = -1;
            for (int i = 0; i < numVertices; i++) {
                if (!isColored[i] &&
                    (maxSaturationVertex == -1 ||
                     saturationDegree[i] > saturationDegree[maxSaturationVertex] ||
                     (saturationDegree[i] == saturationDegree[maxSaturationVertex] &&
                      currentGraph.getAdjacencyList().get(i).size() > currentGraph.getAdjacencyList().get(maxSaturationVertex).size()))) {
                    maxSaturationVertex = i;
                }
            }

            List<Integer> neighbors = currentGraph.getAdjacencyList().get(maxSaturationVertex);
            for (int neighbor : neighbors) {
                if (currentGraph.getColor(neighbor) != -1) {
                    usedColors[currentGraph.getColor(neighbor)] = true;
                }
            }

            int color = 0;
            while (color < numVertices && usedColors[color]) {
                color++;
            }

            currentGraph.setColor(maxSaturationVertex, color);
            Circle circle = vertexCircles[maxSaturationVertex];
            circle.setFill(Color.web(getColorHex(color)));
            isColored[maxSaturationVertex] = true;

            for (int neighbor : neighbors) {
                if (currentGraph.getColor(neighbor) != -1) {
                    usedColors[currentGraph.getColor(neighbor)] = false;
                }
            }

            for (int neighbor : neighbors) {
                if (!isColored[neighbor]) {
                    saturationDegree[neighbor]++;
                }
            }
        }
    }

    private String getColorHex(int colorIndex) {
        String[] colors = {
            "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", "#800000", "#808000",
            "#008000", "#800080", "#008080", "#000080", "#FFA500", "#A52A2A", "#5F9EA0", "#7FFF00"
        };
        return colors[colorIndex % colors.length];
    }

    private void hint(Graph graph) {

        List<Integer> colors = graph.colors;
        List<List<Integer>> adjacencyList = graph.getAdjacencyList();
        List<Integer> usedColors = new ArrayList<>();
        int i = -1;
        int index = 0;


        if (currentGameMode.equals("Random Order") || currentGameMode.equals("I Changed My Mind")) {
            System.out.println("Random order: " + randomOrder);
            for (int currentRandomItem = 0; currentRandomItem < randomOrder.size(); currentRandomItem++) {
                if (randomOrder.get(currentRandomItem) == index) {
                    if (colors.get(currentRandomItem).equals(-1)) {
                        i = currentRandomItem;
                        System.out.println("Actually found the vertex to color: " + i);
                        break;
                    } else {
                        index++;
                        System.out.println("Didn't do shit. Index in iteration: " + index);
                    }
                }
            }
        } else {
            for (int p = 0; p < colors.size(); p++) {
                if (colors.get(p).equals(-1)) {
                    i = p;
                    break;
                }
            }
        }

        System.out.println("Index at the end of the check: " + i);

        //Finds an empty vertex to have a hint for
        

        if(i == -1){
            showAlert("Hint Error", "All vertices have been colored", AlertType.ERROR);
        }
        
        for (int j = 0; j < adjacencyList.get(i).size(); j++) {
            int neighbor = adjacencyList.get(i).get(j);
            if (neighbor != -1 && colors.get(neighbor) != -1) {
            usedColors.add(colors.get(neighbor));
            
            }
        }
       
        int k = -1;
        //Finds the first color in colors that is not -1
        for (int l = 0; l < colors.size(); l++){
            if(!(colors.get(l).equals(-1)) && !usedColors.contains(colors.get(l))){
       
                k = colors.get(l);
                break;
                }
        }  
        if(k == -1){
            int d = (int) (Math.random() * Integer.MAX_VALUE);  
            graph.setColor(i, d);  
            //showAlert("Heres your hint", "Color" + i + " " + d, AlertType.INFORMATION );
            Circle circle = vertexCircles[i];
            circle.setFill(intToColor(d));
            coloringHistory.add(i);
        }
        else{
            if(isValidColoring(i, k)){
            Circle circle = vertexCircles[i];
            circle.setFill(intToColor(k));
            graph.setColor(i, k);
            coloringHistory.add(i);
            //showAlert("Heres your hint", "Color" + i + " " + k, AlertType.INFORMATION );
            }
            else{
                int d = (int) (Math.random() * Integer.MAX_VALUE);
                graph.setColor(i, d);
                 
                //showAlert("Heres your hint", "Color" + i + " " + d, AlertType.INFORMATION );
                Circle circle = vertexCircles[i];
                circle.setFill(intToColor(d));
                coloringHistory.add(i);
                }
        }
        checkGameStatus();
    }
   
    private Color intToColor(int colorId) {
       
        int red = (colorId >> 16) & 0xFF;    
        int green = (colorId >> 8) & 0xFF;  
        int blue = colorId & 0xFF;          
        return Color.rgb(red, green, blue);
    }


    private void checkGameStatus(){
        boolean allColored = true;
        for(int i = 0; i < currentGraph.getVertices(); i++){
            if(currentGraph.getColor(i) == -1){
                allColored = false;
                break;
            }
        }

        if(allColored){
            stopTimer();
            calculateScore();
            showAlert("You win!", "Congratulations! You successfully colored the graph with " + currentGraph.countUsedColors() + " colors! Managing to get an amazing " + score + " score.", Alert.AlertType.INFORMATION);
            unColorGraph();
            resetTimer();
            currentGameMode = "";
            coloringHistory.clear();

        }
    }

    public void startTimer(){
        secondsElapsed = 0;
        timerLabel.setText("00:00");

        if(timeline != null){
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed++;
            timerLabel.setText(formatTime(secondsElapsed));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Loads a Graph from a file
    private void loadGraphFromFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Graph File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files != null) {
            for (File file : files) {
                try {
                    Graph graph = Graph.fromFile(file.getAbsolutePath());
                    loadedGraphs.add(graph);
                    fileDropdown.getItems().add(file.getName());
                } catch (IOException ex) {
                    showAlert("Error", "Failed to load graph from file: " + file.getName(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    // Stops the timer
    public int stopTimer() {
        if (timeline != null) {
            timeline.stop();
            System.out.println("Timer stopped at " + formatTime(secondsElapsed));
        }
        return secondsElapsed;
    }

    public void resetTimer() {
        if (timeline != null) {
            timeline.stop();
            secondsElapsed = 0;
            timerLabel.setText("00:00");
        }
    }

    private void calculateScore() {
        System.out.println("Seconds elapsed: " + secondsElapsed);
        score = (score - secondsElapsed) - (currentGraph.countUsedColors() * 2);
        System.out.println("Calculated Score: " + score + "");
        if (score < 0) {
            score = 0;
        }        
    }

    private void unColorGraph() {
        for (int i = 0; i < currentGraph.getVertices(); i++) {
            Circle circle = vertexCircles[i];
            circle.setFill(Color.WHITE);
            currentGraph.setColor(i, -1);
        }
    }

    // Formats the timer
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void displayGraph(Graph graph, Pane pane) {
        pane.getChildren().clear();
        List<List<Integer>> adjacencyList = graph.getAdjacencyList();
        int numVertices = graph.getVertices();
    
        double centerX = pane.getWidth() / 2.0;
        double centerY = pane.getHeight() / 2.0;
        double radius = GRAPH_SIZE / 2.5;
        Circle[] vertexCircles = new Circle[numVertices];
        this.vertexCircles = vertexCircles;
        double[] xCoords = new double[numVertices];
        double[] yCoords = new double[numVertices];
        List<Line> lines = new ArrayList<>();
    
        // Calculate positions for each vertex in a circular layout
        for (int i = 0; i < numVertices; i++) {
            double angle = 2 * Math.PI * i / numVertices;
            xCoords[i] = centerX + radius * Math.cos(angle);
            yCoords[i] = centerY + radius * Math.sin(angle);
    
            // Create circles for vertices
            Circle circle = new Circle(xCoords[i], yCoords[i], VERTEX_RADIUS);
            vertexCircles[i] = circle;
            pane.getChildren().add(circle);
            circle.setFill(Color.WHITE);
            circle.setId(String.valueOf(i));

            // Add dragging functionality
            final int index = i;
            circle.setOnMousePressed(event -> {
                circle.setUserData(new double[]{event.getX(), event.getY()});
            });
    
            circle.setOnMouseDragged(event -> {
                double[] prevCoords = (double[]) circle.getUserData();
                double offsetX = event.getX() - prevCoords[0];
                double offsetY = event.getY() - prevCoords[1];
                circle.setCenterX(circle.getCenterX() + offsetX);
                circle.setCenterY(circle.getCenterY() + offsetY);
                circle.setUserData(new double[]{event.getX(), event.getY()});
    
                // Update connected lines
                for (Line line : lines) {
                    if (line.getStartX() == xCoords[index] && line.getStartY() == yCoords[index]) {
                        line.setStartX(circle.getCenterX());
                        line.setStartY(circle.getCenterY());
                    } else if (line.getEndX() == xCoords[index] && line.getEndY() == yCoords[index]) {
                        line.setEndX(circle.getCenterX());
                        line.setEndY(circle.getCenterY());
                    }
                }
    
                // Update coordinates arra
                xCoords[index] = circle.getCenterX();
                yCoords[index] = circle.getCenterY();
            });
        }
    
        // Draw edges
        for (int i = 0; i < numVertices; i++) {
            for (int neighbor : adjacencyList.get(i)) {
                if (i < neighbor) { // To avoid duplicate lines
                    Line line = new Line(xCoords[i], yCoords[i], xCoords[neighbor], yCoords[neighbor]);
                    line.setStroke(Color.BLACK);
                    lines.add(line);
                    pane.getChildren().add(0, line); // Add the line before circles to make sure it appears below them
                }
            }
        }
    }  
    
    private boolean completeGraph(){
        double vertices = currentGraph.getVertices();
        int edges = currentGraph.getEdges();    

        vertices = (int)((vertices)* (vertices - 1)) / 2;

        if (vertices == edges){
            return true;
        } else {
            return false;
        }
    }

    private void colorCompleteGraph(){
        int color = 100;
        int numberOfVertex = currentGraph.getVertices();
        for(int i = 0; i < numberOfVertex; i++){
            currentGraph.setColor(i, color);
            Circle circle = vertexCircles[i];
            circle.setFill(intToColor(color));
            color = color + 123456;
        }
    }

    //
    private boolean isTree(){
        ArrayList<Integer> currentNeighbours = new ArrayList<>();
        ArrayList<Integer> nextNeighbours = new ArrayList<>();
        List<List<Integer>> adjacencyList = currentGraph.getAdjacencyList();
        int currenctVertex = 0;

        if((currentGraph.getVertices() - 1) != (currentGraph.getEdges())){
            return false;
        }
        while (currenctVertex < adjacencyList.size() - 1) {
            
     
        for(int i = 0; i < adjacencyList.get(currenctVertex).size(); i++){
            currentNeighbours.add(adjacencyList.get(currenctVertex).get(i));

        }

       
        for(int p = 0; p < currentNeighbours.size(); p++ ){
            nextNeighbours = new ArrayList<>();
  
            
            for (int i = 0; i< adjacencyList.get(currentNeighbours.get(p)).size(); i++){
            
                nextNeighbours.add(adjacencyList.get(currentNeighbours.get(p)).get(i));
             
            }
            for (int i = 0; i < currentNeighbours.size();  i++){
              
                for (int j = 0; j< nextNeighbours.size(); j++){
                   
                    if(currentNeighbours.get(i) == nextNeighbours.get(j)){
                        
                        return false;
                }
            }
        }
    }
        currenctVertex++;
        currentNeighbours = new ArrayList<>();
        nextNeighbours =  new ArrayList<>();
    }
        
       

    return true;
    }

    public void algorithmIsTree(){
        int color1 = 1000;
        int color2 = 1234567;
        int startVertex = 0;
        boolean complete = false;
        boolean check = true;

        while (complete == false){
            check = true;
            for(int i = startVertex; i < currentGraph.getVertices(); i++ ){
                if(isValidColoring(i, color1)){
                    currentGraph.setColor(i, color1);
                    Circle circle = vertexCircles[i];
                    circle.setFill(intToColor(color1));

                }
                else if(isValidColoring(i, color2)){
                    currentGraph.setColor(i, color2);
                    Circle circle = vertexCircles[i];
                    circle.setFill(intToColor(color2));
                }
                else{
                    for (int x = 0; x < currentGraph.getVertices(); x++){
                        currentGraph.setColor(i, -1);
                    }
                    startVertex++;
                    i = startVertex;
                }

            }
            if(startVertex != 0){
                for (int i = 0; i < startVertex; i++){
                    if(isValidColoring(i, color1)){
                        currentGraph.setColor(i, color1);
                        Circle circle = vertexCircles[i];
                        circle.setFill(intToColor(color1));
    
                    }
                    else if(isValidColoring(i, color2)){
                        currentGraph.setColor(i, color2);
                        Circle circle = vertexCircles[i];
                        circle.setFill(intToColor(color2));
                    }
                    else{
                        for (int x = 0; x < currentGraph.getVertices(); x++){
                            currentGraph.setColor(i, -1);
                        }
                        check = false;
                        startVertex++;
                        break;
                    }
             }
            }
            if(check == false){
                complete = false;
            }
            if(check == true){
                complete = true;

            }
    }
}


    private boolean isCycle(){
        int vertices = currentGraph.getVertices();
        int edges = currentGraph.getEdges();
        List<List<Integer>> adjacencyList = currentGraph.getAdjacencyList();

        // A cycle graph requires the number of edges to be equal to the number of vertices
        if (edges != vertices) {
            return false;
        }

        // Check if all vertices have degree exactly 2
        for (List<Integer> neighbors : adjacencyList) {
            if (neighbors.size() != 2) {
                return false;
            }
        }

        // Use a visited array to ensure the graph is connected and forms a single cycle
        boolean[] visited = new boolean[vertices];
        int startVertex = 0;
        int currentVertex = startVertex;
        int countVisited = 0;

        do {
            visited[currentVertex] = true;
            countVisited++;
            List<Integer> neighbors = adjacencyList.get(currentVertex);
            // Visit the next unvisited neighbor
            currentVertex = (visited[neighbors.get(0)] ? neighbors.get(1) : neighbors.get(0));
        } while (currentVertex != startVertex && countVisited < vertices);

        // Ensure we visited all vertices exactly once and ended back at the start
        return countVisited == vertices && currentVertex == startVertex;    
    }

    public void algorithmIsCycle(){
        if((currentGraph.getVertices() % 2) == 0){
        int color1 = 1000;
        int color2 = 1234567;
        int startVertex = 0;
        boolean complete = false;
        boolean check = true;

        while (complete == false){
            check = true;
            for(int i = startVertex; i < currentGraph.getVertices(); i++ ){
                if(isValidColoring(i, color1)){
                    currentGraph.setColor(i, color1);
                    Circle circle = vertexCircles[i];
                    circle.setFill(intToColor(color1));

                }
                else if(isValidColoring(i, color2)){
                    currentGraph.setColor(i, color2);
                    Circle circle = vertexCircles[i];
                    circle.setFill(intToColor(color2));
                }
                else{
                    for (int x = 0; x < currentGraph.getVertices(); x++){
                        currentGraph.setColor(i, -1);
                    }
                    startVertex++;
                    i = startVertex;
                }

            }
            if(startVertex != 0){
                for (int i = 0; i < startVertex; i++){
                    if(isValidColoring(i, color1)){
                        currentGraph.setColor(i, color1);
                        Circle circle = vertexCircles[i];
                        circle.setFill(intToColor(color1));
    
                    }
                    else if(isValidColoring(i, color2)){
                        currentGraph.setColor(i, color2);
                        Circle circle = vertexCircles[i];
                        circle.setFill(intToColor(color2));
                    }
                    else{
                        for (int x = 0; x < currentGraph.getVertices(); x++){
                            currentGraph.setColor(i, -1);
                        }
                        check = false;
                        startVertex++;
                        break;
                    }
             }
            }
            if(check == false){
                complete = false;
            }
            if(check == true){
                complete = true;

            }
    }
    
}

    if((currentGraph.getVertices() % 2) == 1){
        int color1 = 1000;
        int color2 = 1234567;
        int color3 = 3456789;
        int startVertex = 0;
        boolean complete = false;
        boolean check = true;

        while (complete == false){
            check = true;
            for(int i = startVertex; i < currentGraph.getVertices(); i++ ){
                if(isValidColoring(i, color1)){
                    currentGraph.setColor(i, color1);
                    Circle circle = vertexCircles[i];
                    circle.setFill(intToColor(color1));

                }
                else if(isValidColoring(i, color2)){
                    currentGraph.setColor(i, color2);
                    Circle circle = vertexCircles[i];
                    circle.setFill(intToColor(color2));
                }
                else if(isValidColoring(i, color3)){
                    currentGraph.setColor(i, color3);
                    Circle circle = vertexCircles[i];
                    circle.setFill(intToColor(color3));
                }
                else{
                    for (int x = 0; x < currentGraph.getVertices(); x++){
                        currentGraph.setColor(i, -1);
                    }
                    startVertex++;
                    i = startVertex;
                }

            }
            if(startVertex != 0){
                for (int i = 0; i < startVertex; i++){
                    if(isValidColoring(i, color1)){
                        currentGraph.setColor(i, color1);
                        Circle circle = vertexCircles[i];
                        circle.setFill(intToColor(color1));

                    }
                    else if(isValidColoring(i, color2)){
                        currentGraph.setColor(i, color2);
                        Circle circle = vertexCircles[i];
                        circle.setFill(intToColor(color2));
                    }
                    else if(isValidColoring(i, color3)){
                        currentGraph.setColor(i, color3);
                        Circle circle = vertexCircles[i];
                        circle.setFill(intToColor(color3));
                    }
                    else{
                        for (int x = 0; x < currentGraph.getVertices(); x++){
                            currentGraph.setColor(i, -1);
                        }
                        check = false;
                        startVertex++;
                        break;
                    }
                }
                }
            if(check == false){
                complete = false;
                }
            if(check == true){
                complete = true;
                }
            }
        }
        System.out.println("used this");
    }

    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}