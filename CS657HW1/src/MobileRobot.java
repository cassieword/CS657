import java.io.*;
import java.util.*;
;
/**
 * Created by xinyu on 2/17/17.
 */
public class MobileRobot {
    public static final char ESC = 27;
    public static void main(String[] args) throws Exception {
        String[][] map = new String[30][30];
        initMap(map, 20);
        printMap(map);
    }
    public static void initMap(String map[][], int blockPercent) {
        int mapLength = map.length * map[0].length;
        int num = mapLength * blockPercent / 100;
        Random randx = new Random();
        Random randy = new Random();

        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                map[i][j] = "_ ";
            }
        }
        for(int l = 0; l < num; l++) {
            int blockX = randx.nextInt(map.length);
            int blockY = randy.nextInt(map[0].length);
            map[blockX][blockY] = "x ";
        }
        map[5][5] = "O ";
        map[29][29] = "E ";
    }

    public static void initRobotMap(String map[][]) {

    }

    public static void printMap(String map[][]) throws Exception {
        //init robot map
        String[][] robotMap = new String[30][30];
        for(int i = 0; i < robotMap.length; i++) {
            for(int j = 0; j < robotMap[i].length; j++) {
                robotMap[i][j] = "_ ";
            }
        }

        Console c = System.console();
        c.readLine();
        if (c == null) {
            System.err.println("no console");
            System.exit(1);
        }

        // clear screen only the first time
        c.writer().print(ESC + "[2J");
        c.flush();
        Thread.sleep(200);
        //int position x, y
        int x = 5;
        int y = 5;
        //init angle
        float robotAngle = 45;
        //init path
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        ListIterator<Vertex> listIterator = path.listIterator();
        while(true) {
            // reposition the cursor to 1|1
            c.writer().print(ESC + "[1;1H");
            c.flush();

            for(int i = 0; i < map.length; i++) {
                for(int j = 0; j < map[i].length; j++) {
                    c.writer().print(map[i][j]);
                }
                c.writer().println();
            }
            c.writer().println();
            for(int i = 0; i < robotMap.length; i++) {
                for(int j = 0; j < robotMap[i].length; j++) {
                    c.writer().print(robotMap[i][j]);
                }
                c.writer().println();
            }
            c.flush();
            map[x][y] = "_ ";
            //robot move
            robotSensor(x, y, robotAngle-45, robotMap, map);
            robotSensor(x, y, robotAngle, robotMap, map);
            robotSensor(x, y, robotAngle+45, robotMap, map);
            if(path.isEmpty())
            {
                path = getPath(robotMap, x, y, 29, 29);
                listIterator = path.listIterator();
            }
            if(listIterator.hasNext()) {
                Vertex v = listIterator.next();
                robotAngle = getAngle(x, y, v.getI(), v.getJ());
                x = v.getI();
                y = v.getJ();
            }
            if(map[x][y].equals("x ")) {
                path = getPath(robotMap, x, y, 29, 29);
                listIterator = path.listIterator();
            }
            else{
                map[x][y] = "O ";
            }
            Thread.sleep(800);
        }
    }

    //DijkstraAlgorithm addEdge
    private static void addLane(int i, int j, int i2, int j2,
                                List<Vertex> nodes, List<Edge> edges) {
        if(i<0 || i>29 || j<0 || j>29 || i2<0 || i2>29 || j2<0 || j2>29) {
            return;
        }
        int sourceLocNo = i*30+j;
        int destLocNo = i2*30+j2;
        Vertex v1 = nodes.get(sourceLocNo);
        Vertex v2 = nodes.get(destLocNo);
        if(v1.getIsBlock() || v2.getIsBlock()) {
            return;
        }
        Edge lane = new Edge(sourceLocNo + "_" + destLocNo,nodes.get(sourceLocNo), nodes.get(destLocNo), 1 );
        edges.add(lane);
    }

    public static LinkedList<Vertex> getPath(String[][] map, int x, int y, int x2, int y2) {
        List<Vertex> nodes;
        List<Edge> edges;
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                boolean isBlock = map[i][j].equals("x ");
                Vertex location = new Vertex(i, j, isBlock);
                nodes.add(location);
            }
        }
        for(int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                addLane(i, j,i-1, j, nodes, edges);
                addLane(i, j,i-1, j+1, nodes, edges);
                addLane(i, j,i,j+1, nodes, edges);
                addLane(i, j, i+1, j+1, nodes, edges);
                addLane(i, j,i+1, j, nodes, edges);
                addLane(i, j, i+1, j-1, nodes, edges);
                addLane(i, j, i, j-1, nodes, edges);
                addLane(i, j, i-1, j-1, nodes, edges);
            }
        }

        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(x*map.length+y));
        LinkedList<Vertex> path = dijkstra.getPath(nodes.get(x2*map.length+y2));

        return path;
    }

    //robot map moveStartPoint, moveNextPoint
    public static float getAngle(int x, int y, int x2, int y2) {
        float angle = (float) Math.toDegrees(Math.atan2(x2-x, y2-y));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
    public static int getSinSign(float angle) {
        double radians = Math.toRadians(angle);
        double i = Math.sin(radians);
        if (Math.abs(i) < 2 * 0.01) {
            return 0;
        }
        else if(i > 0) {
            return 1;
        }
        else {
            return -1;
        }
    }
    public static int getCosSign(float angle) {
        double radians = Math.toRadians(angle);
        double i = Math.cos(radians);
        if (Math.abs(i) < 2 * 0.01) {
            return 0;
        }
        else if(i > 0) {
            return 1;
        }
        else {
            return -1;
        }
    }
    //robot sensor
    public static void robotSensor(int x, int y, float angle, String robotMap[][], String map[][]) {
        String s = map[x][y];
        while(s.equals("_ ")) {
            x = x + getSinSign(angle);
            y = y + getCosSign(angle);
            if(x>29 || x<0 || y>29 || y<0){
                return;
            }
            s = map[x][y];
        }
        robotMap[x][y] = "x ";
    }
}