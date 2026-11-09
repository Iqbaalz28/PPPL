package com.lastpenguin.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk entitas Projectile.
 * Menguji normalisasi lintasan peluru, pembaruan posisi,
 * status piercing, dan ukuran bounding box hitbox.
 */
public class ProjectileTest {

    @Test
    @DisplayName("Inisialisasi proyektil standar pemain dengan hitbox normal (12x12)")
    void testStandardProjectile() {
        Projectile p = new Projectile(100, 100, 10, 0, Projectile.PLAYER_TYPE, 8);

        assertEquals(100, p.getX());
        assertEquals(100, p.getY());
        assertEquals(Projectile.PLAYER_TYPE, p.getOwner());
        assertTrue(p.isActive());
        assertFalse(p.isHit());
        assertFalse(p.isPiercing());

        Rectangle bounds = p.getBounds();
        assertEquals(12, bounds.width);
        assertEquals(12, bounds.height);
    }

    @Test
    @DisplayName("Pembaruan posisi (update) menggerakkan proyektil sesuai kecepatan")
    void testProjectileMovement() {
        // Peluru bergerak lurus ke kanan dengan speed 8
        Projectile p = new Projectile(0, 0, 10, 0, Projectile.PLAYER_TYPE, 8);
        p.update();
        assertEquals(8, p.getX());
        assertEquals(0, p.getY());

        p.update();
        assertEquals(16, p.getX());
    }

    @Test
    @DisplayName("Proyektil piercing (Giant Snowball) memiliki hitbox lebih besar (40x40)")
    void testPiercingProjectile() {
        Projectile p = new Projectile(50, 50, 0, 10, Projectile.PLAYER_TYPE, 8);
        p.setPiercing(true);

        assertTrue(p.isPiercing());
        Rectangle bounds = p.getBounds();
        assertEquals(40, bounds.width);
        assertEquals(40, bounds.height);
    }
}

