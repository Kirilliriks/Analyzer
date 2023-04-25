package me.kirillirik.analyzer;

import imgui.ImGui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextAnalyzer extends Analyzer {

    public TextAnalyzer() {
        super("test.txt");
    }

    @Override
    public void analyze() {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            for (byte b : bytes) {
                map.merge(Byte.toUnsignedInt(b), 1, Integer::sum);
                length++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update() {
        ImGui.begin("Работа?");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }

        ImGui.sameLine();

        if (ImGui.button(showTable ? "Show graph" : "Show table")) {
            showTable = !showTable;
        }

        super.update();
    }
}
