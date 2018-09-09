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
package io.mykit.async.spring.utils;

import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author liuyazhuang
 * @date 2018/9/9 22:08
 * @description 通用工具类
 * @version 1.0.0
 */
public class CommonUtil {

    public static Class<?> getClass(Object object) {
        boolean isCglibProxy = false;
        if (AopUtils.isCglibProxy(object)) {
            isCglibProxy = true;
        }
        if (isCglibProxy || ClassUtils.isCglibProxy(object)) {
            isCglibProxy = true;
        }
        Class<?> targetClass = object.getClass();
        if (isCglibProxy) {
            targetClass = targetClass.getSuperclass();
        }
        return targetClass;
    }

    public static String buildkey(Object object, Method method) {
        ValidationUtils.checkNotNull(object);
        return new StringBuilder(getClass(object).getName()).append(".").append(buildMethod(method)).toString();
    }

    public static String buildkey(String name, boolean b) {
        ValidationUtils.checkNotNull(name);
        return new StringBuilder(name).append("#").append(b).toString();
    }

    public static String buildMethod(Method method) {
        StringBuilder strbuilder = new StringBuilder(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                strbuilder.append("#").append(parameterType.getName());
            }
        }
        return strbuilder.toString();
    }

    /**
     * <p>
     * <p>
     * 校验是否只包含 数字0-9 是 返回true 否则返回false
     * </p>
     */
    public static boolean isNumber(String number) {
        String regex = "^[0-9]+$";
        return Pattern.matches(regex, number);
    }
}
