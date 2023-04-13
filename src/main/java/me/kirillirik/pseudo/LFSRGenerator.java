package me.kirillirik.pseudo;

public final class LFSRGenerator extends Generator {

    private final int[] TAPS = { 1, 22,  22, 32 };
    private final int M = 32;
    private final boolean[] bits  = new boolean[M + 1];

    public LFSRGenerator() {
        final int seed = (int) System.currentTimeMillis();
        for (int i = 0; i < M; i++) {
            bits[i] = (((1 << i) & seed) >>> i) == 1;
        }
    }

    @Override
    protected int random(int min, int max) {
        int next = 0;

        for (int i = 0; i < M; i++) {
            next |= (bits[i] ? 1 : 0) << i;
        }

        if (next < 0) {
            next++;
        }

        bits[M] = false;

        for (final int tap : TAPS) {
            bits[M] ^= bits[M - tap];
        }

        for (int i = 0; i < M; i++) {
            bits[i] = bits[i + 1];
        }

        return min + (next % (max - min));
    }

    @Override
    public void generate(int symbols) {
        buffer.delete(0, buffer.length());

        for (int i = 0; i < symbols; i++) {
            final int number = random(94, 126);
            System.out.println("I " + number + " C " + (char) number);
            buffer.append((char) number);
        }
    }
}
