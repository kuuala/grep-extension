package test.grepgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import test.grepgui.model.Searcher;

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
    private void searchButtonClick() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResultWindow.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
        mainForm.getScene().getWindow().hide();
        String extensionFieldText = extensionText.getText();
        String extension = extensionFieldText.length() == 0 ? "log" : extensionFieldText;
        Searcher searcher = new Searcher(locationText.getText(), searchText.getText(), extension);
        searcher.doRecurseTraverse();
    }
}
