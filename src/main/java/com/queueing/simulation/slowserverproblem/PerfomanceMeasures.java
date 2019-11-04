package com.queueing.simulation.slowserverproblem;

public class PerfomanceMeasures {

    private double responseTime;
    private int sampleSize;

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
                .append(getSampleSize());

        return builder.toString();
    }
}
