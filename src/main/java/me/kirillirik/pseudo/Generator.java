package me.kirillirik.pseudo;

import imgui.ImGui;

public abstract class Generator {

    protected final StringBuffer buffer;
    protected boolean needClose = false;

    protected int seed;

    public Generator() {
        this.buffer = new StringBuffer();
    }

    public abstract void generate(int symbols);

    protected abstract int random(int min, int max);

    protected void init() {

    }

    protected void showResult() {
        if (ImGui.button("Close")) {
            needClose = true;
        }

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

        ImGui.textWrapped(buffer.toString());
    }

    public void update() {
        ImGui.begin("Generator");
        showResult();
        ImGui.end();
    }

    public boolean isNeedClose() {
        return needClose;
    }
}
