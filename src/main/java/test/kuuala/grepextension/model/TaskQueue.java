package test.kuuala.grepextension.model;

import java.util.Queue;

public class TaskQueue {

    final private Queue<FileTask> queue;
    private Callback callback;

    public TaskQueue(Queue<FileTask> queue) {
        this.queue = queue;
    }

    public void registerCallBack(Callback callback) {
        this.callback = callback;
    }

    public void addTask(FileTask fileTask) {
        queue.add(fileTask);
        synchronized (queue) {
            callback.callingBack(queue.poll());
        }
    }
}
