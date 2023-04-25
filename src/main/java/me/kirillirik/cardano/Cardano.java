package me.kirillirik.cardano;

import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.utils.NumericUtils;

import java.io.UnsupportedEncodingException;
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

    private boolean showMatrix;

    public Cardano() {
        super("");

        text = """
                В Израиле сходятся границы трёх растительных поясов: средиземноморского, ирано-туранского и сахаро-синдского. В стране насчитывается примерно 2600 видов растений (250 — эндемичные) из 700 родов, входящих в 115 семейств[212].
                 К моменту обретения Израилем независимости на его территории Еврейским национальным фондом было высажено 4,5 млн деревьев, а к XXI веку их в стране насчитывается более 200 миллионов[234].
                  Из 6 % территории страны, покрытых лесом, около ⅔ являются искусственными насаждениями[176]. В лесопосадках чаще всего сажают алеппскую сосну, акацию и эвкалипт,
                   в то время как для озеленения населённых пунктов используют кипарис, казуарину, фикус, тамариск, олеандр и фисташку[212]. Природный лес сохранился в горных районах — в Галилее, Самарии, Иудейских горах и на кряже Кармель;
                    естественная растительность также сохранилась в пустынных районах[48].
                                
                Фауна Израиля насчитывает более 100 видов млекопитающих[176], свыше 600 видов птиц[212], около 100 видов рептилий[176],
                 в том числе 30 видов змей[212], и около десятка видов амфибий[235], а также тысячи видов насекомых, включая более ста видов бабочек[48].
                  Более половины видов птиц постоянно обитают в стране, остальные являются перелётными. В прибрежных водах Израиля встречаются дельфины и дюгони[212].
                                
                В общей сложности, в Израиле создано около 400 заповедников и национальных парков, в совокупности занимающих порядка четверти территории страны[236].
                 В 1963 году под эгидой министерства главы правительства в Израиле сформировано Управление заповедниками, вместе с Обществом по защите окружающей среды ведущее работы по охране и восстановлению естественных ландшафтов[237].
                """;
        charArray = text.replaceAll("\\s", "").toCharArray();
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
