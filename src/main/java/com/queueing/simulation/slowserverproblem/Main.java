package com.queueing.simulation.slowserverproblem;


import com.random.ExponentialRV;
import com.random.RandomVariable;

import java.util.Random;

public class Main {
    public static QueueingSystem exponentialQS(double lambda, int numberOfNodes, int numberOfSiblings,
                                               double serviceRates[], int[] thresholdValues) {
        Random random = new Random();
        RandomVariable[] serviceRV = new RandomVariable[numberOfNodes];
        for (int i = 0; i < serviceRates.length; i++) {
            serviceRV[i] = new ExponentialRV(random, serviceRates[i]);
        }

        return new QueueingSystem(random, numberOfNodes, numberOfSiblings, thresholdValues,
                new ExponentialRV(random, lambda), serviceRV);
    }


    public static double makeExperiment(int[] thresholdValues) {
        double lambda = 1;
        double[] mu = {4, 3, 2, 1};
        int numberOfNodes = mu.length;
        int numberOfSiblings = 5;

        double simulationTime = 10000000;

        QueueingSystem queueingSystem = exponentialQS(lambda, numberOfNodes, numberOfSiblings, mu, thresholdValues);
        PerfomanceMeasures measures = queueingSystem.start(simulationTime);


        System.out.println("Performance Measures");
        System.out.println(measures);

        return measures.getResponseTime();

    }

    public static void main(String[] args) {


        int[] thresholdValues = {1, 4, 6, 10};
        System.out.print(makeExperiment(thresholdValues));


//        int n = 18;
//
//        int[] bestResult = {1, 1, 1, 1};
//
//
//        double responseTimes[][][][] = new double[n][n][n][n];
//
//        //check different thresholdValues
//        for (int i1 = 1; i1 < n; i1++) {
//            for (int i2 = 0; i2 < n; i2++) {
//                for (int i3 = 0; i3 < n; i3++) {
//                    for (int i4 = 0; i4 < n; i4++) {
//                        int[] thresholdValues = {1, i1, i2, i3, i4};
//                        System.out.println("------------------------------------------------------");
//                        System.out.println("thresholdValues: " + Arrays.toString(thresholdValues));
//                        responseTimes[i1][i2][i3][i4] = makeExperiment(thresholdValues);
//
//                        if (responseTimes[i1][i2][i3][i4] <
//                                responseTimes[bestResult[0]][bestResult[1]][bestResult[2]][bestResult[3]]) {
//                            bestResult[0] = i1;
//                            bestResult[1] = i2;
//                            bestResult[2] = i3;
//                            bestResult[3] = i4;
//
//                        }
//
//                    }
//                }
//            }
//        }
//
//        System.out.println("The best response time = " + responseTimes[bestResult[0]][bestResult[1]][bestResult[2]][bestResult[3]] + "  for thresholdValues = " + Arrays.toString(bestResult));
    }

}
