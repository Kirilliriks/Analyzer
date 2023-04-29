package me.kirillirik.pseudo;

import imgui.ImGui;
import me.kirillirik.analyzer.Analyzer;

public abstract class Generator extends Analyzer {

    protected int next;

    public void generate(int symbols) {
        init();

        map.clear();
        length = 0;

        for (int i = 0; i < symbols; i++) {
            map.merge(random(0, 32), 1, Integer::sum);
            length++;
        }
    }

    public abstract int random(int min, int max);

    protected void init() {
        next = (int) System.currentTimeMillis();
    }

    protected void showResult() {
        if (ImGui.button("50")) {
            generate(50);
        }

        ImGui.sameLine();

        if (ImGui.button("100")) {
            generate(100);
        }

        ImGui.sameLine();

        if (ImGui.button("1000")) {
            generate(1000);
        }
    }

    @Override
    public void update() {
        ImGui.begin("Generator");

        if (ImGui.button("Close")) {
            needClose = true;
        }

        ImGui.sameLine();

        if (ImGui.button(showTable ? "Show graph" : "Show table")) {
            showTable = !showTable;
        }

        showResult();

        super.update();
    }
}
