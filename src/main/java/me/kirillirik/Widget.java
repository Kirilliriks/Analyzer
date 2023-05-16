package me.kirillirik;

public abstract class Widget {

    protected boolean needClose = false;

    public abstract void update();
    public abstract void analyze();

    public boolean isNeedClose() {
        return needClose;
    }
}
