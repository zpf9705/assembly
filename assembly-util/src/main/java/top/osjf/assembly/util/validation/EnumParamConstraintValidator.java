package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;

/**
 * It is a validator for {@link EnumParam} annotations, relying on
 * {@link ConstraintValidator} implementation.
 * <p>The specific logic can refer to the following two methods.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class EnumParamConstraintValidator implements ConstraintValidator<EnumParam, Object> {

    private EnumParam enumParam;

    private EnumValidate<? extends Enum<?>, ?>[] validates;

    @Override
    public void initialize(EnumParam enumParam) {
        this.enumParam = enumParam;
        this.validates = enumParam.value().getEnumConstants();
        if (ArrayUtils.isEmpty(validates)) {
            //Here, a validation exception is directly thrown for empty enumeration values.
            throw new IllegalArgumentException("No enumeration values were " +
                    "found in " + enumParam.value().getName() + ", unable to perform validation operation.");
        }
    }

    @Override
    public boolean isValid(Object sign, ConstraintValidatorContext context) {
        if (Objects.isNull(sign)) {
            //The verification of whether it is empty is not supported here.
            // If necessary, please provide additional support.
            return true;
        }
        if (Arrays.stream(validates).anyMatch(v -> Objects.equals(sign, v.getSign()))) {
            //A single match is considered successful.
            return true;
        }
        // If the verification fails, assemble the prompt statement based on the language type.
        context.disableDefaultConstraintViolation(); // Disable default message values.
        String failedMessage;
        if (StringUtils.isNotBlank(enumParam.message())) {
            //Prioritize using default messages.
            failedMessage = enumParam.message();
        } else {
            //Secondly, format the template using language.
            failedMessage = enumParam.language().format(validates);
        }
        // Add error prompt statement again.
        context.buildConstraintViolationWithTemplate(failedMessage)
                .addConstraintViolation();
        return false;
    }
}
