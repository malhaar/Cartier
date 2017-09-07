package xyz.vopen.cartier.commons.task;

import org.junit.Test;

/**
 * xyz.vopen.cartier.commons.task
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 24/02/2017.
 */
public class TaskerTester {


    @Test
    public void main () throws Exception {

        // get input
        DemoTask.DemoInput demoInput = new DemoTask.DemoInput();
        demoInput.setName("demo-name");

        // new task
        Task task = new DemoTask.Builder()
                .input(demoInput)
                .addListeners(
                        new Task.AbstractTaskProgressListener() {
                            public void onCreate (Task.Input input) {
                                super.onCreate(input);
                            }
                        })
                .build();

        // runner 
        Runner runner = new DemoRunner.DemoBuilder()
                .taskId(task.taskId)
                .build();

        // set runner
        task.setRunner(runner);

        // submit
        TaskKeeper.getKeeper().submitTaskAsync(task);

    }


}
