package test.grepgui.model;

import javafx.concurrent.Task;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class FileTask extends Task<Optional<FileResult>> {
    final private File file;
    final private String text;

    FileTask(File file, String text) {
        this.file = file;
        this.text = text;
    }

    private List<Integer> getIndexOfAllMatches(String string) {
        List<Integer> matches = new LinkedList<>();
        int i = string.indexOf(text);
        while (i >= 0) {
            matches.add(i);
            i = string.indexOf(text, i + text.length());
        }
        return matches;
    }

    private String getFileContent() {
        StringBuilder builder = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            builder.append(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Override
    protected Optional<FileResult> call() {
        String content = getFileContent();
        List<Integer> matches = getIndexOfAllMatches(content);
        return matches.isEmpty() ? Optional.empty() : Optional.of(new FileResult(file, matches));
    }
}
