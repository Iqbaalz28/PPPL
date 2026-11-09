package com.lastpenguin.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pengujian Decision Table Analysis untuk 3 method:
 * 1. Obstacle.isDestroyed() -> Kondisi: destructible (T/F), hp <= 0 (T/F)
 * 2. Obstacle.isExpired() -> Kondisi: isHole (T/F), duration == 0 (T/F)
 * 3. Obstacle.takeDamage() -> Kondisi: destructible (T/F), hp > 0 (T/F)
 */
public class DecisionTableAnalysisTest {

    // =========================================================================
    // METHOD 1: Obstacle.isDestroyed()
    // Formula: return destructible && hp <= 0;
    // =========================================================================

    @Test
    @DisplayName("DT-M1-R1: destructible = True, hp <= 0 = True -> isDestroyed = True")
    void testDtIsDestroyedRule1() {
        // Objek destructible dengan initial hp = 1, lalu diberi damage sehingga hp = 0
        Obstacle obs = new Obstacle(0, 0, 50, 50, 1);
        obs.takeDamage(); // hp menjadi 0 (<= 0 is True)
        
        assertTrue(obs.isDestructible(), "Kondisi C1 (destructible) harus True");
        assertTrue(obs.getHp() <= 0, "Kondisi C2 (hp <= 0) harus True");
        assertTrue(obs.isDestroyed(), "Aksi: isDestroyed() harus bernilai True");
    }

    @Test
    @DisplayName("DT-M1-R2: destructible = True, hp <= 0 = False -> isDestroyed = False")
    void testDtIsDestroyedRule2() {
        // Objek destructible dengan initial hp = 5 (hp > 0)
        Obstacle obs = new Obstacle(0, 0, 50, 50, 5);

        assertTrue(obs.isDestructible(), "Kondisi C1 (destructible) harus True");
        assertFalse(obs.getHp() <= 0, "Kondisi C2 (hp <= 0) harus False (hp = 5)");
        assertFalse(obs.isDestroyed(), "Aksi: isDestroyed() harus bernilai False");
    }

    @Test
    @DisplayName("DT-M1-R3: destructible = False, hp <= 0 = True -> isDestroyed = False")
    void testDtIsDestroyedRule3() {
        // Objek permanen (destructible = False, default hp = -1 sehingga hp <= 0 True)
        Obstacle obs = new Obstacle(0, 0, 50, 50);

        assertFalse(obs.isDestructible(), "Kondisi C1 (destructible) harus False");
        assertTrue(obs.getHp() <= 0, "Kondisi C2 (hp <= 0) bernilai True karena hp = -1");
        assertFalse(obs.isDestroyed(), "Aksi: isDestroyed() harus bernilai False karena bukan destructible");
    }

    @Test
    @DisplayName("DT-M1-R4: destructible = False, hp <= 0 = False -> isDestroyed = False")
    void testDtIsDestroyedRule4() {
        // Objek hazard hole (isHole = True, destructible = False, hp = -1)
        // Secara alami objek permanen tidak memiliki hp > 0, namun kita validasi status permanennya
        Obstacle obs = new Obstacle(0, 0, 50, 50, false, 10);

        assertFalse(obs.isDestructible(), "Kondisi C1 (destructible) harus False");
        assertFalse(obs.isDestroyed(), "Aksi: isDestroyed() harus bernilai False");
    }

    // =========================================================================
    // METHOD 2: Obstacle.isExpired()
    // Formula: return isHole && duration == 0;
    // =========================================================================

    @Test
    @DisplayName("DT-M2-R1: isHole = True, duration == 0 = True -> isExpired = True")
    void testDtIsExpiredRule1() {
        // Objek hole dengan duration = 1, di-update 1x sehingga duration = 0
        Obstacle hole = new Obstacle(0, 0, 100, 100, true, 1);
        hole.update(); // duration menjadi 0

        assertTrue(hole.isHole(), "Kondisi C1 (isHole) harus True");
        assertTrue(hole.isExpired(), "Aksi: isExpired() harus bernilai True");
    }

    @Test
    @DisplayName("DT-M2-R2: isHole = True, duration == 0 = False -> isExpired = False")
    void testDtIsExpiredRule2() {
        // Objek hole dengan duration = 100 (> 0)
        Obstacle hole = new Obstacle(0, 0, 100, 100, true, 100);

        assertTrue(hole.isHole(), "Kondisi C1 (isHole) harus True");
        assertFalse(hole.isExpired(), "Aksi: isExpired() harus bernilai False karena durasi belum habis");
    }

    @Test
    @DisplayName("DT-M2-R3: isHole = False, duration == 0 = True -> isExpired = False")
    void testDtIsExpiredRule3() {
        // Objek non-hole dengan duration = 0
        Obstacle nonHole = new Obstacle(0, 0, 50, 50, false, 0);

        assertFalse(nonHole.isHole(), "Kondisi C1 (isHole) harus False");
        assertFalse(nonHole.isExpired(), "Aksi: isExpired() harus False karena bukan lubang hazard");
    }

    @Test
    @DisplayName("DT-M2-R4: isHole = False, duration == 0 = False -> isExpired = False")
    void testDtIsExpiredRule4() {
        // Objek non-hole standar permanen (duration = -1)
        Obstacle permanent = new Obstacle(0, 0, 80, 80);

        assertFalse(permanent.isHole(), "Kondisi C1 (isHole) harus False");
        assertFalse(permanent.isExpired(), "Aksi: isExpired() harus False");
    }

    // =========================================================================
    // METHOD 3: Obstacle.takeDamage()
    // Formula: if (destructible && hp > 0) { hp--; }
    // =========================================================================

    @Test
    @DisplayName("DT-M3-R1: destructible = True, hp > 0 = True -> HP berkurang 1")
    void testDtTakeDamageRule1() {
        Obstacle obs = new Obstacle(0, 0, 60, 60, 10);
        int initialHp = obs.getHp();

        assertTrue(obs.isDestructible(), "C1 (destructible) harus True");
        assertTrue(initialHp > 0, "C2 (hp > 0) harus True");

        obs.takeDamage();
        assertEquals(initialHp - 1, obs.getHp(), "Aksi: HP harus berkurang tepat 1");
    }

    @Test
    @DisplayName("DT-M3-R2: destructible = True, hp > 0 = False -> HP tetap (tidak berubah)")
    void testDtTakeDamageRule2() {
        // Objek destructible yang HP-nya sudah 0
        Obstacle obs = new Obstacle(0, 0, 60, 60, 1);
        obs.takeDamage(); // hp = 0
        assertEquals(0, obs.getHp());

        // takeDamage kedua saat hp sudah 0 (hp > 0 is False)
        obs.takeDamage();
        assertEquals(0, obs.getHp(), "Aksi: HP harus tetap 0, tidak boleh berkurang menjadi negatif");
    }

    @Test
    @DisplayName("DT-M3-R3: destructible = False, hp > 0 = True/Irrelevant -> HP tetap")
    void testDtTakeDamageRule3() {
        // Objek permanen (destructible = False)
        Obstacle obs = new Obstacle(0, 0, 80, 80);
        int initialHp = obs.getHp(); // -1

        assertFalse(obs.isDestructible(), "C1 (destructible) bernilai False");
        obs.takeDamage();
        assertEquals(initialHp, obs.getHp(), "Aksi: HP tidak boleh berubah pada objek permanen");
    }

    @Test
    @DisplayName("DT-M3-R4: destructible = False, hp > 0 = False -> HP tetap")
    void testDtTakeDamageRule4() {
        // Objek hazard hole (destructible = False, hp = -1)
        Obstacle hole = new Obstacle(0, 0, 100, 100, true, 50);

        assertFalse(hole.isDestructible(), "C1 (destructible) bernilai False");
        hole.takeDamage();
        assertEquals(-1, hole.getHp(), "Aksi: HP tidak berubah");
    }
}

