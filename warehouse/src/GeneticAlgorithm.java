import java.util.Arrays;
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
        System.out.println();
        System.out.println("Fitness Array: ");
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
    //step 3-1: calculate fitness of the chromosomes
    public static void calculateFitness(int[][] chromosomeArray) {
        int[] fitness  = new int[chromosomeNum];
        int distance;
        int backtowarehouse;
        for(int i = 0; i < chromosomeArray.length; i++) {
            for(int j = 0; j < chromosomeArray[0].length; j++) {
                int pointx = chromosomeArray[i][j] % 30;
                int pointy = chromosomeArray[i][j] / 30;
                if(j==0) {
                    distance = (int)Math.sqrt(Math.pow((pointx - 5),2) + Math.pow((pointy - 5),2));
                } else {
                    int pointxPre = chromosomeArray[i][j-1] % 30;
                    int pointyPre = chromosomeArray[i][j-1] / 30;
                    distance = (int)Math.sqrt(Math.pow((pointx - pointxPre),2) + Math.pow((pointy - pointyPre),2));
                }
                fitness[i] = fitness[i] + distance;
            }
            int pointxEnd = chromosomeArray[i][chromosomeArray[0].length-1] % 30;
            int pointyEnd = chromosomeArray[i][chromosomeArray[0].length-1] / 30;
            backtowarehouse = (int)Math.sqrt(Math.pow((pointxEnd - 5),2) + Math.pow((pointyEnd - 5),2));
            fitness[i] = fitness[i] + backtowarehouse;
            // initial fitness
            System.out.print(fitness[i] + " ");
        }
        // array used to crossover
        System.out.println();
        System.out.println("selected fitness number: ");
        int[] arrNum = randomSelectChromosome(fitness, 2);
        int[] crossoverArrOne = new int[chromosomeArray[0].length];
        int[] crossoverArrTwo = new int[chromosomeArray[1].length];
        for(int i = 0; i<2; i++) {
            System.out.print(arrNum[i] + " ");
        }
        System.out.println();
        for(int i = 0; i < chromosomeArray[0].length; i++) {
            //System.out.print(arrNum[i] + " ");
            crossoverArrOne[i] = chromosomeArray[arrNum[0]][i];
            System.out.print(crossoverArrOne[i] + " ");
        }
        System.out.println();
        for(int i = 0; i < chromosomeArray[0].length; i++) {
            //System.out.print(arrNum[i] + " ");
            crossoverArrTwo[i] = chromosomeArray[arrNum[1]][i];
            System.out.print(crossoverArrTwo[i] + " ");
        }
        crossoverMethod(crossoverArrOne, crossoverArrTwo);

    }
    //step 3-2: base on probability, randomly select # chromosomes
    public static int[] randomSelectChromosome(int[] array, int selectNum) {
        int total = 0;
        int[] totalArray = new int[array.length];
        Set<Integer> selectedSet = new HashSet<>();
        //Random rand = new Random();
        int[] tempArray = new int[selectNum];
        int[] selectedArray = new int[selectNum];
        for(int i = 0; i < array.length; i++) {
            total = total + array[i];
            totalArray[i] = total;
        }
        for(int i = 0; i < selectNum; i++) {
            int select = (int)(Math.random() * total);
            while(selectedSet.contains(select)) {
                select = (int)(Math.random() * total);
            }
            selectedSet.add(select);
        }
        Integer[] arr = selectedSet.toArray(new Integer[selectedSet.size()]);
        /*for(int j = 0; j < totalArray.length; j++) {
            System.out.print(totalArray[j] + " ");
        }
        System.out.println();*/
        for(int i = 0; i < selectNum; i++) {
            //System.out.println(arr[i]);
            for(int j = 0; j < totalArray.length; j++) {
                if(arr[i] < totalArray[j]) {
                    tempArray[i] = j;
                    break;
                } else {
                   j++;
                }
            }
        }
        for(int i = 0; i < selectNum; i++) {
            int num = tempArray[i];
            selectedArray[i] = num;
            //System.out.println(tempArray[i] + ": " +totalArray[num]+" "+ array[num]);
        }
        return selectedArray;
    }

    //random select #
    public static Integer[] randomSelect(int num, int maximum) {
        Set<Integer> set = new HashSet<>();
        int[] arr = new int[num];
        Random rand = new Random();
        for(int i = 0; i < num; i++) {
            int a = rand.nextInt(maximum);
            while(set.contains(a)) {
                a = rand.nextInt(maximum);
            }
            set.add(a);
        }
        Integer[] array = set.toArray(new Integer[set.size()]);
        return array;
    }

    public static void crossoverMethod(int[] arr1, int[] arr2) {
        int crossoverNum = ThreadLocalRandom.current().nextInt(3, 15);
        Integer[] crossoverLocation = randomSelect(crossoverNum, 30);
        Arrays.sort(crossoverLocation);
        System.out.println();
        System.out.println("The first crossover select position: ");
        for(int i = 0; i<crossoverNum; i++) {
            System.out.print(crossoverLocation[i] + " ");
        }
        System.out.println();
        int[] firstCrossLocation = new int[crossoverNum];
        int[] firstCrossContent = new int[crossoverNum];
        int[] secondCrossLocaion = new int[crossoverNum];
        int[] secondCrossContent = new int[crossoverNum];
        for(int i = 0; i < crossoverNum; i++) {
            firstCrossLocation[i] = crossoverLocation[i];
            firstCrossContent[i] = arr1[crossoverLocation[i]];
            System.out.print(firstCrossContent[i] + " ");
        }
        for(int i = 0; i < crossoverNum; i++) {
            for(int j = 0; j < arr1.length; j++) {
                if(firstCrossContent[i] == arr2[j]) {
                    secondCrossLocaion[i] = j;
                    secondCrossContent[i] = firstCrossContent[i];
                }
            }
        }
        System.out.println();
        System.out.println("The second crossover position: ");
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(secondCrossLocaion[i] + " ");
        }
        System.out.println();
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(secondCrossContent[i] + " ");
        }
        // Sort secondCross
        boolean swapped = true;
        int j = 0;
        int tmpLocation;
        int tmpContent;
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < secondCrossLocaion.length - j; i++) {
                if (secondCrossLocaion[i] > secondCrossLocaion[i + 1]) {
                    tmpLocation = secondCrossLocaion[i];
                    tmpContent = secondCrossContent[i];
                    secondCrossLocaion[i] = secondCrossLocaion[i + 1];
                    secondCrossContent[i] = secondCrossContent[i+1];
                    secondCrossLocaion[i + 1] = tmpLocation;
                    secondCrossContent[i+1] = tmpContent;
                    swapped = true;
                }
            }
        }
        System.out.println();
        System.out.println("The sort second crossover position: ");
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(secondCrossLocaion[i] + " ");
        }
        System.out.println();
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(secondCrossContent[i] + " ");
        }
        int tempCrossContent;
        for(int i = 0; i < crossoverNum; i++) {
            tempCrossContent = secondCrossContent[i];
            secondCrossContent[i] = firstCrossContent[i];
            firstCrossContent[i] = tempCrossContent;
        }
        System.out.println();
        System.out.println("The new first crossover position: ");
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(firstCrossLocation[i] + " ");
        }
        System.out.println();
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(firstCrossContent[i] + " ");
        }
        System.out.println();
        System.out.println("The new second crossover position: ");
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(secondCrossLocaion[i] + " ");
        }
        System.out.println();
        for(int i = 0; i < crossoverNum; i++) {
            System.out.print(secondCrossContent[i] + " ");
        }
        for(int i = 0; i < crossoverNum; i++) {
            arr1[firstCrossLocation[i]] = secondCrossContent[i];
            arr2[secondCrossLocaion[i]] = firstCrossContent[i];
        }
        System.out.println();
        System.out.println("new array one: ");
        for(int i = 0; i < arr1.length; i++) {
            //crossoverArrOne[i] = chromosomeArray[arrNum[0]][i];
            System.out.print(arr1[i] + " ");
        }
        System.out.println();
        System.out.println("new array two: ");
        for(int i = 0; i < arr2.length; i++) {
            //crossoverArrTwo[i] = chromosomeArray[arrNum[1]][i];
            System.out.print(arr2[i] + " ");
        }
    }
}
