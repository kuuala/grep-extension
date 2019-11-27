package test.grepgui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class MainWindowController {

    @FXML
    private TextField locationText;
    @FXML
    private TextArea searchText;
    @FXML
    private TextField extensionText;
    @FXML
    private VBox mainForm;

    @FXML
    private void browseButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(mainForm.getScene().getWindow());
        if (dir != null) {
            locationText.setText(dir.getAbsolutePath());
        }
    }

    @FXML
    private void clearButtonClick() {
        locationText.clear();
        searchText.clear();
        extensionText.clear();
    }

    @FXML
    private void searchButtonClick() {

    }
}
