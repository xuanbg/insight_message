package com.insight.common.message.common.config;

import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.common.BusinessException;
import com.insight.utils.pojo.Reply;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
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
import java.util.Objects;

/**
 * @author 宣炳刚
 * @date 2019/9/2
 * @remark 全局异常捕获
 */
@ResponseStatus(HttpStatus.OK)
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理缺少请求参数的异常
     *
     * @param ex 缺少请求参数
     * @return Reply
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Reply handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String msg = "缺少请求参数: " + ex.getParameterName();
        logger(LogLevel.WARN, msg);

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
        String msg = "不合法的参数: " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 处理参数绑定出现的异常
     *
     * @param ex 参数绑定错误异常
     * @return Reply
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public Reply handleServletRequestBindingException(ServletRequestBindingException ex) {
        String msg = "参数绑定错误: " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 处理参数解析失败的异常
     *
     * @param ex 参数解析失败异常
     * @return Reply
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Reply handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String msg = "参数解析失败: " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
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
            String msg = "参数解析失败: " + ex.getMessage();
            logger(LogLevel.WARN, msg);

            return ReplyHelper.invalidParam("参数解析失败");
        }

        String parameter = error.getField();
        String msg = "参数绑定失败: " + parameter;
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
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
            String msg = "参数绑定失败: " + ex.getMessage();
            logger(LogLevel.WARN, msg);

            return ReplyHelper.invalidParam("参数绑定失败");
        }

        String parameter = error.getField();
        String msg = "参数绑定失败: " + parameter;
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 参数类型不匹配的异常
     *
     * @param ex 参数类型不匹配异常
     * @return Reply
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Reply handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String msg = "不支持当前媒体类型: " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 非预期类型的异常
     *
     * @param ex 非预期类型异常
     * @return Reply
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public Reply handleUnexpectedTypeException(UnexpectedTypeException ex) {
        String msg = "参数类型不匹配: " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.invalidParam(msg);
    }

    /**
     * 业务异常
     *
     * @param ex 业务异常
     * @return Reply
     */
    @ExceptionHandler(BusinessException.class)
    public Reply handleBusinessException(BusinessException ex) {
        String msg = "业务发生异常: " + ex.getMessage();
        logger(LogLevel.WARN, msg);

        return ReplyHelper.fail(msg);
    }

    /**
     * 服务调用异常
     *
     * @param ex 服务调用异常
     * @return Reply
     */
    @ExceptionHandler(FeignException.class)
    public Reply handleFeignException(FeignException ex) {
        String msg = "服务调用异常: " + ex.getMessage();
        logger(LogLevel.ERROR, msg);

        return ReplyHelper.error();
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param ex 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Reply handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String msg = "数据库操作异常: " + ex.getCause().getMessage();
        logger(LogLevel.ERROR, msg);

        return ReplyHelper.error();
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param ex 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public Reply handleBadSqlGrammarException(BadSqlGrammarException ex) {
        String msg = "数据库操作异常: " + ex.getCause().getMessage();
        logger(LogLevel.ERROR, msg);

        return ReplyHelper.error();
    }

    /**
     * 数据库操作出现异常：插入、删除和修改数据的时候，违背数据完整性约束抛出的异常
     *
     * @param ex 违背数据完整性约异常
     * @return Reply
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Reply handleSqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        String msg = "数据库操作异常: " + ex.getCause().getMessage();
        logger(LogLevel.ERROR, msg);

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
        String msg = "数据库操作异常: " + ex.getCause().getMessage();
        logger(LogLevel.ERROR, msg);

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
        String msg = "空指针异常: " + ex.getMessage();
        logger(LogLevel.ERROR, msg);

        return ReplyHelper.error();
    }

    /**
     * 异步请求超时异常
     *
     * @param ex 异步请求超时异常
     * @return Reply
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Reply handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        String msg = "异步请求超时异常: " + ex.getMessage();
        logger(LogLevel.ERROR, msg);

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
        String msg = "运行时异常: " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);
        printStack(requestId, ex);

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
        String msg = "服务器异常: " + ex.getMessage();
        String requestId = logger(LogLevel.ERROR, msg);
        printStack(requestId, ex);

        return ReplyHelper.error();
    }

    /**
     * 打印日志
     *
     * @param level   日志等级
     * @param message 错误信息
     * @return 请求ID
     */
    private String logger(LogLevel level, String message) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String requestId = request.getHeader("requestId");
        switch (level) {
            case ERROR:
                LOGGER.error("requestId: {}. 错误信息: {}", requestId, message);
                break;
            case WARN:
                LOGGER.warn("requestId: {}. 警告信息: {}", requestId, message);
                break;
            default:
                LOGGER.info("requestId: {}. 日志信息: {}", requestId, message);
        }

        return requestId;
    }

    /**
     * 打印异常堆栈
     *
     * @param requestId 请求ID
     * @param ex        Exception
     */
    private void printStack(String requestId, Exception ex) {
        String stackTrace = Json.toJson(ex.getStackTrace());
        LOGGER.error("requestId: {}. 异常堆栈: {}", requestId, stackTrace);
    }
}
