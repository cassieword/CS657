import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by zhangxinyu on 4/1/17.
 */
public class GeneticAlgorithm {
    public static Set<Integer> set = new HashSet<Integer>();
    public static int chromosomeNum;
    public static void main(String[] args) {
        String[][] map = new String[30][30];
        initHouse(map);
        generateChromosomes(set);
    }
    // step 1: Generate Let n=30 random home locations in the above rectangular area.
    public static void initHouse(String map[][]) {
        Random rand = new Random();
        //warehouse point(5,5)
        set.add(25);
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                map[i][j] = "_ ";
            }
        }
        for(int l = 0; l < 30; l++) {
            int num = rand.nextInt(900);
            while(set.contains(num)) {
                num = rand.nextInt(900);
            }
            set.add(num);
            int pointX = num % 30;
            int pointY = num / 30;
            map[pointX][pointY] = "x ";
        }
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }
    //step 2: Generate the sequence of home deliveries, i.e. AH0H1...HnA
    public static void generateChromosomes(Set<Integer> set) {
        //step 3: Randomly generate C (= 8 to 12, your choice) initial chromosomes.
        chromosomeNum = ThreadLocalRandom.current().nextInt(8, 12 + 1);
        System.out.println();
        System.out.println("Number of chromosomes:");
        System.out.println();
        System.out.println(chromosomeNum);
        System.out.println();
        System.out.println(chromosomeNum+" chromosome sequences:");
        System.out.println();
        set.remove(25);
        int[][] chromosomeArray = new int[chromosomeNum][30];
        for(int i = 0; i < chromosomeNum; i++) {
            Integer[] arr = set.toArray(new Integer[set.size()]);
            shuffleArray(arr);
            for(int j = 0; j < arr.length; j++){
                chromosomeArray[i][j] = arr[j];
                System.out.print(chromosomeArray[i][j] + " ");
            }
            System.out.println();
        }
        calculateFitness(chromosomeArray);
    }

    private static void shuffleArray(Integer[] array)
    {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static void calculateFitness(int[][] chromosomeArray) {
        double[] fitness  = new double[chromosomeNum];
        double distance = 0;
        double backtowarehouse = 0;
        for(int i = 0; i < chromosomeArray.length; i++) {
            for(int j = 0; j < chromosomeArray[0].length; j++) {
                int pointx = chromosomeArray[i][j] % 30;
                int pointy = chromosomeArray[i][j] / 30;
                if(j==0) {
                    distance = Math.sqrt(Math.pow((pointx - 5),2) + Math.pow((pointy - 5),2));
                } else {
                    int pointxPre = chromosomeArray[i][j-1] % 30;
                    int pointyPre = chromosomeArray[i][j-1] / 30;
                    distance = Math.sqrt(Math.pow((pointx - pointxPre),2) + Math.pow((pointy - pointyPre),2));
                }
                fitness[i] = fitness[i] + distance;
            }
            int pointxEnd = chromosomeArray[i][chromosomeArray[0].length-1] % 30;
            int pointyEnd = chromosomeArray[i][chromosomeArray[0].length-1] / 30;
            backtowarehouse = Math.sqrt(Math.pow((pointxEnd - 5),2) + Math.pow((pointyEnd - 5),2));
            fitness[i] = fitness[i] + backtowarehouse;
            System.out.println(fitness[i]);
        }
    }
}
