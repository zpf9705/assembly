package top.osjf.assembly.simplified.sdk.process;

/**
 * The default extension {@link ResponseData} is to check
 * whether the response is successful when the conditions
 * are met.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.6
 */
public interface InspectionResponseData extends ResponseData {

    /** {@inheritDoc}*/
    @Override
    default boolean inspectionResponseResult() {
        return true;
    }
}
