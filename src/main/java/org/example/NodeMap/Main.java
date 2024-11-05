package org.example.NodeMap;

import Classes.Route;
import Classes.Stop;
import Controller.WorldMap;
import Enum.Priority;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Crear instancias de WorldMap
        /*WorldMap worldMap = WorldMap.getInstance();

        // Crear paradas sin conexiones iniciales
        Stop stop1 = new Stop();
        Stop stop2 = new Stop();
        Stop stop3 = new Stop();
        Stop stop4 = new Stop();

        // Agregar paradas al mapa
        worldMap.createStop(stop1);
        worldMap.createStop(stop2);
        worldMap.createStop(stop3);
        worldMap.createStop(stop4);



        // Crear las conexiones manualmente entre paradas usando addVertex
        stop1.addVertex(stop2, 10, 5, 15, 1);
        stop2.addVertex(stop3, 20, 10, 25, 1);
        stop3.addVertex(stop4, 15, 7, 10, 1);
        stop1.addVertex(stop4, 40, 20, 50, 1);
        stop4.addVertex(stop2, -10, 6, 5, 1);

        /*stop1.addVertex(stop2, 15, 5, 15, 1);
        stop1.addVertex(stop4, 40, 20, 50, 1);
        stop2.addVertex(stop3, 25, 10, 25, 1);
        stop3.addVertex(stop4, 10, 7, 10, 1);*/



        // Llamar al método BellmanFord desde la parada inicial
        /*System.out.println("Ejecutando Bellman-Ford desde la parada 1:");
        worldMap.BellmanFord(stop1);

        // Ejecutar Floyd-Warshall en todo el mapa
        System.out.println("Ejecutando Floyd-Warshall en el mapa completo:");
        worldMap.FloydWarshall();

        System.out.println("Ejecutando Dijkstra desde la parada 1:");
        worldMap.dijkstra(stop1, stop4, Priority.DISTANCE);

        System.out.println("Ejecutando Prim:");
        worldMap.Prim();*/

        /*System.out.println("Ejecutando Kruskal:");
        worldMap.Kruskal();*/


        // Crear una instancia de WorldMap
        WorldMap worldMap = WorldMap.getInstance();

        // Crear algunos Stops
        Stop stop1 = new Stop();
        Stop stop2 = new Stop();
        Stop stop3 = new Stop();
        Stop stop4 = new Stop();

        // Añadir stops al WorldMap
        worldMap.addStop(stop1);
        worldMap.addStop(stop2);
        worldMap.addStop(stop3);
        worldMap.addStop(stop4);

        // Añadir rutas (edges) entre los stops
        stop1.addVertex(stop2, 10, 5, 20, 1); // Ruta de stop1 a stop2
        stop1.addVertex(stop3, 15, 10, 30, 2); // Ruta de stop1 a stop3
        stop2.addVertex(stop3, 12, 8, 25, 1); // Ruta de stop2 a stop3
        stop2.addVertex(stop4, 5, 2, 10, 1); // Ruta de stop2 a stop4
        stop3.addVertex(stop4, 8, 6, 15, 1); // Ruta de stop3 a stop4

        System.out.println("Ejecutando Bellman-Ford desde la parada 1:");
        worldMap.BellmanFord(stop1);

        System.out.println("----------------------------------------");


        System.out.println("Ejecutando Dijkstra desde la parada 1:");
        worldMap.dijkstra(stop2, stop4, Priority.DISTANCE);

        System.out.println("----------------------------------------");

        System.out.println("Ejecutando Floyd-Warshall en el mapa completo:");
        worldMap.FloydWarshall();

        System.out.println("----------------------------------------");

        System.out.println("Ejecutando Prim:");
        worldMap.Prim();

        System.out.println("----------------------------------------");
        // Ejecutar el algoritmo de Kruskal
        List<Route> minimumSpanningTree = worldMap.Kruskal();

        // Imprimir las rutas del árbol de expansión mínima
        System.out.println("Rutas del Árbol de Expansión Mínima:");
        for (Route route : minimumSpanningTree) {
            System.out.println("Stop " + route.getStart().getId() + " -> Stop " + route.getEnd().getId() +
                    " | Distancia: " + route.getDistance() +
                    " | Costo: " + route.getCost() +
                    " | Tiempo: " + route.getTime());
        }

    }
}