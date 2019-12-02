package test.grepgui.controllers;

import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import test.grepgui.model.FileResult;
import test.grepgui.model.FileTask;
import test.grepgui.model.Searcher;
import test.grepgui.model.TabWithFileInfo;

import java.io.File;
import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (text.isEmpty())
            return;
        Runnable solver = this::delegateSolving;
        Thread thread = new Thread(solver);
        thread.start();
    }

    @FXML
    private void upClickButton() {
        selectText(SearchDirection.UP);
    }

    @FXML
    private void downClickButton() {
        selectText(SearchDirection.DOWN);
    }

    @FXML
    private void copyAllClickButton() {
        SingleSelectionModel<Tab> selectionModel =  tabPane.getSelectionModel();
        TextArea area =  (TextArea) selectionModel.getSelectedItem().getContent();
        area.selectAll();
        setClipboard(area.getText());
    }

    @FXML
    private void treeViewMouseClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TreeItem<FileResult> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem.getClass() == TreeItem.class && selectedItem.isLeaf()) {
                TabWithFileInfo tab = new TabWithFileInfo(selectedItem.getValue());
                addTabToTabPane(tab);
            }
        }
    }

    private boolean isTabWithFileInfo(Tab tab) {
        return tab.getClass() == TabWithFileInfo.class;
    }

    private boolean isTabNotOpen(TabWithFileInfo tabWithFileInfo) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        return tabs.filtered(x -> isTabWithFileInfo(x) && x.equals(tabWithFileInfo)).size() == 0;
    }

    private void addTabToTabPane(TabWithFileInfo tabWithFileInfo) {
        if (isTabNotOpen(tabWithFileInfo)) {
            tabPane.getTabs().add(tabWithFileInfo);
            selectText(SearchDirection.START);
        }
        SingleSelectionModel<Tab> selectionModel =  tabPane.getSelectionModel();
        selectionModel.select(tabWithFileInfo);
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
                treeView.setRoot(new TreeItem<>(new FileResult(path, null, text.length())));
            }
            TreeItem<FileResult> currentItem = treeView.getRoot();
            String lessPath = result.getPath().substring(path.length() + 1);
            String[] split = lessPath.split(File.separator);
            for (int i = 0; i < split.length; ++i) {
                ObservableList<TreeItem<FileResult>> kids = currentItem.getChildren();
                ObservableList<TreeItem<FileResult>> root = kids.filtered(x -> x.getValue().getFileName().equals(split[i]));
                if (root.size() == 0) {
                    TreeItem<FileResult> item;
                    if (i == split.length - 1) {
                        item = new TreeItem<>(result);
                    } else {
                        item = new TreeItem<>(new FileResult(split[i], null, 0));
                    }
                    kids.add(item);
                    currentItem = item;
                } else {
                    currentItem = root.get(0);
                }
            }
        }
    }

    private enum SearchDirection {
        START, UP, DOWN
    }

    private int getCurrentPosition(FileResult fileResult, SearchDirection searchDirection) {
        switch (searchDirection) {
            case UP:
                return fileResult.getUpPosition();
            case DOWN:
                return fileResult.getDownPosition();
            default:
                return fileResult.getStartPosition();
        }
    }

    private void selectText(SearchDirection searchDirection) {
        TabWithFileInfo tab = (TabWithFileInfo) tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            TextArea textArea = (TextArea) tab.getContent();
            FileResult fileResult = tab.getFileResult();
            int currentPosition = getCurrentPosition(fileResult, searchDirection);
            textArea.selectRange(currentPosition, currentPosition + fileResult.getTextLength());
        }
    }

    private void setClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(string);
        clipboard.setContent(clipboardContent);
    }
}
