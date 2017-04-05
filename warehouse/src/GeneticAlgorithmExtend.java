import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by xinyu on 4/2/17.
 */
public class GeneticAlgorithmExtend {
    public static Set<Integer> set = new HashSet<Integer>();
    public static int chromosomeNum;
    public static double probability;
    //public static int[][] initialChromosomes;
    public static int[][] gaChromosomes;
    public static int[][] previousGeneration;
    public static int[][] nextGeneration;
    public static void main(String[] args) {
        String[][] map = new String[30][30];
        initHouse();
        int loopNum = 0;
        //initialize the fist generation
        previousGeneration = generateChromosomes(set);

        while( loopNum < 400) {
            //chromosomes sequence after genetic algorithm:
            gaChromosomes = GA(previousGeneration);
            //generate next generation
            nextGeneration = getNextGeneration(gaChromosomes);
            previousGeneration = nextGeneration;
            loopNum++;
        }
        System.out.println("### FINAL GENERATION ###");
        for(int i = 0; i < previousGeneration.length; i++) {
            for(int j = 0; j < previousGeneration[0].length; j++) {
                System.out.print(previousGeneration[i][j] + " ");
            }
            System.out.println();
        }

        // chosen chromosome
        int[] chosenChromosome = getChosenChromosome(previousGeneration);

        int totalDistance = 0;
        int shortestDistance = 0;
        int shortestPosition = 0;

        Vector<Integer> chromosomeA = new Vector();
        Vector<Integer> chromosomeB = new Vector();

        for(int h = 0; h < 31; h++) {
            // 30 houses delivered by warehouseB
            if(h==0) {
                totalDistance = getDistance(chosenChromosome, 25);
                shortestDistance = totalDistance;
                shortestPosition = 0;
            }
            // 30 houses delivered by warehouseA
            else if(h==30) {
                totalDistance = getDistance(chosenChromosome, 5);
                if(totalDistance < shortestDistance) {
                    shortestDistance = totalDistance;
                    shortestPosition = h;
                }
            }
            // h (0 - h-1) houses delivered by warehouseA, 30-h(h - 30) houses delivered by warehouseB
            else {
                int[] cA = new int[h];
                int[] cB= new int[30-h];
                for (int a = 0; a < h; a++) {
                    cA[a] = chosenChromosome[a];
                }
                for(int b = 0; b < 30-h; b++) {
                    cB[b] = chosenChromosome[h+b];
                }
                int distanceA = getDistance(cA, 5);
                int distanceB = getDistance(cB, 25);
                totalDistance = distanceA + distanceB;
                if(totalDistance < shortestDistance) {
                    shortestDistance = totalDistance;
                    shortestPosition = h;
                }
            }
        }
        for(int i = 0; i < shortestPosition; i++) {
            chromosomeA.add(chosenChromosome[i]);
        }
        for(int i = 0; i < 30-shortestPosition; i++) {
            chromosomeB.add(chosenChromosome[i+shortestPosition]);
        }

        System.out.println();
        System.out.println("### SHORTEST DISTANCE ###");
        System.out.println(shortestDistance);
        System.out.println("### CHROMOSOME A ###");
        for (int i = 0; i < chromosomeA.size(); i++) {
            System.out.print(chromosomeA.get(i) + " ");
        }
        System.out.println();
        System.out.println("### CHROMOSOME B ###");
        for (int i = 0; i < chromosomeB.size(); i++) {
            System.out.print(chromosomeB.get(i) + " ");
        }

        // Print Delivery Order
        System.out.println();
        System.out.println("### PRINT ###");
        printDelivery(map, chromosomeA, chromosomeB);
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
    // step 1: Generate Let n=30 random home locations in the above rectangular area.
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
    //step 2: Generate the sequence of home deliveries, i.e. AH0H1...HnA
    public static int[][] generateChromosomes(Set<Integer> set) {
        //step 3: Randomly generate C (= 8 to 12, your choice) initial chromosomes.
        chromosomeNum = ThreadLocalRandom.current().nextInt(8, 12 + 1);
        System.out.println();
        System.out.println("### NUMBER OF CHROMOSOMES ###");
        System.out.println(chromosomeNum);
        System.out.println("### INITIAL GENERATION ###");
        //remove (5,5),(25,25)
        set.remove(25);
        set.remove(625);
        int[][] chromosomeArray = new int[chromosomeNum*2][30];
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

    public static int[][] GA(int[][] array) {
        int[][] gaArray = new int[chromosomeNum*2][30];
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++) {
                gaArray[i][j] = array[i][j];
            }
        }
        Integer[] selectNumber;
        int n = chromosomeNum;
        while(n < chromosomeNum*2) {
            probability = Math.random();
            if (probability > 0.1) {
                /*System.out.println("Use crossover method: ");*/
                // array used to crossover
                selectNumber = randomSelect(2, chromosomeNum);
                int[] crossoverArrOne = new int[array[0].length];
                int[] crossoverArrTwo = new int[array[0].length];
                /*for(int i = 0; i<2; i++) {
                    System.out.print(selectNumber[i] + " ");
                }*/
                //Original two crossover arrays
                for(int i = 0; i < array[0].length; i++) {
                    crossoverArrOne[i] = gaArray[selectNumber[0]][i];
                }
                for(int i = 0; i < array[0].length; i++) {
                    crossoverArrTwo[i] = gaArray[selectNumber[1]][i];
                }
                //Generate new crossover array
                int[][] newChromosome = crossoverMethod(crossoverArrOne, crossoverArrTwo);

                for(int i = 0; i < 30; i++) {
                    gaArray[n][i] = newChromosome[0][i];
                    if(n+1 == chromosomeNum * 2) {
                        continue;
                    } else {
                        gaArray[n+1][i] = newChromosome[1][i];
                    }
                }
                n = n+2;
                if(n > chromosomeNum * 2) {
                    n = chromosomeNum * 2;
                }
            }
            else {
                /*System.out.println("Use mutation method: ");*/
                // use mutation
                // select mutation chromosome
                int number = ThreadLocalRandom.current().nextInt(0, chromosomeNum);
                /*System.out.println(number);*/
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
                    gaArray[n][i] = mutationArr[i];
                }
                n = n + 1;
            }
        }
        return gaArray;
    }

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
    //step 3-1: calculate fitness of the chromosomes
    public static int[][] getNextGeneration(int[][] chromosomeArray) {
        int[] fitness = getFitness(chromosomeArray);
        int[][] nextGeneration = randomSelectChromosome(fitness, chromosomeNum, chromosomeArray);
        return nextGeneration;
    }
    //get fitness
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

    //step 3-2: base on probability, randomly select # chromosomes, generatte next generation
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
