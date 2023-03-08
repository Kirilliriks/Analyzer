package me.kirillirik.analyzer;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.flag.ImGuiCond;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageAnalyzer extends Analyzer {

    private Channel channel = Channel.RED;

    public ImageAnalyzer() {
        super("test.bmp");
    }

    @Override
    public void analyze() {
        clearMap();

        BufferedImage image;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int rgb = image.getRGB(x, y);
                //final int a = (rgb >> 24) & 0xff;

                final int value = switch (channel) {
                    case RED ->   (rgb >> 16) & 0xff;
                    case GREEN -> (rgb >> 8 ) & 0xff;
                    case BLUE ->  (rgb      ) & 0xff;
                };
                map.merge((char) value, 1, Integer::sum);

                length += 3;
            }
        }
    }

    @Override
    public void update() {
        ImGui.begin("Window");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }


        if (ImGui.radioButton("RED", channel == Channel.RED)) {
            channel = Channel.RED;
            analyze();
        }

        ImGui.sameLine();

        if (ImGui.radioButton("GREEN", channel == Channel.GREEN)) {
            channel = Channel.GREEN;
            analyze();
        }

        ImGui.sameLine();

        if (ImGui.radioButton("BLUE", channel == Channel.BLUE)) {
            channel = Channel.BLUE;
            analyze();
        }

        super.update();
    }

    public enum Channel {
        RED,
        GREEN,
        BLUE
    }
}
