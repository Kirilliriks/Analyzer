package me.kirillirik.analyzer;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.extension.implot.flag.ImPlotFlags;
import imgui.extension.implot.flag.ImPlotYAxis;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiPopupFlags;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImageAnalyzer extends Analyzer {

    private BufferedImage image;
    private final Map<Character, Integer> map = new HashMap<>();
    private final List<Pixel> pixels = new ArrayList<>();
    private int length = 0;

    public ImageAnalyzer() {
        super("test.bmp");

        for (int i = 32; i < 128; i++) {
            map.put((char) i, 0);
        }
    }

    @Override
    public void analyze() {
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int rgb = image.getRGB(x, y);
                final int a = (rgb >> 24) & 0xff;
                final int r = (rgb >> 16) & 0xff;
                final int g = (rgb >> 8) & 0xff;
                final int b = rgb & 0xff;

                pixels.add(new Pixel(r, g, b, a));
                //map.merge((char) a, 1, Integer::sum);
                //length++;
                map.merge((char) r, 1, Integer::sum);
                length++;
                map.merge((char) g, 1, Integer::sum);
                length++;
                map.merge((char) b, 1, Integer::sum);
                length++;
            }
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

            ImPlot.plotBars("H", x, y);

            ImPlot.endPlot();
        }
        ImGui.text("Entropy " + String.format("%.3f", entropy));

        ImGui.end();
    }

    public record Pixel(int r, int g, int b, int a) {
    }
}
