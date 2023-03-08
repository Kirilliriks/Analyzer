package me.kirillirik.analyzer;

import imgui.ImGui;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class TextAnalyzer extends Analyzer {

    public TextAnalyzer() {
        super("test.txt");
    }

    @Override
    public void analyze() {
        try (final BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while (line != null) {

                for (final char ch : line.toCharArray()) {
                    map.merge(ch, 1, Integer::sum);
                    length++;
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        ImGui.begin("Window");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }

        super.update();
    }
}
