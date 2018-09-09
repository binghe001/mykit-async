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
package io.mykit.async.spring.processor;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:52
 * @description 重试结果
 * @version 1.0.0
 */
public class RetryResult<T> {

    private T data;

    private Throwable throwable;

    public RetryResult() {
    }

    public RetryResult(T data, Throwable throwable) {
        this.data = data;
        this.throwable = throwable;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public Throwable getThrowable() {
        return throwable;
    }

    public RetryResult<T> setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }
}
