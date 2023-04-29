package me.kirillirik.other;

import imgui.ImGui;
import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.utils.PreparedText;

import java.nio.charset.Charset;

public final class StringCoder extends Analyzer {

    private final String key = "По хозяину и собаке честь";
    private byte[] encodedText;
    private boolean showText;

    @Override
    public void analyze() {
        byte[] bytes = PreparedText.ISRAEL.getBytes(Charset.forName("cp866"));

        encodedText = new byte[bytes.length];
        for (byte b : bytes) {
            b += (b + key.charAt(length % key.length())) % 255;
            map.merge(Byte.toUnsignedInt(b), 1, Integer::sum);

            encodedText[length++] = (byte) Byte.toUnsignedInt(b);
        }
    }

    @Override
    public void update() {
        ImGui.begin("Second encoder");

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
