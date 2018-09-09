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

import io.mykit.async.spring.bean.AsyncMethod;
import io.mykit.async.spring.cache.AsyncProxyCache;
import io.mykit.async.spring.core.AsyncExecutor;
import io.mykit.async.spring.core.AsyncFutureCallable;
import io.mykit.async.spring.core.AsyncFutureTask;
import io.mykit.async.spring.exception.AsyncException;
import io.mykit.async.spring.utils.CommonUtil;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:54
 * @description 异步方法拦截器
 * @version 1.0.0
 */
public class AsyncMethodInterceptor implements MethodInterceptor {

    private Object targetObject;

    public AsyncMethodInterceptor(Object targetObject) {

        this.targetObject = targetObject;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        final String cacheKey = CommonUtil.buildkey(targetObject, method);

        final AsyncMethod asyncMethod = AsyncProxyCache.getAsyncMethod(cacheKey);

        if (asyncMethod == null || !ReflectionHelper.canProxyInvoke(method)) {
            return ReflectionHelper.invoke(targetObject, args, method);
        }
        if (AsyncExecutor.isDestroyed()) {
            return ReflectionHelper.invoke(asyncMethod.getObject(), args, method);
        }

        final Object[] finArgs = args;

        AsyncFutureTask<Object> future = AsyncExecutor.submit(new AsyncFutureCallable<Object>() {

            @Override
            public Object call() throws Exception {
                try {
                    return ReflectionHelper.invoke(asyncMethod.getObject(), finArgs, asyncMethod.getMethod());
                } catch (Throwable e) {
                    throw new AsyncException(e);
                }
            }

            @Override
            public int maxAttemps() {
                return asyncMethod.getRetry().getMaxAttemps();
            }

            @Override
            public long timeout() {
                return asyncMethod.getTimeout();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Class<? extends Throwable>[] exceptions() {
                return new Class[]{TimeoutException.class};
            }

            @Override
            public String cacheKey() {
                return cacheKey;
            }
        });
        if (asyncMethod.isVoid()) {
            return null;
        }

        return new AsyncResultProxy(future).buildProxy(method.getReturnType(), asyncMethod.getTimeout(), true);

    }
}
