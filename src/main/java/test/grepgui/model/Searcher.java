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
                TaskQueue tasks = TaskQueue.getInstance();
                tasks.addTask(new Task(file, text));
            }
        }
    }

    public void doRecurseTraverse() {
        File root = new File(path);
        traverse(root, text, extension);
    }
}
