package me.kirillirik;

import imgui.ImGui;
import me.kirillirik.analyzer.Analyzer;
import me.kirillirik.analyzer.ImageAnalyzer;
import me.kirillirik.analyzer.TextAnalyzer;
import me.kirillirik.cardano.Cardano;
import me.kirillirik.other.BitsCoder;
import me.kirillirik.other.Caesar;
import me.kirillirik.other.StringCoder;
import me.kirillirik.pseudo.BBSGenerator;
import me.kirillirik.pseudo.Generator;
import me.kirillirik.pseudo.LFSRGenerator;
import me.kirillirik.pseudo.LGGGenerator;

public final class Selector {

    private Analyzer analyzer = null;
    private Generator generator = null;

    public void update() {
        if (analyzer != null) {
            analyzer.update();

            if (analyzer.isNeedClose()) {
                analyzer = null;
            }

            return;
        }

        if (generator != null) {
            generator.update();

            if (generator.isNeedClose()) {
                generator = null;
            }

            return;
        }

        ImGui.begin("Select tool");

        if (ImGui.button("Cardano")) {
            analyzer = new Cardano();
        }


        if (ImGui.button("Caesar coder")) {
            analyzer = new Caesar();
        }

        if (ImGui.button("String coder")) {
            analyzer = new StringCoder();
        }

        if (ImGui.button("Bits coder")) {
            analyzer = new BitsCoder();
        }

        if (ImGui.button("Generator LGG")) {
            generator = new LGGGenerator();
            generator.generate(50);
        }

        if (ImGui.button("Generator BBS")) {
            generator = new BBSGenerator();
            generator.generate(50);
        }

        if (ImGui.button("Generator LFSR")) {
            generator = new LFSRGenerator();
            generator.generate(50);
        }

        if (ImGui.button("Text analyzer")) {
            analyzer = new TextAnalyzer();
        }

        if (ImGui.button("Image analyzer")) {
            analyzer = new ImageAnalyzer();
        }

        if (analyzer != null) {
            analyzer.analyze();
        }

        ImGui.end();
    }
}
