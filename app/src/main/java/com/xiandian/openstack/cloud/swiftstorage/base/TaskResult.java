/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.openstack.cloud.swiftstorage.base;

/**
 * 异步任务执行结果封装。包括正常结果或异常。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class TaskResult<T> {

    /** 异常. */
    private Exception exception;

    /** 数据类型. */
    private T result;

    /**
     * 创建任务结果。
     *
     * @param result
     *            the result
     */
    public TaskResult(final T result) {
        this.result = result;
    }

    /**
     * 创建任务结果。
     *
     * @param result
     *            the result
     */
    public TaskResult(final Exception exception) {
        this.exception = exception;
    }

    /**
     * 是否有异常。
     *
     * @return true, 正常。
     */
    public final boolean isValid() {
        return exception == null;
    }

    /**
     *获得异常。
     *
     * @return the exception
     */
    public final Exception getException() {
        return exception;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public final T getResult() {
        return result;
    }
}
