package test.grepgui.model;

import java.io.File;

class FileScanner {

    final private File file;

    FileScanner(File file) {
        this.file = file;
    }

    FileStat doScan() {
        return new FileStat(file, true, 0);
    }
}
