package me.kirillirik;

import imgui.ImGui;
import me.kirillirik.analyzer.ImageAnalyzer;
import me.kirillirik.analyzer.TextAnalyzer;
import me.kirillirik.cardano.Cardano;
import me.kirillirik.feist.Feist;
import me.kirillirik.feist.DESAnalyzer;
import me.kirillirik.last.DiffiHelm;
import me.kirillirik.last.RSA;
import me.kirillirik.other.BitsCoder;
import me.kirillirik.other.Caesar;
import me.kirillirik.other.StringCoder;
import me.kirillirik.pseudo.BBSGenerator;
import me.kirillirik.pseudo.Generator;
import me.kirillirik.pseudo.LFSRGenerator;
import me.kirillirik.pseudo.LGGGenerator;

public final class Selector {

    private Widget widget = null;
    private Generator generator = null;

    public void update() {
        if (widget != null) {
            widget.update();

            if (widget.isNeedClose()) {
                widget = null;
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

        if (ImGui.button("RSA")) {
            widget = new RSA();
        }

        if (ImGui.button("Diffie Helman")) {
            widget = new DiffiHelm();
        }

        if (ImGui.button("DES with modes")) {
            widget = new DESAnalyzer();
        }

        if (ImGui.button("Feist")) {
            widget = new Feist();
        }

        if (ImGui.button("Cardano")) {
            widget = new Cardano();
        }

        if (ImGui.button("Caesar coder")) {
            widget = new Caesar();
        }

        if (ImGui.button("String coder")) {
            widget = new StringCoder();
        }

        if (ImGui.button("Bits coder")) {
            widget = new BitsCoder();
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
            widget = new TextAnalyzer();
        }

        if (ImGui.button("Image analyzer")) {
            widget = new ImageAnalyzer();
        }

        if (widget != null) {
            widget.analyze();
        }

        ImGui.end();
    }
}
