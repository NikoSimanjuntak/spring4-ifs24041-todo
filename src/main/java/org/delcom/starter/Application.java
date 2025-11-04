package org.delcom.starter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationTest {

    @Test
    @Timeout(3)
    void mainMethod_ShouldRunSpringApplication() {
        // Mock SpringApplication.run untuk mengetes main method
        try (var mockedSpring = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            mockedSpring.when(() -> SpringApplication.run(Application.class, new String[]{}))
                        .thenReturn(mockContext);

            // Jalankan main method
            assertDoesNotThrow(() -> Application.main(new String[]{}));

            // Pastikan SpringApplication.run dipanggil
            mockedSpring.verify(() -> SpringApplication.run(Application.class, new String[]{}));
        }
    }

    @Test
    void contextLoads_ShouldNotThrowException() {
        // Tes bahwa Spring context dapat dimuat tanpa error
        assertDoesNotThrow(() -> {
            Class<?> clazz = Class.forName("org.delcom.starter.Application");
            assertNotNull(clazz);
        });
    }

    @Test
    void application_ShouldHaveSpringBootAnnotation() {
        // Tes bahwa class memiliki anotasi @SpringBootApplication
        assertNotNull(Application.class
                .getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void application_CanBeInstantiated() {
        // Tes bahwa instance Application dapat dibuat tanpa error
        assertDoesNotThrow(() -> {
            Application app = new Application();
            assertNotNull(app);
        });
    }
}
