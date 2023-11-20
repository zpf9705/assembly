package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.ReflectUtils;
import top.osjf.assembly.util.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

/**
 * The executor of {@link MethodAutoExec} will throw error messages when executing incorrectly.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.6
 */
public class MethodAutomaticActuator implements ConstraintValidator<MethodAutoExec, Object> {

    private MethodAutoExec auto;

    @Override
    public void initialize(MethodAutoExec auto) {
        this.auto = auto;
    }

    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        //target is null direct pass
        if (target == null){
            return true;
        }
        //having AutoExecutor.Runnable or self names
        Method[] methods = ReflectUtils.getMethods(target.getClass(),
                method -> method.getAnnotation(Runnable.class) != null ||
                ArrayUtils.contains(auto.value(), method.getName()));
        if (ArrayUtils.isEmpty(methods)) {
            //no found sure methods direct return true
            //no message to throw
            return true;
        }
        String error = null;
        for (Method method : methods) {
            //try to invoke cache message
            //if happened exception and get cause
            //break circulate
            try {
                ReflectUtils.invoke(target, method);
            } catch (Throwable e) {
                error = e.getMessage();
                break;
            }
        }
        if (StringUtils.isNotBlank(error)) {
            //let having exception message repackaging throw
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Error in automatic execution method : "
                    + error).addConstraintViolation();
            return false;
        }
        return true;
    }
}
