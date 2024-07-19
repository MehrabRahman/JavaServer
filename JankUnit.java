import java.lang.annotation.*;
import java.lang.reflect.*;

public class JankUnit {
    public static void assertThat(boolean condition) {
        if(!condition) {
            throw new AssertionError();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running tests...");
        int passed = 0, failed = 0;
        Class<?> testCaseClass = Class.forName(args[0]);
        for (Method m : testCaseClass.getMethods()) {
            if (m.isAnnotationPresent(JTest.class)) {
                try {
                    m.invoke(testCaseClass.newInstance());
                    passed++;
                    System.out.printf("Test %s: passed%n", m.getName());
                } catch (Exception e) {
                    failed++;
                    System.err.printf("Test %s: %s %n", m.getName(), e.getCause());
                }
            }
        }

        System.out.printf("Passed: %d, Failed: %d", passed, failed);
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface JTest {}

class UnitTests {
    @JTest
    public void addTest() {
        int expected = 4;
        int actual = 2 + 2;
        JankUnit.assertThat(expected == actual);
    }

    @JTest
    public void subTest() {
        int expected = 1;
        int actual = 3 - 1;
        JankUnit.assertThat(expected == actual);
    }
}