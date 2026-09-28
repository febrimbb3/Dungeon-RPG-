package com.example

import com.example.model.GameFormulas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBossEquipmentDropTiers() {
        // Floor 1-25 drops EPIK
        val equip20 = GameFormulas.generateBossEquipmentDrop(20, "PEDANG")
        assertEquals("EPIK", equip20.rarity)

        // Floor 30-60 drops LEGEND
        val equip50 = GameFormulas.generateBossEquipmentDrop(50, "TONGKAT")
        assertEquals("LEGEND", equip50.rarity)

        // Floor 65-90 drops MYTHIC
        val equip80 = GameFormulas.generateBossEquipmentDrop(80, "PEDANG")
        assertEquals("MYTHIC", equip80.rarity)

        // Floor 95-100 drops DIVINE
        val equip100 = GameFormulas.generateBossEquipmentDrop(100, "TONGKAT")
        assertEquals("DIVINE", equip100.rarity)
    }

    @Test
    fun testTenSacredArtifacts() {
        assertEquals(10, GameFormulas.TEN_ARTIFACTS.size)

        // Check slot numbers 1 to 10 exist
        for (i in 1..10) {
            val art = GameFormulas.TEN_ARTIFACTS.find { it.slotNumber == i }
            assertNotNull("Artifact with slot $i should exist", art)
        }

        // Slot 10 is the ultimate Floor 100 weapon artifact
        val art10 = GameFormulas.TEN_ARTIFACTS.find { it.slotNumber == 10 }!!
        assertTrue(art10.dropChanceText.contains("100%"))
        assertTrue(art10.dropFloorRange.contains("100"))
    }

    @Test
    fun testCheckpointCalculation() {
        // Checkpoint is every 5 floors: (floor / 5) * 5
        fun getCheckpoint(floor: Int) = ((floor / 5) * 5).coerceAtLeast(1)

        assertEquals(1, getCheckpoint(4))
        assertEquals(5, getCheckpoint(7))
        assertEquals(25, getCheckpoint(28))
        assertEquals(50, getCheckpoint(53))
        assertEquals(95, getCheckpoint(99))
        assertEquals(100, getCheckpoint(100))
    }

    @Test
    fun testRaceSystemAndDragonBossRestriction() {
        // 2 races for swordsman
        assertEquals(2, GameFormulas.SWORDSMAN_RACES.size)
        val swordRaceIds = GameFormulas.SWORDSMAN_RACES.map { it.id }
        assertTrue(swordRaceIds.contains("MANUSIA"))
        assertTrue(swordRaceIds.contains("BEASTKIN"))

        // 2 races for mage
        assertEquals(2, GameFormulas.MAGE_RACES.size)
        val mageRaceIds = GameFormulas.MAGE_RACES.map { it.id }
        assertTrue(mageRaceIds.contains("ELF"))
        assertTrue(mageRaceIds.contains("DEMONKIN"))

        // Dragon race is strictly non-playable and reserved for upper dungeon bosses
        val dragonRace = GameFormulas.DRAGON_BOSS_RACE
        assertEquals("NAGA", dragonRace.id)
        assertFalse(dragonRace.isPlayable)

        // Lower floor bosses (5-60) are not dragon
        for (f in 5..60 step 5) {
            val boss = GameFormulas.generateMonsterForFloor(f)
            assertFalse("Boss floor $f should not be dragon", boss.isDragonBoss)
        }

        // Upper floor bosses (65-100) are strictly Ras Naga
        for (f in 65..100 step 5) {
            val boss = GameFormulas.generateMonsterForFloor(f)
            assertTrue("Boss floor $f must be Ras Naga", boss.isDragonBoss)
            assertTrue("Boss name floor $f must indicate Ras Naga", boss.name.contains("Ras Naga"))
        }
    }
}

