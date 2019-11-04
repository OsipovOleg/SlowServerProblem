package com.queueing.simulation.slowserverproblem;

import com.random.RandomVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

public class QueueingSystem {


    private static final Logger LOGGER = LoggerFactory.getLogger(QueueingSystem.class);

    private Random random;

    //Parameters of the model
    private int numberOfNodes;
    private int numberOfSiblings;
    private int[] thresholdValues;
    private RandomVariable interarrivalTimeRV;
    private RandomVariable[] serviceTimeRVs;


    private double averageResponseTime;


    public QueueingSystem(Random random, int numberOfNodes, int numberOfSiblings, int[] thresholdValues, RandomVariable interarrivalTimeRV, RandomVariable[] serviceTimeRVs) {
        this.random = random;
        this.numberOfNodes = numberOfNodes;
        this.numberOfSiblings = numberOfSiblings;
        this.thresholdValues = thresholdValues;
        this.interarrivalTimeRV = interarrivalTimeRV;
        this.serviceTimeRVs = serviceTimeRVs;
    }


    //Queue of task
    private Queue<Task> queue;

    private Map<Integer, Task> joiner;
    // States of servers
    private Task[] servers;


    //Times of events
    private double nextArrivalTime;
    private double startServiceTime;


    private double[] endServiceTimes;
    private int arrivedDemandCounter;
    private int leftDemandCounter;
    private double currentTime;


    /**
     * Run simulation
     *
     * @param time
     */
    public PerfomanceMeasures start(double time) {

        LOGGER.info("Simulation is starting");


        currentTime = 0;

        arrivedDemandCounter = 0;
        leftDemandCounter = 0;

        servers = new Task[numberOfNodes];
        endServiceTimes = new double[numberOfNodes];
        queue = new ArrayDeque<>();
        joiner = new HashMap<>();
        nextArrivalTime = 0;
        Arrays.fill(endServiceTimes, Double.POSITIVE_INFINITY);
        startServiceTime = Double.POSITIVE_INFINITY;


        while (currentTime <= time) {
            //Time of a next event
            int endServiceIndex = 0;

            //Search minimum elements in arrays
            for (int i = 1; i < numberOfNodes; i++) {
                if (endServiceTimes[endServiceIndex] > endServiceTimes[i]) {
                    endServiceIndex = i;
                }
            }


            //Get event time
            double nextEventTime = Math.min(nextArrivalTime,
                    Math.min(startServiceTime, endServiceTimes[endServiceIndex]));

            //Update current time
            currentTime = nextEventTime;


            //Run a handler for the current event
            if (nextEventTime == nextArrivalTime) {
                LOGGER.debug("Arrival Event, current time = {}", currentTime);
                arrivalEvent();
                continue;
            }
            if (nextEventTime == startServiceTime) {
                LOGGER.debug("Start Service Event, current time = {}", currentTime);
                startServiceEvent();
                continue;
            }
            if (nextEventTime == endServiceTimes[endServiceIndex]) {
                LOGGER.debug("End Service Event, current time = {}", currentTime);
                endServiceEvent(endServiceIndex);
                continue;
            }
        }


        averageResponseTime /= leftDemandCounter;
        PerfomanceMeasures measures = new PerfomanceMeasures();
        measures.setResponseTime(averageResponseTime);
        measures.setSampleSize(leftDemandCounter);
        return measures;

    }


    /**
     * Handler of arriving
     */
    private void arrivalEvent() {
        arrivedDemandCounter++;
        for (int i = 0; i < numberOfSiblings; i++) {
            Task task = new Task(arrivedDemandCounter, currentTime, numberOfSiblings);
            queue.add(task);
        }
        startServiceTime = currentTime;
        nextArrivalTime += interarrivalTimeRV.nextValue();
    }

    /**
     * Handler of start service event (check all servers - not so an optimal way)
     */
    private void startServiceEvent() {
        for (int i = 0; i < servers.length; i++) {
            if (queue.size() == 0) break;
            if ((servers[i] == null) && (queue.size() >= thresholdValues[i])) {
                LOGGER.debug("Start service in server {}", i);
                servers[i] = queue.remove();
                servers[i].setStartServiceTime(currentTime);
                endServiceTimes[i] = currentTime + serviceTimeRVs[i].nextValue();
            }

        }
        startServiceTime = Double.POSITIVE_INFINITY;
    }


    /**
     * Handler of end service event
     *
     * @param index of node
     */
    private void endServiceEvent(int index) {
        Task task = servers[index];
        task.setEndServiceTime(currentTime);

        LOGGER.debug("Joiner size = {}", joiner.size());
        Task temp = joiner.get(task.getParentId());
        if (temp != null) {
            temp.setNumberOfSiblings(temp.getNumberOfSiblings() - 1);
            if (temp.getNumberOfSiblings() == 1) {
                joiner.remove(temp.getParentId());
                leftDemandCounter++;
                temp.setLeaveTime(currentTime);
                averageResponseTime += temp.getLeaveTime() - temp.getArrivalTime();

            }
        } else {
            joiner.put(task.getParentId(), task);
        }


        //make server empty
        servers[index] = null;
        endServiceTimes[index] = Double.POSITIVE_INFINITY;
        startServiceTime = currentTime;


    }


}
