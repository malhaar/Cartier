package xyz.vopen.cartier.commons.task;

/**
 * xyz.vopen.cartier.commons.task
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 27/02/2017.
 */
public class DemoRunner extends Runner {

    private DemoRunner (DemoBuilder builder) {
        super(builder);
    }


    public static class DemoBuilder extends Builder {
        @Override
        public Runner build () {
            return new DemoRunner(this);
        }
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @param taskId
     * @return computed result
     */
    @Override
    public Task.Output execute (String taskId) {
        //TODO DEMO RUNNER
        return null;
    }
}
