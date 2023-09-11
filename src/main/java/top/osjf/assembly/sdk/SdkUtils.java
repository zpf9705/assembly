package top.osjf.assembly.sdk;

import copy.cn.hutool.v_5819.core.bean.BeanUtil;
import copy.cn.hutool.v_5819.core.bean.copier.CopyOptions;
import copy.cn.hutool.v_5819.core.collection.CollectionUtil;
import copy.cn.hutool.v_5819.core.util.ArrayUtil;
import copy.cn.hutool.v_5819.core.util.ReflectUtil;
import copy.cn.hutool.v_5819.http.ContentType;
import top.osjf.assembly.sdk.annotation.MapFiled;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zpf
 * @since 1.1.0
 */
public abstract class SdkUtils {

    static final String named = "Content-Type";

    //MapFiled
    public static Map<String, Object> parseObjectToMap(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }
        //get all fields include self and parent private and public
        Field[] allFields = ReflectUtil.getFields(obj.getClass());
        if (ArrayUtil.isEmpty(allFields)) {
            return BeanUtil.beanToMap(obj);
        } else {
            Map<String, String> fieldMapping = Arrays.stream(allFields)
                    //at MapFiled
                    .filter(f -> Objects.nonNull(f.getAnnotation(MapFiled.class)) &&
                            //no static
                            !Modifier.isStatic(f.getModifiers()))
                    .collect(Collectors.toMap(Field::getName,
                            field -> field.getAnnotation(MapFiled.class).name()));
            return BeanUtil.beanToMap(obj, new LinkedHashMap<>(), CopyOptions.create().setFieldMapping(fieldMapping));
        }
    }

    public static void checkContentType(Map<String, String> headers) {
        if (CollectionUtil.isEmpty(headers)) {
            return;
        }
        //if no Content-Type
        if (!headers.containsKey(named)) {
            //default to JSON Content-Type
            headers.put(named, ContentType.JSON.getValue());
        }
    }
}
