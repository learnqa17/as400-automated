# LFA AS400 Automation

Automation project for AS400 (TN5250) connection testing.

------------------------------------------------------------------------

## 📦 Prerequisite

-   Java 11+
-   Maven 

------------------------------------------------------------------------

## ⚠ IMPORTANT (First Time Setup Only)

Project ini menggunakan library **TN5250J** yang tidak tersedia di Maven
Central.

Sebelum menjalankan project, WAJIB install jar berikut ke local Maven
repository:


```bash
mvn install:install-file -Dfile=lib/tn5250j.jar -DgroupId=id.co.aiafinancial.automation.test -DartifactId=tn5250j -Dversion=1.0 -Dpackaging=jar
```

Command ini hanya perlu dijalankan 1x saja di setiap laptop.

------------------------------------------------------------------------

## 🚀 Build Project

 Build tanpa menjalankan test

```bash
mvn clean install -DskipTests
```

Build dengan menjalankan test

```bash
mvn clean install
```


------------------------------------------------------------------------

## 👨‍💻 Notes

-   Jangan hapus folder `lib/`
-   Jalankan install TN5250J sebelum build pertama kali
