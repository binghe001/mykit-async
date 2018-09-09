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
package io.mykit.async.spring.pool;

import io.mykit.async.spring.bean.AsyncMethod;
import io.mykit.async.spring.core.AsyncFutureCallback;
import io.mykit.async.spring.core.AsyncFutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:47
 * @description 异步任务线程池
 * @version 1.0.0
 */
public class AsyncTaskThreadPool {

    private static Logger logger = LoggerFactory.getLogger(AsyncTaskThreadPool.class);

    private ThreadPoolExecutor executor = null;

    private RunnableAround runnableAround;

    private int corePoolSize;

    public AsyncTaskThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue) {
        this.corePoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public AsyncTaskThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this.corePoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public AsyncTaskThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, ThreadFactory threadFactory) {
        this.corePoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }


    public <T> AsyncFutureTask<T> submit(Callable<T> callable, AsyncFutureCallback<T> callback, AsyncMethod method) {
        if (callable == null) throw new NullPointerException();
        AsyncFutureTask<T> futureTask = new AsyncFutureTask<T>(callable, callback, method);
        if (futureTask.getCounter() > 0 && corePoolSize <= executor.getActiveCount()) {
            futureTask.syncRun();
            return futureTask;
        }
        execute(futureTask);
        return futureTask;
    }

    private void execute(Runnable command) {
        if (runnableAround != null) {
            command = runnableAround.advice(command);
        }
        executor.execute(command);
    }

    public void destroy() throws Exception {
        if (!executor.isShutdown()) {
            executor.shutdown();
            boolean loop = true;
            do {
                loop = executor.awaitTermination(2000, TimeUnit.MILLISECONDS);
                logger.info("Wait for the async thread to finish the work; The remaining queue size: {}", executor.getQueue().size());
            } while (!loop);
            logger.info("AsyncThreadTaskPool destroyed {}", executor.toString());
            executor = null;
        }
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return executor;
    }

    public void setRunnableAround(RunnableAround runnableAround) {
        this.runnableAround = runnableAround;
    }
}
