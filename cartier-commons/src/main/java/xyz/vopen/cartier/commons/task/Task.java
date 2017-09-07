package xyz.vopen.cartier.commons.task;

import com.google.common.collect.Lists;
import xyz.vopen.cartier.commons.utils.Collections3;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Abstract task
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 24/02/2017.
 */
public abstract class Task {

    /**
     * Task id (unique)
     **/
    final String taskId;

    /**
     * task runner
     **/
    protected Runner runner;

    /**
     * task input
     */
    final Input input;

    /**
     * task status
     */
    private Status status;

    /**
     * task progress
     */
    private Progress progress;

    /**
     * 创建时间
     **/
    final Date createTime = Calendar.getInstance().getTime();


    final List<TaskProgressListener> listeners;

    /**
     * private task builder
     **/
    protected Task (Builder builder) {
        this.taskId = builder.taskId;
        this.input = builder.input;
        this.listeners = builder.listeners;
        if (!Collections3.isEmpty(listeners)) {
            for (TaskProgressListener progressListener : listeners) {
                progressListener.onCreate(this.input);
            }
            progress = Progress.CREATE;
        }
    }

    /**
     * TASK Builder
     */
    public abstract static class Builder {
        private String taskId;
        private Input input;
        private List<TaskProgressListener> listeners = Lists.newLinkedList();

        Builder () {
            this.taskId(Task.newTaskId());
        }

        private void taskId (String taskId) {
            this.taskId = taskId;
        }

        public Builder input (Input input) {
            this.input = input;
            return this;
        }

        public Builder addListeners (TaskProgressListener listener) {
            if (listeners != null) {
                if (listeners.size() == 0) {
                    this.listeners.add(listener);
                }
            }
            return this;
        }

        public abstract Task build ();

    }

    public Runner getRunner () {
        return runner;
    }

    public void setRunner (Runner runner) {
        this.runner = runner;
    }

    /**
     * 判断两个任务是否是同一个任务
     **/
    public abstract boolean compareTask (Task task);

    @Override
    public int hashCode () {
        return super.hashCode();
    }

    @Override
    public boolean equals (Object obj) {
        if (obj != null) {
            if (obj instanceof Task) {
                Task temp = (Task) obj;
                return compareTask(temp);
            }
        }
        return false;
    }

    /**
     * 任务输出结果
     **/
    public static class Output {
        Status status = Status.INIT; // 状态
        Long spendTime; // 执行时间


        public static Output defaultOutput () {
            return new Output();
        }
    }

    /**
     * 任务输入参数
     **/
    public static class Input {
        
    }

    /**
     * 任务监听器
     */
    public interface TaskProgressListener {

        /**
         * 任务创建
         *
         * @param input
         */
        void onCreate (Input input);

        /**
         * 任务提交
         *
         * @param input
         */
        void onSubmit (Input input);

        /**
         * 任务运行
         *
         * @param input
         * @param output
         */
        void onRunning (Input input, Output output);

        /**
         * 任务执行成功
         *
         * @param input
         * @param output
         */
        void onSuccess (Input input, Output output);

        /**
         * 任务执行失败
         *
         * @param input
         * @param output
         */
        void onFail (Input input, Output output);

        /**
         * 取消任务
         *
         * @param input
         * @param output
         */
        void onCancel (Input input, Output output);
    }

    public abstract static class AbstractTaskProgressListener implements TaskProgressListener {
        /**
         * 任务创建
         *
         * @param input
         */
        @Override
        public void onCreate (Input input) {

        }

        /**
         * 任务提交
         *
         * @param input
         */
        @Override
        public void onSubmit (Input input) {

        }

        /**
         * 任务运行
         *
         * @param input
         * @param output
         */
        @Override
        public void onRunning (Input input, Output output) {

        }

        /**
         * 任务执行成功
         *
         * @param input
         * @param output
         */
        @Override
        public void onSuccess (Input input, Output output) {

        }

        /**
         * 任务执行失败
         *
         * @param input
         * @param output
         */
        @Override
        public void onFail (Input input, Output output) {

        }

        /**
         * 取消任务
         *
         * @param input
         * @param output
         */
        @Override
        public void onCancel (Input input, Output output) {

        }
    }

    /**
     * 任务进度
     */
    public static enum Progress {
        CREATE,
        SUBMIT,
        RUNNING,
        FINISH,
        EXCEPTION
    }

    /**
     * 状态
     */
    public static enum Status {
        INIT,
        RETRY,
        SUCCESS,
        FAIL,
        CANCELED
    }


    public Status getStatus () {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public Progress getProgress () {
        return progress;
    }

    public void setProgress (Progress progress) {
        this.progress = progress;
        TaskKeeper.Tasks.updateTaskProgress(this.taskId ,progress);
    }

    public static String newTaskId () {
        return UUID.randomUUID().toString();
    }

    public List<TaskProgressListener> getListeners () {
        return listeners;
    }

    public Input getInput () {
        return input;
    }

    public Date getCreateTime () {
        return createTime;
    }
}
