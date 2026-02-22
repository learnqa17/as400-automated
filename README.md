# LFA AS400 Automation

Automation project for AS400 (TN5250) connection testing.

---

## 📦 Prerequisites

- Java 11+
- Maven 3+

---

## ⚠ IMPORTANT (First Time Setup Only)

This project uses the **TN5250J** library which is not available in Maven Central.

Before running the project, you MUST install the following JAR into your local Maven repository:

```bash
mvn install:install-file -Dfile=lib/tn5250j.jar -DgroupId=id.co.aiafinancial.automation.test -DartifactId=tn5250j -Dversion=1.0 -Dpackaging=jar
```

This command only needs to be executed once on each machine.

---

## 🚀 Build Project

### Build without running tests

```bash
mvn clean install -DskipTests
```

### Build with running tests

```bash
mvn clean install
```

---

## 👨‍💻 Notes

- Do not delete the `lib/` folder
- Make sure TN5250J is installed before the first build