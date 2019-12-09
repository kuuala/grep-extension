package test.kuuala.grepextension.controller;

import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import test.kuuala.grepextension.model.FileResult;
import test.kuuala.grepextension.model.FileTask;
import test.kuuala.grepextension.model.Searcher;
import test.kuuala.grepextension.model.TaskQueue;
import test.kuuala.grepextension.view.TabWithFileInfo;

import java.io.File;
import java.net.URL;
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
        Runnable solver = this::delegateSolving;
        Thread thread = new Thread(solver);
        thread.start();
    }

    @FXML
    private void upClickButton() {
        selectTextByDirection(SearchDirection.UP);
    }

    @FXML
    private void downClickButton() {
        selectTextByDirection(SearchDirection.DOWN);
    }

    @FXML
    private void selectAllClickButton() {
        SingleSelectionModel<Tab> selectionModel =  tabPane.getSelectionModel();
        Tab tab = selectionModel.getSelectedItem();
        if (tab instanceof TabWithFileInfo) {
            TextArea area = (TextArea) tab.getContent();
            area.selectAll();
            setClipboard(area.getText());
        }
    }

    @FXML
    private void treeViewMouseClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TreeItem<FileResult> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.isLeaf()) {
                TabWithFileInfo tab = new TabWithFileInfo(selectedItem.getValue());
                addTabToTabPane(tab);
            }
        }
    }

    private boolean isTabNotOpen(TabWithFileInfo tabWithFileInfo) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        return tabs.filtered(tabWithFileInfo::equals).isEmpty();
    }

    private void selectTab(TabWithFileInfo tabWithFileInfo) {
        SingleSelectionModel<Tab> selectionModel =  tabPane.getSelectionModel();
        selectionModel.select(tabWithFileInfo);
    }

    private void addTabToTabPane(TabWithFileInfo tabWithFileInfo) {
        if (isTabNotOpen(tabWithFileInfo)) {
            tabPane.getTabs().add(tabWithFileInfo);
            selectTab(tabWithFileInfo);
            selectTextByDirection(SearchDirection.START);
        } else {
            selectTab(tabWithFileInfo);
        }
    }

    private void solveFileTask(ExecutorService service, FileTask task) {
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                workerStateEvent -> {
                    Optional<FileResult> result = task.getValue();
                    result.ifPresent(this::addElementOnTree);
                });
        service.submit(task);
    }

    private void delegateSolving() {
        ExecutorService service = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() + 1);
        TaskQueue taskQueue = new TaskQueue();
        taskQueue.registerCallBack((task) -> solveFileTask(service, task));
        Searcher searcher = new Searcher(path, text, extension, taskQueue);
        searcher.fillTaskQueue();
        service.shutdown();
    }

    private boolean isIndexOfLastNode(int index, int length) {
        return index == length - 1;
    }

    private String[] getPathByDirectoriesArray(FileResult leaf) {
        return leaf.getPath().substring(path.length() + 1).split(File.separator);
    }

    private ObservableList<TreeItem<FileResult>> getNextNodesByDirName(
            ObservableList<TreeItem<FileResult>> childes,
            String directoryName
    ) {
        return childes.filtered(x -> x.getValue()
                .getFileName()
                .equals(directoryName));
    }

    private FileResult addInterimNode(String name) {
        return new FileResult(name, null, 0);
    }

    private void addElementOnTree(FileResult leaf) {
        synchronized (treeView) {
            if (treeView.getRoot() == null) {
                treeView.setRoot(new TreeItem<>(addInterimNode(path)));
            }
            TreeItem<FileResult> currentNode = treeView.getRoot();
            String[] pathByDirectories = getPathByDirectoriesArray(leaf);
            for (int i = 0; i < pathByDirectories.length; ++i) {
                var childes = currentNode.getChildren();
                var nextNodes = getNextNodesByDirName(childes, pathByDirectories[i]);
                if (nextNodes.isEmpty()) {
                    TreeItem<FileResult> tempNode;
                    if (isIndexOfLastNode(i, pathByDirectories.length)) {
                        tempNode = new TreeItem<>(leaf);
                    } else {
                        tempNode = new TreeItem<>(addInterimNode(pathByDirectories[i]));
                    }
                    childes.add(tempNode);
                    currentNode = tempNode;
                } else {
                    currentNode = nextNodes.get(0);
                }
            }
        }
    }

    private enum SearchDirection {
        START, UP, DOWN
    }

    private void setNextPositionByDirection(FileResult fileResult, SearchDirection searchDirection) {
        switch (searchDirection) {
            case UP:
                fileResult.setUpPosition();
                break;
            case DOWN:
                fileResult.setDownPosition();
                break;
            default:
                fileResult.setStartPosition();
                break;
        }
    }

    private void selectTextByDirection(SearchDirection searchDirection) {
        TabWithFileInfo tab = (TabWithFileInfo) tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            TextArea textArea = (TextArea) tab.getContent();
            FileResult fileResult = tab.getFileResult();
            setNextPositionByDirection(fileResult, searchDirection);
            textArea.selectRange(fileResult.getStartPosition(), fileResult.getEndPosition());
        }
    }

    private void setClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(string);
        clipboard.setContent(clipboardContent);
    }
}
