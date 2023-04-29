package me.kirillirik.other;

import imgui.ImGui;
import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.pseudo.LFSRGenerator;
import me.kirillirik.utils.PreparedText;

import java.nio.charset.Charset;

public final class BitsCoder extends Analyzer {

    private byte[] encodedText;
    private boolean showText;

    @Override
    public void analyze() {
        byte[] bytes = PreparedText.ISRAEL.getBytes(Charset.forName("cp866"));

        encodedText = new byte[bytes.length];

        final LFSRGenerator generator = new LFSRGenerator(8);
        int num = 0;

        generator.random(0, 255);
        final var bits = generator.getBits();

        for (int i = 0; i < 8; i++) {
            num |= (bits[i] ? 1 : 0) << i;
        }

        for (byte b : bytes) {

            b += (b + num) % 255;
            map.merge(Byte.toUnsignedInt(b), 1, Integer::sum);

            encodedText[length++] = (byte) Byte.toUnsignedInt(b);
        }
    }

    @Override
    public void update() {
        ImGui.begin("Bits encoder");

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
            ImGui.textWrapped(PreparedText.ISRAEL);
            ImGui.spacing();
            ImGui.textWrapped(new String(encodedText, Charset.forName("cp866")));
            ImGui.end();
            return;
        }

        super.update();
    }
}
