package me.kirillirik.cardano;

import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.utils.NumericUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Cardano extends Analyzer {

    private final String text;
    private final char[] charArray;
    private final int blockSize;
    private final int halfSize;

    private final Matrix<String> field;
    private final Matrix<String> mask;
    private final Matrix<Integer> rotationMatrix;

    public Cardano() {
        super("");

        text = "Текст после шифрования станет непонятным";
        charArray = text.replaceAll("\\s", "").toCharArray();
        blockSize = (int) Math.floor(charArray.length / 4.0D);
        halfSize = (int) Math.floor(Math.log(blockSize) / Math.log(2));
        field = new Matrix<>(halfSize * 2);
        mask = new Matrix<>(halfSize * 2);
        rotationMatrix = new Matrix<>(halfSize);
    }

    @Override
    public void analyze() {
        final List<Integer> key = new ArrayList<>();
        final Random random = new Random();
        for (int i = 0; i < blockSize; i++) {
            final int blockID = random.nextInt(1, 5);
            key.add(blockID);
        }

        System.out.println("Size " + blockSize);
        System.out.println("HalfSize " + halfSize);
        System.out.println("Key " + String.join("", key.stream().map(String::valueOf).toList()));

        int i = 1;
        for (int y = 0; y < halfSize; y++) {
            for (int x = 0; x < halfSize; x++) {
                rotationMatrix.put(x, y, i);
                field.put(x, y, String.valueOf(i));
                mask.put(x, y, " ");

                for (int k = 0; k < blockSize; k++) {
                    if (key.get(k) == 1 && (k + 1) == i) {
                        mask.put(x, y, String.valueOf(i));
                    }
                }

                i++;
            }
        }
        field.debug();

        rotate(key, 2, halfSize, 0);
        rotate(key, 3, halfSize, halfSize);
        rotate(key, 4, 0, halfSize);

        mask.debug();

        for (i = 0; i < 4; i++) {
            for (int charIndex = 0; charIndex < blockSize; charIndex++) {
                final char ch = charArray[(i * blockSize) + charIndex];

                for (int y = 0; y < halfSize * 2; y++) {
                    for (int x = 0; x < halfSize * 2; x++) {
                        final int maskKey = NumericUtils.parseInt(mask.get(x, y), -1);
                        if (maskKey == -1) {
                            continue;
                        }

                        if (maskKey - 1 == charIndex) {
                            field.put(x, y, String.valueOf(ch));
                        }
                    }
                }
            }

            mask.rotate();
        }

        field.debug();
    }

    private void rotate(List<Integer> key, int id, int xOffset, int yOffset) {
        rotationMatrix.rotate();
        for (int y = 0; y < halfSize; y++) {
            for (int x = 0; x < halfSize; x++) {
                final int value = rotationMatrix.get(x, y);
                field.put(x + xOffset, y + yOffset, String.valueOf(value));
                mask.put(x + xOffset, y + yOffset, " ");

                for (int k = 0; k < blockSize; k++) {
                    if (key.get(k) == id && (k + 1) == value) {
                        mask.put(x + xOffset, y + yOffset, String.valueOf(value));
                    }
                }
            }
        }
        field.debug();
    }
}
