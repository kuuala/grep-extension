package test.kuuala.grepextension.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        String extension = getExtension();
        ResultWindowController controller = new ResultWindowController(locationText.getText(),
                searchText.getText(), extension);
        loader.setController(controller);
        Parent root = loader.load();
        showAndWaitResultModalWindow(root);
    }

    private String getExtension() {
        String extensionTextString = extensionText.getText();
        if (extensionTextString.isBlank()) {
            return "log";
        } else {
            return extensionTextString.substring(extensionTextString.indexOf('.') + 1);
        }
    }

    private void showAndWaitResultModalWindow(Parent parent) {
        Stage dialog = new Stage();
        dialog.setScene(new Scene(parent));
        dialog.setResizable(false);
        dialog.setTitle("Search Result");
        dialog.initOwner(mainForm.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}
