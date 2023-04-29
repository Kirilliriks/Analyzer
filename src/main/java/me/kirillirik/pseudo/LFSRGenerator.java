package me.kirillirik.pseudo;

public final class LFSRGenerator extends Generator {

    private final int[] TAPS;
    private final int M;
    private final boolean[] bits;

    public LFSRGenerator() {
        this.M = 32;
        this.bits  = new boolean[M + 1];

        final int seed = (int) System.currentTimeMillis();
        for (int i = 0; i < M; i++) {
            bits[i] = (((1 << i) & seed) >>> i) == 1;
        }

        this.TAPS = new int[]{1, 22, 22, 32};
    }

    public LFSRGenerator(int m) {
        this.M = m;
        this.bits  = new boolean[M + 1];

        final int seed = (int) System.currentTimeMillis();
        for (int i = 0; i < M; i++) {
            bits[i] = (((1 << i) & seed) >>> i) == 1;
        }

        this.TAPS = new int[]{1, 2, 4, 6};
    }

    @Override
    public int random(int min, int max) {
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

        return Math.abs(min + (next % (max - min)));
    }

    public boolean[] getBits() {
        return bits;
    }
}
