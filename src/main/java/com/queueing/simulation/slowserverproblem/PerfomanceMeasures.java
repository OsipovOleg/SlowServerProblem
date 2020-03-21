package com.queueing.simulation.slowserverproblem;

import java.util.ArrayList;
import java.util.Map;

public class PerfomanceMeasures {

    private double responseTime;


    private double averageServiceTime;
    private double averageJobQueueSize;
    private int sampleSize;

    private Map<QueueingSystem.State, Double> distribution;
    private Map<QueueingSystem.State, Double> discreteDistribution;
    private Map<QueueingSystem.State, Double> arrivalDistribution;
    private Map<QueueingSystem.State, Double> leaveDistribution;
    private Map<QueueingSystem.State, Double> startServiceDistribution;


    public String distributionToString(Map<QueueingSystem.State, Double> distribution) {
        StringBuilder result = new StringBuilder();

        ArrayList<String> list = new ArrayList<>();

        for (Map.Entry<QueueingSystem.State, Double> item :
                distribution.entrySet()) {
            list.add(item.getKey() + ": " + item.getValue());
        }
        list.sort(String::compareTo);

        for (String item :
                list) {
            result.append(item + "\n");
        }

        return result.toString() + "Count of observes states: " + distribution.size() + "\n";
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }


    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Distribution Prob: ")
                .append(System.lineSeparator())
                .append(distributionToString(distribution))
                .append(System.lineSeparator())
                .append("Discrete distribution Prob: ")
                .append(System.lineSeparator())
                .append(distributionToString(discreteDistribution))
                .append(System.lineSeparator())
                .append("Start Service Distribution: ")
                .append(System.lineSeparator())
                .append(distributionToString(startServiceDistribution))
                .append(System.lineSeparator())
                .append("Response Time: ")
                .append(getResponseTime())
                .append(System.lineSeparator())
                .append("Service Time: ")
                .append(getAverageServiceTime())
                .append(System.lineSeparator())
                .append("Job Queue Size (r mean): ")
                .append(getAverageJobQueueSize())
                .append(System.lineSeparator())
                .append("Sample Size: ")
                .append(getSampleSize())
                .append(System.lineSeparator());

        return builder.toString();
    }

    public void setDistribution(Map<QueueingSystem.State, Double> distribution) {
        this.distribution = distribution;
    }

    public Map<QueueingSystem.State, Double> getDistribution() {
        return distribution;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public double getAverageJobQueueSize() {
        return averageJobQueueSize;
    }

    public void setAverageJobQueueSize(double averageJobQueueSize) {
        this.averageJobQueueSize = averageJobQueueSize;
    }

    public Map<QueueingSystem.State, Double> getStartServiceDistribution() {
        return startServiceDistribution;
    }

    public void setStartServiceDistribution(Map<QueueingSystem.State, Double> startServiceDistribution) {
        this.startServiceDistribution = startServiceDistribution;
    }

    public Map<QueueingSystem.State, Double> getDiscreteDistribution() {
        return discreteDistribution;
    }

    public void setDiscreteDistribution(Map<QueueingSystem.State, Double> discreteDistribution) {
        this.discreteDistribution = discreteDistribution;
    }
}
