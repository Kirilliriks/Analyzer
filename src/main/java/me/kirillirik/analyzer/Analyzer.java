package me.kirillirik.analyzer;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.flag.ImGuiCond;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Analyzer {

    public static final Logger LOGGER = Logger.getLogger("Analyzer");

    protected final String filePath;
    protected final Map<Integer, Integer> map = new HashMap<>();
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

        for (int i = 0; i < 256; i++) {
            map.put(i, 0);
        }
    }

    public abstract void analyze();

    public void update() {
        entropy = 0;

        ImPlot.setNextPlotLimitsX(-100, 0, ImGuiCond.FirstUseEver);
        if (ImPlot.beginPlot("Plot", "Characters", "Probabilities (Amount)",
                new ImVec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY() - 100),
                1, ImPlotAxisFlags.NoTickLabels, ImPlotAxisFlags.NoTickLabels)) {

            final var x = new Double[map.size()];
            final var y = new Double[map.size()];
            int count = 0;

            for (final var entry : map.entrySet()) {
                final double p = (double) entry.getValue() / (double) length;

                double h = 0;
                if (p > 0) {
                    h = -(p * (Math.log(p) / Math.log(2.0f)));
                    entropy += h;
                }

                final double xPos = (0.5D + (double) count * 5);
                final double yPos = h;
                x[count] = xPos;
                y[count] = yPos;

                try {
                    ImPlot.plotText(count + " (" + new String(new byte[]{entry.getKey().byteValue()}, "cp866") + ")", xPos, -0.01f);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                if (yPos != 0) {
                    ImPlot.plotText(String.format("%.3f", yPos) + "   (" + entry.getValue() + ")", xPos, yPos + 0.1f, true);
                }

                count++;
            }

            ImPlot.plotBars("Probabilities", x, y);

            ImPlot.endPlot();
        }

        ImGui.text("Entropy " + (float) entropy);

        ImGui.end();
    }

    public boolean isNeedClose() {
        return needClose;
    }
}
