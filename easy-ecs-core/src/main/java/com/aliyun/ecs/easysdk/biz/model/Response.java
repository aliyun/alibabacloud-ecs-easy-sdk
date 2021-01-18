/*
 * Copyright (c) 2021-present, Alibaba Cloud All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aliyun.ecs.easysdk.biz.model;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class Response<T> {
    public static final Long SUCCESS_CODE = 200L;
    public static final Long COMMON_ERROR = 400L;
    public static final Long ILLEGAL_PARAM = 401L;

    /**
     * 系统: 本次调用是否正常
     */
    private Boolean success = true;
    private Long code = SUCCESS_CODE;
    private String message;
    /**
     * 业务: 本次调用结果值
     */
    private T data;

    public Response(T t) {
        this.data = t;
        code = SUCCESS_CODE;
        success = true;
    }

    public Response() {
        code = SUCCESS_CODE;
        success = true;
    }

    public Response(Long code, String message) {
        if (!SUCCESS_CODE.equals(code)) {
            this.code = code;
            success = false;
            this.message = message;
        } else {
            this.code = SUCCESS_CODE;
            success = true;
        }
    }

    public Response(Exception e) {
        this.success = false;
        this.code = COMMON_ERROR;
        this.message = ExceptionUtils.getStackTrace(e);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
