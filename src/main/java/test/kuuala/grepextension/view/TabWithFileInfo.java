package test.kuuala.grepextension.view;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.apache.commons.io.IOUtils;
import test.kuuala.grepextension.model.FileResult;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class TabWithFileInfo extends Tab {

    private FileResult fileResult;
    private File file;

    public TabWithFileInfo(FileResult fileResult) {
        this.fileResult = fileResult;
        file = new File(fileResult.getPath());
        TextArea tabText = new TextArea();
        StringBuilder content = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            content.append(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabText.appendText(content.toString());
        tabText.setEditable(false);
        setContent(tabText);
        setGraphic(new Label(file.getAbsolutePath()));
        this.setOnClosed((event -> fileResult.setCurrentIndex(0)));
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public boolean equals(Object tabWithFileInfo) {
        if (this == tabWithFileInfo) {
            return true;
        }
        if (tabWithFileInfo == null) {
            return false;
        }
        if (tabWithFileInfo instanceof TabWithFileInfo) {
            return file.equals(((TabWithFileInfo)tabWithFileInfo).file);
        }
        return false;
    }

    public FileResult getFileResult() {
        return fileResult;
    }
}
