package com.cjyfff.dq.task.queue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 2018/10/3.
 */
@Getter
@Setter
public class QueueTask implements Delayed {

    private String taskId;

    private String functionName;

    private String params;

    private Long delayTime;

    public TimeUnit delayTimeUnit;

    public Long executeTime;

    private QueueTask() {}

    public QueueTask(String taskId, String functionName, String params, Long delayTime) {
        this.taskId = taskId;
        this.functionName = functionName;
        this.params = params;
        this.delayTime = delayTime;
        this.delayTimeUnit = TimeUnit.MILLISECONDS;
        this.executeTime = System.currentTimeMillis() + delayTimeUnit.toMillis(delayTime);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
            return 1;
        }else if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)) {
            return -1;
        }
        return 0;
    }
}
