package test.grepgui.model;

import java.util.ArrayList;

class TaskSolver {

    final private Task task;

    TaskSolver(Task task) {
        this.task = task;
    }

    void doScan() {
        Result result = new Result(task.getFile(), new ArrayList<>());
//        TODO main search logic
//        if (!result.getMatchList().isEmpty()) {
//        }
        ResultQueue resultQueue = ResultQueue.getInstance();
        resultQueue.addResult(result);
    }
}
