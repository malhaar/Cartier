package xyz.vopen.cartier.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 对日期和字符串做指定格式的相互转换工具.
 * <p/>
 * <code>DateUtils</code>类包含日期转换为字符串, 字符串转换为日期, 并可以指定转换格式. 类中提供了几种标准的格式方便开发者使用,
 * 如:
 * <p/>
 * <blockquote>
 * <p/>
 * <p>
 * <pre>
 * DateUtils.DATE_PATTERN
 * DateUtils.LONG_DATE_PATTERN
 * </pre>
 * <p/>
 * </blockquote>.
 *
 * @version 1.1
 * @since jdk1.7
 */
public class DateUtils {

    /**
     * {@code String}常量, 表示{@value #DATE_PATTERN}日期格式.
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * {@code String}常量, 表示{@value #LONG_DATE_PATTERN}日期格式.
     */
    public static final String LONG_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * {@code String}常量, 表示{@value #TIME_PATTERN}日期格式.
     */
    public static final String TIME_PATTERN = "HH:mm";

    /**
     * 日期转换为{@value #DATE_PATTERN}格式的字符串. <blockquote>
     * <p>
     * <pre>
     * String strDate = DateUtils.dateToString(new Date());
     * </pre>
     * <p>
     * </blockquote> 如果<code>date</code>传入<code>null</code>时, 则返回空字符串给调用者.
     *
     * @param date
     *         转换的日期.
     *
     * @return 一个{@value #DATE_PATTERN}格式的日期.
     */
    public static final String dateToString (Date date) {
        return dateToString(date, DATE_PATTERN);
    }

    /**
     * 日期转换自定义格式的字符串. <blockquote>
     * <p>
     * <pre>
     * String strDate = DateUtils.dateToString(new Date(), "yyyy-MM-dd");
     * </pre>
     * <p>
     * </blockquote> 如果<code>date</code>传入<code>null</code>时, 则返回空字符串给调用者.
     *
     * @param date
     *         转换的日期.
     * @param mask
     *         转换的格式.
     *
     * @return 按指定格式转换后的字符串.
     */
    public static final String dateToString (Date date, String mask) {
        SimpleDateFormat df = null;
        String returnValue = ""; // 返回值
        if (date != null) {
            df = new SimpleDateFormat(mask);
            returnValue = df.format(date);
        }
        return returnValue;
    }

    /**
     * 字符串转换为{@value #DATE_PATTERN}格式的日期. <blockquote>
     * <p>
     * <pre>
     * Date date = DateUtils.stringToDate("2016-07-20 10:10");
     * </pre>
     * <p>
     * </blockquote>
     *
     * @param strDate
     *         转换为日期的字符串, 该字符串必须是能转换为日期的标准格式, 否则会抛出{@code RuntimeException}
     *         异常.
     *
     * @return 转换后的日期.
     */
    public static final Date stringToDate (String strDate) {
        return stringToDate(strDate, DATE_PATTERN);
    }

    /**
     * 字符串转换为日期. <blockquote>
     * <p>
     * <pre>
     * Date date = DateUtils.stringToDate("2016-07-20 10:10", "yyyy-MM-dd");
     * </pre>
     * <p>
     * </blockquote>
     *
     * @param strDate
     *         转换为日期的字符串, 该字符串必须是能转换为日期的标准格式, 否则会抛出 {@code RuntimeException}
     *         异常.
     * @param mask
     *         转换成日期的格式
     *
     * @return 转换后的日期.
     */
    public static final Date stringToDate (String strDate, String mask) {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(mask);
        try {
            date = df.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * 当前时间偏移.
     *
     * @param minute
     *         偏移时间, 单位分
     * @param mask
     *         格式
     *
     * @return
     */
    public static final Date dateOffset (int minute, String mask) {
        return dateOffset(new Date(), minute, LONG_DATE_PATTERN);
    }

    /**
     * 时间偏移.
     *
     * @param date
     *         指定时间.
     * @param minute
     *         偏移时间, 单位分.
     * @param mask
     *         格式
     *
     * @return
     */
    public static final Date dateOffset (Date date, int minute, String mask) {
        Calendar calendar = Calendar.getInstance();
        // 设置时间
        calendar.setTime(date);
        // 设置偏移
        calendar.add(Calendar.MINUTE, minute);
        // 格式化成偏移后的时间字符串
        String strDate = dateToString(calendar.getTime(), mask);

        return stringToDate(strDate, mask);
    }
}
