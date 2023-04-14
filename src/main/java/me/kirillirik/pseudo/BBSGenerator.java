package me.kirillirik.pseudo;

public final class BBSGenerator extends Generator {

    private final int p, q, m;
    private int next;

    public BBSGenerator() {
        p = 65003;
        q = 65001;
        m = p * q;
    }

    @Override
    protected void init() {
        next = (int) System.currentTimeMillis() % 100;
    }

    @Override
    protected int random(int min, int max) {
        next = (next * next) % m;
        return Math.abs(min + next % (max - min));
    }
}
