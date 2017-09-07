package xyz.vopen.cartier.commons.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import xyz.vopen.cartier.commons.command.LinuxCommand;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件工具类
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 12/08/2017.
 */
public final class FileUtilx {


    public final static String MD5_SHELL = "md5 \"%s\" | awk '{print $NF}' > \"%s.md5.txt\"";

    /**
     * 计算文件 MD5
     *
     * @param filePath
     *         文件路径
     *
     * @return
     *
     * @throws IOException
     */
    public static String md5 (String filePath) throws IOException {
        if (StringUtils.isNoneBlank(filePath) && Files.exists(Paths.get(filePath))) {
            String md5File = filePath + ".md5.txt";
            String shell = String.format(MD5_SHELL, filePath, md5File);
            int code = LinuxCommand.executeCommand(shell, null);
            if (code == 0) {
                if (Files.exists(Paths.get(md5File))) {
                    return Files.readAllLines(Paths.get(md5File), Charset.forName("UTF-8")).get(0);
                }
            }
            return DigestUtils.md5Hex(new FileInputStream(new File(filePath)));
        }
        throw new FileNotFoundException("文件不存在");
    }

}
