# Java 25 테스트 가이드

> [!NOTE]
> Java 25는 2025년 9월 16일에 출시된 LTS(Long-Term Support) 버전으로, 18개의 JEP(JDK Enhancement Proposal)를 포함하고 있습니다. 최소 8년간의 장기 지원이 제공됩니다.

## 목차

1. [환경 설정](#환경-설정)
2. [주요 기능 테스트](#주요-기능-테스트)
3. [성능 개선 테스트](#성능-개선-테스트)
4. [보안 및 API 테스트](#보안-및-api-테스트)
5. [마이그레이션 체크리스트](#마이그레이션-체크리스트)

---

## 환경 설정

### 1. Java 25 설치 확인

```bash
java -version
```

예상 출력:
```
java version "25" 2025-09-16
Java(TM) SE Runtime Environment (build 25+...)
Java HotSpot(TM) 64-Bit Server VM (build 25+..., mixed mode, sharing)
```

### 2. 프로젝트 구조 생성

```bash
# 기본 디렉토리 구조
mkdir -p src/main/java/com/example/java25
mkdir -p src/test/java/com/example/java25
mkdir -p docs/examples
```

### 3. 빌드 도구 설정

#### Gradle (build.gradle)
```gradle
plugins {
    id 'java'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}

tasks.withType(JavaCompile) {
    options.compilerArgs += ['--enable-preview']
}

tasks.withType(Test) {
    jvmArgs += ['--enable-preview']
}

tasks.withType(JavaExec) {
    jvmArgs += ['--enable-preview']
}
```

#### Maven (pom.xml)
```xml
<properties>
    <maven.compiler.source>25</maven.compiler.source>
    <maven.compiler.target>25</maven.compiler.target>
    <maven.compiler.release>25</maven.compiler.release>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <compilerArgs>
                    <arg>--enable-preview</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 주요 기능 테스트

### 1. Compact Source Files & Instance Main Methods (JEP 512) ✅ Final

> [!IMPORTANT]
> 이 기능은 초보자와 빠른 프로토타이핑에 매우 유용합니다. 보일러플레이트 코드를 대폭 줄일 수 있습니다.

#### 테스트 파일: `HelloJava25.java`

**기존 방식 (Java 24 이전):**
```java
public class HelloJava25 {
    public static void main(String[] args) {
        System.out.println("Hello, Java 25!");
    }
}
```

**새로운 방식 (Java 25):**
```java
// 클래스 선언 없이 바로 작성 가능
void main() {
    println("Hello, Java 25!");
}
```

**실행 방법:**
```bash
# 컴파일 없이 바로 실행
java --enable-preview HelloJava25.java

# 또는 컴파일 후 실행
javac --enable-preview HelloJava25.java
java --enable-preview HelloJava25
```

**테스트 시나리오:**
- [ ] 클래스 없는 파일 실행
- [ ] 인스턴스 main 메서드 실행
- [ ] 암시적 import 확인 (System.out.println → println)

---

### 2. Flexible Constructor Bodies (JEP 513) ✅ Final

> [!TIP]
> 생성자에서 `super()` 또는 `this()` 호출 전에 문장을 배치할 수 있어 객체 초기화가 더 유연해졌습니다.

#### 테스트 파일: `FlexibleConstructorTest.java`

```java
package com.example.java25;

public class FlexibleConstructorTest {
    
    static class Parent {
        private final String name;
        
        public Parent(String name) {
            this.name = name;
        }
    }
    
    static class Child extends Parent {
        private final int age;
        
        public Child(String rawInput) {
            // ✨ Java 25: super() 호출 전에 로직 실행 가능
            String processedName = rawInput.trim().toUpperCase();
            int calculatedAge = rawInput.length();
            
            super(processedName);  // 이제 중간에 호출 가능
            this.age = calculatedAge;
        }
        
        @Override
        public String toString() {
            return "Child{name='" + super.name + "', age=" + age + "}";
        }
    }
    
    public static void main(String[] args) {
        Child child = new Child("  john  ");
        System.out.println(child);  // Child{name='JOHN', age=8}
    }
}
```

**테스트 시나리오:**
- [ ] super() 전 변수 초기화
- [ ] this() 전 유효성 검증
- [ ] 복잡한 생성자 로직 단순화

---

### 3. Primitive Types in Patterns (JEP 507) 🔄 Third Preview

> [!WARNING]
> Preview 기능이므로 `--enable-preview` 플래그가 필수입니다.

#### 테스트 파일: `PrimitivePatternTest.java`

```java
package com.example.java25;

public class PrimitivePatternTest {
    
    public static void processValue(Object obj) {
        switch (obj) {
            case int i when i > 0 -> 
                System.out.println("Positive integer: " + i);
            case int i when i < 0 -> 
                System.out.println("Negative integer: " + i);
            case int i -> 
                System.out.println("Zero");
            case double d -> 
                System.out.println("Double: " + d);
            case String s -> 
                System.out.println("String: " + s);
            case null -> 
                System.out.println("Null value");
            default -> 
                System.out.println("Unknown type");
        }
    }
    
    public static void main(String[] args) {
        processValue(42);           // Positive integer: 42
        processValue(-10);          // Negative integer: -10
        processValue(0);            // Zero
        processValue(3.14);         // Double: 3.14
        processValue("Hello");      // String: Hello
        processValue(null);         // Null value
    }
}
```

**테스트 시나리오:**
- [ ] int, long, double 등 primitive 타입 패턴 매칭
- [ ] Guard 조건과 함께 사용
- [ ] instanceof와 함께 사용

---

### 4. Module Import Declarations (JEP 511) 🔄 Preview

#### 테스트 파일: `ModuleImportTest.java`

**기존 방식:**
```java
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
```

**새로운 방식:**
```java
import module java.base;  // java.base 모듈의 모든 exported 패키지 import

public class ModuleImportTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        // 명시적 import 없이 사용 가능
    }
}
```

**테스트 시나리오:**
- [ ] 모듈 단위 import 테스트
- [ ] 코드 가독성 개선 확인
- [ ] 빌드 시간 비교

---

### 5. Stable Values 🔄 Preview

#### 테스트 파일: `StableValuesTest.java`

```java
package com.example.java25;

public class StableValuesTest {
    
    // ✨ Stable value: 애플리케이션 생명주기 동안 한 번만 설정
    private stable String configValue;
    
    public StableValuesTest() {
        // 초기화는 한 번만 가능
        this.configValue = loadConfiguration();
    }
    
    private String loadConfiguration() {
        System.out.println("Loading configuration...");
        return "Production Config";
    }
    
    public String getConfig() {
        return configValue;  // 성능 최적화: final 필드보다 빠름
    }
    
    public static void main(String[] args) {
        StableValuesTest test = new StableValuesTest();
        System.out.println(test.getConfig());
        System.out.println(test.getConfig());  // 재로딩 없음
    }
}
```

**테스트 시나리오:**
- [ ] Lazy initialization 테스트
- [ ] 성능 비교 (vs final fields)
- [ ] Thread-safety 확인

---

## 성능 개선 테스트

### 6. Compact Object Headers (JEP 519) ✅ Final

> [!IMPORTANT]
> 64비트 아키텍처에서 객체 헤더 크기를 줄여 메모리 사용량을 대폭 감소시킵니다.

#### 테스트 방법

```bash
# JVM 옵션으로 활성화
java -XX:+UseCompactObjectHeaders -XX:+PrintFlagsFinal -version | grep CompactObjectHeaders
```

#### 메모리 사용량 측정 코드: `MemoryTest.java`

```java
package com.example.java25;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;

public class MemoryTest {
    
    static class SmallObject {
        private int id;
    }
    
    public static void main(String[] args) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // 초기 메모리
        long beforeMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        // 100만 개의 작은 객체 생성
        List<SmallObject> objects = new ArrayList<>();
        for (int i = 0; i < 1_000_000; i++) {
            objects.add(new SmallObject());
        }
        
        // 사용된 메모리
        long afterMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long usedMemory = (afterMemory - beforeMemory) / 1024 / 1024;
        
        System.out.println("Objects created: " + objects.size());
        System.out.println("Memory used: " + usedMemory + " MB");
        System.out.println("Avg per object: " + (usedMemory * 1024.0 / objects.size()) + " KB");
    }
}
```

**실행 및 비교:**
```bash
# Compact Object Headers 비활성화
java -XX:-UseCompactObjectHeaders MemoryTest

# Compact Object Headers 활성화 (기본값)
java -XX:+UseCompactObjectHeaders MemoryTest
```

**테스트 시나리오:**
- [ ] 메모리 사용량 비교
- [ ] 대용량 힙에서의 성능 측정
- [ ] 마이크로서비스 환경에서 테스트

---

### 7. Generational Shenandoah GC (JEP 521) ✅ Final

#### 테스트 방법

```bash
# Generational Shenandoah GC 활성화
java -XX:+UseShenandoahGC -XX:ShenandoahGCMode=generational \
     -Xlog:gc*:file=gc.log \
     -jar your-application.jar
```

#### GC 성능 테스트 코드: `GCTest.java`

```java
package com.example.java25;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GCTest {
    
    public static void main(String[] args) {
        Random random = new Random();
        List<byte[]> youngGen = new ArrayList<>();
        List<byte[]> oldGen = new ArrayList<>();
        
        System.out.println("Starting GC stress test...");
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10000; i++) {
            // Young generation 객체 (단기 생존)
            for (int j = 0; j < 100; j++) {
                youngGen.add(new byte[1024]);
            }
            youngGen.clear();
            
            // Old generation 객체 (장기 생존)
            if (i % 100 == 0) {
                oldGen.add(new byte[10240]);
            }
            
            if (i % 1000 == 0) {
                System.out.println("Iteration: " + i);
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Test completed in: " + (endTime - startTime) + " ms");
        System.out.println("Old gen objects: " + oldGen.size());
    }
}
```

**실행 및 비교:**
```bash
# 기존 Shenandoah
java -XX:+UseShenandoahGC -Xlog:gc GCTest

# Generational Shenandoah
java -XX:+UseShenandoahGC -XX:ShenandoahGCMode=generational -Xlog:gc GCTest
```

**테스트 시나리오:**
- [ ] GC 일시 정지 시간 측정
- [ ] 처리량(throughput) 비교
- [ ] 시작 시간 개선 확인

---

### 8. AOT Method Profiling (JEP 515) ✅ Final

> [!TIP]
> 클라우드 애플리케이션과 마이크로서비스의 콜드 스타트를 개선합니다.

#### 프로파일 생성 및 사용

```bash
# 1단계: 프로파일 수집
java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf \
     -jar your-application.jar

# 2단계: 프로파일을 사용하여 실행
java -XX:AOTMode=on -XX:AOTConfiguration=app.aotconf \
     -jar your-application.jar
```

#### 성능 측정 코드: `AOTTest.java`

```java
package com.example.java25;

public class AOTTest {
    
    private static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        
        // Warm-up
        for (int i = 0; i < 5; i++) {
            fibonacci(30);
        }
        
        long warmupTime = System.nanoTime();
        
        // Actual test
        long result = 0;
        for (int i = 0; i < 100; i++) {
            result += fibonacci(35);
        }
        
        long endTime = System.nanoTime();
        
        System.out.println("Warmup time: " + (warmupTime - startTime) / 1_000_000 + " ms");
        System.out.println("Execution time: " + (endTime - warmupTime) / 1_000_000 + " ms");
        System.out.println("Result: " + result);
    }
}
```

**테스트 시나리오:**
- [ ] 콜드 스타트 시간 비교
- [ ] JIT 컴파일 성능 개선 확인
- [ ] 예측 가능한 런타임 성능 검증

---

## 보안 및 API 테스트

### 9. Key Derivation Function (KDF) API (JEP 510) ✅ Final

#### 테스트 파일: `KDFTest.java`

```java
package com.example.java25;

import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;

public class KDFTest {
    
    public static void main(String[] args) throws Exception {
        // PBKDF2 예제
        testPBKDF2();
        
        // Argon2 예제 (Java 25 신규)
        // testArgon2();
    }
    
    private static void testPBKDF2() throws Exception {
        String password = "mySecurePassword123";
        byte[] salt = "randomSalt".getBytes();
        
        // PBKDF2 KDF 사용
        KDF kdf = KDF.getInstance("PBKDF2WithHmacSHA256");
        
        SecretKey derivedKey = kdf.deriveKey(
            password.toCharArray(),
            salt,
            10000,  // iterations
            256     // key length
        );
        
        System.out.println("Derived Key: " + 
            HexFormat.of().formatHex(derivedKey.getEncoded()));
    }
}
```

**테스트 시나리오:**
- [ ] PBKDF2 키 유도 테스트
- [ ] Argon2 알고리즘 테스트
- [ ] 성능 및 보안 강도 검증

---

### 10. JFR (Java Flight Recorder) 개선 사항

#### JFR CPU-Time Profiling (JEP 509) - Experimental

```bash
# JFR 활성화 및 CPU 프로파일링
java -XX:StartFlightRecording=filename=recording.jfr,settings=profile \
     -XX:+UnlockExperimentalVMOptions \
     -XX:+EnableJFRCPUProfiling \
     -jar your-application.jar

# 녹화 분석
jfr print --events jdk.CPUSample recording.jfr
```

#### JFR Method Timing (JEP 520) - Final

```java
package com.example.java25;

import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Description;

@Label("Custom Business Event")
@Description("Tracks business logic execution")
class BusinessEvent extends Event {
    @Label("Operation Name")
    String operationName;
    
    @Label("Duration")
    long duration;
}

public class JFRMethodTimingTest {
    
    public static void businessLogic(String operation) {
        BusinessEvent event = new BusinessEvent();
        event.begin();
        
        try {
            // 비즈니스 로직 실행
            Thread.sleep(100);
            event.operationName = operation;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            event.end();
            event.commit();
        }
    }
    
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            businessLogic("Operation-" + i);
        }
    }
}
```

**실행:**
```bash
java -XX:StartFlightRecording=filename=method-timing.jfr \
     JFRMethodTimingTest

# 분석
jfr print --events BusinessEvent method-timing.jfr
```

**테스트 시나리오:**
- [ ] CPU 시간 프로파일링 정확도 확인
- [ ] 메서드 타이밍 측정
- [ ] 커스텀 이벤트 추적

---

## 마이그레이션 체크리스트

### 호환성 확인

- [ ] **32-bit x86 지원 종료 (JEP 503)**: 64-bit 환경 확인
- [ ] **Deprecated API 제거**: 사용 중인 API 확인
- [ ] **Third-party 라이브러리 호환성**: 주요 라이브러리 업데이트 확인

### 성능 최적화 기회

- [ ] Compact Object Headers 활성화로 메모리 절감
- [ ] Generational Shenandoah GC로 GC 성능 개선
- [ ] AOT Method Profiling으로 시작 시간 단축
- [ ] JFR 개선 사항으로 프로파일링 강화

### 코드 현대화

- [ ] Compact Source Files로 간단한 스크립트 단순화
- [ ] Flexible Constructor Bodies로 초기화 로직 개선
- [ ] Primitive Patterns로 타입 체크 코드 개선
- [ ] Module Imports로 import 문 단순화

### 보안 강화

- [ ] KDF API로 키 유도 표준화
- [ ] PEM Encoding API로 인증서 처리 개선

---

## 추가 리소스

### 공식 문서
- [OpenJDK Java 25](https://openjdk.org/projects/jdk/25/)
- [JEP Index](https://openjdk.org/jeps/0)
- [Java 25 Release Notes](https://www.oracle.com/java/technologies/javase/25-relnotes.html)

### 커뮤니티
- [Java Subreddit](https://reddit.com/r/java)
- [Stack Overflow - Java 25 Tag](https://stackoverflow.com/questions/tagged/java-25)

### 벤치마킹 도구
- [JMH (Java Microbenchmark Harness)](https://github.com/openjdk/jmh)
- [GCViewer](https://github.com/chewiebug/GCViewer)

---

## 빠른 시작 예제

### 올인원 테스트 스크립트

```java
// QuickTest.java
void main() {
    println("=== Java 25 Quick Test ===");
    
    // 1. Compact Source Files 테스트
    println("✓ Compact source files working!");
    
    // 2. Primitive Pattern Matching
    Object value = 42;
    switch (value) {
        case int i when i > 0 -> println("✓ Primitive patterns working!");
        default -> println("✗ Unexpected");
    }
    
    // 3. 시스템 정보
    println("\nJava Version: " + System.getProperty("java.version"));
    println("VM Name: " + System.getProperty("java.vm.name"));
    
    println("\n=== All tests passed! ===");
}
```

**실행:**
```bash
java --enable-preview QuickTest.java
```

---

> [!CAUTION]
> Preview 기능들은 프로덕션 환경에서 사용하기 전에 충분한 테스트가 필요합니다. `--enable-preview` 플래그를 사용해야 하며, 향후 버전에서 변경될 수 있습니다.

**문서 작성일**: 2025-11-30  
**Java 버전**: Java 25 (LTS)  
**최종 업데이트**: 2025-11-30
