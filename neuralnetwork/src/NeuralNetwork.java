import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by xinyu on 4/19/17.
 */
public class NeuralNetwork {
    public static int inputNum = 2;
    public static int hiddenNum = 6;
    public static int outputNum = 2;
    public static double rate = 2;
    public static int epoch = 0;

    public static double[] input = new double[inputNum];
    public static double[][] inputWeight = new double[hiddenNum][inputNum];
    public static double[] hiddenThreshold = new double[hiddenNum];
    public static double[][] hiddenWeight = new double[outputNum][hiddenNum];
    public static double[] outputThreshold = new double[outputNum];
    public static double[] hiddenOutput = new double[hiddenNum];
    public static double[] output = new double[outputNum];
    public static double err = 10;
    public static int testErrors = 0;
    public static double[][] testSet = new double[100][2];


    public static void main(String[] args) {
        System.out.println("Neural Network");
        System.out.println();
        //initial first weight, threshold, input num 2, output num 5
        initial();
        while(err > 0.001) {
            err = 0;
            training(0,0, 0,0);
            training(0,1, 1,0);
            training(1,0, 1,0);
            training(1,1, 0,1);
            System.out.println(err + " ");
            epoch++;
        }
        System.out.println("Epoch");
        System.out.println(epoch);
        initialTest();
        for(int i = 0; i < testSet.length; i++) {
            testErrors += testing(testSet[i][0] , testSet[i][1]);
        }
        System.out.println("Errors");
        System.out.println(testErrors);

    }

    public static void initial() {
        for(int i = 0; i < inputWeight.length; i++) {
            for(int j = 0; j < inputWeight[0].length; j++) {
                inputWeight[i][j] = randomWeight(inputNum);
            }
        }
        for(int i = 0; i < hiddenThreshold.length; i++) {
            hiddenThreshold[i] = randomThreshold(inputNum);
        }
        //initial second weight, threshold, input num 5, output num 2
        for(int i = 0; i < hiddenWeight.length; i++) {
            for(int j = 0; j < hiddenWeight[0].length; j++) {
                hiddenWeight[i][j] = randomWeight(hiddenNum);
            }
        }
        for(int i = 0; i < outputThreshold.length; i++) {
            outputThreshold[i] = randomThreshold(hiddenNum);
        }
    }

    public static void initialTest() {
        ArrayList<Integer> value = new ArrayList<Integer>();
        value.add(0);
        value.add(1);
        for (int i = 0; i < testSet.length; i++) {
            for(int j = 0; j < testSet[0].length; j++) {
                Collections.shuffle(value);
                testSet[i][j] = value.get(0) + randomNoise();
            }
        }
    }

    public static int testing(double x1, double x2) {

        int error = 0;
        input[0] = x1;
        input[1] = x2;

        hiddenOutput = sigmoidActivation(input, inputWeight, hiddenThreshold, hiddenNum);
        //calculate activate for output
        output = sigmoidActivation(hiddenOutput, hiddenWeight, outputThreshold, outputNum);

        if(x1 > 0.8 && x2 > 0.8) {
            if(output[0] > 0.5 && output[1] > 0.5) {
                error = 0;
            }
            else {
                error = 1;
            }
        }
        else if(x1 < 0.8 && x2 > 0.8) {
            if(output[0] > 0.5 && output[1] < 0.5) {
                error = 0;
            }
            else {
                error = 1;
            }
        }
        else if(x1 > 0.8 && x2 < 0.8) {
            if(output[0] > 0.5 && output[1] < 0.5) {
                error = 0;
            }
            else {
                error = 1;
            }
        }
        else {
            if(output[0] < 0.5 && output[1] < 0.5) {
                error = 0;
            }
            else {
                error = 1;
            }
        }
        return error;
    }

    public static void neuralNetwork(int x1, int x2, int y1, int y2) {
        input[0] = x1 + randomNoise();
        input[1] = x2 + randomNoise();
        //calculate activate for hidden output
        hiddenOutput = sigmoidActivation(input, inputWeight, hiddenThreshold, hiddenNum);
        //calculate activate for output
        output = sigmoidActivation(hiddenOutput, hiddenWeight, outputThreshold, outputNum);
        err += Math.pow(output[0] - y1,2) + Math.pow(output[1] - y2,2);
    }

    public static void training(int x1, int x2, int y1, int y2) {
        neuralNetwork(x1, x2, y1, y2);
        double[][] inputWeightNew = new double[hiddenNum][inputNum];
        double[] hiddenThresholdNew = new double[hiddenNum];
        double[][] hiddenWeightNew = new double[outputNum][hiddenNum];
        double[] outputThresholdNew = new double[outputNum];

        //weight training for hidden layer
        double[] outputErrGradient = new double[outputNum];


        outputErrGradient[0] = output[0] * (1-output[0]) * (y1 - output[0]);
        outputErrGradient[1] = output[1] * (1-output[1]) * (y2 - output[1]);

        for(int i = 0; i < hiddenOutput.length; i++) {
            hiddenWeightNew[0][i] = hiddenWeight[0][i] + rate * hiddenOutput[i] * outputErrGradient[0];
            hiddenWeightNew[1][i] = hiddenWeight[1][i] + rate * hiddenOutput[i] * outputErrGradient[1];
        }

        //outputThresholdNew[0] = outputThreshold[0] + rate * outputThreshold[0] * outputErrGradient[0];
        //outputThresholdNew[1] = outputThreshold[1] + rate * outputThreshold[1] * outputErrGradient[1];

        double[] hiddenErrGradient = new double[hiddenNum];
        double[] sumGradient  = new double[hiddenNum];
        for(int i = 0; i < sumGradient.length; i++) {
            sumGradient[i] = 0;
        }

        for(int i = 0; i < hiddenOutput.length; i++) {
            for(int j = 0; j < outputErrGradient.length; j++) {
                sumGradient[i] += outputErrGradient[j] * hiddenWeight[j][i];
            }
            hiddenErrGradient[i] = hiddenOutput[i] * (1 - hiddenOutput[i]) * sumGradient[i];
        }

        for(int i = 0; i < inputWeight.length; i++) {
            for(int j = 0; j < inputWeight[0].length; j++) {
                inputWeightNew[i][j] = inputWeight[i][j] + rate * input[j] * hiddenErrGradient[i];
            }
        }
        for(int i = 0; i < hiddenThresholdNew.length; i++) {
            //hiddenThresholdNew[i] = hiddenThreshold[i] + rate * hiddenThreshold[i] * hiddenErrGradient[i];
        }
        hiddenWeight = hiddenWeightNew;
        //outputThreshold = outputThresholdNew;
        inputWeight = inputWeightNew;
        //hiddenThreshold = hiddenThresholdNew;
    }

    //sigmoid activation method, num is # of output
    public static double[] sigmoidActivation(double[] inputTemp, double[][] weight, double[] threshold, int num) {
        double[] outputTemp = new double[num];
        for(int i = 0; i < outputTemp.length; i++) {
            outputTemp[i] = 0;
        }
        for(int i = 0; i < outputTemp.length; i++) {
            for(int j = 0; j < inputTemp.length; j++) {
                outputTemp[i] += inputTemp[j] * weight[i][j];
            }
            outputTemp[i] = outputTemp[i] - threshold[i];
            //sigmoid
            outputTemp[i] = 1 / (1 + Math.exp(-1 * outputTemp[i]));
        }
        return outputTemp;
    }
    // random noise range [-0.2, +0.2]
    public static double randomNoise() {
        return (Math.random() * 0.4) - 0.2;
    }
    // random weight range [-2.4/n, +2.4/n], n is the number of input
    public static double randomWeight(int num) {
        double range = (2.4 + 2.4) / num;
        return (Math.random() * range) - (2.4 / num);
    }
    //random threshold range [-2.4/n, +2.4/n], n is the number of input
    public static double randomThreshold(int num) {
        double range = (2.4 + 2.4) / num;
        return (Math.random() * range) - (2.4 / num);
    }
}
