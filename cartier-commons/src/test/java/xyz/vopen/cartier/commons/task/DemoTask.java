package xyz.vopen.cartier.commons.task;

/**
 * xyz.vopen.cartier.commons.task
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 27/02/2017.
 */
public class DemoTask extends Task {
    /**
     * private task builder
     *
     * @param builder
     */
    private DemoTask (Builder builder) {
        super(builder);
    }

    public static class Builder extends Task.Builder {

        public Builder () {
            super();
        }

        @Override
        public Task build () {
            return new DemoTask(this);
        }
    }

    /**
     * 判断两个任务是否是同一个任务
     *
     * @param task
     */
    @Override
    public boolean compareTask (Task task) {
        Input input = task.input;
        if (input instanceof DemoInput) {
            DemoInput demoInput = (DemoInput) input;
            if (((DemoInput) this.input).getName().equals(demoInput.getName())) {
                return true;
            }
        }
        return false;
    }


    public static class DemoInput extends Input {
        private String name;

        public String getName () {
            return name;
        }

        public void setName (String name) {
            this.name = name;
        }
    }

    public static class DemoOutput extends Output {

    }
}
