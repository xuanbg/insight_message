package com.insight.base.message.common.config;

import com.insight.util.Json;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 宣炳刚
 * @date 2019/9/2
 * @remark 全局异常捕获
 */
@ResponseStatus(HttpStatus.OK)
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static Error error = new Error();

    static {
        error.setLevel();
    }

    /**
     * 处理缺少请求参数的异常
     *
     * @param e 缺少请求参数
     * @return Reply
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Reply handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String msg = "缺少请求参数: " + e.getParameterName();

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 处理不合法的参数的异常
     *
     * @param e 不合法的参数异常
     * @return Reply
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Reply handleIllegalArgumentException(IllegalArgumentException e) {
        logger.info("不合法的参数: {}", e.getMessage());

        return ReplyHelper.invalidParam("不合法的参数");
    }

    /**
     * 处理参数绑定出现的异常
     *
     * @param e 参数绑定错误异常
     * @return Reply
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public Reply handleServletRequestBindingException(ServletRequestBindingException e) {
        logger.info("参数绑定错误: {}", e.getMessage());

        return ReplyHelper.invalidParam("参数绑定错误");
    }

    /**
     * 处理参数解析失败的异常
     *
     * @param e 参数解析失败异常
     * @return Reply
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Reply handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.info("参数解析失败: {}", e.getMessage());

        return ReplyHelper.invalidParam("参数解析失败");
    }

    /**
     * 参数验证失败的异常
     *
     * @param e 参数验证失败异常
     * @return Reply
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Reply handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError error = e.getBindingResult().getFieldError();
        if (error == null) {
            logger.info("参数解析失败: {}", e.getMessage());

            return ReplyHelper.invalidParam("参数解析失败");
        }

        return ReplyHelper.invalidParam("参数解析失败: " + error.getDefaultMessage());
    }

    /**
     * 参数绑定失败的异常
     *
     * @param e 参数绑定失败异常
     * @return Reply
     */
    @ExceptionHandler(BindException.class)
    public Reply handleBindException(BindException e) {
        FieldError error = e.getBindingResult().getFieldError();
        if (error == null) {
            logger.info("参数绑定失败: {}", e.getMessage());

            return ReplyHelper.invalidParam("参数绑定失败");
        }

        return ReplyHelper.invalidParam("参数绑定失败: " + error.getDefaultMessage());
    }

    /**
     * 参数类型不匹配的异常
     *
     * @param e 参数类型不匹配异常
     * @return Reply
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Reply handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        logger.info("不支持当前媒体类型: {}", e.getMessage());

        return ReplyHelper.invalidParam("不支持当前媒体类型");
    }

    /**
     * 非预期类型的异常
     *
     * @param e 非预期类型异常
     * @return Reply
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public Reply handleUnexpectedTypeException(UnexpectedTypeException e) {
        logger.info("参数类型不匹配: {}", e.getMessage());

        return ReplyHelper.invalidParam("参数类型不匹配");
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param e 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Reply handleSqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        initError(e);

        String json = Json.toJson(error);
        logger.error("发生异常: {}", json);

        return ReplyHelper.error();
    }

    /**
     * SQL语句执行错误抛出的异常
     *
     * @param e SQL语句执行错误的异常
     * @return Reply
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Reply handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        initError(e);

        String json = Json.toJson(error);
        logger.error("发生异常: {}", json);

        return ReplyHelper.error();
    }

    /**
     * 空指针抛出的异常
     *
     * @param e 空指针异常
     * @return Reply
     */
    @ExceptionHandler(NullPointerException.class)
    public Reply handleNullPointerException(NullPointerException e) {
        initError(e);

        String json = Json.toJson(error);
        logger.error("发生异常: {}", json);

        return ReplyHelper.error();
    }

    /**
     * 服务器异常
     *
     * @param e 服务器异常
     * @return Reply
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Reply handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e) {
        initError(e);

        String json = Json.toJson(error);
        logger.error("发生异常: {}", json);

        return ReplyHelper.error();
    }

    /**
     * 运行时异常
     *
     * @param e 运行时异常
     * @return Reply
     */
    @ExceptionHandler(RuntimeException.class)
    public Reply handleRuntimeException(RuntimeException e) {
        initError(e);

        String json = Json.toJson(error);
        logger.error("发生异常: {}", json);

        return ReplyHelper.error();
    }

    /**
     * 服务器异常
     *
     * @param e 通用异常
     * @return Reply
     */
    @ExceptionHandler(Exception.class)
    public Reply handleException(Exception e) {
        initError(e);

        String json = Json.toJson(error);
        logger.error("发生异常: {}", json);

        return ReplyHelper.error();
    }

    /**
     * 初始化错误实体类
     *
     * @param e Exception
     */
    private void initError(Exception e) {
        error.setTime();
        error.setRequestId();
        error.setError(e.getMessage());
        error.setException(e.getStackTrace());
    }

    /**
     * 错误日志实体类
     */
    private static class Error {

        /**
         * 请求ID
         */
        private String requestId;

        /**
         * 日志时间
         */
        private Date time;

        /**
         * 日志级别(DEBUG,INFO,WARN,ERROR)
         */
        private String level;

        /**
         * 错误消息
         */
        private String error;

        /**
         * 异常堆栈
         */
        private Object exception;

        public String getRequestId() {
            return requestId;
        }

        void setRequestId() {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
            requestId = request.getHeader("requestId");
        }

        public Date getTime() {
            return time;
        }

        void setTime() {
            time = new Date();
        }

        public String getLevel() {
            return level;
        }

        void setLevel() {
            level = "ERROR";
        }

        public String getError() {
            return error;
        }

        void setError(String error) {
            this.error = error;
        }

        public Object getException() {
            return exception;
        }

        void setException(StackTraceElement[] trace) {
            exception = Arrays.stream(trace).map(StackTraceElement::toString).collect(Collectors.toList());
        }
    }
}
