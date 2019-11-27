package test.grepgui.model;

import java.util.ArrayList;
import java.util.List;

public class LazySingleton {

    private static LazySingleton instance;
    final private List<FileStat> matchedFiles = new ArrayList<>();

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

    void addFileStat(FileStat fileStat) {
        matchedFiles.add(fileStat);
    }

    public List<FileStat> getFileStat() {
        return matchedFiles;
    }
}
