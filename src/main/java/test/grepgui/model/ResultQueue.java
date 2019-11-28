package test.grepgui.model;

import java.util.Deque;
import java.util.LinkedList;

public class ResultQueue {

    private static ResultQueue instance;
    final private Deque<Result> resultList;

    private ResultQueue() {
        resultList = new LinkedList<>();
    }

    public static ResultQueue getInstance() {
        if (instance == null) {
            instance = new ResultQueue();
        }
        return instance;
    }

    void addResult(Result result) {
        resultList.add(result);
    }

    public Result getResult() {
        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.pop();
    }
}
