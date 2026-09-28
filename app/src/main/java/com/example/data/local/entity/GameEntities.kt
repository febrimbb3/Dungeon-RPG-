package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character")
data class CharacterEntity(
    @PrimaryKey val id: Long = 1L,
    val name: String,
    val characterClass: String, // "PEDANG" or "SIHIR"
    val characterRace: String = "MANUSIA", // "MANUSIA", "BEASTKIN", "ELF", "DEMONKIN"
    val level: Int = 1, // 1 to 100
    val exp: Long = 0L,
    val currentHp: Int = 120,
    val maxHp: Int = 120,
    val currentMp: Int = 50,
    val maxMp: Int = 50,
    val baseAtk: Int = 22,
    val baseDef: Int = 10,
    val gold: Int = 100,
    val currentFloor: Int = 1, // 1 to 100
    val maxFloorReached: Int = 1,
    val tutorialCompleted: Boolean = false,
    val monstersDefeated: Int = 0,
    val bossesDefeated: Int = 0
)

@Entity(tableName = "weapons")
data class WeaponEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val weaponType: String, // "PEDANG" or "TONGKAT"
    val tier: Int = 1, // 1 to 5
    val rarity: String = "COMMON", // "COMMON", "EPIK", "LEGEND", "MYTHIC", "DIVINE"
    val basePower: Int = 15,
    val upgradeLevel: Int = 0, // 0 to 15 (+1, +2, ..., +15)
    val runeType: String = "NONE", // "NONE", "API", "ES", "PETIR", "KEHIDUPAN"
    val runePower: Int = 0,
    val isEquipped: Boolean = false,
    val description: String = ""
)

@Entity(tableName = "artifacts")
data class ArtifactEntity(
    @PrimaryKey val artifactId: String, // "art_1" .. "art_10"
    val slotNumber: Int, // 1 to 10
    val name: String,
    val title: String,
    val description: String,
    val statBonusDesc: String,
    val atkBonus: Int = 0,
    val defBonus: Int = 0,
    val hpBonus: Int = 0,
    val mpBonus: Int = 0,
    val isFloor100Mastery: Boolean = false,
    val acquiredAtFloor: Int = 0,
    val isStolen: Boolean = false,
    val stolenByRivalName: String? = null
)

@Entity(tableName = "inventory")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val itemId: String,
    val name: String,
    val itemType: String, // "POTION", "MATERIAL", "RUNE", "SPECIAL"
    val quantity: Int = 1,
    val description: String = "",
    val value: Int = 10
)

@Entity(tableName = "party_members")
data class PartyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val role: String, // "PEDANG", "SIHIR", "TABIB", "TANK"
    val level: Int = 1,
    val hp: Int = 100,
    val maxHp: Int = 100,
    val atk: Int = 18,
    val def: Int = 10,
    val skillName: String,
    val skillDesc: String,
    val isRecruited: Boolean = false,
    val isInParty: Boolean = false,
    val recruitCost: Int = 200
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val rankNumber: Int,
    val name: String,
    val className: String,
    val level: Int,
    val floorReached: Int,
    val score: Int,
    val isPlayer: Boolean = false,
    val title: String = ""
)
