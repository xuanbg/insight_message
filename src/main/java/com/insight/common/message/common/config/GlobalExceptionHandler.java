package com.insight.common.message.common.config;

import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.pojo.Reply;
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
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Error error = new Error();

    /**
     * 处理缺少请求参数的异常
     *
     * @param ex 缺少请求参数
     * @return Reply
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Reply handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String msg = "缺少请求参数: " + ex.getParameterName();

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 处理不合法的参数的异常
     *
     * @param ex 不合法的参数异常
     * @return Reply
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Reply handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.info("不合法的参数: {}", ex.getMessage());

        return ReplyHelper.invalidParam("不合法的参数");
    }

    /**
     * 处理参数绑定出现的异常
     *
     * @param ex 参数绑定错误异常
     * @return Reply
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public Reply handleServletRequestBindingException(ServletRequestBindingException ex) {
        logger.info("参数绑定错误: {}", ex.getMessage());

        return ReplyHelper.invalidParam("参数绑定错误");
    }

    /**
     * 处理参数解析失败的异常
     *
     * @param ex 参数解析失败异常
     * @return Reply
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Reply handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.info("参数解析失败: {}", ex.getMessage());

        return ReplyHelper.invalidParam("参数解析失败");
    }

    /**
     * 参数验证失败的异常
     *
     * @param ex 参数验证失败异常
     * @return Reply
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Reply handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        if (error == null) {
            logger.info("参数解析失败: {}", ex.getMessage());

            return ReplyHelper.invalidParam("参数解析失败");
        }

        String parameter = error.getField();
        return ReplyHelper.invalidParam("参数「" + parameter + "」" + error.getDefaultMessage());
    }

    /**
     * 参数绑定失败的异常
     *
     * @param ex 参数绑定失败异常
     * @return Reply
     */
    @ExceptionHandler(BindException.class)
    public Reply handleBindException(BindException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        if (error == null) {
            logger.info("参数绑定失败: {}", ex.getMessage());

            return ReplyHelper.invalidParam("参数绑定失败");
        }

        String parameter = error.getField();
        return ReplyHelper.invalidParam("参数「" + parameter + "」" + error.getDefaultMessage());
    }

    /**
     * 参数类型不匹配的异常
     *
     * @param ex 参数类型不匹配异常
     * @return Reply
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Reply handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        logger.info("不支持当前媒体类型: {}", ex.getMessage());

        return ReplyHelper.invalidParam("不支持当前媒体类型");
    }

    /**
     * 非预期类型的异常
     *
     * @param ex 非预期类型异常
     * @return Reply
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public Reply handleUnexpectedTypeException(UnexpectedTypeException ex) {
        logger.info("参数类型不匹配: {}", ex.getMessage());

        return ReplyHelper.invalidParam("参数类型不匹配");
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param ex 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Reply handleSqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        initError(ex);

        return ReplyHelper.error();
    }

    /**
     * SQL语句执行错误抛出的异常
     *
     * @param ex SQL语句执行错误的异常
     * @return Reply
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Reply handleSqlSyntaxErrorException(SQLSyntaxErrorException ex) {
        initError(ex);

        return ReplyHelper.error();
    }

    /**
     * 空指针抛出的异常
     *
     * @param ex 空指针异常
     * @return Reply
     */
    @ExceptionHandler(NullPointerException.class)
    public Reply handleNullPointerException(NullPointerException ex) {
        initError(ex);

        return ReplyHelper.error();
    }

    /**
     * 服务器异常
     *
     * @param ex 服务器异常
     * @return Reply
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Reply handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        initError(ex);

        return ReplyHelper.error();
    }

    /**
     * 运行时异常
     *
     * @param ex 运行时异常
     * @return Reply
     */
    @ExceptionHandler(RuntimeException.class)
    public Reply handleRuntimeException(RuntimeException ex) {
        initError(ex);

        return ReplyHelper.error();
    }

    /**
     * 服务器异常
     *
     * @param ex 通用异常
     * @return Reply
     */
    @ExceptionHandler(Exception.class)
    public Reply handleException(Exception ex) {
        initError(ex);

        return ReplyHelper.error();
    }

    /**
     * 初始化错误实体类
     *
     * @param ex Exception
     */
    private void initError(Exception ex) {
        String message = ex.getMessage();
        if (message == null){
            message = ex.getClass().getSimpleName();
        }

        error.setRequestId();
        error.setError(message);
        error.setException(ex.getStackTrace());

        logger.error("发生异常: {}", error.toString());
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

        @Override
        public String toString() {
            return Json.toJson(this);
        }
    }
}
