package personal.litespring.models;

import lombok.Builder;
import lombok.Data;
import personal.litespring.enums.MethodType;

import java.lang.reflect.Method;

@Data
@Builder(toBuilder = true)
public class ControllerMethod {
    private Class<?> clazz;
    private Object instance;
    private Method method;
    private MethodType methodType;
    private String url;
}
