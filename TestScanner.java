import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class TestScanner {

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java TestScanner <path_to_target_classes> [output_file_path]");
            return;
        }

        File classesDir = new File(args[0]);
        if (!classesDir.exists() || !classesDir.isDirectory()) {
            System.out.println("Invalid directory: " + args[0]);
            return;
        }

        File outputFile;
        File skippedLogFile;
        if (args.length == 2) {
            outputFile = new File(args[1]);
            skippedLogFile = new File(args[1].replace(".txt", "-skipped-classes.log"));
        } else {
            File parentDir = classesDir.getParentFile();  // should be 'target'
            if (parentDir != null && parentDir.getName().equals("target")) {
                File projectRoot = parentDir.getParentFile();
                outputFile = new File(projectRoot, "fully-qualified-methods-with-classloader.txt");
                skippedLogFile = new File(projectRoot, "skipped-classes.log");
            } else {
                outputFile = new File("fully-qualified-methods-with-classloader.txt");
                skippedLogFile = new File("skipped-classes.log");
            }
        }

        outputFile.getParentFile().mkdirs();
        skippedLogFile.getParentFile().mkdirs();

        try (
                PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
                PrintWriter skippedWriter = new PrintWriter(new FileWriter(skippedLogFile))
        ) {

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
                    skippedWriter.println("Could not load class: " + className);
                    t.printStackTrace(skippedWriter);
                    skippedWriter.println("--------------------------------------------------");
                }
            }

            System.out.println("Method list saved to " + outputFile);
            System.out.println("Skipped class log saved to " + skippedLogFile);

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
