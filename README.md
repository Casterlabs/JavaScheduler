# JavaScheduler

[![JitPack](https://jitpack.io/v/Casterlabs/JavaScheduler.svg)](https://jitpack.io/#Casterlabs/JavaScheduler)

## Example usage
```java
import static co.casterlabs.javascheduler.Scheduler.Static.*;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        int intervalId = setInterval(() -> {
            System.out.println(System.currentTimeMillis() - start);
        }, 2000);

        setTimeout(() -> {
            clearInterval(intervalId);
			System.exit(0);
        }, 10000);

        Thread.sleep(1000000); // Hold the JVM open, the scheduler will NOT.
    }

}
```

```
2015
4019
6018
8013
<exit 0>
```

## Maven  
```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>co.casterlabs</groupId>
            <artifactId>JavaScheduler</artifactId>
            <version>1.0.0</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
```