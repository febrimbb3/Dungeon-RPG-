package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ArtifactEntity
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.InventoryItemEntity
import com.example.data.local.entity.LeaderboardEntity
import com.example.data.local.entity.PartyMemberEntity
import com.example.data.local.entity.WeaponEntity
import com.example.model.ArtifactDefinition
import com.example.model.CheckpointShopItem
import com.example.model.GameFormulas
import com.example.model.RivalAdventurer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max
import kotlin.random.Random

data class BossLootOutcome(
    val droppedEquipment: WeaponEntity?,
    val droppedArtifact: ArtifactEntity?,
    val isFloor100Completed: Boolean = false,
    val summaryMessage: String
)

data class PvpDuelOutcome(
    val isVictory: Boolean,
    val stolenArtifact: ArtifactEntity?,
    val reclaimedArtifact: ArtifactEntity?,
    val respawnFloor: Int,
    val message: String
)

class GameRepository(private val db: AppDatabase) {

    val character: Flow<CharacterEntity?> = db.characterDao().getCharacter()
    val allWeapons: Flow<List<WeaponEntity>> = db.weaponDao().getAllWeapons()
    val equippedWeapon: Flow<WeaponEntity?> = db.weaponDao().getEquippedWeapon()
    val inventory: Flow<List<InventoryItemEntity>> = db.inventoryDao().getAllInventory()
    val partyMembers: Flow<List<PartyMemberEntity>> = db.partyMemberDao().getAllMembers()
    val activeParty: Flow<List<PartyMemberEntity>> = db.partyMemberDao().getActiveParty()
    val leaderboard: Flow<List<LeaderboardEntity>> = db.leaderboardDao().getAllLeaderboard()
    val allArtifacts: Flow<List<ArtifactEntity>> = db.artifactDao().getAllArtifacts()
    val activeArtifacts: Flow<List<ArtifactEntity>> = db.artifactDao().getActiveArtifacts()

    suspend fun getCharacterOnce(): CharacterEntity? = db.characterDao().getCharacterOnce()
    suspend fun getEquippedWeaponOnce(): WeaponEntity? = db.weaponDao().getEquippedWeaponOnce()
    suspend fun getActiveArtifactsOnce(): List<ArtifactEntity> = db.artifactDao().getActiveArtifactsOnce()

    suspend fun createNewCharacter(name: String, characterClass: String, characterRace: String = "MANUSIA") {
        val isSwordsman = characterClass == "PEDANG"
        var maxHp = if (isSwordsman) 130 else 95
        var maxMp = if (isSwordsman) 50 else 90
        var baseAtk = if (isSwordsman) 24 else 28
        var baseDef = if (isSwordsman) 12 else 8

        // Racial stat adjustments
        when (characterRace) {
            "MANUSIA" -> {
                maxHp += 20
                baseDef += 5
            }
            "BEASTKIN" -> {
                maxHp += 30
                baseAtk += 6
            }
            "ELF" -> {
                maxMp += 35
                baseAtk += 4
            }
            "DEMONKIN" -> {
                maxHp += 15
                maxMp += 20
                baseAtk += 8
            }
        }

        val newChar = CharacterEntity(
            id = 1L,
            name = name.trim().ifEmpty { if (isSwordsman) "Arthur" else "Merlin" },
            characterClass = characterClass,
            characterRace = characterRace,
            level = 1,
            exp = 0L,
            currentHp = maxHp,
            maxHp = maxHp,
            currentMp = maxMp,
            maxMp = maxMp,
            baseAtk = baseAtk,
            baseDef = baseDef,
            gold = 150,
            currentFloor = 1,
            maxFloorReached = 1,
            tutorialCompleted = false,
            monstersDefeated = 0,
            bossesDefeated = 0
        )
        db.characterDao().insertOrUpdate(newChar)

        // Starter Weapon
        db.weaponDao().unequipAll()
        val starterWeapon = if (isSwordsman) {
            WeaponEntity(
                name = "Pedang Kayu Pemula",
                weaponType = "PEDANG",
                tier = 1,
                basePower = 16,
                upgradeLevel = 0,
                runeType = "NONE",
                runePower = 0,
                isEquipped = true,
                description = "Pedang kayu kokoh yang diberikan oleh Guild Petualang untuk melatih refleks pemula."
            )
        } else {
            WeaponEntity(
                name = "Tongkat Sihir Akasia",
                weaponType = "TONGKAT",
                tier = 1,
                basePower = 18,
                upgradeLevel = 0,
                runeType = "NONE",
                runePower = 0,
                isEquipped = true,
                description = "Tongkat kayu akasia beralur mana halus, memperkuat aliran sihir dasar penyihir."
            )
        }
        db.weaponDao().insertWeapon(starterWeapon)

        // Starter items
        db.inventoryDao().insertOrUpdate(
            InventoryItemEntity(
                itemId = "potion_hp_small",
                name = "Ramuan HP Kecil",
                itemType = "POTION",
                quantity = 5,
                description = "Memulihkan 60 HP karakter.",
                value = 25
            )
        )
        db.inventoryDao().insertOrUpdate(
            InventoryItemEntity(
                itemId = "potion_mp_small",
                name = "Ramuan MP Kecil",
                itemType = "POTION",
                quantity = 4,
                description = "Memulihkan 45 MP karakter.",
                value = 25
            )
        )
        db.inventoryDao().insertOrUpdate(
            InventoryItemEntity(
                itemId = "ore_iron",
                name = "Batu Besi Tempa",
                itemType = "MATERIAL",
                quantity = 10,
                description = "Bahan dasar untuk menempa dan meningkatkan level senjata di bengkel.",
                value = 15
            )
        )

        updateLeaderboardPlayer(newChar)
    }

    suspend fun completeTutorial() {
        val char = db.characterDao().getCharacterOnce() ?: return
        val updated = char.copy(
            tutorialCompleted = true,
            gold = char.gold + 100,
            exp = char.exp + 60
        )
        db.characterDao().insertOrUpdate(updated)

        // Give reward items
        val ironOre = db.inventoryDao().getItem("ore_iron")
        if (ironOre != null) {
            db.inventoryDao().updateItem(ironOre.copy(quantity = ironOre.quantity + 8))
        } else {
            db.inventoryDao().insertOrUpdate(
                InventoryItemEntity(
                    itemId = "ore_iron",
                    name = "Batu Besi Tempa",
                    itemType = "MATERIAL",
                    quantity = 8,
                    description = "Bahan dasar untuk menempa dan meningkatkan level senjata di bengkel."
                )
            )
        }
        checkAndApplyLevelUp()
    }

    suspend fun updateCharacterHpMp(hp: Int, mp: Int) {
        val char = db.characterDao().getCharacterOnce() ?: return
        val validHp = hp.coerceIn(0, char.maxHp)
        val validMp = mp.coerceIn(0, char.maxMp)
        db.characterDao().updateHpMp(validHp, validMp)
    }

    suspend fun onFloorVictory(floor: Int, isBoss: Boolean, expGained: Long, goldGained: Int): BossLootOutcome {
        val char = db.characterDao().getCharacterOnce() ?: return BossLootOutcome(null, null, false, "")
        val newDefeated = char.monstersDefeated + 1
        val newBosses = if (isBoss) char.bossesDefeated + 1 else char.bossesDefeated
        val nextFloor = if (floor < 100) floor + 1 else 100
        val maxFloor = max(char.maxFloorReached, nextFloor)

        val updated = char.copy(
            exp = char.exp + expGained,
            gold = char.gold + goldGained,
            currentFloor = nextFloor,
            maxFloorReached = maxFloor,
            monstersDefeated = newDefeated,
            bossesDefeated = newBosses
        )
        db.characterDao().insertOrUpdate(updated)

        // Drop random materials based on floor
        val dropId = when {
            floor >= 80 -> "material_divine_ore"
            floor >= 50 -> "material_dragon_scale"
            floor >= 25 -> "material_mana_crystal"
            else -> "ore_iron"
        }
        val dropName = when {
            floor >= 80 -> "Batu Inti Dewa"
            floor >= 50 -> "Sisik Naga Bawah Tanah"
            floor >= 25 -> "Kristal Mana Halus"
            else -> "Batu Besi Tempa"
        }
        val existingDrop = db.inventoryDao().getItem(dropId)
        val dropQty = if (isBoss) 3 else 1
        if (existingDrop != null) {
            db.inventoryDao().updateItem(existingDrop.copy(quantity = existingDrop.quantity + dropQty))
        } else {
            db.inventoryDao().insertOrUpdate(
                InventoryItemEntity(
                    itemId = dropId,
                    name = dropName,
                    itemType = "MATERIAL",
                    quantity = dropQty,
                    description = "Bahan crafting langka dari dungeon lantai $floor.",
                    value = 20 + floor * 2
                )
            )
        }

        var droppedWeapon: WeaponEntity? = null
        var droppedArtifact: ArtifactEntity? = null
        var isFloor100Completed = false
        val summaryMessages = mutableListOf<String>()

        if (isBoss) {
            // 1. Guaranteed Equipment Drop by Floor Tiers:
            // Floor 1-25: Epik equipment
            // Floor 30-60: Legend equipment
            // Floor 65-90: Mythic equipment
            // Floor 95-100: Divine/Godly equipment
            val bossEquip = GameFormulas.generateBossEquipmentDrop(floor, char.characterClass)
            val newWeaponId = db.weaponDao().insertWeapon(bossEquip)
            droppedWeapon = bossEquip.copy(id = newWeaponId)
            val rarityBadge = when (bossEquip.rarity) {
                "DIVINE" -> "[DIVINE / GODLY]"
                "MYTHIC" -> "[MYTHIC]"
                "LEGEND" -> "[LEGEND]"
                else -> "[EPIK]"
            }
            summaryMessages.add("BOS MENJATUHKAN PERALATAN $rarityBadge: ${bossEquip.name} (Tier ${bossEquip.tier}, Power ${bossEquip.basePower})!")

            // 2. Artifact Drop Logic:
            // Lantai 100: Otomatis dapat Artefak Ke-10 penanda tamat 100 lantai!
            if (floor == 100) {
                isFloor100Completed = true
                val art10Def = GameFormulas.TEN_ARTIFACTS.first { it.id == "art_10" }
                val artEntity = ArtifactEntity(
                    artifactId = art10Def.id,
                    slotNumber = art10Def.slotNumber,
                    name = art10Def.name,
                    title = art10Def.title,
                    description = art10Def.description,
                    statBonusDesc = art10Def.statBonusDesc,
                    atkBonus = art10Def.atkBonus,
                    defBonus = art10Def.defBonus,
                    hpBonus = art10Def.hpBonus,
                    mpBonus = art10Def.mpBonus,
                    isFloor100Mastery = true,
                    acquiredAtFloor = 100
                )
                db.artifactDao().insertOrUpdate(artEntity)
                droppedArtifact = artEntity
                summaryMessages.add("SELAMAT! Kemenangan atas Boss Lantai 100 menganugerahi Artefak Senjata Tertinggi: ${art10Def.name}!")
            } else if (floor in 76..95) {
                // Drop rate 0.2%
                val roll = Random.nextDouble()
                if (roll < 0.002) {
                    val candidateDefs = GameFormulas.TEN_ARTIFACTS.filter { it.slotNumber in 5..9 }
                    val randomArtDef = candidateDefs.random()
                    val artEntity = ArtifactEntity(
                        artifactId = randomArtDef.id,
                        slotNumber = randomArtDef.slotNumber,
                        name = randomArtDef.name,
                        title = randomArtDef.title,
                        description = randomArtDef.description,
                        statBonusDesc = randomArtDef.statBonusDesc,
                        atkBonus = randomArtDef.atkBonus,
                        defBonus = randomArtDef.defBonus,
                        hpBonus = randomArtDef.hpBonus,
                        mpBonus = randomArtDef.mpBonus,
                        acquiredAtFloor = floor
                    )
                    db.artifactDao().insertOrUpdate(artEntity)
                    droppedArtifact = artEntity
                    summaryMessages.add("BERKAH KEAJAIBAN! Artefak Langka Langit jatuh (0.2%): ${randomArtDef.name}!")
                }
            } else if (floor in 50..75) {
                // Drop rate 0.1%
                val roll = Random.nextDouble()
                if (roll < 0.001) {
                    val candidateDefs = GameFormulas.TEN_ARTIFACTS.filter { it.slotNumber in 1..4 }
                    val randomArtDef = candidateDefs.random()
                    val artEntity = ArtifactEntity(
                        artifactId = randomArtDef.id,
                        slotNumber = randomArtDef.slotNumber,
                        name = randomArtDef.name,
                        title = randomArtDef.title,
                        description = randomArtDef.description,
                        statBonusDesc = randomArtDef.statBonusDesc,
                        atkBonus = randomArtDef.atkBonus,
                        defBonus = randomArtDef.defBonus,
                        hpBonus = randomArtDef.hpBonus,
                        mpBonus = randomArtDef.mpBonus,
                        acquiredAtFloor = floor
                    )
                    db.artifactDao().insertOrUpdate(artEntity)
                    droppedArtifact = artEntity
                    summaryMessages.add("BERKAH KEAJAIBAN! Artefak Langka Nether jatuh (0.1%): ${randomArtDef.name}!")
                }
            }
        }

        checkAndApplyLevelUp()
        updateLeaderboardPlayer(db.characterDao().getCharacterOnce() ?: updated)

        return BossLootOutcome(
            droppedEquipment = droppedWeapon,
            droppedArtifact = droppedArtifact,
            isFloor100Completed = isFloor100Completed,
            summaryMessage = summaryMessages.joinToString("\n")
        )
    }

    suspend fun executePvpDuel(rival: RivalAdventurer, playerWon: Boolean): PvpDuelOutcome {
        val char = db.characterDao().getCharacterOnce() ?: return PvpDuelOutcome(false, null, null, 1, "Karakter tidak ada")

        if (playerWon) {
            // Player defeated the rival!
            val bonusGold = rival.floor * 40 + 100
            val bonusExp = rival.floor * 70L + 120
            val updatedChar = char.copy(
                gold = char.gold + bonusGold,
                exp = char.exp + bonusExp
            )
            db.characterDao().insertOrUpdate(updatedChar)

            // Check if player had any artifact stolen by this rival to reclaim
            val allArtifacts = db.artifactDao().getAllArtifacts().firstOrNull() ?: emptyList()
            val stolenByThis = allArtifacts.firstOrNull { it.isStolen && it.stolenByRivalName == rival.name }
            var reclaimed: ArtifactEntity? = null
            if (stolenByThis != null) {
                db.artifactDao().reclaimArtifact(stolenByThis.artifactId)
                reclaimed = stolenByThis.copy(isStolen = false, stolenByRivalName = null)
            }

            checkAndApplyLevelUp()
            return PvpDuelOutcome(
                isVictory = true,
                stolenArtifact = null,
                reclaimedArtifact = reclaimed,
                respawnFloor = char.currentFloor,
                message = if (reclaimed != null) {
                    "Kemenangan Mutlak! Kamu mengalahkan ${rival.name} di Lantai ${rival.floor} dan MEREBUT KEMBALI ${reclaimed.name}!"
                } else {
                    "Kemenangan! Kamu menumbangkan ${rival.name} di Lantai ${rival.floor}! (+${bonusGold} Gold, +${bonusExp} EXP)"
                }
            )
        } else {
            // Player was killed by rival on same floor!
            // Check active artifacts to steal
            val activeArts = db.artifactDao().getActiveArtifactsOnce()
            var stolen: ArtifactEntity? = null
            if (activeArts.isNotEmpty()) {
                val targetArt = activeArts.maxByOrNull { it.slotNumber } ?: activeArts.first()
                db.artifactDao().markArtifactStolen(targetArt.artifactId, rival.name)
                stolen = targetArt.copy(isStolen = true, stolenByRivalName = rival.name)
            }

            // Wake up at last checkpoint!
            // Checkpoints are every 5 floors: (floor / 5) * 5
            val lastCheckpoint = ((rival.floor / 5) * 5).coerceAtLeast(1)
            val updatedChar = char.copy(
                currentFloor = lastCheckpoint,
                currentHp = char.maxHp,
                currentMp = char.maxMp
            )
            db.characterDao().insertOrUpdate(updatedChar)

            val stealMsg = if (stolen != null) {
                "Artefakmu [${stolen.name}] telah DICURI oleh ${rival.name}!"
            } else {
                "${rival.name} menjarah bekalmu!"
            }

            return PvpDuelOutcome(
                isVictory = false,
                stolenArtifact = stolen,
                reclaimedArtifact = null,
                respawnFloor = lastCheckpoint,
                message = "Kamu terbunuh dalam duel oleh ${rival.name} di Lantai ${rival.floor}! $stealMsg Kamu terbangun di Checkpoint Lantai $lastCheckpoint."
            )
        }
    }

    suspend fun checkAndApplyLevelUp() {
        var char = db.characterDao().getCharacterOnce() ?: return
        var currentLevel = char.level
        var currentExp = char.exp
        var leveledUp = false

        while (currentLevel < 100) {
            val reqExp = GameFormulas.expForNextLevel(currentLevel)
            if (currentExp >= reqExp) {
                currentExp -= reqExp
                currentLevel += 1
                leveledUp = true
            } else {
                break
            }
        }

        if (leveledUp) {
            val isSwordsman = char.characterClass == "PEDANG"
            val hpGainPerLevel = if (isSwordsman) 16 else 11
            val mpGainPerLevel = if (isSwordsman) 7 else 18
            val atkGainPerLevel = if (isSwordsman) 4 else 3
            val defGainPerLevel = if (isSwordsman) 2 else 1

            val totalLevelDiff = currentLevel - char.level
            val newMaxHp = char.maxHp + (hpGainPerLevel * totalLevelDiff)
            val newMaxMp = char.maxMp + (mpGainPerLevel * totalLevelDiff)
            val newAtk = char.baseAtk + (atkGainPerLevel * totalLevelDiff)
            val newDef = char.baseDef + (defGainPerLevel * totalLevelDiff)

            val updatedChar = char.copy(
                level = currentLevel,
                exp = currentExp,
                maxHp = newMaxHp,
                currentHp = newMaxHp, // Full heal on level up
                maxMp = newMaxMp,
                currentMp = newMaxMp,
                baseAtk = newAtk,
                baseDef = newDef
            )
            db.characterDao().insertOrUpdate(updatedChar)
            updateLeaderboardPlayer(updatedChar)
        }
    }

    suspend fun restAtSanctuary(): String {
        val char = db.characterDao().getCharacterOnce() ?: return "Karakter tidak ditemukan"
        val updated = char.copy(
            currentHp = char.maxHp,
            currentMp = char.maxMp
        )
        db.characterDao().insertOrUpdate(updated)

        // Also heal all party members
        val members = db.partyMemberDao().getActivePartyOnce()
        for (m in members) {
            db.partyMemberDao().updateMember(m.copy(hp = m.maxHp))
        }
        return "Seluruh HP dan MP pahlawan & party telah pulih sepenuhnya!"
    }

    suspend fun warpToCheckpoint(floor: Int) {
        val char = db.characterDao().getCharacterOnce() ?: return
        if (floor <= char.maxFloorReached) {
            db.characterDao().updateFloor(floor)
        }
    }

    suspend fun equipWeapon(weaponId: Long) {
        db.weaponDao().unequipAll()
        db.weaponDao().equipWeapon(weaponId)
    }

    suspend fun upgradeWeapon(weapon: WeaponEntity): Result<String> {
        val char = db.characterDao().getCharacterOnce() ?: return Result.failure(Exception("Karakter tidak ditemukan"))
        if (weapon.upgradeLevel >= 15) {
            return Result.failure(Exception("Senjata sudah mencapai tingkat penguatan maksimal (+15)!"))
        }

        val nextLevel = weapon.upgradeLevel + 1
        val goldCost = nextLevel * 45 + 50
        val oreRequired = (nextLevel / 2) + 1

        if (char.gold < goldCost) {
            return Result.failure(Exception("Gold tidak cukup! Butuh $goldCost Gold."))
        }

        val oreItem = db.inventoryDao().getItem("ore_iron")
        if (oreItem == null || oreItem.quantity < oreRequired) {
            return Result.failure(Exception("Batu Besi Tempa tidak cukup! Butuh $oreRequired buah."))
        }

        // Deduct gold
        db.characterDao().insertOrUpdate(char.copy(gold = char.gold - goldCost))
        // Deduct ore
        if (oreItem.quantity == oreRequired) {
            db.inventoryDao().deleteItem(oreItem.id)
        } else {
            db.inventoryDao().updateItem(oreItem.copy(quantity = oreItem.quantity - oreRequired))
        }

        // Upgrade weapon
        val powerBonus = 6 + (weapon.tier * 2)
        val updatedWeapon = weapon.copy(
            upgradeLevel = nextLevel,
            basePower = weapon.basePower + powerBonus
        )
        db.weaponDao().updateWeapon(updatedWeapon)

        return Result.success("Berhasil meningkatkan ${weapon.name} ke +$nextLevel! (+${powerBonus} Power)")
    }

    suspend fun combineMaterialUpgrade(weapon: WeaponEntity, materialId: String): Result<String> {
        val char = db.characterDao().getCharacterOnce() ?: return Result.failure(Exception("Karakter tidak ditemukan"))
        val material = db.inventoryDao().getItem(materialId)

        when (materialId) {
            "ore_iron" -> {
                return upgradeWeapon(weapon)
            }
            "material_mana_crystal" -> {
                val neededQty = 2
                val goldCost = 180
                if (char.gold < goldCost) {
                    return Result.failure(Exception("Gold tidak cukup! Butuh $goldCost Gold."))
                }
                if (material == null || material.quantity < neededQty) {
                    return Result.failure(Exception("Kristal Mana Halus tidak cukup! Butuh $neededQty buah."))
                }
                db.characterDao().insertOrUpdate(char.copy(gold = char.gold - goldCost))
                if (material.quantity == neededQty) {
                    db.inventoryDao().deleteItem(material.id)
                } else {
                    db.inventoryDao().updateItem(material.copy(quantity = material.quantity - neededQty))
                }
                val powerGain = 16
                val updated = weapon.copy(basePower = weapon.basePower + powerGain)
                db.weaponDao().updateWeapon(updated)
                return Result.success("Berhasil menginfus Kristal Mana Halus ke ${weapon.name}! (+${powerGain} Power)")
            }
            "material_dragon_scale" -> {
                val neededQty = 2
                val goldCost = 380
                if (char.gold < goldCost) {
                    return Result.failure(Exception("Gold tidak cukup! Butuh $goldCost Gold."))
                }
                if (material == null || material.quantity < neededQty) {
                    return Result.failure(Exception("Sisik Naga Bawah Tanah tidak cukup! Butuh $neededQty buah."))
                }
                db.characterDao().insertOrUpdate(char.copy(gold = char.gold - goldCost))
                if (material.quantity == neededQty) {
                    db.inventoryDao().deleteItem(material.id)
                } else {
                    db.inventoryDao().updateItem(material.copy(quantity = material.quantity - neededQty))
                }
                val powerGain = 32
                val updated = weapon.copy(basePower = weapon.basePower + powerGain)
                db.weaponDao().updateWeapon(updated)
                return Result.success("Berhasil memperkuat ${weapon.name} dengan Sisik Naga Bawah Tanah! (+${powerGain} Power)")
            }
            "material_divine_ore" -> {
                val neededQty = 1
                val goldCost = 750
                if (char.gold < goldCost) {
                    return Result.failure(Exception("Gold tidak cukup! Butuh $goldCost Gold."))
                }
                if (material == null || material.quantity < neededQty) {
                    return Result.failure(Exception("Batu Inti Dewa tidak cukup! Butuh $neededQty buah."))
                }
                db.characterDao().insertOrUpdate(char.copy(gold = char.gold - goldCost))
                if (material.quantity == neededQty) {
                    db.inventoryDao().deleteItem(material.id)
                } else {
                    db.inventoryDao().updateItem(material.copy(quantity = material.quantity - neededQty))
                }
                val powerGain = 60
                val updated = weapon.copy(basePower = weapon.basePower + powerGain)
                db.weaponDao().updateWeapon(updated)
                return Result.success("Luar Biasa! Berhasil meleburkan Batu Inti Dewa ke ${weapon.name}! (+${powerGain} Divine Power)")
            }
            else -> {
                if (materialId.startsWith("rune_")) {
                    val runeType = materialId.removePrefix("rune_").uppercase()
                    return infuseRune(weapon, runeType)
                }
                return Result.failure(Exception("Bahan tidak dikenali untuk kombinasi senjata."))
            }
        }
    }

    suspend fun infuseRune(weapon: WeaponEntity, runeType: String): Result<String> {
        val runeItem = db.inventoryDao().getItem("rune_" + runeType.lowercase())
        if (runeItem == null || runeItem.quantity < 1) {
            return Result.failure(Exception("Kamu tidak memiliki Runa $runeType di tas!"))
        }

        // Deduct 1 rune
        if (runeItem.quantity == 1) {
            db.inventoryDao().deleteItem(runeItem.id)
        } else {
            db.inventoryDao().updateItem(runeItem.copy(quantity = runeItem.quantity - 1))
        }

        val power = when (runeType) {
            "API" -> 25
            "ES" -> 20
            "PETIR" -> 30
            "KEHIDUPAN" -> 15
            else -> 10
        }

        val updated = weapon.copy(
            runeType = runeType,
            runePower = power
        )
        db.weaponDao().updateWeapon(updated)

        return Result.success("Berhasil menginfus Runa $runeType ke ${weapon.name}!")
    }

    suspend fun forgeNewWeapon(
        name: String,
        type: String,
        tier: Int,
        power: Int,
        desc: String,
        goldCost: Int,
        materialId: String,
        materialQty: Int
    ): Result<String> {
        val char = db.characterDao().getCharacterOnce() ?: return Result.failure(Exception("Karakter tidak ada"))
        if (char.gold < goldCost) {
            return Result.failure(Exception("Gold tidak cukup! Butuh $goldCost Gold."))
        }

        val material = db.inventoryDao().getItem(materialId)
        if (material == null || material.quantity < materialQty) {
            return Result.failure(Exception("Bahan tidak cukup! Butuh $materialQty ${material?.name ?: "Bahan"}`."))
        }

        // Deduct
        db.characterDao().insertOrUpdate(char.copy(gold = char.gold - goldCost))
        if (material.quantity == materialQty) {
            db.inventoryDao().deleteItem(material.id)
        } else {
            db.inventoryDao().updateItem(material.copy(quantity = material.quantity - materialQty))
        }

        val newWeapon = WeaponEntity(
            name = name,
            weaponType = type,
            tier = tier,
            basePower = power,
            upgradeLevel = 0,
            runeType = "NONE",
            runePower = 0,
            isEquipped = false,
            description = desc
        )
        db.weaponDao().insertWeapon(newWeapon)
        return Result.success("Berhasil menempa senjata baru: $name (Tier $tier)!")
    }

    suspend fun buyShopItem(item: CheckpointShopItem, qty: Int): Result<String> {
        val char = db.characterDao().getCharacterOnce() ?: return Result.failure(Exception("Karakter tidak ada"))
        val totalCost = item.price * qty
        if (char.gold < totalCost) {
            return Result.failure(Exception("Gold tidak cukup! Butuh $totalCost Gold."))
        }

        db.characterDao().insertOrUpdate(char.copy(gold = char.gold - totalCost))

        val existing = db.inventoryDao().getItem(item.id)
        if (existing != null) {
            db.inventoryDao().updateItem(existing.copy(quantity = existing.quantity + qty))
        } else {
            db.inventoryDao().insertOrUpdate(
                InventoryItemEntity(
                    itemId = item.id,
                    name = item.name,
                    itemType = item.type,
                    quantity = qty,
                    description = item.description,
                    value = item.price / 2
                )
            )
        }
        return Result.success("Berhasil membeli $qty x ${item.name}!")
    }

    suspend fun usePotion(itemId: String): Result<String> {
        val item = db.inventoryDao().getItem(itemId) ?: return Result.failure(Exception("Ramuan habis!"))
        val char = db.characterDao().getCharacterOnce() ?: return Result.failure(Exception("Karakter tidak ada"))

        var msg = ""
        var updatedChar = char
        if (itemId.contains("hp")) {
            val healAmount = if (itemId.contains("large")) 180 else if (itemId.contains("med")) 100 else 60
            val newHp = (char.currentHp + healAmount).coerceAtMost(char.maxHp)
            updatedChar = updatedChar.copy(currentHp = newHp)
            msg = "Memulihkan $healAmount HP!"
        } else if (itemId.contains("mp")) {
            val manaAmount = if (itemId.contains("large")) 120 else if (itemId.contains("med")) 75 else 45
            val newMp = (char.currentMp + manaAmount).coerceAtMost(char.maxMp)
            updatedChar = updatedChar.copy(currentMp = newMp)
            msg = "Memulihkan $manaAmount MP!"
        }

        db.characterDao().insertOrUpdate(updatedChar)

        if (item.quantity <= 1) {
            db.inventoryDao().deleteItem(item.id)
        } else {
            db.inventoryDao().updateItem(item.copy(quantity = item.quantity - 1))
        }

        return Result.success(msg)
    }

    suspend fun recruitPartyMember(member: PartyMemberEntity): Result<String> {
        val char = db.characterDao().getCharacterOnce() ?: return Result.failure(Exception("Karakter tidak ada"))
        if (char.gold < member.recruitCost) {
            return Result.failure(Exception("Gold tidak cukup! Butuh ${member.recruitCost} Gold."))
        }

        db.characterDao().insertOrUpdate(char.copy(gold = char.gold - member.recruitCost))
        db.partyMemberDao().updateMember(member.copy(isRecruited = true, isInParty = true))
        return Result.success("Berhasil merekrut ${member.name} ke dalam tim!")
    }

    suspend fun togglePartyMember(member: PartyMemberEntity): Result<String> {
        val active = db.partyMemberDao().getActivePartyOnce()
        if (!member.isInParty && active.size >= 3) {
            return Result.failure(Exception("Party sudah penuh! Maksimal 3 rekan aktif bersama ketua party."))
        }

        db.partyMemberDao().updateMember(member.copy(isInParty = !member.isInParty))
        val status = if (!member.isInParty) "bergabung ke formasi dungeon!" else "kembali beristirahat di markas guild."
        return Result.success("${member.name} $status")
    }

    suspend fun invitePartyMember(
        name: String,
        role: String,
        skillName: String,
        skillDesc: String,
        hp: Int = 110,
        atk: Int = 22,
        def: Int = 12
    ): Result<String> {
        val active = db.partyMemberDao().getActivePartyOnce()
        val shouldAutoJoin = active.size < 3

        val newMember = PartyMemberEntity(
            name = name.trim(),
            role = role,
            level = 1,
            hp = hp,
            maxHp = hp,
            atk = atk,
            def = def,
            skillName = skillName,
            skillDesc = skillDesc,
            isRecruited = true,
            isInParty = shouldAutoJoin,
            recruitCost = 0
        )
        db.partyMemberDao().insertMember(newMember)
        val joinMsg = if (shouldAutoJoin) "dan langsung masuk ke formasi aktif dungeon!" else "dan siap dipasang ke formasi!"
        return Result.success("Undangan diterima! $name ($role) resmi bergabung ke kelompok petualangan $joinMsg")
    }

    suspend fun dismissPartyMember(member: PartyMemberEntity): Result<String> {
        db.partyMemberDao().deleteMember(member)
        return Result.success("${member.name} telah dikeluarkan dari guild kelompok.")
    }

    private suspend fun updateLeaderboardPlayer(char: CharacterEntity) {
        val score = (char.maxFloorReached * 2800) + (char.level * 450) + (char.monstersDefeated * 25) + (char.bossesDefeated * 400)
        val rankName = GameFormulas.getRankName(char.maxFloorReached)

        db.leaderboardDao().deletePlayerEntry()
        db.leaderboardDao().insertEntry(
            LeaderboardEntity(
                rankNumber = 1, // Will be ordered by query
                name = char.name + " (Kamu)",
                className = char.characterClass,
                level = char.level,
                floorReached = char.maxFloorReached,
                score = score,
                isPlayer = true,
                title = rankName
            )
        )
    }
}
