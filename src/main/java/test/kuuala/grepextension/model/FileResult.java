package test.kuuala.grepextension.model;

import java.io.File;
import java.util.List;

public class FileResult {

    final private String path;
    final private List<Integer> matchList;
    final private int textLength;
    private int currentIndex;

    public FileResult(String path, List<Integer> matchList, int textLength) {
        this.path = path;
        this.matchList = matchList;
        this.textLength = textLength;
        currentIndex = 0;
    }

    @Override
    public String toString() {
        return getFileName();
    }

    public void setCurrentIndex(int index) {
        if (0 <= index && index < matchList.size()) {
            currentIndex = index;
        }
    }

    public String getPath() {
        return path;
    }

    public void setDownPosition() {
        currentIndex = currentIndex + 1 == matchList.size() ? 0 : currentIndex + 1;
    }

    public void setUpPosition() {
        currentIndex = currentIndex == 0 ? matchList.size() - 1 : currentIndex - 1;
    }

    public void setStartPosition() {
        currentIndex = 0;
    }

    public String getFileName() {
        String[] splits = path.split(File.separator);
        return splits[splits.length - 1];
    }

    public int getStartPosition() {
        return matchList.get(currentIndex);
    }

    public int getEndPosition() {
        return matchList.get(currentIndex) + textLength;
    }
}
