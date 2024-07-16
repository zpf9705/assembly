/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.osjf.sdk.core.support;

import cn.hutool.core.util.ReflectUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import top.osjf.sdk.core.exception.RequestCreateException;
import top.osjf.sdk.core.exception.UnknownRequestParameterException;
import top.osjf.sdk.core.exception.UnknownResponseParameterException;
import top.osjf.sdk.core.process.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Support classes for SDK.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class SdkSupport {

    /***The prefix name of the set method.*/
    protected static final String SET_METHOD_PREFIX = "set";

    /**
     * Create corresponding request parameters based on extension
     * type annotations and interfaces.
     *
     * @param method Proxy target method.
     * @param args   Request parameters.
     * @return The request class parameters created.
     * @see ResponseData
     * @see RequestParam
     */
    public static Request<?> invokeCreateRequest(Method method, Object[] args) {
        int length = ArrayUtils.isEmpty(args) ? 0 : args.length;
        Request<?> request;
        if (length == 1) {
            Object arg = args[0];
            if (arg instanceof Request) {
                //Consider first whether it is the actual request parameter.
                request = (Request<?>) arg;
            } else {
                //When a single parameter is not a request class, consider implementing
                // the parameter interface. If the parameter interface is not implemented,
                // an unknown exception will be thrown.
                if (arg instanceof RequestParameter) {
                    Class<? extends Request> requestType = ((RequestParameter) arg).getRequestType();
                    if (requestType == null) throw new UnknownRequestParameterException();
                    return invokeCreateRequestConstructorWhenFailedUseSet(requestType, arg);
                }
                throw new UnknownRequestParameterException();
            }
        } else {
            //Consider annotations for multiple parameters, and throw an
            // exception for unknown parameter types when there are no annotations present.
            RequestParam requestParam = method.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                return invokeCreateRequestConstructorWhenFailedUseSet(requestParam.value(), args);
            }
            throw new UnknownRequestParameterException();
        }
        return request;
    }

    /**
     * When obtaining a response, convert to the desired type, which can be
     * specified in {@link ResponseData}.
     *
     * @param method   Proxy target method.
     * @param response The response type obtained.
     * @return The required return object.
     */
    public static Object getResponse(Method method, Response response) {
        if (Response.class.isAssignableFrom(method.getReturnType())) {
            //If the method returns a response class, then return it directly.
            return response;
        }
        if (response instanceof ResponseData) {
            ResponseData responseData = (ResponseData) response;
            if (responseData.inspectionResponseResult()) {
                //If the type value is specified, the data returned
                // when the request is successful is obtained.
                if (responseData.isSuccess()) {
                    return responseData.getData();
                } else {
                    //The default data returned when a customization request fails.
                    if (responseData instanceof InspectionResponseData) {
                        Object data = ((InspectionResponseData) responseData).failedSeatData();
                        if (data != null) {
                            //If it is empty, the requested data should prevail.
                            return data;
                        }
                    }
                }
            }
            //On the contrary, directly return the data result.
            return responseData.getData();
        }
        throw new UnknownResponseParameterException();
    }

    static Request<?> invokeCreateRequestConstructorWhenFailedUseSet(Class<? extends Request> requestType,
                                                                     Object... args) {
        Request<?> request;
        try {
            //First, directly instantiate the request class using the
            // construction method based on the parameters.
            request = ReflectUtil.newInstance(requestType, args);
        } catch (Throwable e) {
            //This step determines whether the parameter is empty to
            // determine whether the above is an empty construction instantiation.
            if (ArrayUtils.isEmpty(args)) throw new RequestCreateException(e);
            request = invokeCreateRequestUseSet(requestType, args);
        }
        return request;
    }

    static Request<?> invokeCreateRequestUseSet(Class<? extends Request> requestType,
                                                Object... args) {
        Request<?> request;
        try {
            //When parameter construction fails, first use an empty
            // construction to instantiate, and then find the set method.
            request = ReflectUtil.newInstance(requestType);
            Field[] fields =
                    ReflectUtil.getFields(requestType, field -> field.getAnnotation(RequestField.class) != null);
            if (ArrayUtils.isEmpty(fields)) {
                //When using methods for assignment, annotations must be identified.
                throw new IllegalArgumentException("When no construction method is provided, please " +
                        "use \"top.osjf.assembly.simplified.sdk.process.RequestField\" to mark the real" +
                        " name of the field.");
            }
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                RequestField requestField = field.getAnnotation(RequestField.class);
                int order = getOrder(requestField, i, fields);
                Object arg = args[order];
                if (requestField.useReflect()) {
                    //Directly assign values in the case of sequential reflection assignment.
                    ReflectUtil.setFieldValue(request, field, arg);
                } else {
                    //The real name of the field cannot be empty at this time.
                    String fieldName = requestField.value();
                    if (StringUtils.isBlank(fieldName)) {
                        throw new IllegalArgumentException("When using the set method to set a value, " +
                                "the actual field name cannot be empty.");
                    }
                    //The set method performs an assignment.
                    ReflectUtil.invoke(request,
                            SET_METHOD_PREFIX + Character.toUpperCase(fieldName.charAt(0))
                                    + fieldName.substring(1), arg);
                }
            }
        } catch (Throwable e) {
            //There is no remedy at this step, simply throw an exception.
            throw new RequestCreateException(e);
        }
        return request;
    }

    static int getOrder(RequestField requestField, int i, Field[] fields) {
        int order = requestField.order();
        if (order == -1) {
            //If the default value is used for sorting,
            // it will be sorted in the default order of times.
            order = i;
        } else {
            if (order >= fields.length) {
                throw new ArrayIndexOutOfBoundsException("Current order " + order + "," +
                        " parameter length " + fields.length + ".");
            }
        }
        return order;
    }
}
