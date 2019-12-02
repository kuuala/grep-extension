package test.grepgui.model;

import java.io.File;
import java.util.List;

public class FileResult {

    final private String path;
    final private List<Integer> matchList;
    final private int textLength;
    int currentIndex;

    public FileResult(String path, List<Integer> matchList, int textLength) {
        this.path = path;
        this.matchList = matchList;
        this.textLength = textLength;
        currentIndex = 0;
    }

    public String getPath() {
        return path;
    }

    public int getTextLength() {
        return textLength;
    }

    public int getDownPosition() {
        currentIndex = currentIndex + 1 == matchList.size() ? 0 : currentIndex + 1;
        return matchList.get(currentIndex);
    }

    public int getUpPosition() {
        currentIndex = currentIndex == 0 ? matchList.size() - 1 : currentIndex - 1;
        return matchList.get(currentIndex);
    }

    public int getStartPosition() {
        return matchList.get(0);
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
