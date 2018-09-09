/**
 * Copyright 2018-2118 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.async.spring.bean;

import io.mykit.async.spring.utils.ReflectionHelper;

import java.lang.reflect.Method;

/**
 * @author liuyazhuang
 * @date 2018/9/9 20:58
 * @description 封装调用的异步方法信息
 * @version 1.0.0
 */
public class AsyncMethod {
    private long timeout;
    private boolean isVoid;
    private Object object;
    private Method method;
    private AsyncRetry retry;

    public AsyncMethod(Object object, Method method, long timeout) {
        this.object = object;
        this.method = method;
        this.timeout = timeout;
        this.isVoid = ReflectionHelper.isVoid(method);
    }

    public AsyncMethod(Object object, Method method, long timeout, AsyncRetry retries) {
        this.object = object;
        this.method = method;
        this.timeout = timeout;
        this.retry = retries;
        this.isVoid = ReflectionHelper.isVoid(method);
    }

    public Method getMethod() {
        return method;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public void setMethod(Method method) {
        this.method = method;
        this.isVoid = ReflectionHelper.isVoid(method);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    public Object getObject() {
        return object;
    }


    public void setObject(Object object) {
        this.object = object;
    }


    public AsyncRetry getRetry() {
        return retry;
    }


    public void setRetry(AsyncRetry retry) {
        this.retry = retry;
    }

    public void setVoid(boolean isVoid) {
        this.isVoid = isVoid;
    }
}
