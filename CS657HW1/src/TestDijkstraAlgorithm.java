import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestDijkstraAlgorithm {



    public static void main(String[] args) {
        List<Vertex> nodes;
        List<Edge> edges;
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        for (int i = 0; i < 11; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i);
            nodes.add(location);
        }

        addLane("Edge_0", 0, 1, 85, nodes, edges);
        addLane("Edge_1", 0, 2, 217, nodes, edges);
        addLane("Edge_2", 0, 4, 173, nodes, edges);
        addLane("Edge_3", 2, 6, 186, nodes, edges);
        addLane("Edge_4", 2, 7, 103, nodes, edges);
        addLane("Edge_5", 3, 7, 183, nodes, edges);
        addLane("Edge_6", 5, 8, 250, nodes, edges);
        addLane("Edge_7", 8, 9, 84, nodes, edges);
        addLane("Edge_8", 7, 9, 167, nodes, edges);
        addLane("Edge_9", 4, 9, 502, nodes, edges);
        addLane("Edge_10", 9, 10, 40, nodes, edges);
        addLane("Edge_11", 1, 10, 600, nodes, edges);

        // Lets check from location Loc_1 to Loc_10
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(0));
        LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));

        for (Vertex vertex : path) {
            System.out.println(vertex);
        }

    }

    private static void addLane(String laneId, int sourceLocNo, int destLocNo,
                         int duration, List<Vertex> nodes, List<Edge> edges) {
        Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration );
        edges.add(lane);
    }
}