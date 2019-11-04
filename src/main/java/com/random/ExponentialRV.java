package com.random;

import java.util.Random;

public class ExponentialRV extends RandomVariable {

    private Random random;
    private double rate;

    public ExponentialRV(Random random, double rate) {
        this.random = random;
        this.rate = rate;
    }


    public double nextValue() {
        return -Math.log(random.nextDouble()) / rate;
    }

    public double getRate() {
        return rate;
    }
}
