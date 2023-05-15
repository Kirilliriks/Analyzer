package me.kirillirik.feist;

import imgui.ImGui;
import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.pseudo.LFSRGenerator;
import me.kirillirik.utils.PreparedText;

import java.nio.charset.Charset;
import java.util.Arrays;

public final class DESAnalyzer extends Analyzer {

    private final int rounds;
    private final byte[] key;
    private byte[] encodedText;
    private byte[] decodedText;
    private boolean showText;

    public DESAnalyzer() {
        this.rounds = 8;
        key = keygen();
    }

    @Override
    public void analyze() {
        byte[] bytes = PreparedText.ISRAEL.getBytes(Charset.forName("cp866"));
        if (bytes.length % 2 != 0) {
            bytes = Arrays.copyOf(bytes, bytes.length + 1);
        }

        des(bytes, key, false);

        encodedText = new byte[bytes.length];
        for (final byte b : bytes) {
            map.merge(Byte.toUnsignedInt(b), 1, Integer::sum);
            encodedText[length++] = (byte) Byte.toUnsignedInt(b);
        }

        des(bytes, key, true);

        decodedText = new byte[bytes.length];

        int locLength = 0;
        for (final byte b : bytes) {
            decodedText[locLength++] = (byte) Byte.toUnsignedInt(b);
        }
    }

    @Override
    public void update() {
        ImGui.begin("DES coder with modes");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }

        ImGui.sameLine();

        if (ImGui.button(showTable ? "Show graph" : "Show table")) {
            showTable = !showTable;
        }

        if (ImGui.button(showText ? "Hide text" : "Show text")) {
            showText = !showText;
        }

        if (showText) {
            ImGui.textColored(255, 0, 0, 255,  "Исходный текст");
            ImGui.spacing();
            ImGui.textWrapped(PreparedText.ISRAEL);
            ImGui.spacing();
            ImGui.textColored(255, 0, 0, 255,  "Зашифрованный текст");
            ImGui.spacing();
            ImGui.textWrapped(new String(encodedText, Charset.forName("cp866")));
            ImGui.spacing();
            ImGui.textColored(255, 0, 0, 255,  "Расшифрованный текст");
            ImGui.spacing();
            ImGui.textWrapped(new String(decodedText, Charset.forName("cp866")));
            ImGui.end();
            return;
        }

        super.update();
    }

    public byte[] keygen() {
        final byte[] array = new byte[(byte) rounds];
        final var generator = new LFSRGenerator();
        for (byte i = 0; i < (byte) rounds; i++) {
            array[i] = (byte) generator.random(0, 255);
        }
        return array;

    }

    public void des(byte[] byteArray, byte[] key, boolean reverse) {
        if (byteArray.length % 2 != 0) {
            throw new RuntimeException("Check this");
        }

        for (int i = 0; i < byteArray.length - 1; i += 2) {     //Разбиваем на левый и правый блок
            byte left = byteArray[i];
            byte right = byteArray[i + 1];

            int ki = reverse ? rounds - 1 : 0;

            for (int j = 0; j < rounds; j++) {
                if (j == rounds - 1) {
                    final byte x = (byte) (key[ki] ^ right);
                    left ^= byteFunction(x);
                    continue;
                }

                final byte x = (byte) (key[ki] ^ right);
                left ^= byteFunction(x);

                final byte temp = left;
                left = right;
                right = temp;

                if (reverse) {
                    ki--;
                } else {
                    ki++;
                }
            }

            byteArray[i] = left;
            byteArray[i + 1] = right;
        }
    }

    private byte byteFunction(byte a) {
        return (byte) (((a << 17)) ^ (((a & 0x0F0F0F0F) >> 4) | (~a & 0xF0F0F0F0)));
    }
}
