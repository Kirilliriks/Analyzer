package me.kirillirik.pseudo;

public final class LGGGenerator extends Generator {

    private final int a, c, m;

    public LGGGenerator() {
        a = 9301;
        c = 49297;
        m = 233280;
        init();
    }

    @Override
    protected int random(int min, int max) {
        next = Math.abs((next * a + c) % m);
        return Math.abs(min + next % (max - min));
    }
}
