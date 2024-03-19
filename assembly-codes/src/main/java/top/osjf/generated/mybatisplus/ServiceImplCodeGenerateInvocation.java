package top.osjf.generated.mybatisplus;

import top.osjf.generated.CodeGenerateInvocation;

/**
 * The implementation class interface of service sets the entry
 * point after mapper and service generation.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.0
 */
public interface ServiceImplCodeGenerateInvocation extends CodeGenerateInvocation {

    /**
     * Set the mapper to access the generated {@link CodeGenerateInvocation}.
     * @param mapperCodeGenerateInvocation Construct the mapper interface
     *                                     for accessing the corresponding entity target.
     * @return The implementation class of the current service itself.
     */
    ServiceImplCodeGenerateInvocation mapper(CodeGenerateInvocation mapperCodeGenerateInvocation);

    /**
     * Set the service to access the generated {@link CodeGenerateInvocation}.
     * @param ServiceCodeGenerateInvocation Construct the service interface
     *                                      for accessing the corresponding entity target.
     * @return The implementation class of the current service itself.
     */
    ServiceImplCodeGenerateInvocation service(CodeGenerateInvocation ServiceCodeGenerateInvocation);
}
