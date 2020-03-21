package com.queueing.simulation.slowserverproblem;

import com.random.RandomVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class QueueingSystem {

    public static class State {
        private int[] state;

        public State(int[] state) {
            this.state = state;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(state);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (this == obj) return true;
            if (obj instanceof State) {
                int[] other = ((State) obj).state;
                return Arrays.equals(state, other);
            } else return false;
        }

        @Override
        public String toString() {
            return Arrays.toString(state);
        }
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(QueueingSystem.class);

    private Random random;

    //Parameters of the model
    private int numberOfNodes;
    private int numberOfSiblings;
    private int[] thresholdValues;
    private RandomVariable interarrivalTimeRV;
    private RandomVariable[] serviceTimeRVs;


    private double averageResponseTime;
    private double averageServiceTimeForJob;
    private double averageJobQueueSize;
    private int stateBound = 10;

    private Map<State, Double> arrivingDistribution;
    private Map<State, Double> distribution;
    private Map<State, Double> discreteDistribution;
    private Map<State, Double> leaveDistribution;
    private Map<State, Double> startServiceDistribution;
    private int sampleVolumeForStartServiceDistribution;
    private int sampleVolumeForDiscreteDistr;

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


        distribution = new HashMap<>();
        discreteDistribution = new HashMap<>();
        arrivingDistribution = new HashMap<>();
        leaveDistribution = new HashMap<>();
        startServiceDistribution = new HashMap<>();


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


        State previousState = new State(getCurrentState());

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


            double delta = nextEventTime - currentTime;
            int r = queue.size() / numberOfSiblings;
            averageJobQueueSize += r * delta;


            if (currentTime != nextEventTime) {
                State s = new State(getCurrentState());
                if (!previousState.equals(s)) {
                    sampleVolumeForDiscreteDistr++;
                }

                if (queue.size() < stateBound) {
                    Double accumulatedTimeForState = distribution.get(s);
                    if (accumulatedTimeForState == null) accumulatedTimeForState = 0.0;
                    accumulatedTimeForState += delta;
                    distribution.put(s, accumulatedTimeForState);


                    if (!previousState.equals(s)) {
                        previousState = s;
                        accumulatedTimeForState = discreteDistribution.get(s);
                        if (accumulatedTimeForState == null) accumulatedTimeForState = 0.0;
                        accumulatedTimeForState++;
                        discreteDistribution.put(s, accumulatedTimeForState);
                    }
                }
            }

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
        averageServiceTimeForJob /= leftDemandCounter;
        averageJobQueueSize /= time;

        PerfomanceMeasures measures = new PerfomanceMeasures();
        measures.setDistribution(distribution.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() / time)));


        measures.setDiscreteDistribution(discreteDistribution.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() / sampleVolumeForStartServiceDistribution)));

        measures.setStartServiceDistribution(startServiceDistribution
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() / sampleVolumeForStartServiceDistribution)));


        measures.setResponseTime(averageResponseTime);
        measures.setAverageServiceTime(averageServiceTimeForJob);
        measures.setAverageJobQueueSize(averageJobQueueSize);
        measures.setSampleSize(leftDemandCounter);
        return measures;

    }

    private int[] getCurrentState() {
        int r = queue.size() / numberOfSiblings;
        int n0 = queue.size() % numberOfSiblings;
        int[] s = new int[2 + servers.length];
        s[0] = r;
        s[1] = n0;
        for (int i = 0; i < servers.length; i++) {
            s[i + 2] = servers[i] == null ? 0 : 1;
        }
        return s;
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

                int n0 = queue.size() % numberOfSiblings;

                if (n0 == numberOfSiblings - 1 && queue.size() < stateBound) {
                    //we have started service of a new job
                    State state = new State(getCurrentState());
                    Double number = startServiceDistribution.get(state);
                    number = number == null ? 0 : number;
                    number++;
                    startServiceDistribution.put(state, number);
                }
                sampleVolumeForStartServiceDistribution++;

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
        Task firstArrivedTask = joiner.get(task.getParentId());
        if (firstArrivedTask != null) {
            firstArrivedTask.setNumberOfSiblings(firstArrivedTask.getNumberOfSiblings() - 1);
            if (task.getStartServiceTime() < firstArrivedTask.getStartServiceTime()) {
                firstArrivedTask.setStartServiceTime(task.getStartServiceTime());
            }
            if (firstArrivedTask.getNumberOfSiblings() == 1) {
                joiner.remove(firstArrivedTask.getParentId());
                leftDemandCounter++;
                firstArrivedTask.setLeaveTime(currentTime);
                averageResponseTime += firstArrivedTask.getLeaveTime() - firstArrivedTask.getArrivalTime();
                averageServiceTimeForJob += firstArrivedTask.getLeaveTime() - firstArrivedTask.getStartServiceTime();
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
