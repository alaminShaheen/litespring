package personal.litespring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassScanner {
    public static List<Class<?>> scan(File dir, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            } else classes.addAll(scan(file, packageName + "." + file.getName()));
        }
        return classes;
    }
}
