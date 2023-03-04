package me.kirillirik.analyzer;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.flag.ImGuiCond;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class TextAnalyzer extends Analyzer {


    private final Map<Character, Integer> map = new HashMap<>();
    private int length = 0;

    public TextAnalyzer() {
        super("test.txt");

        for (int i = 32; i < 128; i++) {
            map.put((char) i, 0);
        }
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
        double entropy = 0;

        ImGui.begin("Window");

        Double[] x = new Double[map.size()];
        Double[] y = new Double[map.size()];
        int count = 0;


        ImPlot.setNextPlotLimitsX(-100, 0, ImGuiCond.FirstUseEver);
        if (ImPlot.beginPlot("Plot", "Characters", "H",
                new ImVec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY()),
                1, ImPlotAxisFlags.NoTickLabels, ImPlotAxisFlags.NoTickLabels)) {

            for (final var entry : map.entrySet()) {
                final double p = (double) entry.getValue() / (double) length;
                final double h = p * (Math.log(p) / Math.log(2.0f));

                entropy -= h;

                final double xPos = (0.5D + (double) count * 5);
                final double yPos = -h;
                x[count] = xPos;
                y[count] = yPos;

                ImPlot.plotText(String.valueOf(entry.getKey()), xPos, -0.01f);

                ImPlot.plotText(String.format("%.3f", yPos), xPos, yPos + 0.1f, true);

                count++;
            }

            ImPlot.plotBars("H", x, y);

            ImPlot.endPlot();
        }
        ImGui.text("Entropy " + String.format("%.3f", entropy));

        ImGui.end();
    }
}
