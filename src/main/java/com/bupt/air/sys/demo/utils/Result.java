package com.bupt.air.sys.demo.utils;

import lombok.Data;
import java.io.Serializable;

public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    public static final Integer SC_OK_200 = 200;
    private boolean success = true;
    private String message = "操作成功！";
    private Integer code = 0;
    private T result;
    private long timestamp = System.currentTimeMillis();
    public Result() {

    }

    public Result<T> success(String message) {
        this.message = message;
        this.code = SC_OK_200;
        this.success = true;
        return this;
    }

    public static Result<Object> ok() {
        Result<Object> r = new Result<Object>();
        r.setSuccess(true);
        r.setCode(SC_OK_200);
        r.setMessage("成功");
        return r;
    }

    public static Result<Object> ok(String msg) {
        Result<Object> r = new Result<Object>();
        r.setSuccess(true);
        r.setCode(SC_OK_200);
        r.setMessage(msg);
        return r;
    }

    public static Result<Object> ok(Object data) {
        Result<Object> r = new Result<Object>();
        r.setSuccess(true);
        r.setCode(SC_OK_200);
        r.setResult(data);
        return r;
    }

    public static Result<Object> ok(Object data, String msg) {
        Result<Object> r = new Result<Object>();
        r.setSuccess(true);
        r.setCode(SC_OK_200);
        r.setResult(data);
        r.setMessage(msg);
        return r;
    }

    public static Result<Object> error(String msg) {
        return error(SC_INTERNAL_SERVER_ERROR_500, msg);
    }

    public static Result<Object> error(int code, String msg) {
        Result<Object> r = new Result<Object>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public Result<T> error500(String message) {
        this.message = message;
        this.code = SC_INTERNAL_SERVER_ERROR_500;
        this.success = false;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}