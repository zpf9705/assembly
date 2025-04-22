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


package top.osjf.sdk.core.caller;

import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.util.ReflectUtil;

/**
 * The {@code ComprehensiveWrapperInspectCallback} class is an abstract class that
 * extends from {@code WrapperInspectCallback}, designed to handle callbacks with
 * generic {@code Request} (REQ) and {@code Response} (RSP) types.
 *
 * <p>This class leverages generics to allow subclasses to specify concrete {@code Request}
 * and {@code Response} types, and provides corresponding success and exception handling
 * callback methods.
 *
 * <p>The definition of this class is relatively broad, and all methods do not need
 * to be rewritten. Inheritors can rewrite the corresponding types of methods as needed,
 * where the methods for {@code Request} and {@code Response} are all related types
 * specified by the inheritor after rewriting.
 *
 * @param <REQ> Subclass generic type of {@code Request<RSP>}.
 * @param <RSP> Subclass generic type of {@code Response}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class ComprehensiveWrapperInspectCallback<REQ extends Request<RSP>, RSP extends Response>
        extends WrapperInspectCallback<RSP> {

    /**
     * {@inheritDoc}
     * <p>
     * Unwrap {@code Request} and {@code Response} for generic designation.
     *
     * @param request  {@inheritDoc}
     * @param response {@inheritDoc}
     */
    @Override
    public final void success(@NotNull Request<?> request, @NotNull Response response) {
        successInternal(unwrapReq(request), unwrapRsp(response));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Unwrap {@code Request}  for generic designation.
     *
     * @param request {@inheritDoc}
     * @param e       {@inheritDoc}
     */
    @Override
    public final void exception(@NotNull Request<?> request, @NotNull Throwable e) {
        exceptionInternal(unwrapReq(request), e);
    }

    /**
     * Gets the expected {@code Request} type.
     *
     * <p>This method should be implemented by subclasses to return
     * a {@code Class} object representing the expected request type.
     *
     * @return a {@code Class} object representing the expected request type.
     */
    @NotNull
    public Class<REQ> getReqType() {
        return ReflectUtil.getSuperGenericClass(getClass(), 0);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Change the specified generic of the response class to 1.
     *
     * @return {@inheritDoc}
     */
    @NotNull
    @Override
    public Class<RSP> getType() {
        return ReflectUtil.getSuperGenericClass(getClass(), 1);
    }

    /**
     * This method is called to handle request and response that
     * matches the expected type.
     *
     * <p>This method should be implemented by subclasses to handle
     * the request and response object that matches the type.
     *
     * @param req the request object that matches the type.
     * @param rsp the response object that matches the type.
     */
    public void successInternal(@NotNull REQ req, @NotNull RSP rsp) {
        successInternal(rsp);
    }

    @Override
    public void successInternal(@NotNull RSP response) {
    }

    /**
     * This method is called to handle a request that matches the
     * expected type.
     *
     * <p>This method should be implemented by subclasses to handle
     * the request object that matches the type.
     *
     * @param req the request object that matches the type.
     * @param e   The {@code Throwable} object where the error occurred.
     */
    public void exceptionInternal(@NotNull REQ req, @NotNull Throwable e) {
        exception(req.matchSdkEnum().name(), e);
    }

    @Override
    public void exception(@NotNull String name, @NotNull Throwable e) {
    }

    /**
     * Unwrap a {@code Request} to expected  generic type.
     *
     * @param request input {@code Request}
     * @return {@code Request} instance after unwrap.
     * @throws ClassCastException An error is thrown when the expected
     *                            type is inconsistent with the type
     *                            provided by the callback.
     */
    protected final REQ unwrapReq(@NotNull Request<?> request) throws ClassCastException {
        Class<REQ> reqType = getReqType();
        if (!request.isWrapperFor(reqType)) {
            throw new ClassCastException(request.getClass().getName() + " cannot be cast to " + reqType.getName());
        }
        return request.unwrap(reqType);
    }
}
