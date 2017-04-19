/**
 * Created by xinyu on 4/19/17.
 */
public class NeuralNetwork {
    public static int inputNum = 2;
    public static int hiddenNum = 5;
    public static int outputNum = 2;
    public static double rate = 0.2;

    public static double[][] inputWeight = new double[hiddenNum][inputNum];
    public static double[] hiddenThreshold = new double[hiddenNum];
    public static double[][] hiddenWeight = new double[outputNum][hiddenNum];
    public static double[] outputThreshold = new double[outputNum];

    public static void main(String[] args) {
        System.out.println("Neural Network");
        System.out.println();
        for(int i = 0; i < 4000; i++) {
            neuralNetwork(0,0, 0,0);
            neuralNetwork(0,1, 1,0);
            neuralNetwork(1,0, 1,0);
            neuralNetwork(1,1, 0,1);
        }




    }

    public static void neuralNetwork(int x1, int x2, int y1, int y2) {
        //epoch 1-1 x1 = 0, x2 = 0, desired output sum = 0, carry = 0
        System.out.println("========Input x1=0, x2=0:");
        double[] input = new double[inputNum];
        input[0] = x1 + randomNoise();
        input[1] = x2 + randomNoise();
        System.out.println(input[0] + ", " + input[1]);
        //initial first weight, threshold, input num 2, output num 5

        double[][] inputWeightNew = new double[hiddenNum][inputNum];
        double[] hiddenThresholdNew = new double[hiddenNum];
        double[][] hiddenWeightNew = new double[outputNum][hiddenNum];
        double[] outputThresholdNew = new double[outputNum];

        System.out.println("========Input weight");
        for(int i = 0; i < inputWeight.length; i++) {
            for(int j = 0; j < inputWeight[0].length; j++) {
                inputWeight[i][j] = randomWeight(inputNum);
                inputWeightNew[i][j] = randomWeight(inputNum);
                System.out.print(inputWeight[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("========Hidden threshold");
        for(int i = 0; i < hiddenThreshold.length; i++) {
            hiddenThreshold[i] = randomThreshold(inputNum);
            hiddenThresholdNew[i] = randomThreshold(inputNum);
            System.out.print(hiddenThreshold[i] + " ");
        }
        System.out.println();
        //calculate activate for hidden output
        double[] hiddenOutput = sigmoidActivation(input, inputWeight, hiddenThreshold, hiddenNum);
        System.out.println("========Hidden output");
        for(int i = 0; i < hiddenOutput.length; i++) {
            System.out.println(hiddenOutput[i]+" ");
        }
        //initial second weight, threshold, input num 5, output num 2

        System.out.println("========Hidden weight");
        for(int i = 0; i < hiddenWeight.length; i++) {
            for(int j = 0; j < hiddenWeight[0].length; j++) {
                hiddenWeight[i][j] = randomWeight(hiddenNum);
                hiddenWeightNew[i][j] = randomWeight(hiddenNum);
                System.out.print(hiddenWeight[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("========Output threshold");
        for(int i = 0; i < outputThreshold.length; i++) {
            outputThreshold[i] = randomThreshold(hiddenNum);
            outputThresholdNew[i] = randomThreshold(hiddenNum);
            System.out.print(outputThreshold[i] + " ");
        }
        System.out.println();
        //calculate activate for output
        double[] output = sigmoidActivation(hiddenOutput, hiddenWeight, outputThreshold, outputNum);
        System.out.println("========Final output");
        for(int i = 0; i < output.length; i++) {
            System.out.println(output[i]+" ");
        }
        //weight training for hidden layer
        System.out.println("========Hidden layer weight training");
        double[] outputErrGradient = new double[outputNum];

        for(int i = 0; i < hiddenOutput.length; i++) {
            outputErrGradient[0] = output[0] * (1-output[0]) * (y1 - output[0]);
            outputErrGradient[1] = output[1] * (1-output[1]) * (y2 - output[1]);
            hiddenWeightNew[0][i] = hiddenWeight[0][i] + rate * hiddenOutput[i] * outputErrGradient[0];
            hiddenWeightNew[1][i] = hiddenWeight[1][i] + rate * hiddenOutput[i] * outputErrGradient[1];
            outputThresholdNew[0] = outputThreshold[0] + rate * outputThreshold[0] * outputErrGradient[0];
            outputThresholdNew[1] = outputThreshold[1] + rate * outputThreshold[1] * outputErrGradient[1];

        }
        for(int i = 0; i < hiddenWeightNew.length; i++) {
            for(int j = 0; j < hiddenWeightNew[0].length; j++) {
                System.out.print(hiddenWeightNew[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("========Output layer threshold training");
        for(int i = 0; i < outputThresholdNew.length; i++) {
            System.out.print(outputThresholdNew[i] + " ");
        }
        System.out.println();
        System.out.println("========Input layer weight training");
        double[] hiddenErrGradient = new double[hiddenNum];
        double[] sumGradient  = new double[hiddenNum];
        for(int i = 0; i < sumGradient.length; i++) {
            sumGradient[i] = 0;
        }
        System.out.println("========Hidden error gradient");
        for(int i = 0; i < hiddenOutput.length; i++) {
            for(int j = 0; j < outputErrGradient.length; j++) {
                sumGradient[i] += outputErrGradient[j] * hiddenWeight[j][i] ;
            }
            hiddenErrGradient[i] = hiddenOutput[i] * (1 - hiddenOutput[i]) * sumGradient[i];
        }
        for(int i = 0; i < inputWeight.length; i++) {
            for(int j = 0; j < inputWeight[0].length; j++) {
                inputWeightNew[i][j] = inputWeight[i][j] + rate * input[j] * hiddenErrGradient[i];
                System.out.print(inputWeightNew[i][j] + " ");

            }
            System.out.println();
        }
        System.out.println("========Hidden layer threshold training");
        for(int i = 0; i < hiddenThresholdNew.length; i++) {
            hiddenThresholdNew[i] = hiddenThreshold[i] + rate * hiddenThreshold[i] * hiddenErrGradient[i];
            System.out.print(hiddenThresholdNew[i] + " ");

        }
        hiddenWeight = hiddenWeightNew;
        outputThreshold = outputThresholdNew;
        inputWeight = inputWeightNew;
        hiddenThreshold = hiddenThresholdNew;

    }

    public static void training(int x1, int x2, int y1, int y2) {
        //epoch 1-1 x1 = 0, x2 = 0, desired output sum = 0, carry = 0
        System.out.println("========Input x1=0, x2=0:");
        double[] input = new double[inputNum];
        input[0] = x1 + randomNoise();
        input[1] = x2 + randomNoise();
        System.out.println(input[0] + ", " + input[1]);
        //initial first weight, threshold, input num 2, output num 5

        double[][] inputWeightNew = new double[hiddenNum][inputNum];
        double[] hiddenThresholdNew = new double[hiddenNum];
        double[][] hiddenWeightNew = new double[outputNum][hiddenNum];
        double[] outputThresholdNew = new double[outputNum];

        System.out.println("========Input weight");
        for(int i = 0; i < inputWeight.length; i++) {
            for(int j = 0; j < inputWeight[0].length; j++) {
                inputWeight[i][j] = randomWeight(inputNum);
                inputWeightNew[i][j] = randomWeight(inputNum);
                System.out.print(inputWeight[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("========Hidden threshold");
        for(int i = 0; i < hiddenThreshold.length; i++) {
            hiddenThreshold[i] = randomThreshold(inputNum);
            hiddenThresholdNew[i] = randomThreshold(inputNum);
            System.out.print(hiddenThreshold[i] + " ");
        }
        System.out.println();
        //calculate activate for hidden output
        double[] hiddenOutput = sigmoidActivation(input, inputWeight, hiddenThreshold, hiddenNum);
        System.out.println("========Hidden output");
        for(int i = 0; i < hiddenOutput.length; i++) {
            System.out.println(hiddenOutput[i]+" ");
        }
        //initial second weight, threshold, input num 5, output num 2

        System.out.println("========Hidden weight");
        for(int i = 0; i < hiddenWeight.length; i++) {
            for(int j = 0; j < hiddenWeight[0].length; j++) {
                hiddenWeight[i][j] = randomWeight(hiddenNum);
                hiddenWeightNew[i][j] = randomWeight(hiddenNum);
                System.out.print(hiddenWeight[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("========Output threshold");
        for(int i = 0; i < outputThreshold.length; i++) {
            outputThreshold[i] = randomThreshold(hiddenNum);
            outputThresholdNew[i] = randomThreshold(hiddenNum);
            System.out.print(outputThreshold[i] + " ");
        }
        System.out.println();
        //calculate activate for output
        double[] output = sigmoidActivation(hiddenOutput, hiddenWeight, outputThreshold, outputNum);
        System.out.println("========Final output");
        for(int i = 0; i < output.length; i++) {
            System.out.println(output[i]+" ");
        }
        //weight training for hidden layer
        System.out.println("========Hidden layer weight training");
        double[] outputErrGradient = new double[outputNum];

        for(int i = 0; i < hiddenOutput.length; i++) {
            outputErrGradient[0] = output[0] * (1-output[0]) * (y1 - output[0]);
            outputErrGradient[1] = output[1] * (1-output[1]) * (y2 - output[1]);
            hiddenWeightNew[0][i] = hiddenWeight[0][i] + rate * hiddenOutput[i] * outputErrGradient[0];
            hiddenWeightNew[1][i] = hiddenWeight[1][i] + rate * hiddenOutput[i] * outputErrGradient[1];
            outputThresholdNew[0] = outputThreshold[0] + rate * outputThreshold[0] * outputErrGradient[0];
            outputThresholdNew[1] = outputThreshold[1] + rate * outputThreshold[1] * outputErrGradient[1];

        }
        for(int i = 0; i < hiddenWeightNew.length; i++) {
            for(int j = 0; j < hiddenWeightNew[0].length; j++) {
                System.out.print(hiddenWeightNew[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("========Output layer threshold training");
        for(int i = 0; i < outputThresholdNew.length; i++) {
            System.out.print(outputThresholdNew[i] + " ");
        }
        System.out.println();
        System.out.println("========Input layer weight training");
        double[] hiddenErrGradient = new double[hiddenNum];
        double[] sumGradient  = new double[hiddenNum];
        for(int i = 0; i < sumGradient.length; i++) {
            sumGradient[i] = 0;
        }
        System.out.println("========Hidden error gradient");
        for(int i = 0; i < hiddenOutput.length; i++) {
            for(int j = 0; j < outputErrGradient.length; j++) {
                sumGradient[i] += outputErrGradient[j] * hiddenWeight[j][i] ;
            }
            hiddenErrGradient[i] = hiddenOutput[i] * (1 - hiddenOutput[i]) * sumGradient[i];
        }
        for(int i = 0; i < inputWeight.length; i++) {
            for(int j = 0; j < inputWeight[0].length; j++) {
                inputWeightNew[i][j] = inputWeight[i][j] + rate * input[j] * hiddenErrGradient[i];
                System.out.print(inputWeightNew[i][j] + " ");

            }
            System.out.println();
        }
        System.out.println("========Hidden layer threshold training");
        for(int i = 0; i < hiddenThresholdNew.length; i++) {
            hiddenThresholdNew[i] = hiddenThreshold[i] + rate * hiddenThreshold[i] * hiddenErrGradient[i];
            System.out.print(hiddenThresholdNew[i] + " ");

        }
        hiddenWeight = hiddenWeightNew;
        outputThreshold = outputThresholdNew;
        inputWeight = inputWeightNew;
        hiddenThreshold = hiddenThresholdNew;

    }

    //sigmoid activation method, num is # of output
    public static double[] sigmoidActivation(double[] input, double[][] weight, double[] threshold, int num) {
        double[] output = new double[num];
        for(int i = 0; i < output.length; i++) {
            output[i] = 0;
        }
        for(int i = 0; i < output.length; i++) {
            for(int j = 0; j < input.length; j++) {
                System.out.println("input " + input[j] + " weight "+ weight[i][j]);
                output[i] += input[j]*weight[i][j];
            }
            System.out.println("sum " + output[i] + " threshold " + threshold[i]);
            output[i] = output[i] - threshold[i];
            //sigmoid
            output[i] = 1 / (1 + Math.pow(Math.E,(-1 * output[i])));
            System.out.println("hidden output "+ output[i]);
        }
        return output;
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
