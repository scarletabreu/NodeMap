package Visual;

import Classes.Stop;
import Classes.Route;
import Controller.WorldMap;
import Enum.Priority;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;

public class MapDashboard {
    @FXML private VBox stopListVBox;
    @FXML private VBox routeListVBox;
    @FXML private Pane mapPane;
    @FXML private Button addStopButton;
    @FXML private Button enrouteButton;
    @FXML private Button findPathButton;
    @FXML private ComboBox<Priority> priorityComboBox;

    private Circle hoverCircle;
    private List<Stop> stops;
    private HashMap<Stop, Circle> stopCircles;
    private boolean isAddingStop = false;
    private boolean isRouting = false;
    private Stop selectedStartStop = null;
    private List<Route> routes;

    private WorldMap worldMap;
    private boolean isPathFinding = false;
    private Stop pathStart = null;
    private HashMap<Pair<Stop, Stop>, Line> routeLines;
    private List<Line> highlightedPath;

    public MapDashboard() {
        stops = new ArrayList<>();
        stopCircles = new HashMap<>();
        routes = new ArrayList<>();
        routeLines = new HashMap<>();
        highlightedPath = new ArrayList<>();
        worldMap = WorldMap.getInstance();
    }

    @FXML
    public void initialize() {
        // Existing initialize code...

        priorityComboBox.getItems().addAll(Priority.values());
        priorityComboBox.setValue(Priority.DISTANCE);

        highlightedPath = new ArrayList<>();
    }

    @FXML
    private void handleFindPath() {
        isPathFinding = !isPathFinding;
        isRouting = false;
        findPathButton.setStyle(isPathFinding ? "-fx-background-color: #ff8080;" : "");
        enrouteButton.setStyle("");
        pathStart = null;

        // Clear previous highlighted path
        clearHighlightedPath();

        // Update cursor for all stop circles
        stopCircles.values().forEach(circle ->
                circle.setStyle(isPathFinding ? "-fx-cursor: hand;" : ""));
    }

    private void handleStopCircleClick(Stop stop) {
        if (isRouting) {
            // Existing routing logic...
        } else if (isPathFinding) {
            if (pathStart == null) {
                pathStart = stop;
                stopCircles.get(stop).setFill(Color.GREEN);
            } else if (pathStart != stop) {
                findAndHighlightPath(pathStart, stop);
                // Reset
                stopCircles.get(pathStart).setFill(Color.RED);
                pathStart = null;
                isPathFinding = false;
                findPathButton.setStyle("");
            }
        }
    }

    private void findAndHighlightPath(Stop start, Stop end) {
        // Clear previous highlighted path
        clearHighlightedPath();

        // Find the shortest path using WorldMap's Dijkstra
        worldMap.dijkstra(start, end, priorityComboBox.getValue());

        // Get the path from the WorldMap
        List<Stop> path = worldMap.getLastCalculatedPath();

        if (path == null || path.isEmpty()) {
            showAlert("No path found between selected stops.");
            return;
        }

        // Highlight the path
        for (int i = 0; i < path.size() - 1; i++) {
            Stop current = path.get(i);
            Stop next = path.get(i + 1);

            // Find the line corresponding to this route
            Pair<Stop, Stop> routePair = new Pair<>(current, next);
            Line line = routeLines.get(routePair);

            if (line != null) {
                // Highlight the line
                line.setStroke(Color.YELLOW);
                line.setStrokeWidth(4);
                highlightedPath.add(line);
            }
        }
    }

    private void clearHighlightedPath() {
        for (Line line : highlightedPath) {
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2);
        }
        highlightedPath.clear();
    }

    private void createRoute(Stop start, Stop end, int distance, int time, int cost, int transport) {
        // Existing route creation code...

        Line line = new Line(
                startCircle.getCenterX(),
                startCircle.getCenterY(),
                endCircle.getCenterX(),
                endCircle.getCenterY()
        );
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        mapPane.getChildren().add(line);

        // Store the line in our routes map
        routeLines.put(new Pair<>(start, end), line);

        // Add route to WorldMap
        Route route = new Route(start, end, distance, time, cost, transport);
        worldMap.createRoute(route);
        routes.add(route);

        // Existing route button creation code...
    }

    private void handleStopPlacement(MouseEvent event) {
        // Existing stop placement code...

        Stop newStop = new Stop(x, y);
        stops.add(newStop);
        worldMap.addStop(newStop);

        // Rest of the existing code...
    }
}