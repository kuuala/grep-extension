package test.kuuala.grepextension.model;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
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
        tasks = new ArrayList<>();
    }

    private boolean isFileMatch(File file) {
        return file.isFile() && FilenameUtils.getExtension(file.getName()).equals(extension);
    }

    public List<FileTask> getTasks() {
        File root = new File(path);
        if (root.exists()) {
            if (text.isEmpty()) {
                System.err.println("Search text is empty");
            } else {
                traverse(root, text);
            }
        } else {
            System.err.println(String.format("Directory %s doesn't exists", path));
        }
        return tasks;
    }

    private void traverse(File root, String text) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    traverse(file, text);
                } else if (isFileMatch(file)) {
                    tasks.add(new FileTask(file.getPath(), text));
                }
            }
        }
    }
}
