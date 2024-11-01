package Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Controller.WorldMap;

public class Stop {
    private static int counter = 0;
    private final int id;
    private final List<Stop> adjacencyList;
    private HashMap<Stop, Integer[]> routeAttributes = new HashMap<>();

    public Stop(List<Stop> vertices, int[] distance, int[] time, int[] price, int[] transports) {
        this.id = ++counter;
        System.out.println("Created STOP: " + id);

        adjacencyList = new ArrayList<>();
        for(int ind = 0; ind < distance.length; ind++) {
            System.out.println("Added STOP: " + vertices.get(ind).id + " to STOP: " + id);
            adjacencyList.add(vertices.get(ind));
            routeAttributes.put(vertices.get(ind), new Integer[]{distance[ind], time[ind], price[ind], transports[ind]});

            WorldMap.getInstance().findStopById(vertices.get(ind).id).addVertex(this, distance[ind], time[ind], price[ind], transports[ind]);
            WorldMap.getInstance().findStopById(vertices.get(ind).id).getRouteAttributes().put(this, new Integer[]{distance[ind], time[ind], price[ind], transports[ind]});
        }
    }

    public Stop() {
        this.id = ++counter;
        System.out.println("Created STOP: " + id);
        this.adjacencyList = new ArrayList<>();
        this.routeAttributes = new HashMap<>();
    }

    public HashMap<Stop, Integer[]> getRouteAttributes() {
        return routeAttributes;
    }

    public int getId() {
        return id;
    }

    public List<Stop> getAdjacencyList() {
        return adjacencyList;
    }

    public void addVertex(Stop vertex, int distance, int time, int price, int transports) {
        if (!adjacencyList.contains(vertex)) {
            adjacencyList.add(vertex);
            routeAttributes.put(vertex, new Integer[]{distance, time, price, transports});
            System.out.println("Added connection from Stop " + id + " to Stop " + vertex.id);
        }
    }

        public void removeVertex(Stop vertex) {
        adjacencyList.remove(vertex);
        routeAttributes.remove(vertex);
    }

    public void clearAdjacencyList() {
        adjacencyList.clear();
        routeAttributes.clear();
    }

    public Route getRoute(Stop vertex) {
        for (Stop stop : adjacencyList) {
            if (stop.id == vertex.id) {
                return new Route(this, stop, routeAttributes.get(vertex)[0],
                        routeAttributes.get(vertex)[1], routeAttributes.get(vertex)[2],
                        routeAttributes.get(vertex)[3]);
            }
        }
        System.out.println("Route not found");
        return null;
    }

    public List<Route> getRoutes() {
        List<Route> routes = new ArrayList<>();
        for (Stop stop : adjacencyList) {
            routes.add(new Route(this, stop, routeAttributes.get(stop)[0],
                    routeAttributes.get(stop)[1], routeAttributes.get(stop)[2],
                    routeAttributes.get(stop)[3]));
        }
        return routes;
    }
}
