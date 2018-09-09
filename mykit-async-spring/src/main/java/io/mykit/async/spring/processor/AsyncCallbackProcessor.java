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

import io.mykit.async.spring.core.AsyncFutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:51
 * @description 异步回调处理器
 * @version 1.0.0
 */
public class AsyncCallbackProcessor {

    private final static Logger logger = LoggerFactory.getLogger(AsyncCallbackProcessor.class);

    public static <V> void doCallback(AsyncFutureCallback<V> futureCallback, RetryResult<V> result) {

        if (futureCallback != null) {
            try {
                if (result.getThrowable() != null) {
                    futureCallback.onFailure(result.getThrowable());
                } else {
                    futureCallback.onSuccess(result.getData());
                }
            } catch (Throwable e) {
                logger.error("async callback error", e);
            }
        }
    }
}
