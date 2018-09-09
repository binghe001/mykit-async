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

import io.mykit.async.spring.bean.AsyncMethod;
import io.mykit.async.spring.bean.AsyncRetry;
import io.mykit.async.spring.cache.AsyncProxyCache;
import io.mykit.async.spring.config.AsyncConfigurer;
import io.mykit.async.spring.config.DefaultAsyncConfigurer;
import io.mykit.async.spring.config.ThreadPoolConfiguration;
import io.mykit.async.spring.constant.HandleMode;
import io.mykit.async.spring.exception.AsyncException;
import io.mykit.async.spring.inject.TransactionBuilder;
import io.mykit.async.spring.pool.AsyncTaskThreadPool;
import io.mykit.async.spring.pool.NamedThreadFactory;
import io.mykit.async.spring.pool.RunnableAround;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author liuyazhuang
 * @date 2018/9/9 21:30
 * @description 异步执行器
 * @version 1.0.0
 */
public class AsyncExecutor {

    private final static Logger logger = LoggerFactory.getLogger(AsyncExecutor.class);

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicBoolean destroyed = new AtomicBoolean(false);

    private static AsyncTaskThreadPool threadPool;
    private static TransactionBuilder transactionBuilder;


    public static void checkArgument(ThreadPoolConfiguration configuration) {
        Assert.notNull(configuration, "thread pool configuration propertie not be null");
        Assert.notNull(configuration.getAllowCoreThreadTimeout(), "configuration propertie async.allowCoreThreadTimeout not be null");
        Assert.notNull(configuration.getCorePoolSize(), "configuration propertie async.allowCoreThreadTimeout not be null");
        Assert.notNull(configuration.getKeepAliveTime(), "configuration propertie async.allowCoreThreadTimeout not be null");
        Assert.notNull(configuration.getMaxAcceptCount(), "configuration propertie async.allowCoreThreadTimeout not be null");
        Assert.notNull(configuration.getMaxPoolSize(), "configuration propertie async.allowCoreThreadTimeout not be null");
        Assert.hasText(configuration.getRejectedExecutionHandler(), "configuration propertie async.allowCoreThreadTimeout not be null");
    }

    public static void initializeThreadPool(ThreadPoolConfiguration threadPoolConfiguration) {
        checkArgument(threadPoolConfiguration);
        initializeThreadPool(threadPoolConfiguration.getCorePoolSize(), threadPoolConfiguration.getMaxPoolSize(), threadPoolConfiguration.getMaxAcceptCount(),
                threadPoolConfiguration.getRejectedExecutionHandler(), threadPoolConfiguration.getKeepAliveTime(), threadPoolConfiguration.getAllowCoreThreadTimeout());
    }

    private static void initializeThreadPool(Integer corePoolSize, Integer maxPoolSize, Integer maxAcceptCount, String rejectedExecutionHandler,
                                             Long keepAliveTime, Boolean allowCoreThreadTimeout) {

        if (!initialized.get()) {
            initialized.set(true);
            HandleMode handleMode = HandleMode.CALLERRUN;
            if (StringUtils.hasText(rejectedExecutionHandler)) {
                if (!HandleMode.REJECT.toString().equals(rejectedExecutionHandler) && !HandleMode.CALLERRUN.toString().equals(rejectedExecutionHandler)) {
                    throw new IllegalArgumentException("Invalid configuration properties async.rejectedExecutionHandler");
                }
                if (HandleMode.REJECT.toString().equals(rejectedExecutionHandler)) {
                    handleMode = HandleMode.REJECT;
                }
            }
            RejectedExecutionHandler handler = getRejectedHandler(handleMode);
            BlockingQueue<Runnable> queue = createQueue(maxAcceptCount);
            threadPool = new AsyncTaskThreadPool(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, queue, handler, new NamedThreadFactory());
            threadPool.getThreadPoolExecutor().allowCoreThreadTimeOut(allowCoreThreadTimeout);
            logger.info("ThreadPoolExecutor initialize info corePoolSize:{} maxPoolSize:{} maxAcceptCount:{} rejectedExecutionHandler:{}", corePoolSize, maxPoolSize, maxAcceptCount, handleMode);
        }
    }


    public static <T> void execute(AsyncCallable<T> task) {
        submit(task);
    }

    public static <T> void execute(AsyncFutureCallable<T> callable, AsyncFutureCallback<T> callback){
        submit(callable, callback);
    }

    public static <T> AsyncFutureTask<T> submit(AsyncFutureCallable<T> callable) {
        return submit(callable, null);
    }

    public static <T> AsyncFutureTask<T> submit(AsyncFutureCallable<T> callable, AsyncFutureCallback<T> callback) {
        if (!initialized.get()) {
            AsyncConfigurer asyncConfigurer = new DefaultAsyncConfigurer();
            ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
            asyncConfigurer.configureThreadPool(threadPoolConfiguration);
            initializeThreadPool(threadPoolConfiguration);
        }
        AsyncMethod method = buildAsyncMethod(callable);
        if (callable instanceof TransactionCallable) {
            callable = executeTransaction(callable);
        }
        return threadPool.submit(callable, callback, method);
    }

    public static void destroy() throws Exception {
        if (initialized.get() && (threadPool != null)) {
            threadPool.destroy();
            threadPool = null;
        }
    }

    public static <T> AsyncCallable<T> executeTransaction(final AsyncFutureCallable<T> callable) {
        if (transactionBuilder == null) {
            throw new AsyncException("you should integration spring transaction");
        }
        return new AsyncCallable<T>() {
            @Override
            public T doAsync() {
                return transactionBuilder.execute(callable);
            }
        };
    }

    private static <T> AsyncMethod buildAsyncMethod(AsyncFutureCallable<T> callable) {
        if (callable.cacheKey() != null) {
            AsyncMethod method = AsyncProxyCache.getAsyncMethod(callable.cacheKey());
            if (method != null) {
                return method;
            }
        }
        AsyncMethod method = new AsyncMethod(null, null, callable.timeout(), new AsyncRetry(callable.maxAttemps(), callable.exceptions()));
        Class<?> returnClass = ReflectionHelper.getGenericClass(callable.getClass());
        if (Void.TYPE.isAssignableFrom(returnClass) || Void.class.equals(returnClass)) {
            method.setVoid(true);
        }
        return method;
    }


    private static BlockingQueue<Runnable> createQueue(int acceptCount) {
        if (acceptCount > 0) {
            return new LinkedBlockingQueue<Runnable>(acceptCount);
        } else {
            return new SynchronousQueue<Runnable>();
        }
    }

    private static RejectedExecutionHandler getRejectedHandler(HandleMode mode) {
        return HandleMode.REJECT == mode ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.CallerRunsPolicy();
    }

    public static boolean isDestroyed() {
        return destroyed.get();
    }

    public static void setIsDestroyed(boolean isDestroyed) {
        AsyncExecutor.destroyed.set(true);
    }

    public static void setTransactionBuilder(TransactionBuilder transactionBuilder) {
        AsyncExecutor.transactionBuilder = transactionBuilder;
    }

    public static void setRunnableAround(RunnableAround runnableAround) {
        if (threadPool != null) {
            threadPool.setRunnableAround(runnableAround);
        }
    }

    public static AsyncTaskThreadPool getAsyncTaskThreadPool() {
        return threadPool;
    }
}
