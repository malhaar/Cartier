package xyz.vopen.cartier.provision.ext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import xyz.vopen.cartier.commons.utils.DomainSerializable;

/**
 * 结果构建类<br>
 * <p/>
 * How to use:
 * <code>
 * <li>Result result = Result.newBuilder().result(Result.Code.SUCCESS).data(data).build();
 * <li>Result.newBuilder().result(Result.Code.SUCCESS ,data).build();
 * </code>
 *
 * @author Zero.zhao
 * @author Elve.xu
 * @version 1.2
 */
public final class Result<T> extends DomainSerializable {

    private static final long serialVersionUID = -3218203719027482249L;
    
    /**
     * 结果状态码
     */
    @JSONField(serialize = true)
    private Integer code;

    /**
     * 消息描述
     */
    @JSONField(serialize = true)
    private String msg;

    /**
     * 数据结果
     */
    private T data;

    /**
     * 扩展字段
     */
    private Object extend;

    public Result () {
    }

    /**
     * Builder
     *
     * @param builder
     *         builder
     */
    private Result (Builder<T> builder) {
        this.code = builder.resultCode;
        this.msg = builder.resultMsg;
        this.data = builder.data;
    }

    public static <T> Builder<T> newBuilder () {
        return new Builder<T>();
    }

    /**
     * inner builder
     */
    public static final class Builder<T> {

        private Integer resultCode;
        private String resultMsg;
        private T data;
        private Object extend;

        /**
         * build builder with Code
         *
         * @param resultCode
         *         result code
         *
         * @return builder
         */
        public Builder<T> result (Code resultCode) {
            this.resultCode = resultCode.code();
            this.resultMsg = resultCode.msg();
            return this;
        }

        public Builder<T> result (Code resultCode, T data) {
            this.resultCode = resultCode.code();
            this.resultMsg = resultCode.msg();
            this.data = data;
            return this;
        }

        public Builder<T> result (Code resultCode, T data, Object extend) {
            this.resultCode = resultCode.code();
            this.resultMsg = resultCode.msg();
            this.data = data;
            this.extend = extend;
            return this;
        }

        /**
         * build builder with Code
         *
         * @param code
         *         result code
         *
         * @return builder
         */
        public Builder<T> result (Integer code, String msg) {
            return result(code, msg, null);
        }

        /**
         * build builder with Code
         *
         * @param code
         *         result code
         *
         * @return builder
         */
        public Builder<T> result (Integer code, String msg, T data) {
            if (!Code.validate(code)) {
                throw new IllegalArgumentException("无效错误码:" + code + " ,@see Result.Code");
            }
            this.resultCode = code;
            this.resultMsg = msg;
            this.data = data;
            return this;
        }

        /**
         * set data
         *
         * @param data
         *         data
         *
         * @return return this
         */
        public Builder<T> data (T data) {
            this.data = data;
            return this;
        }

        public Builder<T> extend (Object extend) {
            this.extend = extend;
            return this;
        }

        /**
         * build result Object
         *
         * @return object instance
         */
        public Result<T> build () {
            return new Result<T>(this);
        }

        /**
         * 直接输出JSON String
         *
         * @return result -> json String
         */
        public String string () {
            return new Result<T>(this).toString();
        }

        /**
         * 直接输出JSON
         *
         * @return json
         */
        public String json () {
            return new Result<T>(this).toJson();
        }

    }


    @Override
    public String toString () {
        return JSON.toJSONString(this);
    }

    /**
     * 获取编码
     *
     * @return return code
     */
    public Integer getCode () {
        return code;
    }

    /**
     * 获取 Message
     *
     * @return return message
     */
    public String getMsg () {
        return msg;
    }

    /**
     * 获取数据(泛型)
     *
     * @return 返回数据
     */
    public T getData () {
        return data;
    }

    public Object getExtend () {
        return extend;
    }

    /**
     * 直接转化成JSON字符串
     *
     * @return JSON字符串
     */
    public String string () {
        return toString();
    }

    /**
     * 直接转化成JSON字符串
     *
     * @return JSON字符串
     */
    public String json () {
        return toJson();
    }

    /**
     * 游戏账号编码结果定义.
     *
     * @author Zero.zhao
     * @author Elve.xu
     */
    public static enum Code {

        /**
         * 失败
         */
        FAIL(500, "fail"),
        /**
         * 成功
         */
        SUCCESS(200, "成功"),;

        private final int code;
        private final String msg;

        /**
         * default cont
         */
        Code (int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        /**
         * get code
         *
         * @return return code
         */
        public int code () {
            return code;
        }

        /**
         * get message
         *
         * @return return message desc
         */
        public String msg () {
            return msg;
        }

        /**
         * 校验状态代码是否是已知的代码
         *
         * @param code
         *         代码
         *
         * @return return true if code is right otherwise return false
         */
        public static boolean validate (Integer code) {
            Code[] codes = Code.values();
            for (Code temp : codes) {
                if (code == temp.code()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 转换 code
         *
         * @param code
         *         code 编码
         *
         * @return return Code instance
         */
        public static Code parseCode (int code) {
            Code[] codes = Code.values();
            for (Code temp : codes) {
                if (code == temp.code()) {
                    return temp;
                }
            }
            throw new IllegalArgumentException("无效错误码:" + code + " ,@see Result.Code");
        }

    }

    /**
     * 默认成功失败状态码
     */
    @SuppressWarnings("rawtypes")
    public interface DefaultResult {
        /**
         * 成功默认状态代码
         */
        public static final Result SUCCESS = Result.newBuilder().result(Code.SUCCESS).build();

        /**
         * 失败默认状态代码
         */
        public static final Result FAIL = Result.newBuilder().result(Code.FAIL).build();
    }


    /**
     * set code
     *
     * @param code
     *         code
     */
    public void setCode (Integer code) {
        this.code = code;
    }

    /**
     * set message
     *
     * @param msg
     *         message
     */
    public void setMsg (String msg) {
        this.msg = msg;
    }

    /**
     * set data
     *
     * @param data
     *         data
     */
    public Result<T> data (T data) {
        this.data = data;
        return this;
    }

    public Result<T> extend (Object extend) {
        this.extend = extend;
        return this;
    }
}
