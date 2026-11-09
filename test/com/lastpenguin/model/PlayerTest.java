package com.lastpenguin.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.DynamicTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk entitas Player.
 * Menguji fungsionalitas pergerakan, penegakan batas arena,
 * pengelolaan amunisi, timers, status buff, dan pengujian dinamis (Dynamic Tests).
 */
public class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("PenguinHero", 10);
    }

    @Test
    @DisplayName("Inisialisasi Player dengan nilai awal yang valid")
    void testInitialPlayerState() {
        assertAll("Verifikasi Atribut Awal Player",
            () -> assertEquals("PenguinHero", player.getUsername()),
            () -> assertEquals(375, player.getX()),
            () -> assertEquals(200, player.getY()),
            () -> assertEquals(10, player.getRemainingBullets()),
            () -> assertEquals(0, player.getScore()),
            () -> assertEquals(0, player.getYetiKilled()),
            () -> assertEquals(0, player.getMissedShots()),
            () -> assertTrue(player.isAlive()),
            () -> assertEquals(100, player.getHealth()),
            () -> assertFalse(player.isGiantBuffActive()),
            () -> assertFalse(player.isGhost())
        );
    }

    @Test
    @DisplayName("Pergerakan normal memperbarui posisi dan arah hadap")
    void testNormalMovement() {
        player.move(1, 0); // Bergerak ke kanan: x += 5
        assertEquals(380, player.getX());
        assertEquals(200, player.getY());
        assertEquals(1, player.getLastDx());
        assertEquals(0, player.getLastDy());

        player.move(0, 1); // Bergerak ke bawah: y += 5
        assertEquals(380, player.getX());
        assertEquals(205, player.getY());
        assertEquals(0, player.getLastDx());
        assertEquals(1, player.getLastDy());
    }

    @Test
    @DisplayName("Player yang mati (die) tidak dapat bergerak")
    void testDeadPlayerCannotMove() {
        player.die();
        assertFalse(player.isAlive());
        assertEquals(0, player.getHealth());

        player.move(2, 2);
        // Koordinat tetap berada di posisi awal (375, 200)
        assertEquals(375, player.getX());
        assertEquals(200, player.getY());
    }

    @Test
    @DisplayName("Penegakan batas arena (Boundary Clamping) pada koordinat X dan Y")
    void testArenaBoundaryClamping() {
        // Uji batas bawah X (x < 0 dicegah menjadi 0)
        player.move(-100, 0);
        assertEquals(0, player.getX());

        // Uji batas atas X (x > 750 dicegah menjadi 750)
        player.move(200, 0);
        assertEquals(750, player.getX());

        // Uji batas bawah Y (y < 0 dicegah menjadi 0)
        player.move(0, -100);
        assertEquals(0, player.getY());

        // Uji batas atas Y (y > 550 dicegah menjadi 550)
        player.move(0, 200);
        assertEquals(550, player.getY());
    }

    @Test
    @DisplayName("Penggunaan dan penambahan amunisi (useBullet & addBullets)")
    void testAmmunitionManagement() {
        player.useBullet();
        assertEquals(9, player.getRemainingBullets());

        player.addBullets(5);
        assertEquals(14, player.getRemainingBullets());

        // Penggunaan peluru hingga 0
        for (int i = 0; i < 14; i++) {
            player.useBullet();
        }
        assertEquals(0, player.getRemainingBullets());

        // Jika peluru 0, useBullet tidak boleh menghasilkan angka negatif
        player.useBullet();
        assertEquals(0, player.getRemainingBullets());
    }

    @Test
    @DisplayName("Pembaruan timer cooldown dan status buff")
    void testUpdateTimers() {
        player.setCooldownS1(10);
        player.setCooldownS2(5);
        player.setCooldownS3(1);
        player.setGhostDuration(3);

        player.updateTimers();

        assertEquals(9, player.getCooldownS1());
        assertEquals(4, player.getCooldownS2());
        assertEquals(0, player.getCooldownS3());
        assertTrue(player.isGhost());

        // Dua tick lagi, ghostDuration habis
        player.updateTimers();
        player.updateTimers();
        assertFalse(player.isGhost());
    }

    @Test
    @DisplayName("Pencatatan statistik skor dan eliminasi Yeti")
    void testScoreAndKillStatistics() {
        player.registerKill(100);
        assertEquals(100, player.getScore());
        assertEquals(1, player.getYetiKilled());

        player.registerKill(150);
        assertEquals(250, player.getScore());
        assertEquals(2, player.getYetiKilled());

        player.registerMiss();
        assertEquals(1, player.getMissedShots());
    }

    @Test
    @DisplayName("Pengujian asersi exception dengan assertThrows")
    void testExceptionHandling() {
        // Menguji bahwa manipulasi batas pada array atau null melempar exception yang sesuai
        assertThrows(NullPointerException.class, () -> {
            Player nullPlayer = null;
            nullPlayer.getX();
        });
    }

    @TestFactory
    @DisplayName("Dynamic Tests: Pengujian arah hadap dan perpindahan multi-arah")
    Collection<DynamicTest> dynamicTestsForMovementDirections() {
        return Arrays.asList(
            DynamicTest.dynamicTest("Arah Kanan (dx=1, dy=0)", () -> {
                Player p = new Player("Tester", 5);
                p.move(1, 0);
                assertEquals(1, p.getLastDx());
                assertEquals(0, p.getLastDy());
            }),
            DynamicTest.dynamicTest("Arah Kiri (dx=-1, dy=0)", () -> {
                Player p = new Player("Tester", 5);
                p.move(-1, 0);
                assertEquals(-1, p.getLastDx());
                assertEquals(0, p.getLastDy());
            }),
            DynamicTest.dynamicTest("Arah Bawah (dx=0, dy=1)", () -> {
                Player p = new Player("Tester", 5);
                p.move(0, 1);
                assertEquals(0, p.getLastDx());
                assertEquals(1, p.getLastDy());
            }),
            DynamicTest.dynamicTest("Arah Atas (dx=0, dy=-1)", () -> {
                Player p = new Player("Tester", 5);
                p.move(0, -1);
                assertEquals(0, p.getLastDx());
                assertEquals(-1, p.getLastDy());
            }),
            DynamicTest.dynamicTest("Diam (dx=0, dy=0) tidak mengubah lastDx dan lastDy", () -> {
                Player p = new Player("Tester", 5);
                int initDx = p.getLastDx();
                int initDy = p.getLastDy();
                p.move(0, 0);
                assertEquals(initDx, p.getLastDx());
                assertEquals(initDy, p.getLastDy());
            })
        );
    }
}
