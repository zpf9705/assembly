package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The executor for checking the running results of the method can uniformly
 * verify the parameters within this method, and simply return a false result to
 * throw the corresponding result error message.
 * <p>The implementation relies on {@link ConstraintValidator}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class MethodValidateConstraintValidator implements ConstraintValidator<SelfMethodValidate, MethodValidate> {

    private SelfMethodValidate selfMethodValidate;

    @Override
    public void initialize(SelfMethodValidate selfMethodValidate) {
        this.selfMethodValidate = selfMethodValidate;
    }

    @Override
    public boolean isValid(MethodValidate validate, ConstraintValidatorContext context) {
        Supplier<Boolean> validSupplier = validate.getValidate();
        //Execution result does not want to be empty.
        Objects.requireNonNull(validSupplier, "The executor does not want to be empty.");
        if (validSupplier.get()) {
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
        Class<Error> errorClass = selfMethodValidate.errorReply();
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
