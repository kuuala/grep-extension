package test.grepgui.model;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Searcher {

    final private String path;
    final private String text;
    final private String extension;
    final private List<FileTask> tasks;

    public Searcher(String path, String text, String extension) {
        this.path = path;
        this.text = text;
        this.extension = extension;
        tasks = new LinkedList<>();
    }

    private boolean isFileMeet(File file) {
        return file.isFile() && FilenameUtils.getExtension(file.getName()).equals(extension);
    }

    private void traverse(File root, String text, String extension) {
        File[] files = root.listFiles();
        if (files == null)
            return;
        for (File file: files) {
            if (file.isDirectory()) {
                traverse(file, text, extension);
            } else if (isFileMeet(file)) {
                tasks.add(new FileTask(file.getPath(), text));
            }
        }
    }

    public List<FileTask> getTasks() {
        File root = new File(path);
        traverse(root, text, extension);
        return tasks;
    }
}
