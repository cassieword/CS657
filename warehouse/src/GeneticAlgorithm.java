import java.io.Console;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by zhangxinyu on 4/1/17.
 */
public class GeneticAlgorithm {
    public static Set<Integer> set = new HashSet<Integer>();
    public static int chromosomeNum;
    public static int[][] gaChromosomes;
    public static int[][] previousGeneration;
    public static int[][] nextGeneration;
    public static int bestResult = 1000;
    public static int bestChromosome[] = new int[30];
    public static int bestGeneration = 0;
    public static final char ESC = 27;
    public static void main(String[] args) {
        String[][] map = new String[30][30];
        initHouse(map);
        int loopNum = 0;
        //initialize the fist generation
        previousGeneration = generateChromosomes(set);
        while( loopNum < 400) {
            //plot average and best fitness versus the number of generations
            getAverage(previousGeneration, 0, loopNum);
            //chromosomes sequence after genetic algorithm:
            gaChromosomes = GA(previousGeneration);
            //generate next generation
            nextGeneration = getNextGeneration(gaChromosomes);
            previousGeneration = nextGeneration;
            loopNum++;
        }
        System.out.println();
        System.out.println("--- last generation ---");
        for(int i = 0; i < previousGeneration.length; i++) {
            for(int j = 0; j < previousGeneration[0].length; j++) {
                System.out.print(previousGeneration[i][j] + " ");
            }
            System.out.println();
        }
        //int[] chosenChromosome = getAverage(previousGeneration, 1, 0);
        System.out.println("bestGeneration: "+ bestGeneration);
        System.out.println("bestResult: "+ bestResult);
        // Print Delivery Order
        System.out.println("--- print ---");
        printDelivery(map, bestChromosome);
    }
    //step 1: Generate Let n=30 random home locations in the above rectangular area.
    public static void initHouse(String map[][]) {
        Random rand = new Random();
        //warehouse point(5,5)
        set.add(25);
        for(int l = 0; l < 30; l++) {
            int num = rand.nextInt(900);
            while(set.contains(num)) {
                num = rand.nextInt(900);
            }
            set.add(num);
        }
    }
    //step 2: Generate the sequence of home deliveries, i.e. AH0H1...HnA
    public static int[][] generateChromosomes(Set<Integer> set) {
        //step 3: Randomly generate C (= 8 to 12, your choice) initial chromosomes.
        chromosomeNum = ThreadLocalRandom.current().nextInt(8, 12 + 1);
        System.out.println();
        System.out.println("--- number of chromosomes ---");
        System.out.println(chromosomeNum);
        System.out.println("--- initial first generation ---");
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
        return chromosomeArray;
    }
    //step 2-1: Shuffle function, generate different Array from the same Set
    private static void shuffleArray(Integer[] array) {
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
    //step 4: Choose either crossover or mutation genetic operator with a probability of
    //        p for crossover and (1-p) for mutation, where p is user defined.
    public static int[][] GA(int[][] array) {
        int[][] gaArray = new int[chromosomeNum*2][30];
        //generate new array for GA
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++) {
                gaArray[i][j] = array[i][j];
            }
        }
        Integer[] selectNumber;
        int n = chromosomeNum;
        //step 5: Apply the genetic operators until the population size is 2N.
        while(n < chromosomeNum*2) {
            double cProbability = Math.random();
            //System.out.println(cProbability);
            if (cProbability > 0.05) {
                //array used to crossover
                //selectNumber = randomSelect(2, chromosomeNum);
                int[] fitness = getFitness(array);
                int[][] selectChromosome = randomSelectChromosome(fitness, 2, array);

                int[] crossoverArrOne = new int[30];
                int[] crossoverArrTwo = new int[30];
                //original two crossover arrays
                //System.out.println();
                //System.out.println("crossover array 1: ");
                for(int i = 0; i < array[0].length; i++) {
                    crossoverArrOne[i] = selectChromosome[0][i];
                    //System.out.print(crossoverArrOne[i] + " ");
                }
                //System.out.println();
                //System.out.println("crossover array 2: ");
                for(int i = 0; i < array[0].length; i++) {
                    crossoverArrTwo[i] = selectChromosome[1][i];
                    //System.out.print(crossoverArrTwo[i] + " ");
                }
                //System.out.println();
                //generate new crossover array
                int[] newChromosome = crossoverMethod(crossoverArrOne, crossoverArrTwo);
                //add to gachromosome array
                for(int i = 0; i < 30; i++) {
                    gaArray[n][i] = newChromosome[i];
                }
                n++;
            }
            else {
                //use mutation
                //select mutation chromosome
                //int number = ThreadLocalRandom.current().nextInt(0, chromosomeNum);
                int[] fitness = getFitness(array);
                int[][] selectChromosome = randomSelectChromosome(fitness, 1, array);
                int[] mutationArr = new int[array[0].length];
                for(int i = 0; i < array[0].length; i++) {
                    mutationArr[i] = selectChromosome[0][i];
                }
                //select mutation position
                selectNumber = randomSelect(2, 30);
                //mutation
                int temp = mutationArr[selectNumber[0]];
                mutationArr[selectNumber[0]] = mutationArr[selectNumber[1]];
                mutationArr[selectNumber[1]] = temp;
                //add to gachromosome array
                for(int i = 0; i < 30; i++) {
                    gaArray[n][i] = mutationArr[i];
                }
                n = n + 1;
            }
        }
        return gaArray;
    }
    //step 4-1: Crossover Method Fuction
    public static int[] crossoverMethod(int[] arr1, int[] arr2) {
        //int[][] newChromosome = new int[2][30];
        int location = ThreadLocalRandom.current().nextInt(1, 27);
        int crossoverNum = ThreadLocalRandom.current().nextInt(2, 30-location);
        int[] crossoverArray = new int[crossoverNum];
        //System.out.println("crossover location: " + location);
        //System.out.println("crossover number: " + crossoverNum);
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < crossoverNum; i++) {
            crossoverArray[i] = arr1[i+location];
            set.add(crossoverArray[i]);
        }
        Vector<Integer> v2 = new Vector<Integer>();
        for(int i = 0; i < arr2.length; i++) {
            if(!set.contains(arr2[i])) {
                v2.add(arr2[i]);
            }
        }
        for(int i = 0; i < crossoverArray.length; i++) {
            v2.add(location+i, crossoverArray[i]);
        }
        for(int i = 0; i < v2.size(); i++) {
            arr2[i] = v2.get(i);
        }
        return arr2;
    }
    //step 6: Out of a total of 2N chromosomes N of them are selected for the next generation.
    public static int[][] getNextGeneration(int[][] gaChromosome) {
        int[] fitness = getFitness(gaChromosome);
        int[][] nextGeneration = randomSelectChromosome(fitness, chromosomeNum, gaChromosome);
        return nextGeneration;
    }
    //step 6-1: The fitness proportion selection is used to keep the size of population equal to N.
    public static int[] getFitness(int[][] array) {
        int[] fitness  = new int[chromosomeNum*2];
        int distance;
        int backtowarehouse;
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++) {
                int pointx = array[i][j] % 30;
                int pointy = array[i][j] / 30;
                if(j==0) {
                    distance = (int)Math.sqrt(Math.pow((pointx - 5),2) + Math.pow((pointy - 5),2));
                } else {
                    int pointxPre = array[i][j-1] % 30;
                    int pointyPre = array[i][j-1] / 30;
                    distance = (int)Math.sqrt(Math.pow((pointx - pointxPre),2) + Math.pow((pointy - pointyPre),2));
                }
                fitness[i] = fitness[i] + distance;
            }
            int pointxEnd = array[i][array[0].length-1] % 30;
            int pointyEnd = array[i][array[0].length-1] / 30;
            backtowarehouse = (int)Math.sqrt(Math.pow((pointxEnd - 5),2) + Math.pow((pointyEnd - 5),2));
            fitness[i] = 10000/(fitness[i] + backtowarehouse);
        }
        return fitness;
    }
    //step 6-2: Base on probability, randomly select # chromosomes, generate next generation
    public static int[][] randomSelectChromosome(int[] fitnessArray, int selectNum, int[][] chromosomeArray) {
        int total = 0;
        int[] totalArray = new int[fitnessArray.length+1];
        int[][] selectArray = new int[selectNum][30];
        totalArray[0] = 0;
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < fitnessArray.length; i++) {
            total = total + fitnessArray[i];
            totalArray[i+1] = total;
        }
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
        //generate new array
        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < 30; j++) {
                selectArray[i][j] = chromosomeArray[arr[i]][j];
            }
        }
        return selectArray;
    }
    //step 7: Get average fitness, best fitness in each generation, return the best chromosome from each generation
    public static int[] getAverage(int[][] array, int flag, int loop) {
        int[] fitness  = new int[chromosomeNum*2];
        int distance;
        int backtowarehouse;
        int best = 1000;
        int average = 0;
        int position = 0;
        int[] chosenChromosome = new int[30];
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++) {
                int pointx = array[i][j] % 30;
                int pointy = array[i][j] / 30;
                if(j==0) {
                    distance = (int)Math.sqrt(Math.pow((pointx - 5),2) + Math.pow((pointy - 5),2));
                } else {
                    int pointxPre = array[i][j-1] % 30;
                    int pointyPre = array[i][j-1] / 30;
                    distance = (int)Math.sqrt(Math.pow((pointx - pointxPre),2) + Math.pow((pointy - pointyPre),2));
                }
                fitness[i] = fitness[i] + distance;
            }
            int pointxEnd = array[i][array[0].length-1] % 30;
            int pointyEnd = array[i][array[0].length-1] / 30;
            backtowarehouse = (int)Math.sqrt(Math.pow((pointxEnd - 5),2) + Math.pow((pointyEnd - 5),2));
            fitness[i] = fitness[i] + backtowarehouse;
            if(fitness[i] < best) {
                best = fitness[i];
                position = i;
            }
        }
        for(int i = 0; i < fitness.length; i++) {
            average = average + fitness[i];
        }
        average = average / fitness.length * 2;

        if(flag == 0) {
            //System.out.println(loop+" average: " + average + " best: " + best);
            System.out.println(average + " " + best);
        } else {
            System.out.println();
            System.out.println("average: " + average + " best: " + best + " position: "+ position);
            System.out.println();
            System.out.println("--- chosen chromosome ---");
            for(int i = 0; i < chosenChromosome.length; i++) {
                chosenChromosome[i] = array[position][i];
                System.out.print(chosenChromosome[i]+ " ");
            }
            System.out.println();
            System.out.println();
            System.out.println("--- deliver sequence ---");
            for(int i = 0; i < chosenChromosome.length; i++) {
                chosenChromosome[i] = array[position][i];
                int x = chosenChromosome[i] % 30;
                int y = chosenChromosome[i] / 30;
                System.out.print("("+x+" "+y+"),");
            }
            System.out.println();
        }
        if(best < bestResult) {
            bestResult = best;
            bestChromosome = chosenChromosome;
            bestGeneration = loop;
        }
        return chosenChromosome;
    }
    //step 8: Calculate the distance fo the best chromosome
    public static int getDistance(int[] c, int x) {
        //c: chromosome, x: start point x
        int distance;
        int totalDistance = 0;
        int backtowarehouse;
        for(int i = 0; i < c.length; i++) {
            int pointx = c[i] % 30;
            int pointy = c[i] / 30;
            if(i == 0) {
                distance = (int)Math.sqrt(Math.pow((pointx - x),2) + Math.pow((pointy - x),2));
            } else {
                int pointxPre = c[i-1] % 30;
                int pointyPre = c[i-1] / 30;
                distance = (int)Math.sqrt(Math.pow((pointx - pointxPre),2) + Math.pow((pointy - pointyPre),2));
            }
            totalDistance = totalDistance + distance;
        }
        int pointxEnd = c[c.length-1] % 30;
        int pointyEnd = c[c.length-1] / 30;
        backtowarehouse = (int)Math.sqrt(Math.pow((pointxEnd - x),2) + Math.pow((pointyEnd - x),2));
        totalDistance = totalDistance + backtowarehouse;
        return totalDistance;
    }
    //step 9: Print sequence of delivery
    public static void printDelivery(String[][] map, int[] chromosome) {
        Console c = System.console();
        if (c == null) {
            //System.err.println("no console");
            System.exit(1);
        }

        // clear screen only the first time
        c.writer().print(ESC + "[2J");
        c.flush();

        int currentPosition = 0;

        while(true) {
            // clear screen only the first time
            c.writer().print(ESC + "[2J");
            c.flush();

            // reposition the cursor to 1|1
            c.writer().print(ESC + "[1;1H");
            c.flush();
            for(int i = 0; i < map.length; i++) {
                for(int j = 0; j < map[i].length; j++) {
                    map[i][j] = "_ ";
                }
            }
            map[5][5] = "A ";
            for(int i = 0; i < chromosome.length; i++) {
                int pointX = chromosome[i] % 30;
                int pointY = chromosome[i] / 30;
                map[pointX][pointY] = i + " ";
                if(i < currentPosition) {
                    map[pointX][pointY] = "A ";
                }
            }
            for(int i = 0; i < map.length; i++) {
                for(int j = 0; j < map[i].length; j++) {
                    System.out.print(map[i][j]);
                }
                System.out.println();
            }
            currentPosition++;
            c.flush();
            c.readLine();
        }
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

}
