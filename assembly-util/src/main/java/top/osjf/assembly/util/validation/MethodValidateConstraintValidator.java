package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.data.Sixfold;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.validation.ConstraintDeclarationException;
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
public class MethodValidateConstraintValidator implements ConstraintValidator<SelfMethodValidate, Object> {

    private SelfMethodValidate selfMethodValidate;

    @Override
    public void initialize(SelfMethodValidate selfMethodValidate) {
        this.selfMethodValidate = selfMethodValidate;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isValid(Object validate, ConstraintValidatorContext context) {
        if (validate == null) {
            throw new ConstraintDeclarationException("The checksum does not want to be empty.");
        }
        Supplier validSupplier = getValidSupplier(validate);
        //The run value must be of Boolean type.
        Object result = validSupplier.get();
        //The type of return value needs to be provided as: Supplier<Boolean>
        if (!(result instanceof Boolean)) {
            throw new ConstraintDeclarationException("The type of return value needs to be provided " +
                    "as: Supplier<Boolean>");
        }
        if ((Boolean) result) {
            //Obtain the true result and proceed directly.
            return true;
        }
        //If there is a message, the exception information
        // of the message will be thrown first.
        if (StringUtils.isNotBlank(selfMethodValidate.message())) {
            return false;//The required Boolean value thrown.
        }
        //Secondly, disable default information and use dependent
        // classes to retrieve information.
        context.disableDefaultConstraintViolation(); //Disable default messages first
        Class<? extends Error> errorClass = selfMethodValidate.errorReply();
        //Specify a default information first.
        String failedMessageTemplate;
        if (Objects.equals(errorClass, Error.class)) {
            //see ifErrorClassDefault
            failedMessageTemplate = ifErrorClassDefault(validate);
        } else {
            //see instantiateReplyMessage
            failedMessageTemplate = instantiateReplyMessage(errorClass);
        }
        //Insert a new information template
        context.buildConstraintViolationWithTemplate(failedMessageTemplate)
                .addConstraintViolation();
        return false;//The required Boolean value thrown.
    }

    //@since 1.0.5
    @SuppressWarnings("rawtypes")
    private Supplier getValidSupplier(Object validate) {
        Supplier validSupplier;
        if (validate instanceof MethodValidate) {
            validSupplier = ((MethodValidate) validate).validate();
        } else {
            //Reflect to find methods with the same name and return
            // value or return value as Boolean.
            //Method names can ignore case.
            validSupplier = ReflectUtils.getAndCheckMethodValue(Sixfold.ofSixfoldWithFiveFold(
                    validate, MethodValidate.name, true, Supplier.class, null, null));
            if (validSupplier == null) {
                throw new ConstraintDeclarationException(
                        "If your self check body does not implement [top.osjf.assembly.util.validation" +
                                ".MethodValidate],then you need to provide a method named [getValidate] to " +
                                "ensure that the return value is [java. util. function. Supplier]");
            }
        }
        return validSupplier;
    }

    //@since 1.0.5
    private String ifErrorClassDefault(Object validate) {
        String failedMessage;
        //If using a high default exception dependency.
        if (validate instanceof Error) {
            failedMessage = ((Error) validate).message();
        } else {
            //next using reflection lookup to ignore case.
            failedMessage = ReflectUtils.getAndCheckMethodValue(
                    Sixfold.ofSixfoldWithFiveFold(validate, Error.name, true, String.class,
                            null, null));
        }
        if (StringUtils.isBlank(failedMessage)) {
            failedMessage = Error.DEFAULT.message();
        }
        return failedMessage;
    }

    //@since 1.0.5
    private String instantiateReplyMessage(Class<? extends Error> errorClass) {
        //If an incorrect attachment is specified,
        // then instantiate and obtain method information.
        //Please ensure that there are available constructions.
        String failedMessage = ReflectUtils.newInstance(errorClass).message();
        if (StringUtils.isBlank(failedMessage)) {
            failedMessage = Error.DEFAULT.message();
        }
        return failedMessage;
    }
}
