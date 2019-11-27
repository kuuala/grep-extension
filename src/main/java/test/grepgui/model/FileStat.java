package test.grepgui.model;

import java.io.File;

public class FileStat {
    final private File file;
    final private boolean match;
    final private int startSymbol;

    FileStat(File file, boolean match, int startSymbol) {
        this.file = file;
        this.match = match;
        this.startSymbol = startSymbol;
    }

    public boolean getMatch() {
        return match;
    }

    public File getFile() {
        return file;
    }

    public int getStartSymbol() {
        return startSymbol;
    }
}
