/**
 * Copyright 2018-2118 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.async.spring.constant;

/**
 * @author liuyazhuang
 * @version 1.0.0
 * @date 2018/9/9 12:09
 * @description 异步注解常量
 */
public class AsyncConstant {
    /**
     * 线程名称
     */
    public static final String ASYNC_DEFAULT_THREAD_NAME = "Async-Pool";
    /**
     * 默认执行任务超时时间-单位毫秒（0表示不限制超时）
     */
    public static final long ASYNC_DEFAULT_TIME_OUT = 0;
    /**
     * 默认线程空闲超时时间
     */
    public static final long ASYNC_DEFAULT_KEEPALIVETIME = 60000l;
    /**
     * 默认跟踪日志关闭
     */
    public static boolean ASYNC_DEFAULT_TRACE_LOG = false;
    /**
     * 默认重试次数
     */
    public static final int ASYNC_DEFAULT_RETRY = 0;
}
