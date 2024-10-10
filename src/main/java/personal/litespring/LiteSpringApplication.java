package personal.litespring;


import personal.litespring.annotation.PackageScan;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LiteSpringApplication {
    public static ApplicationContext run(Class<?> appClass) throws Exception {
        ApplicationContext applicationContext = ApplicationContext.getInstance();

        PackageScan packageScan = appClass.getAnnotation(PackageScan.class);
        ClassLoader appClassLoader = appClass.getClassLoader();

        List<Class<?>> classes = new ArrayList<>();

        // extract the absolute path of the package of MainApplication class
        for (String packageName : packageScan.scanPackages()) {
            // package name is the name of the package of the MainApplication class
            // relativePackagePath is the relative path to the package name of the MainApplication class
            String relativePackagePath = packageName.replace('.', '/');
            // use the getResource() method to get an absolute url of the packageName
            URL url = appClassLoader.getResource(packageName.replace(".", "/"));
            classes.addAll(ClassScanner.scan(new File(url.getPath()), packageName));
        }

        applicationContext.createSpringContainer(classes);

        return applicationContext;
    }
}
