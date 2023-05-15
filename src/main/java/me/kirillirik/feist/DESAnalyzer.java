package me.kirillirik.feist;

import imgui.ImGui;
import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.utils.PreparedText;

import java.nio.charset.Charset;

public final class DESAnalyzer extends Analyzer {

    private final DES des = new DES();
    private byte[] encodedText;
    private byte[] decodedText;
    private boolean showText;

    @Override
    public void analyze() {
        final String key = "random_key";

        String result = des.crypt(key, DESUtils.utfToBin(PreparedText.ISRAEL), true);
        byte[] bytes = DESUtils.binToUTF(result).getBytes(Charset.forName("cp866"));

        encodedText = new byte[bytes.length];
        for (final byte b : bytes) {
            map.merge(Byte.toUnsignedInt(b), 1, Integer::sum);
            encodedText[length++] = (byte) Byte.toUnsignedInt(b);
        }

        result = des.crypt(key, result, false);
        bytes = DESUtils.binToUTF(result).getBytes(Charset.forName("cp866"));

        int locLength = 0;
        decodedText = new byte[bytes.length];
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

        if (ImGui.button("ECB")) {
            clearMap();
            des.setMode(DES.Mode.ECB);
            analyze();
        }

        if (ImGui.button("CBC")) {
            clearMap();
            des.setMode(DES.Mode.CBC);
            analyze();
        }

        if (ImGui.button("CFB")) {
            clearMap();
            des.setMode(DES.Mode.CFB);
            analyze();
        }

        if (ImGui.button("OFB")) {
            clearMap();
            des.setMode(DES.Mode.OFB);
            analyze();
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
}
