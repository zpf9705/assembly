package top.osjf.assembly.simplified.sdk.process;

import top.osjf.assembly.simplified.sdk.SdkUtils;
import top.osjf.assembly.util.annotation.CanNull;

import java.lang.reflect.Method;

/**
 * The default extension {@link ResponseData} is to check
 * whether the response is successful when the conditions
 * are met.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
 */
public interface InspectionResponseData extends ResponseData {

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean inspectionResponseResult() {
        return true;
    }

    /**
     * Check the placeholder data returned when the request fails,
     * instead of {@literal null}, to enhance the applicability
     * of SDK calls.
     *
     * <p>Analyze an example to see {@link SdkUtils#getResponse(Method, Response)}.
     *
     * @return Placeholder returns data, which may be a global default
     * value,can be {@literal null}, adapted according to oneself.
     */
    @CanNull
    Object failedSeatData();
}
