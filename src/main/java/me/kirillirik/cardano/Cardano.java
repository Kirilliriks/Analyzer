package me.kirillirik.cardano;

import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.utils.NumericUtils;
import me.kirillirik.utils.PreparedText;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Cardano extends Analyzer {

    private final char[] charArray;
    private final int blockSize;
    private final int halfSize;

    private final Matrix<String> field;
    private final Matrix<String> mask;
    private final Matrix<Integer> rotationMatrix;

    private boolean showMatrix;
    private boolean showText;

    public Cardano() {
        charArray = PreparedText.ISRAEL.replaceAll("\\s", "").toCharArray();
        blockSize = findLod((int) Math.floor(charArray.length / 4.0D));
        halfSize = (int) Math.floor(Math.log(blockSize) / Math.log(2));
        field = new Matrix<>(halfSize * 2);
        mask = new Matrix<>(halfSize * 2);
        rotationMatrix = new Matrix<>(halfSize);
    }

    private int findLod(int size) {
        int result = 2;
        while (result * result < size) {
            result += 1;
        }

        return result * result;
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
                mask.put(x, y, " ");

                for (int k = 0; k < blockSize; k++) {
                    if (key.get(k) == 1 && (k + 1) == i) {
                        mask.put(x, y, String.valueOf(i));
                    }
                }

                i++;
            }
        }

        rotate(key, 2, halfSize, 0);
        rotate(key, 3, halfSize, halfSize);
        rotate(key, 4, 0, halfSize);

        mask.debug();

        for (i = 0; i < 4; i++) {
            for (int charIndex = 0; charIndex < blockSize; charIndex++) {
                final int index = (i * blockSize) + charIndex;
                if (index >= charArray.length) {
                    continue;
                }

                final char ch = charArray[index];

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

        for (final String b : field.getMatrix()) {
            if (b == null || b.isEmpty()) {
                continue;
            }

            try {
                for (final Byte bt : b.getBytes("cp866")) {
                    map.merge(Byte.toUnsignedInt(bt), 1, Integer::sum);
                    length++;
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void update() {
        ImGui.begin("Cardano");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }

        ImGui.sameLine();

        if (ImGui.button(showTable ? "Show graph" : "Show table")) {
            showTable = !showTable;
        }

        if (ImGui.button(showMatrix ? "Hide matrix" : "Show matrix")) {
            showMatrix = !showMatrix;
        }

        if (ImGui.button(showText ? "Hide text" : "Show text")) {
            showText = !showText;
        }

        if (showText) {
            ImGui.text(PreparedText.ISRAEL);
            ImGui.end();
            return;
        }

        if (!showMatrix) {
            super.update();
            return;
        }

        final int flags = ImGuiTableFlags.Resizable | ImGuiTableFlags.ScrollY | ImGuiTableFlags.SizingFixedFit;

        ImGui.text("Текст");
        if (ImGui.beginTable("#cardano_table", halfSize * 2, flags, ImGui.getWindowSizeX(), ImGui.getWindowSizeY() / 2)) {
            for (int i = 0; i < halfSize * 2; i++) {
                ImGui.tableSetupColumn(String.valueOf(i + 1));
            }

            ImGui.tableHeadersRow();

            int i = halfSize * 2;
            for (String b : field.getMatrix()) {
                if (b == null || b.isEmpty()) {
                    b = " ";
                }

                if (i == halfSize * 2) {
                    ImGui.tableNextRow(ImGuiTableFlags.None);
                    i = 0;
                }

                ImGui.tableSetColumnIndex(i);
                ImGui.text(b);
                i++;
            }

            ImGui.endTable();
        }

        if (ImGui.beginTable("#mask_table", halfSize * 2, flags, ImGui.getWindowSizeX(), ImGui.getWindowSizeY() / 3, 1000)) {
            for (int i = 0; i < halfSize * 2; i++) {
                ImGui.tableSetupColumn(String.valueOf(i + 1));
            }

            ImGui.tableHeadersRow();

            int i = halfSize * 2;
            for (String b : mask.getMatrix()) {
                if (b == null || b.isEmpty()) {
                    b = " ";
                }

                if (i == halfSize * 2) {
                    ImGui.tableNextRow(ImGuiTableFlags.None);
                    i = 0;
                }

                ImGui.tableSetColumnIndex(i);
                ImGui.text(b);
                i++;
            }

            ImGui.endTable();
        }


        ImGui.end();
    }

    private void rotate(List<Integer> key, int id, int xOffset, int yOffset) {
        rotationMatrix.rotate();
        for (int y = 0; y < halfSize; y++) {
            for (int x = 0; x < halfSize; x++) {
                final int value = rotationMatrix.get(x, y);
                mask.put(x + xOffset, y + yOffset, " ");

                for (int k = 0; k < blockSize; k++) {
                    if (key.get(k) == id && (k + 1) == value) {
                        mask.put(x + xOffset, y + yOffset, String.valueOf(value));
                    }
                }
            }
        }
    }
}
