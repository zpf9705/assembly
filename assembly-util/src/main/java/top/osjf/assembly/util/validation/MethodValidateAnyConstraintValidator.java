package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.data.Sixfold;
import top.osjf.assembly.util.lang.ClassUtils;
import top.osjf.assembly.util.lang.MethodReturnTypeNoEqualException;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.function.Supplier;

/**
 * The validator of {@link SelfMethodValidateAny}, which extends the
 * idea of {@link SelfMethodValidate}, maintains the format of the
 * validation method and adds the name of the specified method to the
 * provided aspect of the error dependency.
 * @see SelfMethodValidateAny
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.5
 */
public class MethodValidateAnyConstraintValidator implements ConstraintValidator<SelfMethodValidateAny, Object> {

    private SelfMethodValidateAny selfMethodValidateAny;

    @Override
    public void initialize(SelfMethodValidateAny selfMethodValidateAny) {
        this.selfMethodValidateAny = selfMethodValidateAny;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isValid(Object validate, ConstraintValidatorContext context) {
        if (validate == null) {
            throw new ConstraintDeclarationException("The checksum does not want to be empty.");
        }
        Supplier validSupplier = getValidSupplier(validate, selfMethodValidateAny.selfCheckMethod());
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
        String failedReply = selfMethodValidateAny.selfCheckFailedReply();
        //If there are no errors attached, the default message will be used.
        if (StringUtils.isBlank(failedReply)) {
            return false;
        }
        context.disableDefaultConstraintViolation();//Disable default messages first
        String failedMessageTemplate;
        //Priority should be given to the methods in the checksum.
        failedMessageTemplate = ifValidateMethodGiven(validate, failedReply);
        //The above search method found null, indicating that
        // the method was found without providing a return value.
        //Next, consider the dependent category.
        if (StringUtils.isBlank(failedMessageTemplate)) {
            failedMessageTemplate = ifErrorInstantiate(failedReply);
            if (StringUtils.isBlank(failedMessageTemplate)){
                //Occurred when rewriting but not provided.
                failedMessageTemplate = Error.DEFAULT.message();
            }
        }
        //Insert a new information template
        context.buildConstraintViolationWithTemplate(failedMessageTemplate)
                .addConstraintViolation();
        return false;//The required Boolean value thrown.
    }

    @SuppressWarnings("rawtypes")
    private Supplier getValidSupplier(Object validate, String validateMethodName) {
        //Reflect to find methods with the same name and return
        // value or return value as Boolean.
        //Method names can ignore case.
        Supplier validSupplier;
        try {
            validSupplier = ReflectUtils.getAndCheckMethodValue(
                    Sixfold.ofSixfoldWithFiveFold(validate, validateMethodName, true, Supplier.class,
                            null, null));
        } catch (NoSuchMethodException e) {
            throw new ConstraintDeclarationException(
                    "Self check body does not hava method named [" + validateMethodName + "]");
        } catch (MethodReturnTypeNoEqualException e) {
            throw new ConstraintDeclarationException(
                    "Ensure the method named [" + validateMethodName + "] return value is [" +
                            Supplier.class.getName() + "]");
        }
        return validSupplier;
    }

    private String ifValidateMethodGiven(Object validate, String failedReply) {
        //Query methods and obtain results.
        String methodValue;
        try {
            methodValue = ReflectUtils.getAndCheckMethodValue(
                    Sixfold.ofSixfoldWithFiveFold(validate, failedReply, true, String.class,
                            null, null));
        } catch (NoSuchMethodException | MethodReturnTypeNoEqualException e) {
            return null;
        }
        return StringUtils.isNotBlank(methodValue) ? methodValue : Error.DEFAULT.message();
    }

    private String ifErrorInstantiate(String failedReply) {
        Class<?> clazz;
        try {
            clazz = ClassUtils.getClass(failedReply, false);
        } catch (ClassNotFoundException e) {
            //The default information is also used for all paths that are not classes.
            return Error.DEFAULT.message();
        }
        if (!Error.class.isAssignableFrom(clazz)) {
            //The error interface is not implemented, and default information is used.
            return Error.DEFAULT.message();
        }
        Object instance = ReflectUtils.newInstance(clazz);
        return ((Error) instance).message();
    }
}
