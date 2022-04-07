package org.snowjak.devitae;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("local")
public class DevitaeApplicationTest {

    @Test
    @DisplayName("Should Load Application Context Without Failing")
    public void testShouldLoadApplicationContextWithoutFailing() {
        assertTrue(true);
    }
}