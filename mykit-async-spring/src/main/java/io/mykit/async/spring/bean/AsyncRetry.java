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
package io.mykit.async.spring.bean;

/**
 * @author liuyazhuang
 * @date 2018/9/9 20:59
 * @description 异步调用的重试信息
 * @version 1.0.0
 */
public class AsyncRetry {

    /**
     * 最大重试次数
     */
    private int maxAttemps;

    /**
     * 异常信息
     */
    private Class<?>[] exceptions;

    public AsyncRetry(){

    }

    public AsyncRetry(int maxAttemps, Class<?> ... exceptions){
        if(maxAttemps < 0){
            maxAttemps = 0;
        }
        this.maxAttemps = maxAttemps;
        this.exceptions = exceptions;
    }

    public int getMaxAttemps() {
        return maxAttemps;
    }

    public void setMaxAttemps(int maxAttemps) {
        if(maxAttemps < 0){
            maxAttemps = 0;
        }
        this.maxAttemps = maxAttemps;
    }

    public Class<?>[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(Class<?>[] exceptions) {
        this.exceptions = exceptions;
    }

    public boolean canRetry(){
        return maxAttemps > 0;
    }
}
