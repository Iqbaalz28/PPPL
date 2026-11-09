package com.lastpenguin.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.DynamicTest;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk entitas Yeti.
 * Menguji inisialisasi musuh, mekanisme damage & kematian,
 * penentuan kecepatan berdasarkan difficulty, serta pathfinding dasar.
 */
public class YetiTest {

    private Yeti yeti;

    @BeforeEach
    void setUp() {
        yeti = new Yeti(100, 100, 2);
    }

    @Test
    @DisplayName("Inisialisasi Yeti dengan nilai awal yang valid")
    void testInitialYetiState() {
        assertAll("Verifikasi Atribut Awal Yeti",
            () -> assertEquals(100, yeti.getX()),
            () -> assertEquals(100, yeti.getY()),
            () -> assertEquals(2, yeti.getSpeed()),
            () -> assertTrue(yeti.isAlive()),
            () -> assertNotNull(yeti.getBounds()),
            () -> assertEquals(60, yeti.getBounds().width),
            () -> assertEquals(60, yeti.getBounds().height)
        );
    }

    @Test
    @DisplayName("Penentuan kecepatan berdasarkan difficulty (ternary operator)")
    void testDifficultySpeedMapping() {
        Yeti easyYeti = new Yeti(0, 0, 1);
        assertEquals(1, easyYeti.getSpeed());

        Yeti hardYeti = new Yeti(0, 0, 3);
        assertEquals(3, hardYeti.getSpeed());

        // Nilai difficulty negatif atau 0 harus menghasilkan speed minimal 1
        Yeti subZeroYeti = new Yeti(0, 0, 0);
        assertEquals(1, subZeroYeti.getSpeed());

        Yeti negativeYeti = new Yeti(0, 0, -5);
        assertEquals(1, negativeYeti.getSpeed());
    }

    @Test
    @DisplayName("Penerimaan damage non-fatal mengurangi health dan Yeti tetap hidup")
    void testNonFatalDamage() {
        yeti.takeDamage(20);
        assertTrue(yeti.isAlive(), "Yeti harus tetap hidup jika damage < 50");

        yeti.takeDamage(29);
        assertTrue(yeti.isAlive(), "Total damage 49 (health sisa 1) harus tetap hidup");
    }

    @Test
    @DisplayName("Penerimaan damage fatal (>= 50) mengubah status alive menjadi false")
    void testFatalDamage() {
        yeti.takeDamage(50);
        assertFalse(yeti.isAlive(), "Damage tepat 50 (health 0) harus mematikan Yeti");

        Yeti overkilledYeti = new Yeti(50, 50, 2);
        overkilledYeti.takeDamage(100);
        assertFalse(overkilledYeti.isAlive(), "Overkill damage (100) harus mematikan Yeti");
    }

    @Test
    @DisplayName("Yeti yang mati tidak bergerak saat trackPlayer dipanggil")
    void testDeadYetiDoesNotTrack() {
        yeti.takeDamage(50);
        assertFalse(yeti.isAlive());

        yeti.trackPlayer(500, 500);
        // Posisi tetap tidak berubah
        assertEquals(100, yeti.getX());
        assertEquals(100, yeti.getY());
    }

    @Test
    @DisplayName("Pergerakan mundur Yeti dengan moveBack")
    void testMoveBack() {
        yeti.moveBack(10, 15);
        assertEquals(90, yeti.getX());
        assertEquals(85, yeti.getY());
    }

    @TestFactory
    @DisplayName("Dynamic Tests: Orientasi arah hadap Yeti terhadap posisi target")
    Collection<DynamicTest> dynamicTestsForYetiDirection() {
        return Arrays.asList(
            DynamicTest.dynamicTest("Target di Kanan (X lebih dominan) -> RIGHT", () -> {
                Yeti y = new Yeti(200, 200, 1);
                y.updateAnimation(300, 210); // dx=100 > dy=10
                int sprite = y.getSpriteIndex();
                // Direction RIGHT = 2. Sprite index = (2 * 3) + step
                int direction = sprite / 3;
                assertEquals(Yeti.RIGHT, direction);
            }),
            DynamicTest.dynamicTest("Target di Kiri (X lebih dominan) -> LEFT", () -> {
                Yeti y = new Yeti(200, 200, 1);
                y.updateAnimation(100, 210); // dx=-100 > dy=10
                int direction = y.getSpriteIndex() / 3;
                assertEquals(Yeti.LEFT, direction);
            }),
            DynamicTest.dynamicTest("Target di Depan / Bawah (Y lebih dominan) -> FRONT", () -> {
                Yeti y = new Yeti(200, 200, 1);
                y.updateAnimation(205, 300); // dy=100 > dx=5
                int direction = y.getSpriteIndex() / 3;
                assertEquals(Yeti.FRONT, direction);
            }),
            DynamicTest.dynamicTest("Target di Belakang / Atas (Y lebih dominan) -> BACK", () -> {
                Yeti y = new Yeti(200, 200, 1);
                y.updateAnimation(205, 100); // dy=-100 > dx=5
                int direction = y.getSpriteIndex() / 3;
                assertEquals(Yeti.BACK, direction);
            })
        );
    }
}

