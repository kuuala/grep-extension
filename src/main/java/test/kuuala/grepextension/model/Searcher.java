package test.kuuala.grepextension.model;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class Searcher {

    final private String path;
    final private String text;
    final private String extension;
    final private TaskQueue tasks;

    public Searcher(String path, String text, String extension, TaskQueue tasks) {
        this.path = path;
        this.text = text;
        this.extension = extension;
        this.tasks = tasks;
    }

    public void fillTaskQueue() {
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
    }

    private boolean isFileMatch(File file) {
        return file.isFile() && FilenameUtils.getExtension(file.getName()).equals(extension);
    }

    private void traverse(File root, String text) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    traverse(file, text);
                } else if (isFileMatch(file)) {
                    tasks.addTask(new FileTask(file.getPath(), text));
                }
            }
        }
    }
}
