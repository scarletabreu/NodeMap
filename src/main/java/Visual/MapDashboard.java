package Visual;

import Classes.Stop;
import Controller.WorldMap;
import Enum.Priority;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Pair;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

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
    @FXML private ComboBox<String> priorityComboBox = new ComboBox<>();

    private Priority selectedPriority = Priority.DISTANCE;
    private Circle hoverCircle;
    private final HashMap<Stop, Circle> stopCircles;
    private boolean isAddingStop = false;
    private boolean isRouting = false;
    private Stop selectedStartStop = null;

    private final WorldMap worldMap;
    private boolean isPathFinding = false;
    private Stop pathStart = null;
    private final HashMap<Pair<Stop, Stop>, Line> routeLines;
    private List<Line> highlightedPath;

    public MapDashboard() {
        stopCircles = new HashMap<>();
        routeLines = new HashMap<>();
        highlightedPath = new ArrayList<>();
        worldMap = WorldMap.getInstance();
    }

    @FXML
    public void initialize() {
        hoverCircle = new Circle(10, Color.BLUE);
        hoverCircle.setOpacity(0.5);
        mapPane.getChildren().add(hoverCircle);
        hoverCircle.setVisible(false);

        stopListVBox.setPadding(new Insets(5));
        stopListVBox.setSpacing(5);

        routeListVBox.setPadding(new Insets(5));
        routeListVBox.setSpacing(5);

        priorityComboBox.setItems(FXCollections.observableArrayList("DISTANCE", "TIME", "COST", "TRANSPORTS"));

        // Set up the StringConverter to convert between strings and enum values
        priorityComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String enumValue) {
                return enumValue; // Display the string representation
            }

            @Override
            public String fromString(String stringValue) {
                // Convert the string to the appropriate Enum value
                return stringValue;
            }
        });

        // Optional: Add a listener to handle the selected value change
        priorityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Here you can handle the selected value, convert to enum, etc.
            selectedPriority = Priority.valueOf(newValue);
        });

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
            if (selectedStartStop == null) {
                selectedStartStop = stop;
                stopCircles.get(stop).setFill(Color.GREEN);
            } else if (selectedStartStop != stop) {
                showRouteDialog(selectedStartStop, stop);
            }
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


        if (selectedPriority != null) {
            // Call Dijkstra or any pathfinding logic with the selected priority
            worldMap.dijkstra(start, end, selectedPriority);
        }

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

    @FXML
    private void handleEnroute() {
        isRouting = !isRouting;
        enrouteButton.setStyle(isRouting ? "-fx-background-color: #ff8080;" : "");
        selectedStartStop = null;

        // Update cursor for all stop circles
        stopCircles.values().forEach(circle ->
                circle.setStyle(isRouting ? "-fx-cursor: hand;" : ""));
    }

    @FXML
    private void handleAddStop() {
        isAddingStop = true;
        hoverCircle.setVisible(true);
        mapPane.requestFocus();
    }

    @FXML
    private void handleMouseMove(MouseEvent event) {
        if (!isAddingStop) return;

        Bounds mapBounds = mapPane.getBoundsInLocal();
        double x = Math.min(Math.max(event.getX(), hoverCircle.getRadius()),
                mapBounds.getWidth() - hoverCircle.getRadius());
        double y = Math.min(Math.max(event.getY(), hoverCircle.getRadius()),
                mapBounds.getHeight() - hoverCircle.getRadius());

        hoverCircle.setCenterX(x);
        hoverCircle.setCenterY(y);
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        if (isAddingStop) {
            handleStopPlacement(event);
        }
    }

    private void handleStopPlacement(MouseEvent event) {
        Bounds mapBounds = mapPane.getBoundsInLocal();
        if (!mapBounds.contains(event.getX(), event.getY())) {
            return;
        }

        double x = hoverCircle.getCenterX();
        double y = hoverCircle.getCenterY();
        Stop newStop = new Stop(x, y);
        worldMap.addStop(newStop);

        Circle stopCircle = new Circle(x, y, 10, Color.RED);
        stopCircle.setOnMouseClicked(e -> handleStopCircleClick(newStop));
        mapPane.getChildren().add(stopCircle);
        stopCircles.put(newStop, stopCircle);

        Button stopButton = createStopButton(String.format("Stop %d (%.1f, %.1f)", newStop.getId(), x, y), newStop);

        stopListVBox.getChildren().remove(addStopButton);
        stopListVBox.getChildren().add(stopButton);
        stopListVBox.getChildren().add(addStopButton);

        isAddingStop = false;
        hoverCircle.setVisible(false);
    }

    private void createRoute(Stop start, Stop end, int distance, int time, int cost, int transport) {
        Circle startCircle = stopCircles.get(start);
        Circle endCircle = stopCircles.get(end);

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
        routeLines.put(new Pair<>(end, start), line);

        // Add route to WorldMap
        start.addVertex(end, distance, time, cost, transport);
        worldMap.createRoute(start.getRoute(end));

        Button routeButton = new Button(String.format("Route %d→%d: %dm, %ds, $%d, Type %d",
                start.getId(), end.getId(), distance, time, cost, transport));
        routeButton.setMaxWidth(Double.MAX_VALUE);
        routeListVBox.getChildren().add(routeButton);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showRouteDialog(Stop start, Stop end) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/NodeMap/RouteDialog.fxml"));
            DialogPane dialogPane = loader.load();

            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Create Route");

            TextField distanceField = (TextField) dialogPane.lookup("#distanceField");
            TextField timeField = (TextField) dialogPane.lookup("#timeField");
            TextField costField = (TextField) dialogPane.lookup("#costField");
            ComboBox<Integer> transportComboBox = (ComboBox<Integer>) dialogPane.lookup("#transportComboBox");

            transportComboBox.getItems().addAll(1, 2, 3, 4); // Add your transport types

            ButtonType confirmButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    try {
                        int distance = Integer.parseInt(distanceField.getText());
                        int time = Integer.parseInt(timeField.getText());
                        int cost = Integer.parseInt(costField.getText());
                        int transport = transportComboBox.getValue();

                        createRoute(start, end, distance, time, cost, transport);
                        return new Pair<>(start.getId() + "", end.getId() + "");
                    } catch (NumberFormatException e) {
                        showAlert("Invalid input. Please enter valid numbers.");
                        return null;
                    }
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            // Reset the start stop's appearance
            stopCircles.get(selectedStartStop).setFill(Color.RED);
            selectedStartStop = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Button createStopButton(String text, Stop stop) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> {
            if (isRouting) {
                handleStopCircleClick(stop);
            }
        });
        return button;
    }
}