package xyz.vopen.cartier.commons.task;

import org.apache.commons.lang3.StringUtils;
import xyz.vopen.cartier.commons.utils.Collections3;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Task Runner
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 27/02/2017.
 */
public abstract class Runner implements Callable<Task.Output> {

    /**
     * task unique id
     **/
    private String taskId;

    @Override
    final public Task.Output call () throws Exception {

        long start = System.currentTimeMillis();
        Task.Output defaultOutput = Task.Output.defaultOutput();
        Task task = null;
        try {
            if (StringUtils.isNoneBlank(taskId)) {
                task = TaskKeeper.Tasks.getTask(taskId);
                if (task != null) {
                    task.setProgress(Task.Progress.RUNNING);
                    
                    // start running
                    List<Task.TaskProgressListener> listeners = task.getListeners();
                    if (!Collections3.isEmpty(listeners)) {
                        for (Task.TaskProgressListener listener : listeners) {
                            listener.onRunning(task.getInput(), defaultOutput);
                        }
                    }
                    

                    // get runner 
                    Runner runner = task.getRunner();
                    // execute
                    Task.Output output = runner.execute(taskId);

                    if (output != null) {
                        // reset output
                        defaultOutput = output;
                        Task.Status status = output.status;

                        // success
                        if(status.equals(Task.Status.SUCCESS)) {
                            if (!Collections3.isEmpty(listeners)) {
                                for (Task.TaskProgressListener listener : listeners) {
                                    listener.onSuccess(task.getInput(), defaultOutput);
                                }
                            }
                        }

                        // fail
                        if(status.equals(Task.Status.FAIL)) {
                            if (!Collections3.isEmpty(listeners)) {
                                for (Task.TaskProgressListener listener : listeners) {
                                    listener.onFail(task.getInput(), defaultOutput);
                                }
                            }
                        }
                        
                    } else {
                        //TODO task execute fail
                        
                    }
                }
            }

        } catch (Exception e) {
            if (task != null) {
                task.setProgress(Task.Progress.EXCEPTION);
            }
        } finally {
            defaultOutput.spendTime = (System.currentTimeMillis() - start) % 1000;
            if (task != null) {
                task.setProgress(Task.Progress.FINISH);
            }
        }
        return defaultOutput;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     */
    public abstract Task.Output execute (String taskId);

    protected Runner (Builder builder) {
        this.taskId = builder.taskId;
    }


    public abstract static class Builder {
        private String taskId;

        public Builder taskId (String taskId) {
            this.taskId = taskId;
            return this;
        }

        public abstract Runner build ();

    }
}
