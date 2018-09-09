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
 * @date 2018/9/9 21:28
 * @description 异步执行任务抽象类
 * @version 1.0.0
 */
public abstract class AsyncCallable<T> implements AsyncFutureCallable<T> {

    @Override
    public T call() {
        return doAsync();
    }

    public abstract T doAsync();

    /**
     * 调用超时设置-单位毫秒(默认0-不超时)
     */
    @Override
    public long timeout() {
        return AsyncConstant.ASYNC_DEFAULT_TIME_OUT;
    }

    /**
     * 最多重试次数
     */
    @Override
    public int maxAttemps() {
        return AsyncConstant.ASYNC_DEFAULT_RETRY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Throwable>[] exceptions() {
        return new Class[]{Throwable.class};
    }

    @Override
    public final String cacheKey() {
        return null;
    }
}
