// Java 25의 새로운 기능: Compact Source Files & Instance Main Methods
// 클래스 선언 없이 바로 작성 가능!

void main() {
    System.out.println("🎉 Hello, Java 25!");
    System.out.println("✨ This is a compact source file - no class declaration needed!");

    // 기본 정보 출력
    System.out.println("\n📋 System Information:");
    System.out.println("Java Version: " + System.getProperty("java.version"));
    System.out.println("VM Name: " + System.getProperty("java.vm.name"));
    System.out.println("OS: " + System.getProperty("os.name"));

    // 간단한 계산
    System.out.println("\n🔢 Simple Calculation:");
    int sum = 0;
    for (int i = 1; i <= 10; i++) {
        sum += i;
    }
    System.out.println("Sum of 1 to 10: " + sum);

    System.out.println("\n✅ Java 25 is working perfectly!");
}
