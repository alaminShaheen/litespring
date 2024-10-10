package personal.litespring;


import personal.litespring.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    private static ApplicationContext instance;

    private final Map<String, Object> beanFactory = new HashMap<>();
    private final int TOMCAT_PORT = 8080;
    private final TomCatConfig tomCatConfig;

    private ApplicationContext() {
        this.tomCatConfig = new TomCatConfig(TOMCAT_PORT);
    }

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    protected void createSpringContainer(List<Class<?>> classes) {
        try {
            beanCreates(classes);
            injectDependencies(classes);
            registerControllers(classes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void registerControllers(List<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(RestController.class)) {
                Object instance = beanFactory.get(clazz.getSimpleName());
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    TomCatConfig.registerController(instance, requestMapping.basePath());
                } else {
                    TomCatConfig.registerController(instance, "");
                }
            }
        }
    }

    private void injectDependencies(List<Class<?>> classes) throws IllegalAccessException {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class)) {
                // get instance of the class we are injecting into
                Object primaryClass = beanFactory.get(clazz.getSimpleName());
                // get its declared fields i.e. all fields regardless of what access modifier it has
                Field[] fields = primaryClass.getClass().getDeclaredFields();

                // loop through fields
                for (Field field : fields) {
                    // check if Autowired annotation present
                    if (field.isAnnotationPresent(Autowired.class)) {
                        // get instance of the autowired class
                        Object injectedClass = beanFactory.get(field.getType().getSimpleName());
                        // set field accessibility
                        field.setAccessible(true);
                        // inject the class by setting it into the field of the primary class
                        field.set(primaryClass, injectedClass);
                    }
                }
            }
        }
    }

    private void beanCreates(List<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            // check if the desired annotation is present in the class
            if (clazz.isAnnotationPresent(Component.class)) {
                // create an instance of the class programmatically using reflection
                Object instance = clazz.getDeclaredConstructor().newInstance();
                // getSimpleName() give only the className while getName() gives the name with the packages prefixed with it
                // getName() = personal.litespring.repository.ProductRepository
                // getSimpleName() = ProductRepository
                this.beanFactory.put(clazz.getSimpleName(), instance);
            }
        }
    }

    public Object getBean(Class<?> clazz) {
        return beanFactory.get(clazz.getSimpleName());
    }
}
