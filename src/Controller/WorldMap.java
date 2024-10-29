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

    private List<Integer> routeList(Stop start, Stop end, Map<Stop, Stop> predecessors) {
        List<Integer> path = new ArrayList<>();
        for (Stop at = end; at != null; at = predecessors.get(at)) {
            path.add(at.getId());
        }
        Collections.reverse(path);
        if (path.getFirst().equals(start.getId())) {
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