package com.github.nramc.geojson;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    public void testMainMethodRunsWithoutException() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}