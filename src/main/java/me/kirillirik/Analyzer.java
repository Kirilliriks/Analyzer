package me.kirillirik;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Analyzer {

    private final String filePath;
    private final Map<Character, Integer> map = new HashMap<>();
    private int length = 0;

    public Analyzer(String filePath) {
        this.filePath = filePath;
    }

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

        System.out.println("File length " + length);

        double entropy = 0;

        for (final var entry : map.entrySet()) {
            final double p = (double) entry.getValue() / (double) length;
            final double h = p * (Math.log(p) / Math.log(2.0f));
            System.out.println("Symb " + entry.getKey() + " count " + entry.getValue() + " h " + -h);

            entropy -= h;
        }

        System.out.println("Entropy " + entropy);
    }

    public void update() {
        double entropy = 0;


        ImGui.begin("Window");

        Double []x = new Double[map.size()];
        Double []y = new Double[map.size()];
        int count = 0;

        if (ImPlot.beginPlot("Plot", "Characters", "H", new ImVec2(1000, 700))) {
            for (final var entry : map.entrySet()) {
                final double p = (double) entry.getValue() / (double) length;
                final double h = p * (Math.log(p) / Math.log(2.0f));

                entropy -= h;
                x[count] = 0.5 + (double) count;
                y[count] = -h;

                ImPlot.plotText(String.valueOf(entry.getKey()), 0.5 + count, -0.1f);

                ImPlot.plotText(String.format("%.3f", -h), 0.5 + count, -h + 0.1f, true);

                count++;
            }

            ImPlot.plotBars("H", x, y);

            ImPlot.endPlot();
        }
        ImGui.text("Entropy " + String.format("%.3f", entropy));

        ImGui.end();
    }
}
