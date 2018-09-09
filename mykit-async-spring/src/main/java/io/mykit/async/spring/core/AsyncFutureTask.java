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
import io.mykit.async.spring.processor.AsyncCallbackProcessor;
import io.mykit.async.spring.processor.AsyncRetryProcessor;
import io.mykit.async.spring.processor.RetryResult;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:36
 * @description 异步执行任务类
 * @version 1.0.0
 */
public class AsyncFutureTask<V> extends FutureTask<V> {

    private final static Logger logger = LoggerFactory.getLogger(AsyncFutureTask.class);

    private long startTime = 0;

    private long endTime = 0;

    private volatile V value;

    private int counter;

    private AsyncMethod method;

    private Callable<V> callable;

    private AsyncFutureCallback<V> futureCallback;

    private final ReentrantLock mainLock = new ReentrantLock();

    private final Condition condition = mainLock.newCondition();

    public AsyncFutureTask(Callable<V> callable, AsyncFutureCallback<V> futureCallback, AsyncMethod method) {
        super(callable);
        this.callable = callable;
        this.method = method;
        counter = AsyncCounter.intValue();
        if (futureCallback != null) {
            this.futureCallback = futureCallback;
        }
    }

    @Override
    protected void done() {
        endTime = System.currentTimeMillis();
        if (counter >= 0) {
            AsyncCounter.release();
        }
        RetryResult<V> result = new RetryResult<V>();
        if (super.isCancelled()) {
            AsyncCallbackProcessor.doCallback(futureCallback, result.setThrowable(new TimeoutException()));
            return;
        }

        if (needCallbackAndRetry()) {
            try {
                result.setData(value = innerGetValue(method.getTimeout(), TimeUnit.MILLISECONDS));
            } catch (Throwable e) {
                result.setThrowable(e);
                if (method.isVoid() || getMaxAttemps() > 0) {
                    logger.error("future invoke error", ReflectionHelper.getThrowableCause(e));
                }
                result = AsyncRetryProcessor.handler(callable, method);
                value = result.getData();
            } finally {
                if (needLock()) {
                    final ReentrantLock mainLock = this.mainLock;
                    mainLock.lock();
                    try {
                        condition.signal();
                    } finally {
                        mainLock.unlock();
                    }
                }
                AsyncCallbackProcessor.doCallback(futureCallback, result);
            }
        }
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        if (counter >= 0) {
            AsyncCounter.set(++counter);
        }
        super.run();
    }

    public void syncRun() {
        counter = -1;
        run();
    }

    public V getValue(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        timeout = method.getTimeout();
        if (needCallbackAndRetry()) {
            if (needLock()) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    if (value == null) {
                        if (timeout > 0)
                            condition.await(timeout, unit);
                        else
                            condition.await();
                    }
                } finally {
                    mainLock.unlock();
                }
                if (value == null)
                    throw new TimeoutException();
            }
        }
        return innerGetValue(timeout, unit);
    }

    private V innerGetValue(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        if (value == null) {
            long startRunTime = System.currentTimeMillis();
            if (timeout <= 0) {
                value = super.get();
            } else {
                value = super.get(timeout, unit);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("invoking time:{} load time:{} timeout:{}", this.endTime - this.startTime, System.currentTimeMillis() - startRunTime, timeout);
            }
        }
        return value;
    }

    private boolean needLock() {
        if (method.getTimeout() > 0 && !method.isVoid() && getMaxAttemps() > 0) {
            return true;
        }
        return false;
    }

    private boolean needCallbackAndRetry() {
        if (futureCallback != null || getMaxAttemps() > 0 || method.isVoid()) {
            return true;
        }
        return false;
    }

    public int getMaxAttemps() {
        if (method.getRetry() == null)
            return 0;
        return method.getRetry().getMaxAttemps();
    }

    public int getCounter() {
        return counter;
    }
}
