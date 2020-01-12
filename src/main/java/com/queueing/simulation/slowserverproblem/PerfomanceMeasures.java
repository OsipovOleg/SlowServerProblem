package com.queueing.simulation.slowserverproblem;

import java.util.Arrays;

public class PerfomanceMeasures {

    private double responseTime;
    private int sampleSize;

    private double[] aggr_dist;

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
        builder.append("Response Time ")
                .append(getResponseTime())
                .append(System.lineSeparator())
                .append("Sample Size ")
                .append(getSampleSize())
                .append(System.lineSeparator())
                .append("Aggr Prob: ")
                .append(System.lineSeparator())
                .append(Arrays.toString(aggr_dist));

        return builder.toString();
    }

    public double[] getAggregateDist() {
        return aggr_dist;
    }

    public void setAggregateDist(double[] aggr_dist) {
        this.aggr_dist = aggr_dist;
    }
}
