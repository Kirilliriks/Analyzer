package me.kirillirik.pseudo;

import imgui.ImGui;
import me.kirillirik.analyzer.Analyzer;

public abstract class Generator extends Analyzer {

    protected int next;

    public Generator() {
        super("");
    }

    public void generate(int symbols) {
        init();

        map.clear();
        length = 0;

        for (int i = 0; i < symbols; i++) {
            final int number = random(0, 32);
            System.out.println("I " + number + " C " + number);
            map.merge(number, 1, Integer::sum);
            length++;
        }
    }

    protected abstract int random(int min, int max);

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
