import Classes.Route;
import Classes.Stop;
import Controller.WorldMap;

public class Main {
    public static void main(String[] args) {
        // Crear instancias de WorldMap
        WorldMap worldMap = WorldMap.getInstance();

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

        // Llamar al método BellmanFord desde la parada inicial
        System.out.println("Ejecutando Bellman-Ford desde la parada 1:");
        worldMap.BellmanFord(stop1);
    }
}