# MethodScanner

**MethodScanner** extracts method signatures (with full parameter type and return type information) from compiled `.class` files. It is especially useful for analyzing Java web projects built using Maven or Gradle.

---

## Features

- Recursively scans all `.class` files under a given `target/classes` directory
- Outputs:
    - Fully qualified class names
    - Method names
    - Fully qualified parameter types
    - Return types
- Saves output to a plain `.txt` file

---

## Requirements

- Java 8 or later
- Compiled `.class` files (e.g., `target/classes` from `mvn package`)

---

## Usage

### Step 1: Compile

```bash
javac MethodLister.java
```

### Step 2: Run

```bash
java MethodLister <path_to_target_classes> <output_file_path>
```

### Example:

```bash
java MethodLister ./target/classes ./output/methods.txt
```

---

## Output Format

Each line in the output file will look like:

```
org.example.MyService.getUser(java.lang.String) : org.example.model.User
```

---

## Integration Example

This tool can be used to generate method signature data for use in other tools, such as:

- Codebase documentation generators
- Static analysis pipelines
- [Parameter size estimators](https://github.com/TasnimFabiha/java-param-size-estimator)

---
