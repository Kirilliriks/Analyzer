package me.kirillirik.analyzer;

import imgui.ImGui;

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

                if (channel == Channel.ALL) {
                    map.merge((rgb >> 16) & 0xff, 1, Integer::sum);
                    map.merge((rgb >> 8) & 0xff, 1, Integer::sum);
                    map.merge(rgb & 0xff, 1, Integer::sum);
                    length += 3;
                    continue;
                }

                final int value = switch (channel) {
                    case RED -> (rgb >> 16) & 0xff;
                    case GREEN -> (rgb >> 8) & 0xff;
                    case BLUE -> (rgb) & 0xff;
                    case ALL -> throw new IllegalStateException();
                };
                map.merge(value, 1, Integer::sum);

                length += 1;
            }
        }
    }

    @Override
    public void update() {
        ImGui.begin("Window");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }

        ImGui.sameLine();

        if (ImGui.button(showTable ? "Show graph" : "Show table")) {
            showTable = !showTable;
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

        if (ImGui.radioButton("ALL", channel == Channel.ALL)) {
            channel = Channel.ALL;
            analyze();
        }

        super.update();
    }

    public enum Channel {
        RED,
        GREEN,
        BLUE,
        ALL
    }
}
