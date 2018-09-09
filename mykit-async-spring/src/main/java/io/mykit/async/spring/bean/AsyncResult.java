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

import java.io.Serializable;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:04
 * @description 异步执行结果的封装信息
 * @version 1.0.0
 */
public class AsyncResult<T> implements Serializable {
    private static final long serialVersionUID = -3697666808400317786L;

    private T data;

    public AsyncResult() {
    }

    public AsyncResult(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public AsyncResult<T> setData(T data) {
        this.data = data;
        return this;

    }
}
