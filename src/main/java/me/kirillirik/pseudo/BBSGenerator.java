package me.kirillirik.pseudo;

public final class BBSGenerator extends Generator {

    private final int p, q, m;
    private int next;

    public BBSGenerator() {
        p = 97;
        q = 89;
        m = p * q;
    }

    @Override
    protected void init() {
        next = (int) System.currentTimeMillis();
    }

    @Override
    protected int random(int min, int max) {
        next = (next * next) % m;
        return min + next % (max - min);
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
