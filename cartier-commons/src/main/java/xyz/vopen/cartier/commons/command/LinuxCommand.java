package xyz.vopen.cartier.commons.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Linux 系统命令(复制,移动,删除,压缩,增量压缩)
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 23/02/2017.
 */
public final class LinuxCommand {

    /**
     * 执行 Shell 脚本
     *
     * @param shell
     *         shell脚本路径
     * @param callback
     *         回调
     */
    public static int executeCommand (String shell, CmdExecCallback callback) {


        try {

            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            String[] commands = new String[]{ "/bin/sh", "-c", shell };
            if (isWindows) {
                //commands = new String[]{ "cmd.exe", "/c", shell }
                throw new RuntimeException("Sorry . Unsupported windows os system now.");
            }

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(commands);

            //String[] commands = {"system.exe","-get t"};

            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            StringBuilder stdout = new StringBuilder();
            StringBuilder errout = new StringBuilder();

            // read the output from the command
            stdout.append("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdOutput.readLine()) != null) {
                stdout.append(s).append("\r\n");
            }

            // read any errors from the attempted command
            errout.append("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                errout.append(s).append("\r\n");
            }

            int exitVal = proc.waitFor();

            if (callback != null)
                callback.onComplete(exitVal == 0, exitVal, errout.toString(), stdout.toString(), shell);

            return exitVal;

        } catch (Exception e) {
            e.printStackTrace();
        }
//        return new CmdResult(exitVal, stdout.toString(), errout.toString());
        return -1;
    }

    /**
     * 执行回调
     */
    public interface CmdExecCallback<T> {

        /**
         * 完成回调方法
         *
         * @param success
         *         标记成功
         * @param exitVal
         *         退出标识
         * @param errout
         *         错误日志
         * @param stdout
         *         输出日志
         * @param shell
         *         脚本
         */
        void onComplete (Boolean success, int exitVal, String errout, String stdout, String shell);
    }

    public static class CmdResult {
        int exitVal;
        String stdout;
        String errout;

        public CmdResult (int exitVal, String stdout, String errout) {
            this.exitVal = exitVal;
            this.stdout = stdout;
            this.errout = errout;
        }
    }

}
