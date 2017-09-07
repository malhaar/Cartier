package xyz.vopen.cartier.commons.task;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.commons.task.exception.TaskAlreadyExistException;
import xyz.vopen.cartier.commons.task.exception.TaskNotFoundException;
import xyz.vopen.cartier.commons.utils.Collections3;
import xyz.vopen.cartier.commons.utils.DefaultThreadFactory;
import xyz.vopen.cartier.commons.utils.Threads;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务管理器
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 24/02/2017.
 */
public final class TaskKeeper {

    private static Logger logger = LoggerFactory.getLogger(TaskKeeper.class);

    private ExecutorService defaultExecutor = null;
    private UUID defaultUUID = UUID.randomUUID();


    /**
     * submit task (sync) , wait task execute finished ,then return result
     *
     * @param task
     *         task
     *
     * @throws Exception
     */
    public Task.Output submitTask (Task task) throws Exception {
        checkExecutor();
        Future<Task.Output> future = defaultExecutor.submit(task.runner);
        Tasks.submit(task.taskId, task, false);
        return future.get();
    }

    /**
     * submit task async , only return task id
     *
     * @param task
     *         task instance
     *
     * @return task id
     *
     * @throws Exception
     */
    public String submitTaskAsync (Task task) throws Exception {
        checkExecutor();
        defaultExecutor.submit(task.runner);
        Tasks.submit(task.taskId, task, false);
        return task.taskId;
    }

    /**
     * task's utils
     */
    final static class Tasks {

        /**
         * save all task
         */
        private static final Map<String, Task> ALL_TASKS = Maps.newConcurrentMap();

        /**
         * Cancel queue for task
         */
        private static final Map<String, Task> CANCEL_TASK_QUEUE = Maps.newConcurrentMap();

        /**
         * temp queue { 正在运行的任务 + 失败重试的任务 }
         */
        private static final Map<String, Task> TEMP_RUNNING_QUEUE = Maps.newConcurrentMap();
        private static final Map<String, Task> TEMP_RETRY_QUEUE = Maps.newConcurrentMap();

        // dump service
        private static UUID dumpExecutorServiceUUID = null;
        private static ScheduledExecutorService dumpService = null;

        static void start () {

            checkDumpDirs();

            if (enableDump) {
                dumpExecutorServiceUUID = UUID.randomUUID();
                logger.info("Create new Scheduled Thread pool , UID: {}", dumpExecutorServiceUUID);
                dumpService = Pools.newSingleScheduledThreadPool(dumpExecutorServiceUUID, "schedule-dump-pool-");
                dumpService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run () {
                        // dump running

                    }
                }, 1, 1, TimeUnit.MINUTES);
            }
            /* 
             * TODO: start thread to foreach tasks , schedule dump tasks (all | running | success)
             * 
             */
        }

        private static void checkDumpDirs () {
            try {
                if (StringUtils.isBlank(dumpBaseDir)) {
                    dumpBaseDir = System.getProperty("user.home");
                    logger.warn("Dump base directory is blank ,set default : " + dumpBaseDir);

                    if (!dumpBaseDir.endsWith("/")) {
                        dumpBaseDir = dumpBaseDir + "/";
                    }
                    Files.createDirectories(Paths.get(dumpBaseDir + ".t_dump_dir"));
                    logger.info("Create temp dump directory : " + dumpBaseDir + ".t_dump_dir");

                }

                Files.createDirectories(Paths.get(dumpBaseDir + ".t_dump_dir" + "/succeed/"));
                Files.createDirectories(Paths.get(dumpBaseDir + ".t_dump_dir" + "/failed/"));
                Files.createDirectories(Paths.get(dumpBaseDir + ".t_dump_dir" + "/canceled/"));
                Files.createDirectories(Paths.get(dumpBaseDir + ".t_dump_dir" + "/running/"));
            } catch (IOException e) {
                logger.error("Application check directory error: {}", e);
                enableDump = false;
                logger.warn("Auto disabled set dump enabled = false !");
            }
        }

        /**
         * dump task to disk
         *
         * @param taskId
         *         task id
         */
        public static void dumpTask (String taskId) {


        }

        /**
         * Submit Task for Task's holder
         *
         * @param taskId
         *         task id
         * @param task
         *         task instance
         * @param force
         *         true submit force ,otherwise throw exception
         *
         * @throws TaskAlreadyExistException
         */
        public static void submit (String taskId, Task task, Boolean force) throws TaskAlreadyExistException {
            if (ALL_TASKS.containsKey(taskId)) {
                if (force) {
                    Task oldTask = ALL_TASKS.put(taskId, task);

                    onSubmit(task, task.getListeners(), task.getInput());

                    if (oldTask != null) { // 取消任务
                        cancelTask(oldTask.taskId);
                    }
                } else {
                    throw new TaskAlreadyExistException("Task:[" + task.taskId + "] already exist in queue ,submit fail!");
                }
            } else {
                ALL_TASKS.put(taskId, task);
                onSubmit(task, task.getListeners(), task.getInput());
            }
        }

        /**
         * cancel task
         *
         * @param taskId
         *         task id
         *
         * @throws TaskNotFoundException
         */
        public static void cancelTask (String taskId) throws TaskNotFoundException {
            try {
                if (StringUtils.isNoneBlank(taskId)) {
                    if (ALL_TASKS.containsKey(taskId)) {
                        Task task = ALL_TASKS.get(taskId);
                        if (task != null) {
                            CANCEL_TASK_QUEUE.put(taskId, task); // move cancel task into cancel_queue
                            ALL_TASKS.remove(taskId); // remove from all_task queue
                        }
                    }
                } else {
                    throw new TaskNotFoundException("Task:[" + taskId + "] is not found !");
                }
            } catch (Exception e) {

            }
        }

        /**
         * check task is canceled by user
         *
         * @param taskId
         *         task id
         *
         * @return return true task is canceled otherwise false
         */
        public static Boolean isCanceled (String taskId) {
            if (StringUtils.isNoneBlank(taskId)) {
                if (CANCEL_TASK_QUEUE.containsKey(taskId)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Get Task Info by taskId
         *
         * @param taskId
         *         task id
         *
         * @return task instance
         *
         * @throws TaskNotFoundException
         */
        public static Task getTask (String taskId) throws TaskNotFoundException {
            if (StringUtils.isNoneBlank(taskId)) {
                if (ALL_TASKS.containsKey(taskId)) {
                    return ALL_TASKS.get(taskId);
                }
            }
            throw new TaskNotFoundException("Task:[" + taskId + "] is not found !");
        }

        /**
         * update task progress
         *
         * @param taskId
         *         task id
         * @param progress
         *         progress
         */
        public static void updateTaskProgress (String taskId, Task.Progress progress) {
            updateTaskProgress(getTask(taskId), progress);
        }

        /**
         * update task progress
         *
         * @param task
         *         task
         * @param progress
         *         progress
         */
        public static void updateTaskProgress (Task task, Task.Progress progress) {
            if (task != null) {
                task.setProgress(progress);
                if (Task.Progress.RUNNING.equals(progress))
                    // move task into running queue
                    TEMP_RUNNING_QUEUE.put(task.taskId, task);
                if (Task.Progress.FINISH.equals(progress))
                    // remove task from queue
                    TEMP_RETRY_QUEUE.remove(task.taskId);
            }
        }

        private static void onSubmit (Task task, List<Task.TaskProgressListener> taskListeners, Task.Input input) {
            task.setProgress(Task.Progress.SUBMIT);
            if (!Collections3.isEmpty(taskListeners)) {
                for (Task.TaskProgressListener listener : taskListeners) {
                    listener.onSubmit(input);
                }
            }
        }
    }

    /**
     * dynamic thread pool
     */
    private static class Pools {

        private static final Map<UUID, ExecutorService> EXECUTOR_SERVICE_MAP = Maps.newConcurrentMap();

        /**
         * new cached thread pool
         *
         * @param uuid
         *         thread pool unique key
         * @param executorName
         *         thread pool name
         *
         * @return return pool executor
         */
        public static ExecutorService newCachedThreadPool (UUID uuid, String executorName) {
            ExecutorService temp = Executors.newCachedThreadPool(new DefaultThreadFactory(executorName));
            // save executors
            EXECUTOR_SERVICE_MAP.put(uuid, temp);
            return temp;
        }

        /**
         * new scheduled executor pool
         *
         * @param uuid
         *         thread pool unique key
         * @param executorName
         *         thread pool name
         *
         * @return return pool
         */
        public static ScheduledExecutorService newSingleScheduledThreadPool (UUID uuid, String executorName) {
            ScheduledExecutorService temp = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory(executorName));
            EXECUTOR_SERVICE_MAP.put(uuid, temp);
            return temp;
        }

        /**
         * ready to shutdown thread pool , wait 5 min, wait all running thread task to execute finished ,
         * if not finished (timeout) , execute shutdownNow method ,wait 2min, then interrupt any way.
         *
         * @param uuid
         *         thread pool unique key
         */
        public static void shutdownThreadPool (UUID uuid) {
            if (EXECUTOR_SERVICE_MAP.containsKey(uuid)) {
                ExecutorService service = EXECUTOR_SERVICE_MAP.get(uuid);
                if (service != null) {
                    if (!service.isShutdown()) {
                        if (!service.isTerminated()) {
                            try {
                                Threads.gracefulShutdown(service, 5, 2, TimeUnit.MINUTES);
                            } catch (Exception ignore) {
                            }
                        }
                    }
                }
            }
        }

    }

    private TaskKeeper () {
        initExecutor();

        // add shutdown hook


    }

    private static Boolean enableDump = false;
    private static String dumpBaseDir;

    private void initExecutor () {
        if (defaultExecutor == null) {
            defaultExecutor = Pools.newCachedThreadPool(defaultUUID, "defaultExecutor-");
        }
    }

    private void checkExecutor () {
        if (defaultExecutor == null) {
            initExecutor();
        }
    }

    /**
     * add shutdown hook
     */
    private void addRuntimeShutdownHook () {
// todo
    }

    public static TaskKeeper getKeeper () {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 开启Dump
     */
    public TaskKeeper enableDump (String dumpDir) {
        TaskKeeper.enableDump = true;
        TaskKeeper.dumpBaseDir = dumpDir;
        return this;
    }

    private static class InstanceHolder {
        static final private TaskKeeper INSTANCE = new TaskKeeper();
    }


}
