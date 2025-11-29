// Java 25 Compact Source File with IO class
// Java 25의 새로운 IO 클래스 사용

void main() {
    IO.println("🎉 Hello, Java 25 with compact source file!");
    IO.println("✨ No class declaration needed!");

    // 기본 정보 출력
    IO.println("\n📋 System Information:");
    IO.println("Java Version: " + System.getProperty("java.version"));
    IO.println("VM Name: " + System.getProperty("java.vm.name"));
    IO.println("OS: " + System.getProperty("os.name"));

    // 간단한 계산
    IO.println("\n🔢 Simple Calculation:");
    int sum = 0;
    for (int i = 1; i <= 10; i++) {
        sum += i;
    }
    IO.println("Sum of 1 to 10: " + sum);

    IO.println("\n✅ Java 25 compact source file is working!");
}
