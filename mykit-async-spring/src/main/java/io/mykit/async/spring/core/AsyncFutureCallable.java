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

import java.util.concurrent.Callable;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:26
 * @description 异步任务执行接口
 * @version 1.0.0
 */
public interface AsyncFutureCallable<T> extends Callable<T> {
    /**
     * 超时时间
     */
    long timeout();

    /**
     * 最大重试次数
     */
    int maxAttemps();

    /**
     * 异常信息
     */
    Class<? extends Throwable>[] exceptions();

    /**
     * 缓存Key
     */
    String cacheKey();
}
