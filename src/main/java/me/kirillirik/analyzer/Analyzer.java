package me.kirillirik.analyzer;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableFlags;

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
    protected boolean showTable = false;

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


        if (!showTable) {
            ImPlot.setNextPlotLimitsX(-100, 100, ImGuiCond.FirstUseEver);
            if (ImPlot.beginPlot("Plot", "Characters", "Probabilities (Amount)",
                    new ImVec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY() - 100),
                    1, ImPlotAxisFlags.None, ImPlotAxisFlags.NoTickLabels)) {

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

                    final double xPos = count;
                    final double yPos = entry.getValue();
                    x[count] = xPos;
                    y[count] = yPos;

                    try {
                        ImPlot.plotText(count + " (" + new String(new byte[]{entry.getKey().byteValue()}, "cp866") + ")", xPos, -0.01f);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    if (yPos != 0) {
                        ImPlot.plotText(String.format("%.3f", h) + "   (" + entry.getValue() + ")", xPos, yPos + 0.1f, true);
                    }

                    count++;
                }

                ImPlot.plotBars("Probabilities", x, y);

                ImPlot.endPlot();
            }
        } else {

            final int flags = ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable |
                    ImGuiTableFlags.RowBg | ImGuiTableFlags.Borders | ImGuiTableFlags.NoBordersInBody | ImGuiTableFlags.ScrollY;

            if (ImGui.beginTable("#table", 5, flags)) {
                ImGui.tableSetupColumn("Byte");
                ImGui.tableSetupColumn("Char");
                ImGui.tableSetupColumn("Count");
                ImGui.tableSetupColumn("H");
                ImGui.tableSetupColumn("Chance");

                ImGui.tableHeadersRow();

                for (final var entry : map.entrySet()) {
                    final int charCount = entry.getValue();
                    final double p = (double) charCount / (double) length;

                    double h = 0;
                    if (p > 0) {
                        h = -(p * (Math.log(p) / Math.log(2.0f)));
                        entropy += h;
                    }

                    final String ch;
                    try {
                        ch = new String(new byte[]{entry.getKey().byteValue()}, "cp866");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    ImGui.tableNextRow(ImGuiTableFlags.None, 10);

                    ImGui.tableSetColumnIndex(0);
                    ImGui.text(String.valueOf(entry.getKey()));

                    ImGui.tableSetColumnIndex(1);
                    ImGui.text(ch);

                    if (charCount != 0) {
                        ImGui.tableSetColumnIndex(2);
                        ImGui.text(String.valueOf(charCount));
                    }

                    if (h != 0) {
                        ImGui.tableSetColumnIndex(3);
                        ImGui.text(String.valueOf(h));
                    }

                    if (p != 0) {
                        ImGui.tableSetColumnIndex(4);
                        ImGui.text(p * 100.0f + "%");
                    }
                }

                ImGui.endTable();
            }
        }

        ImGui.text("Entropy " + (float) entropy);
        ImGui.end();
    }

    public boolean isNeedClose() {
        return needClose;
    }
}
