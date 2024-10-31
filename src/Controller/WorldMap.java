package Controller;
import Classes.*;

import java.util.*;
import Enum.Priority;

public class WorldMap {
    private ArrayList<Stop> stops;
    private ArrayList<Route> routes;
    private static WorldMap instance;

    public WorldMap(ArrayList<Stop> stops, ArrayList<Route> routes) {
        this.stops = stops;
        this.routes = routes;
    }

    public void createStop(Stop stop) {
        stops.add(stop);
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void createRoute(Route route) {
        routes.add(route);
    }

    public static WorldMap getInstance() {
        if (instance == null) {
            instance = new WorldMap(new ArrayList<>(), new ArrayList<>());
        }
        return instance;
    }

    public void dijkstra(Stop start, Stop end, Priority priority) {
        // Inicializar distancias y predecesores
        Map<Stop, Integer> distances = new HashMap<>();
        Map<Stop, Stop> predecessors = new HashMap<>();
        for (Stop stop : stops) {
            distances.put(stop, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        PriorityQueue<Stop> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(start);

        while (!queue.isEmpty()) {
            Stop current = queue.poll();

            // Si llegamos al destino, dejamos de buscar
            if (current.equals(end)) {
                break;
            }

            for (Stop neighbor : current.getAdjacencyList()) {
                Route route = current.getRoute(neighbor);
                if (route == null || route.getTime() == -1) {
                    System.out.println("No es posible llegar de " + current.getId() + " a " + neighbor.getId() + " debido a un accidente.");
                    continue;
                }

                int newDist = calculatePriority(distances.get(current), route, priority);

                if (newDist < distances.get(neighbor)) {
                    queue.remove(neighbor);
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current); // Guardar el predecesor
                    queue.add(neighbor);
                }
            }
        }

        // Verificar si hay una ruta desde el inicio hasta el destino
        if (distances.get(end) == Integer.MAX_VALUE) {
            System.out.println("No existe una ruta desde " + start.getId() + " hasta " + end.getId());
        } else {
            System.out.println("Distancia más corta de " + start.getId() + " a " + end.getId() + " usando " + priority.name() + ": " + distances.get(end));
            System.out.println("Ruta: " + routeList(start, end, predecessors));
        }
    }

    public void BellmanFord(Stop start) {
        HashMap<Stop, Integer> distances = new HashMap<>();
        HashMap<Stop, List<Stop>> paths = new HashMap<>();

        // Inicializar distancias
        for (Stop stop : getAllStops()) {
            distances.put(stop, Integer.MAX_VALUE);
            paths.put(stop, new ArrayList<>());
        }
        distances.put(start, 0);
        paths.get(start).add(start);

        // Relajación de las aristas
        for (int i = 1; i < getAllStops().size(); i++) {
            for (Stop stop : getAllStops()) {
                for (Stop neighbor : stop.getAdjacencyList()) {
                    int newDist = distances.get(stop) + stop.getRouteAttributes().get(neighbor)[0];
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        List<Stop> newPath = new ArrayList<>(paths.get(stop));
                        newPath.add(neighbor);
                        paths.put(neighbor, newPath);
                    }
                }
            }
        }

        // Imprimir resultados
        for (Stop stop : getAllStops()) {
            System.out.println("Distancia más corta de " + start.getId() + " a " + stop.getId() + ": " + distances.get(stop));
            System.out.println("Ruta: " + paths.get(stop).stream().map(Stop::getId).toList());
        }
    }

    public void FloydWarshall() {
        int n = stops.size();
        int[][] dist = new int[n][n];
        int[][] next = new int[n][n];

        // Inicializar matrices de distancia y siguiente
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                    next[i][j] = i;
                } else {
                    Route route = stops.get(i).getRoute(stops.get(j));
                    if (route != null && route.getDistance() != -1) {
                        dist[i][j] = route.getDistance();
                        next[i][j] = j;
                    } else {
                        dist[i][j] = Integer.MAX_VALUE;
                        next[i][j] = -1;
                    }
                }
            }
        }

        // Algoritmo de Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < Integer.MAX_VALUE && dist[k][j] < Integer.MAX_VALUE) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
        }

        // Imprimir resultados de distancias más cortas y caminos
        System.out.println("Distancias más cortas entre todas las paradas:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][j] == Integer.MAX_VALUE) {
                    System.out.printf("No hay camino de %d a %d%n", stops.get(i).getId(), stops.get(j).getId());
                } else {
                    System.out.printf("Distancia más corta de %d a %d: %d%n", stops.get(i).getId(), stops.get(j).getId(), dist[i][j]);
                    System.out.print("Ruta: ");
                    printPath(i, j, next);
                    System.out.println();
                }
            }
        }
    }

    // Método auxiliar para imprimir la ruta de i a j
    private void printPath(int i, int j, int[][] next) {
        if (next[i][j] == -1) {
            System.out.print("No existe una ruta");
            return;
        }
        List<Integer> path = new ArrayList<>();
        while (i != j) {
            path.add(stops.get(i).getId());
            i = next[i][j];
        }
        path.add(stops.get(j).getId());
        for (int id : path) {
            System.out.print(id + " ");
        }
    }

    public void Prim() {
        // Inicializar el conjunto de vértices y aristas
        Set<Stop> vertices = new HashSet<>(stops);
        Set<Route> mstEdges = new HashSet<>();

        // Inicializar el conjunto de vértices del árbol de expansión mínima
        Set<Stop> mstVertices = new HashSet<>();
        Stop start = stops.get(0);
        mstVertices.add(start);
        vertices.remove(start);

        while (!vertices.isEmpty()) {
            Route minEdge = null;
            Stop minVertex = null;
            int minCost = Integer.MAX_VALUE;

            // Iterar sobre todos los vértices en el árbol
            for (Stop vertex : mstVertices) {
                for (Stop neighbor : vertex.getAdjacencyList()) {
                    Route edge = vertex.getRoute(neighbor);
                    if (edge != null && vertices.contains(neighbor) && edge.getCost() < minCost) {
                        minCost = edge.getCost();
                        minEdge = edge;
                        minVertex = neighbor;
                    }
                }
            }

            // Verificar si se encontró una arista mínima
            if (minEdge != null) {
                mstVertices.add(minVertex);
                vertices.remove(minVertex);
                mstEdges.add(minEdge); // Aquí se agrega la arista mínima encontrada

                // Impresión de la arista que se está agregando
                System.out.println("Agregando arista: " + minEdge.getStart().getId() + " -> " + minEdge.getEnd().getId() + " Costo: " + minCost);
            } else {
                System.out.println("No hay más aristas para agregar al árbol de expansión mínima.");
                break;
            }
        }

        // Imprimir el árbol de expansión mínima
        System.out.println("Árbol de expansión mínima:");
        for (Route edge : mstEdges) {
            System.out.println(edge.getStart().getId() + " -> " + edge.getEnd().getId() + " Costo: " + edge.getCost());
        }
    }




    private List<Stop> reconstructPath(Stop start, Stop end, int[][] next) {
        int startIndex = stops.indexOf(start);
        int endIndex = stops.indexOf(end);

        if (next[startIndex][endIndex] == -1) return Collections.emptyList();

        List<Stop> path = new ArrayList<>();
        for (int at = startIndex; at != endIndex; at = next[at][endIndex]) {
            path.add(stops.get(at));
            if (at == -1) return Collections.emptyList(); // Verifica si hay una ruta válida
        }
        path.add(stops.get(endIndex));
        return path;
    }


    public List<Stop> getAllStops() {
        return stops; // Retorna la lista de todas las paradas
    }


    private List<Integer> routeList(Stop start, Stop end, Map<Stop, Stop> predecessors) {
        List<Integer> path = new ArrayList<>();
        for (Stop at = end; at != null; at = predecessors.get(at)) {
            path.add(at.getId());
        }
        Collections.reverse(path);
        // Cambiar la verificación a:
        if (!path.isEmpty() && path.getFirst().equals(start.getId())) {
            return path; // Retornar la ruta si es válida
        } else {
            return Collections.emptyList(); // Retornar lista vacía si no hay ruta
        }
    }


    private int calculatePriority(int currentDistance, Route route, Priority priority) {
        int result = 0;
        switch (priority) {
            case DISTANCE:
                result = currentDistance + route.getDistance();
                break;
            case TIME:
                result = currentDistance + route.getTime();
                break;
            case COST:
                result = currentDistance + (int) route.getCost();
                break;
            case TRANSPORTS:
                result = currentDistance + route.getTransports();
                break;
            default:
                throw new IllegalArgumentException("Prioridad desconocida: " + priority.name());
        }
        return result;
    }

    public Stop findStopById(int id){
        for(Stop stop : stops){
            if(stop.getId() == id){
                return stop;
            }
        }
        return null;
    }
}