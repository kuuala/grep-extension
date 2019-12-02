package test.grepgui.controllers;

import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import org.apache.commons.io.IOUtils;
import test.grepgui.model.FileResult;
import test.grepgui.model.FileTask;
import test.grepgui.model.Searcher;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultWindowController implements Initializable {

    @FXML
    private TreeView<FileResult> treeView;
    @FXML
    private TabPane tabPane;

    final private String path;
    final private String text;
    final private String extension;

    ResultWindowController(String path, String text, String extension) {
        this.path = path;
        this.text = text;
        this.extension = extension;
    }

    //todo next match
    @FXML
    private void upClickButton() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
    }

    //todo previous match
    @FXML
    private void downClickButton() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            TextArea textArea = (TextArea) tab.getContent();
        }
    }

    private void setClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(string);
        clipboard.setContent(clipboardContent);
    }

    @FXML
    private void copyAllClickButton() {
        SingleSelectionModel<Tab> selectionModel =  tabPane.getSelectionModel();
        TextArea area =  (TextArea) selectionModel.getSelectedItem().getContent();
        area.selectAll();
        setClipboard(area.getText());
    }

    private Tab createTab(File file, String content) {
        Label tabLabel = new Label(file.getName());
        TextArea tabText = new TextArea();
        tabText.appendText(content);
        tabText.setEditable(false);
        Tab tab = new Tab();
        tab.setContent(tabText);
        tab.setGraphic(tabLabel);
        return tab;
    }

    private void addTabToTabPane(Tab tab) {
        tabPane.getTabs().add(tab);
        SingleSelectionModel<Tab> selectionModel =  tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    private void runTasks(List<FileTask> tasks) {
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        for (FileTask task: tasks) {
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    workerStateEvent -> {
                        Optional<FileResult> result = task.getValue();
                        result.ifPresent(this::addElementOnTree);
                    });
            service.submit(task);
        }
        service.shutdown();
    }

    //todo replace searcher to synchronized query of tasks
    private void delegateSolving() {
        Searcher searcher = new Searcher(path, text, extension);
        List<FileTask> tasks = searcher.getTasks();
        runTasks(tasks);
    }

    private void addElementOnTree(FileResult result) {
        synchronized (treeView) {
            if (treeView.getRoot() == null) {
                treeView.setRoot(new TreeItem<>(new FileResult(path, null)));
            }
            TreeItem<FileResult> currentItem = treeView.getRoot();
            String lessPath = result.getPath().substring(path.length() + 1);
            String[] split = lessPath.split(File.separator);
            for (String elem: split) {
                ObservableList<TreeItem<FileResult>> kids = currentItem.getChildren();
                ObservableList<TreeItem<FileResult>> root = kids.filtered(x -> x.getValue().getFileName().equals(elem));
                if (root.size() == 0) {
                    TreeItem<FileResult> item = new TreeItem<>(result);
                    kids.add(item);
                    currentItem = item;
                } else {
                    currentItem = root.get(0);
                }
            }
        }
    }

    //todo select 0 match
    @FXML
    private void treeViewMouseClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TreeItem<FileResult> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem.getClass() == TreeItem.class && selectedItem.isLeaf()) {
                File file = new File(selectedItem.getValue().getPath());
                StringBuilder content = new StringBuilder();
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    content.append(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Tab tab = createTab(file, content.toString());
                addTabToTabPane(tab);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (text.isEmpty())
            return;
        Runnable solver = this::delegateSolving;
        Thread thread = new Thread(solver);
        thread.start();
    }
}
