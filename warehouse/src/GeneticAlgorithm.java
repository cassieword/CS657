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
    public static double probability;
    public static int[][] initialChromosomes;
    public static void main(String[] args) {
        String[][] map = new String[30][30];
        initHouse(map);
        initialChromosomes = generateChromosomes(set);
        GA(initialChromosomes);

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
    public static int[][] generateChromosomes(Set<Integer> set) {
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
        int[][] chromosomeArray = new int[chromosomeNum * 2][30];
        for(int i = 0; i < chromosomeNum; i++) {
            Integer[] arr = set.toArray(new Integer[set.size()]);
            shuffleArray(arr);
            for(int j = 0; j < arr.length; j++){
                chromosomeArray[i][j] = arr[j];
                System.out.print(chromosomeArray[i][j] + " ");
            }
            System.out.println();
        }
        return chromosomeArray;


        //System.out.println();
        //System.out.println("Fitness Array: ");
        //calculateFitness(chromosomeArray);
    }

    public static void GA(int[][] array) {
        Integer[] selectNumber;
        int n = chromosomeNum;
        while(n < chromosomeNum*2) {
            probability = Math.random();
            if (probability > 0.1) {
                System.out.println("Use crossover method: ");
                // array used to crossover
                selectNumber = randomSelect(2, chromosomeNum);
                int[] crossoverArrOne = new int[array[0].length];
                int[] crossoverArrTwo = new int[array[0].length];
                for(int i = 0; i<2; i++) {
                    System.out.print(selectNumber[i] + " ");
                }
                //Original two crossover arrays
                for(int i = 0; i < array[0].length; i++) {
                    crossoverArrOne[i] = array[selectNumber[0]][i];
                }
                for(int i = 0; i < array[0].length; i++) {
                    crossoverArrTwo[i] = array[selectNumber[1]][i];
                }
                //Generate new crossover array
                int[][] newChromosome = crossoverMethod(crossoverArrOne, crossoverArrTwo);

                for(int i = 0; i < 30; i++) {
                    array[n][i] = newChromosome[0][i];
                    if(n+1 == chromosomeNum * 2) {
                        continue;
                    } else {
                        array[n+1][i] = newChromosome[1][i];
                    }
                }
                n = n+2;
                if(n > chromosomeNum * 2) {
                    n = chromosomeNum * 2;
                }
                System.out.println();
            }
            else {
                System.out.println("Use mutation method: ");
                // use mutation
                // select mutation chromosome
                int number = ThreadLocalRandom.current().nextInt(0, chromosomeNum);
                System.out.println(number);
                int[] mutationArr = new int[array[0].length];
                for(int i = 0; i < array[0].length; i++) {
                    mutationArr[i] = array[number][i];
                }
                // select mutation position
                selectNumber = randomSelect(2, 30);
                // mutation
                int temp = mutationArr[selectNumber[0]];
                mutationArr[selectNumber[0]] = mutationArr[selectNumber[1]];
                mutationArr[selectNumber[1]] = temp;
                // add to original chromosome array
                for(int i = 0; i < 30; i++) {
                    array[n][i] = mutationArr[i];
                }
                n = n + 1;
            }
        }

        System.out.println("Current generation chromosomes sequences: ");
        for(int i = 0; i < array.length; i++) {
            System.out.print(i + " ");
            for(int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j]+ " ");
            }
            System.out.println();
        }
        calculateFitness(array);

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
        int[] fitness  = new int[chromosomeNum*2];
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
            fitness[i] = 1000000/(fitness[i] + backtowarehouse);
            // initial fitness
            System.out.print(fitness[i] + " ");
        }
        System.out.println();
        System.out.println("selected fitness number: ");
        randomSelectChromosome(fitness, chromosomeNum, chromosomeArray);

        /*for(int i = 0; i<chromosomeNum; i++) {
            System.out.print(arrNum[i] + " ");
        }*/

    }
    //step 3-2: base on probability, randomly select # chromosomes
    public static void randomSelectChromosome(int[] fitnessArray, int selectNum, int[][] previousGeneration) {
        int total = 0;
        int[] totalArray = new int[fitnessArray.length+1];
        int[][] nextGeneration = new int[selectNum][30];
        totalArray[0] = 0;
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < fitnessArray.length; i++) {
            total = total + fitnessArray[i];
            totalArray[i+1] = total;
        }
        for(int j = 0; j < totalArray.length; j++) {
            System.out.print(totalArray[j] + " ");
        }
        System.out.println();

        for(int i = 0; i < selectNum;) {
            int select = (int)(Math.random() * total);
            for(int j = 1; j < totalArray.length; j++) {
                if(select < totalArray[j] && select > totalArray[j-1] && !set.contains(j-1) ) {
                    set.add(j-1);
                    i++;
                }
            }
        }
        Integer[] arr = set.toArray(new Integer[set.size()]);
        for(int i=0; i<selectNum; i++) {
            System.out.print(arr[i]+" ");
        }
        System.out.println();
        for(int i = 0; i < selectNum; i++) {
            //int num = arr[i];
            //selectedArray[i] = num;
            System.out.println(arr[i] + ": " +totalArray[arr[i]]+" "+ fitnessArray[arr[i]]);
        }
        //Generate next generation
        System.out.println("Next generation is: ");
        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < 30; j++) {
                nextGeneration[i][j] = previousGeneration[arr[i]][j];
                System.out.print(nextGeneration[i][j] + " ");
            }
            System.out.println();
        }

        //return selectedArray;
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

    public static int[][] crossoverMethod(int[] arr1, int[] arr2) {
        int[][] newChromosome = new int[2][30];
        int crossoverNum = ThreadLocalRandom.current().nextInt(3, 15);
        Integer[] crossoverLocation = randomSelect(crossoverNum, 30);
        //The first crossover select position:
        Arrays.sort(crossoverLocation);
        int[] firstCrossLocation = new int[crossoverNum];
        int[] firstCrossContent = new int[crossoverNum];
        int[] secondCrossLocaion = new int[crossoverNum];
        int[] secondCrossContent = new int[crossoverNum];
        for(int i = 0; i < crossoverNum; i++) {
            firstCrossLocation[i] = crossoverLocation[i];
            firstCrossContent[i] = arr1[crossoverLocation[i]];
        }
        //The second crossover select position:
        for(int i = 0; i < crossoverNum; i++) {
            for(int j = 0; j < arr1.length; j++) {
                if(firstCrossContent[i] == arr2[j]) {
                    secondCrossLocaion[i] = j;
                    secondCrossContent[i] = firstCrossContent[i];
                }
            }
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
        //Crossover
        int tempCrossContent;
        for(int i = 0; i < crossoverNum; i++) {
            tempCrossContent = secondCrossContent[i];
            secondCrossContent[i] = firstCrossContent[i];
            firstCrossContent[i] = tempCrossContent;
        }
        for(int i = 0; i < crossoverNum; i++) {
            arr1[firstCrossLocation[i]] = firstCrossContent[i];
            arr2[secondCrossLocaion[i]] = secondCrossContent[i];
        }
        //Generate new chromosomes
        for(int i = 0; i < 30; i++) {
            newChromosome[0][i] = arr1[i];
            newChromosome[1][i] = arr2[i];
        }
        return newChromosome;
    }
}
