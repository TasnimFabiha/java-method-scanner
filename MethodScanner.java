import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class MethodScanner {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java MethodLister <path_to_target_classes> <output_file_path>");
            return;
        }

        File classesDir = new File(args[0]);
        if (!classesDir.exists() || !classesDir.isDirectory()) {
            System.out.println("Invalid directory: " + args[0]);
            return;
        }

        File outputFile = new File(args[1]);
        outputFile.getParentFile().mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            URLClassLoader classLoader = new URLClassLoader(new URL[]{classesDir.toURI().toURL()});
            List<String> classNames = new ArrayList<>();
            collectClassNames(classesDir, "", classNames);

            for (String className : classNames) {
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    for (Method method : clazz.getDeclaredMethods()) {
                        StringBuilder methodSignature = new StringBuilder();
                        methodSignature.append(clazz.getName()).append(".").append(method.getName()).append("(");

                        Parameter[] parameters = method.getParameters();
                        for (int i = 0; i < parameters.length; i++) {
                            methodSignature.append(parameters[i].getType().getName());
                            if (i < parameters.length - 1) {
                                methodSignature.append(", ");
                            }
                        }

                        methodSignature.append(") : ").append(method.getReturnType().getName());
                        writer.println(methodSignature);
                    }
                } catch (Throwable t) {
                    writer.println("Could not load class: " + className);
                }
            }

            System.out.println("Method list saved to methods.txt");

        } catch (Exception e) {
            System.err.println("Failed to write output: " + e.getMessage());
        }
    }

    private static void collectClassNames(File dir, String pkg, List<String> result) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                collectClassNames(file, pkg + file.getName() + ".", result);
            } else if (file.getName().endsWith(".class")) {
                String className = pkg + file.getName().replace(".class", "");
                result.add(className);
            }
        }
    }
}
