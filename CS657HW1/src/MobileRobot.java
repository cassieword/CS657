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
        System.out.println("No path.");
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
    }

    public static void printMap(String map[][]) throws Exception {
        //init robot map
        String[][] robotMap = new String[30][30];
        String[][] robotPath = new String[30][30];
        //init step counter
        int counter = 0;

        for(int i = 0; i < robotMap.length; i++) {
            for(int j = 0; j < robotMap[i].length; j++) {
                robotMap[i][j] = "_ ";
                robotPath[i][j] = "_ ";
            }
        }

        Console c = System.console();
        if (c == null) {
            System.err.println("no console");
            System.exit(1);
        }

        // clear screen only the first time
        c.writer().print(ESC + "[2J");
        c.flush();

        //int position x, y
        int x = 5;
        int y = 5;
        int nextX = 5;
        int nextY = 5;
        //init angle
        float robotAngle = 45;
        //init path
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        ListIterator<Vertex> listIterator = path.listIterator();

        // See more after moving.
        robotSensor(x, y, robotAngle-45, robotMap, robotPath, map);
        robotSensor(x, y, robotAngle, robotMap, robotPath, map);
        robotSensor(x, y, robotAngle+45, robotMap, robotPath, map);

        while(true) {
            // clear screen only the first time
            c.writer().print(ESC + "[2J");
            c.flush();

            // reposition the cursor to 1|1
            c.writer().print(ESC + "[1;1H");
            c.flush();

            //robot move
            robotPath[x][y] = "O ";

            for(int i = 0; i < map.length; i++) {
                for(int j = 0; j < map[i].length; j++) {
                    if (i == x && j == y) {
                        c.writer().print("O ");
                    } else {
                        c.writer().print(map[i][j]);
                    }
                }
                c.writer().println();
            }
            c.writer().println();
            for(int i = 0; i < robotMap.length; i++) {
                for(int j = 0; j < robotMap[i].length; j++) {
                    if (robotPath[i][j].equals("O ") || robotPath[i][j].equals("E ")) {
                        c.writer().print(robotPath[i][j]);
                    } else {
                        c.writer().print(robotMap[i][j]);
                    }
                }
                c.writer().println();
            }
            c.writer().println("robot angle: " + robotAngle);
            c.writer().println("robot x: " + x);
            c.writer().println("robot y: " + y);


            if(path.isEmpty())
            {
                path = getPath(robotMap, x, y, 29, 29);
                if (path != null) {
                    listIterator = path.listIterator();
                    listIterator.next();
                } else {
                    System.out.println("No path.");
                    return;
                }
            }
            if(listIterator.hasNext()) {
                Vertex v = listIterator.next();
                nextX = v.getI();
                nextY = v.getJ();
            }

            // Turn to that direction.

            float tempAngle = robotAngle;
            robotAngle = getAngle(x, y, nextX, nextY);
            counter = counter + (int)Math.abs(tempAngle - robotAngle)/45;

            // See more in that direction.
            robotSensor(x, y, robotAngle-45, robotMap, robotPath, map);
            robotSensor(x, y, robotAngle, robotMap, robotPath, map);
            robotSensor(x, y, robotAngle+45, robotMap, robotPath, map);

            if(robotMap[nextX][nextY].equals("x ")) {
                path = getPath(robotMap, x, y, 29, 29);
                if (path != null) {
                    listIterator = path.listIterator();
                    listIterator.next();

                    for(Vertex v : path) {
                        c.writer().print("<" + v.getI() + "," + v.getJ() + ">,");
                    }
                    c.writer().println();

                } else {
                    System.out.println("No path.");
                    return;
                }
            }
            else{
                // Move.
                if(x != nextX || y != nextY) {
                    counter = counter + 1;
                }

                x = nextX;
                y = nextY;

                // See more after moving.
                robotSensor(x, y, robotAngle-45, robotMap, robotPath, map);
                robotSensor(x, y, robotAngle, robotMap, robotPath, map);
                robotSensor(x, y, robotAngle+45, robotMap, robotPath, map);

                c.writer().println("robot next x: " + x);
                c.writer().println("robot next y: " + y);
                c.writer().println("path size: " + path.size());
                c.writer().println("current steps: " + counter);
            }

            c.flush();
            c.readLine();
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
                addLane(i, j, i-1, j-1, nodes, edges);
                addLane(i, j, i-1, j,   nodes, edges);
                addLane(i, j, i-1, j+1, nodes, edges);
                addLane(i, j, i,   j-1, nodes, edges);
                addLane(i, j, i,   j+1, nodes, edges);
                addLane(i, j, i+1, j-1, nodes, edges);
                addLane(i, j, i+1, j,   nodes, edges);
                addLane(i, j, i+1, j+1, nodes, edges);
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
    public static void robotSensor(int x, int y, float angle, String robotMap[][], String robotPath[][], String map[][]) {
        String s = map[x][y];
        while(s.equals("_ ")) {
            robotPath[x][y] = "E ";
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