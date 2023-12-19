package top.osjf.assembly.cache.serializer;

/**
 * Type string serialization class implementation of {@link SerializerAdapter}.
 *
 * @author zpf
 * @since 1.0.0
 **/
public class StringPairSerializer extends SerializerAdapter<String> {

    private static final long serialVersionUID = -4700820192449082314L;

    public StringPairSerializer() {
        super(String.class);
    }
}
