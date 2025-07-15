# MethodScanner

**MethodScanner**  extracts method signatures from compiled `.class` files without requiring the full runtime classpath. It uses [ClassGraph](https://github.com/classgraph/classgraph) to scan bytecode directly and works well even if classes depend on external frameworks like Spring or Servlet APIs.

---

## Overview

- Recursively scans all `.class` files in a given directory (e.g., `target/classes`)
- Extracts method names, parameter types (fully qualified), and return types
- Saves the output as a `.txt` file in a configurable or default location

---

## Purpose

- Older tools like [Dependency Finder](https://depfind.sourceforge.io/) rely on class loading and full dependency resolution
- If classes reference external types (e.g., `Validator`, `HttpServletRequest`), those methods are **not reported**
- This results in **incomplete method coverage**, in projects that depend on external frameworks.

**While Dependency Finder supports a wide range of analysis tasks, this tool focuses specifically on method signature extraction**:
- Uses **ClassGraph**, which reads `.class` files directly
- **No need to load classes**, so it works even if referenced types are missing
- Extracts methods even from complex or framework-dependent classes, which were ignored by Dependency Finder

This tool **solves the method extraction limitations** you may have faced with Dependency Finder or reflection-based tools.

---

## How to Use

### Option 1: Using Maven (Recommended)

If you use the provided `pom.xml`, ClassGraph will be automatically handled by Maven.

```bash
mvn compile
mvn exec:java -Dexec.mainClass="MethodScanner" -Dexec.args="path/to/target/classes"
```

> You do NOT need to manually include ClassGraph in the classpath.

### Option 2: Running Manually (Without Maven)

If you prefer to compile and run manually:

1. Download [`classgraph-4.8.162.jar`](https://repo1.maven.org/maven2/io/github/classgraph/classgraph/4.8.162/classgraph-4.8.162.jar)
2. Compile:

```bash
javac -cp classgraph-4.8.162.jar MethodScanner.java
```

3. Run:

```bash
java -cp .:classgraph-4.8.162.jar MethodScanner path/to/target/classes
```

*(Use `;` instead of `:` on Windows)*

---

## Sample Output

```
org.springframework.samples.petclinic.web.PetController.processCreationForm(org.springframework.samples.petclinic.model.Pet, org.springframework.validation.BindingResult) : java.lang.String
org.springframework.samples.petclinic.web.VisitController.loadPetWithVisit(java.lang.Integer, java.util.Map<java.lang.String, java.lang.Object>) : void
```

---
