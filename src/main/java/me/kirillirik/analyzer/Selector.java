package me.kirillirik.analyzer;

import imgui.ImGui;

public final class Selector {

    private Analyzer analyzer = null;

    public void update() {
        if (analyzer != null) {
            analyzer.update();

            if (analyzer.isNeedClose()) {
                analyzer = null;
            }

            return;
        }

        ImGui.begin("Select analyzer");

        if (ImGui.button("Text")) {
            analyzer = new TextAnalyzer();
        }

        if (ImGui.button("Image")) {
            analyzer = new ImageAnalyzer();
        }

        if (analyzer != null) {
            analyzer.analyze();
        }

        ImGui.end();
    }
}
