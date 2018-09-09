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
package io.mykit.async.spring.core;

import io.mykit.async.spring.constant.AsyncConstant;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:34
 * @description 异步执行方法
 * @version 1.0.0
 */
public abstract class AsyncFunction<T, E> {

    public abstract E doAsync(T t);


    public long timeout() {
        return AsyncConstant.ASYNC_DEFAULT_TIME_OUT;
    }

    public int maxAttemps() {
        return AsyncConstant.ASYNC_DEFAULT_RETRY;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Throwable>[] exceptions() {
        return new Class[]{Throwable.class};
    }

}
