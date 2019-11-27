package test.grepgui.model;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class Searcher {

    final private String path;
    final private String text;
    final private String extension;

    public Searcher(String path, String text, String extension) {
        this.path = path;
        this.text = text;
        this.extension = extension;
    }

    private void traverse(File root, String text, String extension) {
        File[] files = root.listFiles();
        if (files == null)
            return;
        for (File file: files) {
            if (file.isDirectory()) {
                traverse(file, text, extension);
            } else if (file.isFile() && FilenameUtils.getExtension(file.getName()).equals(extension)) {
                System.out.println(file.getAbsolutePath());
                FileScanner fileScanner = new FileScanner(file);
                FileStat fileStat = fileScanner.doScan();
                if (fileStat.getMatch()) {
                    LazySingleton single = LazySingleton.getInstance();
                    single.addFileStat(fileStat);
                }
            }
        }
    }

    public void doSome() {
        File root = new File(path);
        traverse(root, text, extension);
    }
}
