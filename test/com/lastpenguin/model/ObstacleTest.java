package com.lastpenguin.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk entitas Obstacle.
 * Menguji perilaku rintangan permanen (indestructible),
 * rintangan yang dapat hancur (destructible HP), dan rintangan lubang sementara (hazard hole).
 */
public class ObstacleTest {

    @Test
    @DisplayName("Rintangan permanen (indestructible) memiliki HP default -1 dan tidak hancur")
    void testIndestructibleObstacle() {
        Obstacle obs = new Obstacle(50, 50, 80, 80);

        assertAll("Verifikasi Rintangan Permanen",
            () -> assertEquals(50, obs.getX()),
            () -> assertEquals(50, obs.getY()),
            () -> assertEquals(80, obs.getWidth()),
            () -> assertEquals(80, obs.getHeight()),
            () -> assertFalse(obs.isDestructible()),
            () -> assertFalse(obs.isHole()),
            () -> assertEquals(-1, obs.getHp()),
            () -> assertFalse(obs.isDestroyed()),
            () -> assertFalse(obs.isExpired())
        );

        // takeDamage tidak boleh memengaruhi rintangan permanen
        obs.takeDamage();
        assertEquals(-1, obs.getHp());
        assertFalse(obs.isDestroyed());
    }

    @Test
    @DisplayName("Rintangan destructible mengalami pengurangan HP saat terkena damage")
    void testDestructibleObstacle() {
        Obstacle destructibleObs = new Obstacle(100, 100, 80, 80, 2);

        assertTrue(destructibleObs.isDestructible());
        assertEquals(2, destructibleObs.getHp());
        assertFalse(destructibleObs.isDestroyed());

        destructibleObs.takeDamage();
        assertEquals(1, destructibleObs.getHp());
        assertFalse(destructibleObs.isDestroyed());

        destructibleObs.takeDamage();
        assertEquals(0, destructibleObs.getHp());
        assertTrue(destructibleObs.isDestroyed(), "Obstacle harus dinyatakan destroyed saat HP <= 0");

        // takeDamage tambahan saat HP sudah 0 tidak boleh mengurangi di bawah 0
        destructibleObs.takeDamage();
        assertEquals(0, destructibleObs.getHp());
    }

    @Test
    @DisplayName("Rintangan hazard hole kadaluwarsa ketika durasi mencapai 0")
    void testHazardHoleObstacle() {
        Obstacle hole = new Obstacle(200, 200, 160, 100, true, 2);

        assertTrue(hole.isHole());
        assertFalse(hole.isExpired());

        // Update tick 1
        hole.update();
        assertFalse(hole.isExpired());

        // Update tick 2 (durasi mencapai 0)
        hole.update();
        assertTrue(hole.isExpired(), "Hole harus expired saat duration == 0");
    }
}

