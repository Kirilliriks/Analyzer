package me.kirillirik.pseudo;
public final class LGGGenerator extends Generator {

    private final int a, c, m;
    private int next;

    public LGGGenerator() {
        a = 9301;
        c = 49297;
        m = 233280;
        init();
    }

    @Override
    protected void init() {
        next = (int) System.currentTimeMillis();
    }

    @Override
    protected int random(int min, int max) {
        next = Math.abs((next * a + c) % m);
        return Math.abs(min + next % (max - min));
    }

    @Override
    public void generate(int symbols) {
        init();

        buffer.delete(0, buffer.length());

        for (int i = 0; i < symbols; i++) {
            final int number = random(94, 126);
            System.out.println("I " + number + " C " + (char) number);
            buffer.append((char) number);
        }
    }
}
