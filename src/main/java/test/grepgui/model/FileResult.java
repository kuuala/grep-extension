package test.grepgui.model;

import java.io.File;
import java.util.List;

public class FileResult {

    final private String path;
    final private List<Integer> matchList;
    private int currentPosition;

    public FileResult(String path, List<Integer> matchList) {
        this.path = path;
        this.matchList = matchList;
        currentPosition = 0;
    }

    public String getPath() {
        return path;
    }

    public List<Integer> getMatchList() {
        return matchList;
    }

    public String getFileName() {
        String[] splits = path.split(File.separator);
        return splits[splits.length - 1];
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
