package top.osjf.assembly.util.validation;

import top.osjf.assembly.util.annotation.NotNull;

/**
 * Each enumeration value that needs to be passed in for validation belongs
 * to an enumeration object, which needs to implement a validation interface
 * and adhere to the principle of unique identification.
 * @param <E> The type of enumeration.
 * @param <T> The type uniquely identified by the enumeration value.
 * @see EnumParam
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface EnumValidate<E extends Enum<E> , T> {

    /**
     * Obtain the meaning of enumeration, which can be the business
     * name and defaults to the full name of the enumeration class.
     * @return Means value.
     */
    default String getMean(){
        return getEnumClass().getName();
    }

    /**
     * Obtain a unique identifier for the enumeration properties
     * of each enumeration class, targeting each enumeration value.
     * @return Sign of enums.
     */
    @NotNull
    T getSign();

    /**
     * Obtain the description values of the enumeration properties
     * for each enumeration class, targeting each enumeration value.
     * @return Desc of enums.
     */
    String getDesc();

    /**
     * Obtain the class of the enumeration class,targeting enum class.
     * @return Class object.
     */
    @NotNull
    Class<E> getEnumClass();
}
