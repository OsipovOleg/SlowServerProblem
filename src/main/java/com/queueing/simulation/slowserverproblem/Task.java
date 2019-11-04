package com.queueing.simulation.slowserverproblem;


public class Task {

    private int parentId;
    private int numberOfSiblings;
    private double arrivalTime;
    private double startServiceTime;
    private double endServiceTime;
    private double leaveTime;


    public Task(int parentId, double arrivalTime, int numberOfSiblings) {
        this.parentId = parentId;
        this.arrivalTime = arrivalTime;
        this.numberOfSiblings = numberOfSiblings;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getNumberOfSiblings() {
        return numberOfSiblings;
    }

    public void setNumberOfSiblings(int numberOfSiblings) {
        this.numberOfSiblings = numberOfSiblings;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getStartServiceTime() {
        return startServiceTime;
    }

    public void setStartServiceTime(double startServiceTime) {
        this.startServiceTime = startServiceTime;
    }

    public double getEndServiceTime() {
        return endServiceTime;
    }

    public void setEndServiceTime(double endServiceTime) {
        this.endServiceTime = endServiceTime;
    }

    public double getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(double leaveTime) {
        this.leaveTime = leaveTime;
    }


    @Override
    public int hashCode() {
        return Integer.hashCode(this.parentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Task)) return false;

        return ((Task) obj).getParentId() == this.parentId;
    }
}