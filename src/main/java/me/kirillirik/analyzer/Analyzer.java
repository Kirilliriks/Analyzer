package me.kirillirik.analyzer;

import java.util.logging.Logger;

public abstract class Analyzer {

    public static final Logger LOGGER = Logger.getLogger("Analyzer");

    protected final String filePath;

    public Analyzer(String filePath) {
        this.filePath = filePath;
    }

    public abstract void analyze();

    public abstract void update();
}
