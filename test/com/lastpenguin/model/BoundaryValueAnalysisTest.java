package com.lastpenguin.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pengujian Boundary Value Analysis (BVA) untuk 3 method:
 * 1. Player.move(int dx, int dy) -> Batas arena X [0, 750] dan Y [0, 550]
 * 2. Yeti.takeDamage(int damage) -> Batas ambang kematian HP <= 0 (damage = 49, 50, 51)
 * 3. Yeti.<init>(int x, int y, int difficultyValue) -> Batas nilai difficultyValue (0, 1, 2)
 */
public class BoundaryValueAnalysisTest {

    // =========================================================================
    // METHOD 1: Player.move(int dx, int dy) - Boundary Value Analysis
    // =========================================================================

    @Test
    @DisplayName("BVA-M1-01: Batas Bawah X (x = 0) - Tepat pada Batas Minimum")
    void testBvaPlayerMoveMinX() {
        Player p = new Player("Hero", 10);
        // Posisi awal (375, 200). Pindahkan ke x = 0 (375 / 5 = 75 langkah ke kiri)
        p.move(-75, 0);
        assertEquals(0, p.getX(), "Koordinat X harus tepat bernilai 0 (batas minimum)");
    }

    @Test
    @DisplayName("BVA-M1-02: Batas Bawah X (x < 0) - Nilai di bawah Minimum (Min - 1)")
    void testBvaPlayerMoveBelowMinX() {
        Player p = new Player("Hero", 10);
        // Pindahkan melebihi batas kiri (76 langkah ke kiri -> kalkulasi: 375 - 380 = -5)
        p.move(-76, 0);
        assertEquals(0, p.getX(), "Koordinat X yang di bawah 0 harus dikunci (clamped) ke 0");
    }

    @Test
    @DisplayName("BVA-M1-03: Batas Bawah X - Nilai di atas Minimum (Min + 5)")
    void testBvaPlayerMoveAboveMinX() {
        Player p = new Player("Hero", 10);
        // Pindahkan ke 74 langkah ke kiri -> kalkulasi: 375 - 370 = 5
        p.move(-74, 0);
        assertEquals(5, p.getX(), "Koordinat X tepat 1 step di atas minimum harus bernilai 5");
    }

    @Test
    @DisplayName("BVA-M1-04: Batas Atas X (x = 750) - Tepat pada Batas Maksimum")
    void testBvaPlayerMoveMaxX() {
        Player p = new Player("Hero", 10);
        // Posisi awal (375, 200). Pindahkan ke kanan 75 langkah -> 375 + 375 = 750
        p.move(75, 0);
        assertEquals(750, p.getX(), "Koordinat X harus tepat bernilai 750 (batas maksimum)");
    }

    @Test
    @DisplayName("BVA-M1-05: Batas Atas X (x > 750) - Nilai di atas Maksimum (Max + 1)")
    void testBvaPlayerMoveAboveMaxX() {
        Player p = new Player("Hero", 10);
        // Pindahkan melebihi batas kanan (76 langkah -> 375 + 380 = 755)
        p.move(76, 0);
        assertEquals(750, p.getX(), "Koordinat X yang melebihi 750 harus dikunci ke 750");
    }

    @Test
    @DisplayName("BVA-M1-06: Batas Atas X - Nilai di bawah Maksimum (Max - 5)")
    void testBvaPlayerMoveBelowMaxX() {
        Player p = new Player("Hero", 10);
        // Pindahkan 74 langkah ke kanan -> 375 + 370 = 745
        p.move(74, 0);
        assertEquals(745, p.getX(), "Koordinat X tepat 1 step di bawah maksimum harus bernilai 745");
    }

    @Test
    @DisplayName("BVA-M1-07: Batas Bawah Y (y = 0) - Tepat pada Batas Minimum")
    void testBvaPlayerMoveMinY() {
        Player p = new Player("Hero", 10);
        // Posisi awal (375, 200). Pindahkan ke atas 40 langkah -> 200 - 200 = 0
        p.move(0, -40);
        assertEquals(0, p.getY(), "Koordinat Y harus tepat bernilai 0 (batas minimum)");
    }

    @Test
    @DisplayName("BVA-M1-08: Batas Bawah Y (y < 0) - Nilai di bawah Minimum (Min - 1)")
    void testBvaPlayerMoveBelowMinY() {
        Player p = new Player("Hero", 10);
        // Pindahkan melebihi batas atas (41 langkah ke atas -> 200 - 205 = -5)
        p.move(0, -41);
        assertEquals(0, p.getY(), "Koordinat Y yang di bawah 0 harus dikunci ke 0");
    }

    @Test
    @DisplayName("BVA-M1-09: Batas Atas Y (y = 550) - Tepat pada Batas Maksimum")
    void testBvaPlayerMoveMaxY() {
        Player p = new Player("Hero", 10);
        // Posisi awal (375, 200). Pindahkan ke bawah 70 langkah -> 200 + 350 = 550
        p.move(0, 70);
        assertEquals(550, p.getY(), "Koordinat Y harus tepat bernilai 550 (batas maksimum)");
    }

    @Test
    @DisplayName("BVA-M1-10: Batas Atas Y (y > 550) - Nilai di atas Maksimum (Max + 1)")
    void testBvaPlayerMoveAboveMaxY() {
        Player p = new Player("Hero", 10);
        // Pindahkan melebihi batas bawah (71 langkah -> 200 + 355 = 555)
        p.move(0, 71);
        assertEquals(550, p.getY(), "Koordinat Y yang melebihi 550 harus dikunci ke 550");
    }

    // =========================================================================
    // METHOD 2: Yeti.takeDamage(int damage) - Boundary Value Analysis
    // =========================================================================

    @Test
    @DisplayName("BVA-M2-01: Damage = 0 (Nilai Minimum Non-Damage)")
    void testBvaYetiDamageZero() {
        Yeti y = new Yeti(100, 100, 1);
        y.takeDamage(0);
        assertTrue(y.isAlive(), "Damage 0 tidak boleh mematikan Yeti");
    }

    @Test
    @DisplayName("BVA-M2-02: Damage = 49 (Ambang Kritis - 1: Sub-Lethal Damage)")
    void testBvaYetiDamageSubLethal() {
        Yeti y = new Yeti(100, 100, 1); // Health awal = 50
        y.takeDamage(49);
        assertTrue(y.isAlive(), "Damage 49 (sisa health 1) harus tetap membuat Yeti hidup");
    }

    @Test
    @DisplayName("BVA-M2-03: Damage = 50 (Tepat pada Ambang Kritis Kematian: Lethal Boundary)")
    void testBvaYetiDamageExactLethal() {
        Yeti y = new Yeti(100, 100, 1);
        y.takeDamage(50);
        assertFalse(y.isAlive(), "Damage 50 (sisa health 0) harus mematikan Yeti (alive = false)");
    }

    @Test
    @DisplayName("BVA-M2-04: Damage = 51 (Ambang Kritis + 1: Overkill Damage)")
    void testBvaYetiDamageOverkill() {
        Yeti y = new Yeti(100, 100, 1);
        y.takeDamage(51);
        assertFalse(y.isAlive(), "Damage 51 (sisa health -1) harus mematikan Yeti (alive = false)");
    }

    @Test
    @DisplayName("BVA-M2-05: Damage = -1 (Nilai di Bawah Minimum / Invalid Boundary)")
    void testBvaYetiDamageNegative() {
        Yeti y = new Yeti(100, 100, 1);
        y.takeDamage(-1);
        assertTrue(y.isAlive(), "Damage negatif tidak boleh mematikan Yeti");
    }

    // =========================================================================
    // METHOD 3: Yeti Constructor (difficultyValue) - Boundary Value Analysis
    // =========================================================================

    @Test
    @DisplayName("BVA-M3-01: difficultyValue = 1 (Tepat pada Batas Ambang Ternary)")
    void testBvaYetiDifficultyBoundaryOne() {
        Yeti y = new Yeti(0, 0, 1);
        assertEquals(1, y.getSpeed(), "difficultyValue 1 harus menghasilkan speed = 1");
    }

    @Test
    @DisplayName("BVA-M3-02: difficultyValue = 0 (Batas Ambang - 1: Nilai Invalid/Sub-Minimum)")
    void testBvaYetiDifficultyZero() {
        Yeti y = new Yeti(0, 0, 0);
        assertEquals(1, y.getSpeed(), "difficultyValue 0 (< 1) harus dikembalikan menjadi speed minimum 1");
    }

    @Test
    @DisplayName("BVA-M3-03: difficultyValue = -1 (Batas Ambang - 2: Nilai Negatif Ekstrim)")
    void testBvaYetiDifficultyNegative() {
        Yeti y = new Yeti(0, 0, -1);
        assertEquals(1, y.getSpeed(), "difficultyValue -1 (< 1) harus dikembalikan menjadi speed minimum 1");
    }

    @Test
    @DisplayName("BVA-M3-04: difficultyValue = 2 (Batas Ambang + 1: Mode Medium)")
    void testBvaYetiDifficultyTwo() {
        Yeti y = new Yeti(0, 0, 2);
        assertEquals(2, y.getSpeed(), "difficultyValue 2 harus menghasilkan speed = 2");
    }

    @Test
    @DisplayName("BVA-M3-05: difficultyValue = 3 (Nilai Maksimum Game Konvensional: Mode Hard)")
    void testBvaYetiDifficultyThree() {
        Yeti y = new Yeti(0, 0, 3);
        assertEquals(3, y.getSpeed(), "difficultyValue 3 harus menghasilkan speed = 3");
    }
}

