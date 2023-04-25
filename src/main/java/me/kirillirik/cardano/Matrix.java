package me.kirillirik.cardano;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class Matrix <T> {

    private final int size;
    private final Vector<T> matrix;

    public Matrix(int size) {
        this.size = size;
        this.matrix = new Vector<>(size * size);
        this.matrix.setSize(size * size);
    }

    public void put(int x, int y, T value) {
        matrix.set(x + y * size, value);
    }

    public T get(int x, int y) {
        return matrix.get(x + y * size);
    }

    /**
     * Поворот на 90 градусов по часовой стрелке
     */
    public void rotate() {
        final List<T> temp = new ArrayList<>(matrix);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                temp.set(x + y * size,  matrix.get(y + (size - x - 1) * size));
            }
        }

        matrix.clear();
        matrix.addAll(temp);
    }

    public void debug() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                final T value = matrix.get(x + y * size);
                if (value == null) {
                    System.out.print("  ");
                } else {
                    System.out.print(value + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
