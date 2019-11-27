package test.grepgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import test.grepgui.model.FileStat;
import test.grepgui.model.LazySingleton;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ResultWindowController implements Initializable {

    @FXML
    private void copyAllClickButton() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<FileStat> filesStat = LazySingleton.getInstance().getFileStat();
        for (FileStat fileStat: filesStat) {
            if (fileStat.getMatch()) {
                System.out.println(fileStat.getFile().getAbsolutePath());
            }
        }
    }
}
