package test.kuuala.grepextension.model;

public class TaskQueue {

    private Callback callback;

    public void registerCallBack(Callback callback) {
        this.callback = callback;
    }

    public void addTask(FileTask fileTask) {
        callback.callingBack(fileTask);
    }
}
