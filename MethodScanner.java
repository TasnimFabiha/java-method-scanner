import io.github.classgraph.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodScanner {

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java MethodLister <path_to_target_classes> [output_file_path]");
            return;
        }

        File classesDir = new File(args[0]);
        if (!classesDir.exists() || !classesDir.isDirectory()) {
            System.out.println("Invalid directory: " + args[0]);
            return;
        }

        File outputFile;
        if (args.length == 2) {
            outputFile = new File(args[1]);
        } else {
            // Use parent of 'target/classes' and name the file 'fully-qualified-methods.txt'
            File parentDir = classesDir.getParentFile();  // should be 'target'
            if (parentDir != null && parentDir.getName().equals("target")) {
                File projectRoot = parentDir.getParentFile();
                outputFile = new File(projectRoot, "fully-qualified-methods.txt");
            } else {
                // fallback: use current working directory
                outputFile = new File("fully-qualified-methods.txt");
            }
        }

        outputFile.getParentFile().mkdirs();


        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            try (ScanResult scanResult = new ClassGraph()
                    .overrideClasspath(classesDir)
                    .enableClassInfo()
                    .enableMethodInfo()
                    .scan()) {

                for (ClassInfo classInfo : scanResult.getAllClasses()) {
                    for (MethodInfo methodInfo : classInfo.getDeclaredMethodInfo()) {
                        String className = classInfo.getName();
                        String methodName = methodInfo.getName();

                        List<String> paramTypes = Arrays.stream(methodInfo.getParameterInfo())
                                .map(p -> p.getTypeDescriptor().toString())
                                .collect(Collectors.toList());

                        String returnType = methodInfo.getTypeDescriptor().getResultType().toString();

                        String methodSignature = className + "." + methodName + "(" +
                                String.join(", ", paramTypes) + ") : " + returnType;

                        writer.println(methodSignature);
                    }
                }
            }

            System.out.println("Method list saved to "+ outputFile);

        } catch (Exception e) {
            System.err.println("Failed to write output: " + e.getMessage());
        }
    }


}
