package tech.tongyu.examples.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinkepeng on 2017/6/15.
 */
public class JsonUtil {
    public static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static String toJson(Object o) throws EncodeException {
        String str = null;
        try {
            str = mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
        }
        return str;
    }

    public static Map<String, Object> fromJson(String context) throws DecodeException {
        Map<String, Object> resultMap = null;
        if (context != null) {
            JavaType type = getCollectionType(HashMap.class, String.class, Object.class);
            try {
                resultMap = mapper.readValue(context, type);
            } catch (Exception e) {
                throw new DecodeException("Failed to decode: " + e.getMessage());
            }
        }
        return resultMap;
    }

    public static Object fromJson(String className, String context) throws DecodeException {
        Object obj = null;
        try{
            obj = mapper.readValue(context, Class.forName(className));
        }catch (Exception e){
            throw new DecodeException("Failed to decode: " + e.getMessage());
        }
        return obj;
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

}
