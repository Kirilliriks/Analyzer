package me.kirillirik.analyzer;

import imgui.ImGui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TextAnalyzer extends Analyzer {

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
                System.out.println(" " + Byte.toUnsignedInt(b));
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

        super.update();
    }
}
