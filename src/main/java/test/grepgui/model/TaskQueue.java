package test.grepgui.model;

import java.util.Deque;
import java.util.LinkedList;

class TaskQueue {

    private static TaskQueue instance;
    final private Deque<Task> fileList;

    private TaskQueue() {
        fileList = new LinkedList<>();
    }

    void addTask(Task task) {
        fileList.add(task);
    }

    Task popLastTask() {
        return fileList.pop();
    }

    static TaskQueue getInstance() {
        if (instance == null) {
            instance = new TaskQueue();
        }
        return instance;
    }
}
