import java.io.Console;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by xinyu on 4/2/17.
 */
public class GeneticAlgorithmExtend {
    public static Set<Integer> set = new HashSet<Integer>();
    public static int chromosomeNum;
    public static double probability;
    public static int[][] gaChromosomes;
    public static int[][] previousGeneration;
    public static int[][] nextGeneration;
    public static int bestResult = 1000;
    public static int bestChromosome[] = new int[30];
    public static int bestGeneration = 0;
    public static int bestPosition = 0;
    public static void main(String[] args) {
        String[][] map = new String[30][30];
        initHouse();
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
        System.out.println("--- last generation ---");
        for(int i = 0; i < previousGeneration.length; i++) {
            for(int j = 0; j < previousGeneration[0].length; j++) {
                System.out.print(previousGeneration[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("bestGeneration: "+ bestGeneration);
        System.out.println("bestResult: "+ bestResult);
        System.out.println("bestChromosome: ");
        for(int i = 0; i < bestChromosome.length; i++) {
            System.out.print(bestChromosome[i]+" ");
        }
        System.out.println();
        System.out.println("bestPosition: "+ bestPosition);
        System.out.println();
        // Print Delivery Order
        printDelivery(map, bestChromosome, bestPosition);
    }
    //step 1: Generate Let n=30 random home locations in the above rectangular area.
    public static void initHouse() {
        Random rand = new Random();
        //warehouse point(5,5) point(25,25)
        set.add(25);
        set.add(625);
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
        //remove (5,5),(25,25)
        set.remove(25);
        set.remove(625);
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
            if (cProbability > 0.8) {
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
    public static int[][] getNextGeneration(int[][] chromosomeArray) {
        int[] fitness = getFitness(chromosomeArray);
        int[][] nextGeneration = randomSelectChromosome(fitness, chromosomeNum, chromosomeArray);
        return nextGeneration;
    }
    //step 6-1: The fitness proportion selection is used to keep the size of population equal to N.
    public static int[] getFitness(int[][] array) {
        int[] fitness  = new int[chromosomeNum*2];
        int[] chromosome = new int[30];
        int totalDistance;
        int shortestDistance = 0;
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++ ) {
                chromosome[j] = array[i][j];
            }
            for(int h = 0; h < 31; h++) {
                // 30 houses delivered by warehouseB
                if(h==0) {
                    totalDistance = getDistance(chromosome, 25);
                    shortestDistance = totalDistance;
                }
                // 30 houses delivered by warehouseA
                else if(h==30) {
                    totalDistance = getDistance(chromosome, 5);
                    if(totalDistance < shortestDistance) {
                        shortestDistance = totalDistance;
                    }
                }
                // h (0 - h-1) houses delivered by warehouseA, 30-h(h - 30) houses delivered by warehouseB
                else {
                    int[] chromosomeA = new int[h];
                    int[] chromosomeB = new int[30-h];
                    for (int a = 0; a < h; a++) {
                        chromosomeA[a] = chromosome[a];
                    }
                    for(int b = 0; b < 30-h; b++) {
                        chromosomeB[b] = chromosome[h+b];
                    }
                    int distanceA = getDistance(chromosomeA, 5);
                    int distanceB = getDistance(chromosomeB, 25);
                    totalDistance = distanceA + distanceB;
                    if(totalDistance < shortestDistance) {
                        shortestDistance = totalDistance;
                    }
                }
            }
            fitness[i] = 10000000/shortestDistance;
        }
        return fitness;
    }
    //step 6-2: Base on probability, randomly select # chromosomes, generate next generation
    public static int[][] randomSelectChromosome(int[] fitnessArray, int selectNum, int[][] previousGeneration) {
        int total = 0;
        int[] totalArray = new int[fitnessArray.length+1];
        int[][] nextGeneration = new int[selectNum][30];
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
        //Generate next generation
        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < 30; j++) {
                nextGeneration[i][j] = previousGeneration[arr[i]][j];
            }
        }
        return nextGeneration;
    }
    //step 7: Get average fitness, best fitness in each generation, return the best chromosome from each generation
    public static int[] getAverage(int[][] array, int flag, int loop) {
        int[] fitness  = new int[chromosomeNum*2];
        int best = 1000;
        int average = 0;
        int[] position = new int[chromosomeNum*2];
        int chromosomePosition = 0;
        int deliverPosition = 0;
        int totalDistance;
        int shortestDistance = 0;
        int[] chromosome = new int[30];
        int[] chosenChromosome = new int[30];
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++ ) {
                chromosome[j] = array[i][j];
            }
            for(int h = 0; h < 31; h++) {
                // 30 houses delivered by warehouseB
                if(h==0) {
                    totalDistance = getDistance(chromosome, 25);
                    shortestDistance = totalDistance;
                }
                // 30 houses delivered by warehouseA
                else if(h==30) {
                    totalDistance = getDistance(chromosome, 5);
                    if(totalDistance < shortestDistance) {
                        shortestDistance = totalDistance;
                        position[i] = h;
                    }
                }
                // h (0 - h-1) houses delivered by warehouseA, 30-h(h - 30) houses delivered by warehouseB
                else {
                    int[] chromosomeA = new int[h];
                    int[] chromosomeB = new int[30-h];
                    for (int a = 0; a < h; a++) {
                        chromosomeA[a] = chromosome[a];
                    }
                    for(int b = 0; b < 30-h; b++) {
                        chromosomeB[b] = chromosome[h+b];
                    }
                    int distanceA = getDistance(chromosomeA, 5);
                    int distanceB = getDistance(chromosomeB, 25);
                    totalDistance = distanceA + distanceB;
                    if(totalDistance < shortestDistance) {
                        shortestDistance = totalDistance;
                        position[i] = h;
                    }
                }
            }
            fitness[i] = shortestDistance;
            if(fitness[i] < best) {
                best = fitness[i];
                chromosomePosition = i;
                deliverPosition = position[i];
            }
        }
        for(int i = 0; i < fitness.length; i++) {
            average = average + fitness[i];
        }
        average = average / fitness.length * 2;
        if(flag == 0) {
            //System.out.println(loop+" average: " + average + " best: " + best);
            System.out.println(average + " " + best);
        }
        for(int i = 0; i < chosenChromosome.length; i++) {
            chosenChromosome[i] = array[chromosomePosition][i];
        }
        if(best < bestResult) {
            bestResult = best;
            bestChromosome = chosenChromosome;
            bestGeneration = loop;
            bestPosition = deliverPosition;
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
    public static int[] getChosenChromosome(int[][] generation) {
        int[] fitness = getFitness(generation);
        System.out.println("### GET FITNESS ###");
        for(int i = 0; i < fitness.length / 2; i++) {
            System.out.print(fitness[i] + " ");
        }
        System.out.println();
        int bestFitness = fitness[0];
        int position = 0;
        for(int i = 0; i < fitness.length / 2; i++) {
            if (fitness[i] < bestFitness) {
                bestFitness = fitness[i];
                position = i;
            }
        }
        System.out.println(bestFitness);
        int[] chosenChromosome = new int[30];
        System.out.println("### CHOSEN CHROMOSOME ###");
        for(int i = 0; i < chosenChromosome.length; i++) {
            chosenChromosome[i] = previousGeneration[position][i];
            System.out.print(chosenChromosome[i]+ " ");
        }
        return chosenChromosome;
    }
    //step 9: Print sequence of delivery
    public static void printDelivery(String map[][], Vector<Integer> a, Vector<Integer> b) {
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                map[i][j] = "_ ";
            }
        }
        for(int i = 0; i < a.size(); i++) {
            int pointX = a.get(i) % 30;
            int pointY = a.get(i) / 30;
            map[pointX][pointY] = "+" + i + " ";
        }
        for(int i = 0; i < b.size(); i++) {
            int pointX = b.get(i) % 30;
            int pointY = b.get(i) / 30;
            map[pointX][pointY] = "-" + i + " ";
        }
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }
    //step 9: Print sequence of delivery
    public static void printDelivery(String[][] map, int[] chromosome, int position) {
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                map[i][j] = "_ ";
            }
        }
        Vector<Integer> a = new Vector();
        Vector<Integer> b = new Vector();
        for(int i = 0; i < position; i++) {
            a.add(chromosome[i]);
        }
        for(int i = 0; i < 30-position; i++) {
            b.add(chromosome[i+position]);
        }
        System.out.println("--- warehouse A ---");
        for (int i = 0; i < a.size(); i++) {
            int x = a.get(i)%30;
            int y = a.get(i)/30;
            System.out.print("(" + x + " " + y + "),");
        }
        System.out.println();
        System.out.println("--- warehouse B ---");
        for (int i = 0; i < b.size(); i++) {
            int x = b.get(i)%30;
            int y = b.get(i)/30;
            System.out.print("(" + x + " " + y + "),");
        }
    }
    //random select
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
