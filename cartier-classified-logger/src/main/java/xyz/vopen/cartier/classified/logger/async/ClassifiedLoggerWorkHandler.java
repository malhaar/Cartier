package xyz.vopen.cartier.classified.logger.async;

import com.lmax.disruptor.WorkHandler;

import java.util.List;

/**
 * event handler
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 17/08/2017.
 */
public class ClassifiedLoggerWorkHandler implements WorkHandler<ClassifiedLoggerEvent> {

    /**
     * Callback to indicate a unit of work needs to be processed.
     *
     * @param event
     *         published to the {@link com.lmax.disruptor.RingBuffer}
     *
     * @throws Exception
     *         if the {@link WorkHandler} would like the exception handled further up the chain.
     */
    @Override
    public void onEvent (ClassifiedLoggerEvent event) throws Exception {
        if (event != null) {
            if (!event.isSortable()) {
                event.getLogger().info(event.getContent());
            } else {
                // for
                List<String> contents = event.getContents();
                for (int i = 0; i < contents.size(); i++) {
                    if (i == contents.size() - 1) {
                        event.getLogger().info(contents.get(i) + "\r\n");
                        continue;
                    }
                    event.getLogger().info(contents.get(i));
                }
            }
        }
    }
}
