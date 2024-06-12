package top.osjf.assembly.cache.serializer;

/**
 * The deserialization type corresponds to the error that should be thrown.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.1.4
 */
public class DeserializeTypeErrorException extends RuntimeException {

    private static final long serialVersionUID = 7537528613631481237L;

    public DeserializeTypeErrorException(Class<?> requiredType, Class<?> providerType) {
        super("Deserialization requires type " + requiredType.getName() + " , but provides data of type " +
                providerType.getName() + " .");
    }
}
