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
package io.mykit.async.spring.proxy;

import io.mykit.async.spring.constant.AsyncConstant;
import io.mykit.async.spring.core.AsyncFutureTask;
import io.mykit.async.spring.utils.CommonUtil;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author liuyazhuang
 * @date 2018/9/9 22:00
 * @description 异步方法拦截器
 * @version 1.0.0
 */
@SuppressWarnings("all")
public class AsyncResultInterceptor implements MethodInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(AsyncResultInterceptor.class);

    private AsyncFutureTask future;

    private long timeout;

    public AsyncResultInterceptor(AsyncFutureTask future, long timeout) {
        this.future = future;
        this.timeout = timeout;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (AsyncConstant.ASYNC_DEFAULT_TRACE_LOG) {
            logger.debug("start call future:{},object:{} method:{}", future, object.getClass().getName(), CommonUtil.buildMethod(method));
        }
        if (!ReflectionHelper.canProxyInvoke(method)) {
            return ReflectionHelper.invoke(object, args, method);
        }
        object = loadFuture();
        if (object != null) {
            return ReflectionHelper.invoke(object, args, method);
        }
        return null;
    }

    private Object loadFuture() throws Throwable {
        try {
            return future.getValue(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (InterruptedException e) {
            throw e;
        } catch (Throwable e) {
            throw ReflectionHelper.getThrowableCause(e);
        }
    }
}
