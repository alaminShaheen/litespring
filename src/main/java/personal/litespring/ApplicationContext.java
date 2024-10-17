package personal.litespring;


import personal.litespring.annotation.*;
import personal.litespring.enums.MethodType;
import personal.litespring.models.ControllerMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            DispatcherServlet dispatcherServlet = new DispatcherServlet(findControllerMethods(classes));
            tomCatConfig.registerServlet(dispatcherServlet, dispatcherServlet.getClass().getSimpleName(), "/");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected List<ControllerMethod> findControllerMethods(List<Class<?>> classes) {
        List<ControllerMethod> controllerMethods = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(RestController.class)) {
                String controllerBasePath = "";
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    controllerBasePath = requestMapping.basePath();
                }
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        String fullUrl = Paths.get(controllerBasePath, requestMapping.basePath()).toString();
                        ControllerMethod controllerMethod = ControllerMethod.builder()
                                .methodType(requestMapping.type())
                                .url(fullUrl)
                                .method(method)
                                .clazz(clazz)
                                .instance(beanFactory.get(clazz.getSimpleName()))
                                .build();
                        controllerMethods.add(controllerMethod);
                    } else if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping getMapping = method.getAnnotation(GetMapping.class);
                        String fullUrl = Paths.get(controllerBasePath, getMapping.basePath()).toString();
                        ControllerMethod controllerMethod = ControllerMethod.builder()
                                .methodType(MethodType.GET)
                                .url(fullUrl)
                                .method(method)
                                .clazz(clazz)
                                .instance(beanFactory.get(clazz.getSimpleName()))
                                .build();
                        controllerMethods.add(controllerMethod);
                    } else if (method.isAnnotationPresent(PostMapping.class)) {
                        PostMapping postMapping = method.getAnnotation(PostMapping.class);
                        String fullUrl = Paths.get(controllerBasePath, postMapping.basePath()).toString();
                        ControllerMethod controllerMethod = ControllerMethod.builder()
                                .methodType(MethodType.POST)
                                .url(fullUrl)
                                .method(method)
                                .clazz(clazz)
                                .instance(beanFactory.get(clazz.getSimpleName()))
                                .build();
                        controllerMethods.add(controllerMethod);
                    }
                }
            }
        }

        return controllerMethods;
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
