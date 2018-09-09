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

import io.mykit.async.spring.bean.AsyncMethod;
import io.mykit.async.spring.bean.AsyncRetry;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:52
 * @description 异步重试处理器
 * @version 1.0.0
 */
public class AsyncRetryProcessor {

    private final static Logger logger = LoggerFactory.getLogger(AsyncRetryProcessor.class);

    public static <T> RetryResult<T> handler(Callable<T> callable, AsyncMethod asyncMethod) {
        RetryResult<T> result = new RetryResult<T>();
        AsyncRetry retry = asyncMethod.getRetry();
        if (retry == null) {
            return result;
        }
        T t = null;
        int i = 1;
        while (retry.getMaxAttemps() >= i) {
            logger.info("async processor trying to retry {} invocation; object:{} method:{}", i, asyncMethod.getObject(), asyncMethod.getMethod());
            result.setThrowable(null);
            try {
                t = callable.call();
            } catch (Throwable e) {
                result.setThrowable(e);
                logger.error("retry " + i + " invoke error", ReflectionHelper.getThrowableCause(e));
                if (!matchThrowable(retry, e)) {
                    break;
                }
            }
            ++i;
        }
        result.setData(t);
        return result;
    }

    private static boolean matchThrowable(AsyncRetry retry, Throwable e) {
        if (retry == null) {
            return false;
        }
        for (Class<?> clzss : retry.getExceptions()) {
            if (clzss.isAssignableFrom(e.getClass()) || clzss.equals(e.getClass())) {
                return true;
            }
        }
        return false;
    }
}
