package me.kirillirik.analyzer;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.flag.ImGuiCond;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Analyzer {

    public static final Logger LOGGER = Logger.getLogger("Analyzer");

    protected final String filePath;
    protected final Map<Character, Integer> map = new HashMap<>();
    protected int length = 0;
    protected double entropy = 0;
    protected boolean needClose = false;

    public Analyzer(String filePath) {
        this.filePath = filePath;

        clearMap();
    }

    protected void clearMap() {
        map.clear();
        length = 0;

        for (int i = 32; i < 128; i++) {
            map.put((char) i, 0);
        }
    }

    public abstract void analyze();

    public void update() {
        entropy = 0;

        final var x = new Double[map.size()];
        final var y = new Double[map.size()];
        int count = 0;

        ImPlot.setNextPlotLimitsX(-100, 0, ImGuiCond.FirstUseEver);
        if (ImPlot.beginPlot("Plot", "Characters", "H",
                new ImVec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY() - 100),
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

            ImPlot.plotBars("Probabilities", x, y);

            ImPlot.endPlot();
        }
        ImGui.text("Entropy " + String.format("%.3f", entropy));

        ImGui.end();
    }

    public boolean isNeedClose() {
        return needClose;
    }
}
