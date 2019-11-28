package test.grepgui.model;

import java.io.File;
import java.util.List;

public class Result {
    final private File file;
    final private List<Integer> matchList;

    Result(File file, List<Integer> matchList) {
        this.file = file;
        this.matchList = matchList;
    }

    public File getFile() {
        return file;
    }

    public List<Integer> getMatchList() {
        return matchList;
    }
}
