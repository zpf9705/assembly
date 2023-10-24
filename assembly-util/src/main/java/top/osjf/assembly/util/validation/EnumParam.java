package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.lang.ArrayUtils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An annotation for verifying the input value of an enumeration.
 * <p>If the input enumeration value {} is not within the specified range,
 * the given prompt information will be thrown in the form of an exception
 * according to the following fields.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumParamConstraintValidator.class)
@Documented
public @interface EnumParam {

    /**
     * A class object that enumerates objects.
     * <p>And it is necessary to implement {@link EnumValidate} and define some plans.
     *
     * @return Must be filled in.
     * @see EnumValidate
     */
    Class<? extends EnumValidate<? extends Enum<?>>> value();

    /**
     * Format language templates.
     * <p>Only when {@link #message()} is empty will its template be used.
     *
     * @return {@link Language}.
     */
    Language language() default Language.CHINESE;

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    enum Language {

        CHINESE("%s必须在指定范围[%s]内传入该参数"),

        ENGLISH("%s parameter must be passed in within the specified range [%s]");

        private final String template;

        Language(String template) {
            this.template = template;
        }

        public String format(EnumValidate<? extends Enum<?>>[] rules) {
            if (ArrayUtils.isEmpty(rules)) {
                return null;
            }
            return String.format(this.template, rules[0].getMean(),
                    Arrays.stream(rules).map(rule -> rule.getSign() + " : " + rule.getDesc())
                            .collect(Collectors.joining(" ")));
        }
    }
}
