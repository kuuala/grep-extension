package test.grepgui.model;

import java.io.File;

class Task {
    final private File file;
    final private String text;

    Task(File file, String text) {
        this.file = file;
        this.text = text;
    }

    File getFile() {
        return file;
    }
}
