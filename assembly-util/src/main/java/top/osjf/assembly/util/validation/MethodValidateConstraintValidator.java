package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The executor for checking the running results of the method can uniformly
 * verify the parameters within this method, and simply return a false result to
 * throw the corresponding result error message.
 * <p>The implementation relies on {@link ConstraintValidator}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class MethodValidateConstraintValidator implements ConstraintValidator<SelfMethodValidate, Object> {

    private SelfMethodValidate selfMethodValidate;

    @Override
    public void initialize(SelfMethodValidate selfMethodValidate) {
        this.selfMethodValidate = selfMethodValidate;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isValid(Object validate, ConstraintValidatorContext context) {
        Objects.requireNonNull(validate, "The checksum does not want to be empty.");
        Supplier validSupplier;
        if (validate instanceof MethodValidate) {
            validSupplier = ((MethodValidate) validate).getValidate();
        } else {
            //Reflect to find methods with the same name and return
            // value or return value as Boolean.
            //Method names can ignore case.
            Method method = ReflectUtils.getMethod(validate.getClass(), true, MethodValidate.name);
            if (method != null && Objects.equals(method.getReturnType(), Supplier.class)) {
                validSupplier = ReflectUtils.invoke(validate, method);
            } else {
                throw new IllegalArgumentException(
                        "If your self check body does not implement [top.osjf.assembly.util.validation" +
                                ".MethodValidate],then you need to provide a method named [getValidate] to " +
                                "ensure that the return value is [java. util. function. Supplier]");
            }
        }
        //Execution result does not want to be empty.
        Objects.requireNonNull(validSupplier, "The executor does not want to be empty.");
        //The run value must be of Boolean type.
        Object result = validSupplier.get();
        if (!(result instanceof Boolean)) {
            throw new IllegalArgumentException("The run value must be of Boolean type.");
        }
        if ((Boolean) result) {
            //Obtain the true result and proceed directly.
            return true;
        }
        //If there is a message, the exception information
        // of the message will be thrown first.
        if (StringUtils.isNotBlank(selfMethodValidate.message())) {
            return false;
        }
        //Secondly, disable default information and use dependent
        // classes to retrieve information.
        context.disableDefaultConstraintViolation();
        Class<? extends Error> errorClass = selfMethodValidate.errorReply();
        String failedMessageTemplate;
        if (Objects.equals(errorClass, Error.class)) {
            //Error.class use default
            failedMessageTemplate = Error.DEFAULT.getError();
        } else {
            //Instantiate and take it.
            failedMessageTemplate = ReflectUtils.newInstance(errorClass).getError();
        }
        context.buildConstraintViolationWithTemplate(failedMessageTemplate)
                .addConstraintViolation();
        return false;
    }
}
