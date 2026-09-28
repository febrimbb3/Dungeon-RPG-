package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ArtifactEntity
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.InventoryItemEntity
import com.example.data.local.entity.LeaderboardEntity
import com.example.data.local.entity.PartyMemberEntity
import com.example.data.local.entity.WeaponEntity
import com.example.data.repository.GameRepository
import com.example.model.CheckpointShopItem
import com.example.model.GameFormulas
import com.example.model.Monster
import com.example.model.RivalAdventurer
import com.example.model.Skill
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class GameScreen {
    object Creation : GameScreen()
    object Tutorial : GameScreen()
    object MainDungeon : GameScreen()
    object Combat : GameScreen()
    object Checkpoint : GameScreen()
    object Party : GameScreen()
    object Crafting : GameScreen()
    object Leaderboard : GameScreen()
    object Artifacts : GameScreen()
    object RivalDuel : GameScreen()
}

data class CombatUiState(
    val monster: Monster? = null,
    val rival: RivalAdventurer? = null,
    val isPvpDuel: Boolean = false,
    val floor: Int = 1,
    val isBoss: Boolean = false,
    val combatLogs: List<String> = emptyList(),
    val isPlayerTurn: Boolean = true,
    val isGuarding: Boolean = false,
    val isVictory: Boolean = false,
    val isDefeat: Boolean = false,
    val rewardsSummary: String = "",
    val companionActionText: String = "",
    val tutorialStep: Int = 1, // 1: Serangan Dasar, 2: Skill, 3: Guard, 4: Potion, 5: Victory
    val droppedEquipment: WeaponEntity? = null,
    val droppedArtifact: ArtifactEntity? = null,
    val isFloor100Completed: Boolean = false,
    val stolenArtifactByRival: ArtifactEntity? = null,
    val reclaimedArtifactFromRival: ArtifactEntity? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository = GameRepository(AppDatabase.getDatabase(application))

    val character: StateFlow<CharacterEntity?> = repository.character
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val equippedWeapon: StateFlow<WeaponEntity?> = repository.equippedWeapon
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allWeapons: StateFlow<List<WeaponEntity>> = repository.allWeapons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryItemEntity>> = repository.inventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partyMembers: StateFlow<List<PartyMemberEntity>> = repository.partyMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeParty: StateFlow<List<PartyMemberEntity>> = repository.activeParty
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<LeaderboardEntity>> = repository.leaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allArtifacts: StateFlow<List<ArtifactEntity>> = repository.allArtifacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeArtifacts: StateFlow<List<ArtifactEntity>> = repository.activeArtifacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow<GameScreen>(GameScreen.MainDungeon)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    private val _combatState = MutableStateFlow(CombatUiState())
    val combatState: StateFlow<CombatUiState> = _combatState.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun navigateTo(screen: GameScreen) {
        _currentScreen.value = screen
    }

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun createCharacter(name: String, characterClass: String, characterRace: String = "MANUSIA") {
        viewModelScope.launch {
            repository.createNewCharacter(name, characterClass, characterRace)
            _currentScreen.value = GameScreen.Tutorial
            startTutorialCombat()
        }
    }

    fun startTutorialCombat() {
        val dummyMonster = Monster(
            name = "Dummy Kayu Pelatihan Guild",
            maxHp = 100,
            currentHp = 100,
            atk = 6,
            def = 2,
            expReward = 80,
            goldReward = 150,
            isBoss = false,
            floor = 1,
            description = "Boneka jerami dan kayu untuk menguji serangan petualang pemula."
        )
        _combatState.value = CombatUiState(
            monster = dummyMonster,
            floor = 1,
            isBoss = false,
            combatLogs = listOf(
                "Selamat datang di Guild Petualang!",
                "Langkah 1: Tekan tombol [SERANG DASAR] untuk menyerang target."
            ),
            isPlayerTurn = true,
            tutorialStep = 1
        )
        _currentScreen.value = GameScreen.Tutorial
    }

    fun executeTutorialAction(actionType: String, skill: Skill? = null, itemId: String? = null) {
        val currentStep = _combatState.value.tutorialStep
        val monster = _combatState.value.monster ?: return
        val char = character.value ?: return

        when (actionType) {
            "ATTACK" -> {
                val dmg = 25
                val newHp = (monster.currentHp - dmg).coerceAtLeast(0)
                monster.currentHp = newHp

                val logs = _combatState.value.combatLogs.toMutableList()
                logs.add("Kamu melancarkan Serangan Dasar! Menimbulkan $dmg damage.")

                if (currentStep == 1) {
                    logs.add("Bagus! Langkah 2: Sekarang gunakan [JURUS / SKILL] yang menggunakan MP.")
                    _combatState.value = _combatState.value.copy(
                        monster = monster,
                        combatLogs = logs,
                        tutorialStep = 2
                    )
                } else {
                    _combatState.value = _combatState.value.copy(monster = monster, combatLogs = logs)
                    checkTutorialVictory()
                }
            }
            "SKILL" -> {
                val s = skill ?: (if (char.characterClass == "PEDANG") GameFormulas.getSwordsmanSkills().first() else GameFormulas.getMageSkills().first())
                val dmg = 45
                val newHp = (monster.currentHp - dmg).coerceAtLeast(0)
                monster.currentHp = newHp

                val logs = _combatState.value.combatLogs.toMutableList()
                logs.add("Kamu menggunakan skill [${s.name}]! Ledakan energi menghasilkan $dmg damage dahsyat!")

                if (currentStep <= 2) {
                    logs.add("Luar biasa! Langkah 3: Gunakan tombol [BERTAHAN / GUARD] untuk menangkis serangan musuh.")
                    _combatState.value = _combatState.value.copy(
                        monster = monster,
                        combatLogs = logs,
                        tutorialStep = 3
                    )
                } else {
                    _combatState.value = _combatState.value.copy(monster = monster, combatLogs = logs)
                    checkTutorialVictory()
                }
            }
            "GUARD" -> {
                val logs = _combatState.value.combatLogs.toMutableList()
                logs.add("Kamu masuk ke posisi Bertahan! Damage musuh berkurang 50% dan kamu memulihkan 10 MP.")
                logs.add("Pelatih Boneka menyerang, hanya memberi 3 damage karena pertahananmu!")

                if (currentStep <= 3) {
                    logs.add("Hebat! Langkah 4: Buka [TAS RAMUAN] dan gunakan Ramuan HP untuk memulihkan tubuhmu.")
                    _combatState.value = _combatState.value.copy(
                        combatLogs = logs,
                        tutorialStep = 4
                    )
                }
            }
            "POTION" -> {
                val logs = _combatState.value.combatLogs.toMutableList()
                logs.add("Kamu meminum Ramuan HP! Tubuhmu segar kembali dan HP penuh!")
                logs.add("Sempurna! Kamu telah menguasai dasar pertempuran.")
                logs.add("Tekan [SELESAIKAN MISI PERTAMA] untuk mengklaim hadiah dan membuka Dungeon 100 Lantai!")
                _combatState.value = _combatState.value.copy(
                    combatLogs = logs,
                    tutorialStep = 5,
                    isVictory = true
                )
            }
        }
    }

    private fun checkTutorialVictory() {
        val monster = _combatState.value.monster ?: return
        if (monster.currentHp <= 0) {
            val logs = _combatState.value.combatLogs.toMutableList()
            logs.add("Boneka Pelatihan hancur! Latihan selesai dengan sempurna!")
            _combatState.value = _combatState.value.copy(
                isVictory = true,
                tutorialStep = 5,
                combatLogs = logs
            )
        }
    }

    fun finishTutorialAndClaim() {
        viewModelScope.launch {
            repository.completeTutorial()
            _combatState.value = CombatUiState()
            _currentScreen.value = GameScreen.MainDungeon
            showMessage("Misi Pertama Selesai! Kamu mendapat 100 Gold, 8 Batu Besi, dan izin memasuki Dungeon 100 Lantai!")
        }
    }

    fun startDungeonFloor(floor: Int) {
        val monster = GameFormulas.generateMonsterForFloor(floor)
        val isBoss = (floor % 5 == 0)
        _combatState.value = CombatUiState(
            monster = monster,
            floor = floor,
            isBoss = isBoss,
            combatLogs = listOf(
                "Memasuki Dungeon Lantai $floor...",
                if (isBoss) "PERINGATAN: ${monster.name} menghadang jalanmu!" else "Kamu berhadapan dengan ${monster.name}!"
            ),
            isPlayerTurn = true,
            isGuarding = false,
            isVictory = false,
            isDefeat = false
        )
        _currentScreen.value = GameScreen.Combat
    }

    fun performPlayerAttack() {
        val char = character.value ?: return
        val weapon = equippedWeapon.value
        val monster = _combatState.value.monster ?: return
        if (!_combatState.value.isPlayerTurn || _combatState.value.isVictory || _combatState.value.isDefeat) return

        viewModelScope.launch {
            val isCrit = Random.nextInt(100) < 20
            val weaponPower = weapon?.basePower ?: 10
            val runePower = weapon?.runePower ?: 0
            val rawDmg = char.baseAtk + weaponPower + runePower - (monster.def / 2)
            val baseDmg = rawDmg.coerceAtLeast(10)
            val finalDmg = if (isCrit) (baseDmg * 1.65f).toInt() else baseDmg

            val newMonsterHp = (monster.currentHp - finalDmg).coerceAtLeast(0)
            monster.currentHp = newMonsterHp

            val logs = _combatState.value.combatLogs.toMutableList()
            val runeEffect = if (weapon != null && weapon.runeType != "NONE") " (+Efek ${weapon.runeType})" else ""
            val critText = if (isCrit) " [CRITICAL HIT!]" else ""
            logs.add("Kamu menyerang ${monster.name}$critText$runeEffect, menghasilkan $finalDmg damage!")

            // Life rune leech
            if (weapon?.runeType == "KEHIDUPAN") {
                val healAmt = 15
                repository.updateCharacterHpMp((char.currentHp + healAmt).coerceAtMost(char.maxHp), char.currentMp)
                logs.add("Runa Kehidupan menyerap 15 HP musuh ke tubuhmu!")
            }

            _combatState.value = _combatState.value.copy(
                monster = monster,
                combatLogs = logs,
                isPlayerTurn = false
            )

            // Companion action
            triggerCompanionAssists(monster, logs)

            if (monster.currentHp <= 0) {
                handleVictory(monster, logs)
            } else {
                delay(600)
                handleMonsterTurn(monster, logs)
            }
        }
    }

    fun performPlayerSkill(skill: Skill) {
        val char = character.value ?: return
        val weapon = equippedWeapon.value
        val monster = _combatState.value.monster ?: return
        if (!_combatState.value.isPlayerTurn || _combatState.value.isVictory || _combatState.value.isDefeat) return

        if (char.currentMp < skill.mpCost) {
            showMessage("MP tidak cukup! Butuh ${skill.mpCost} MP.")
            return
        }

        viewModelScope.launch {
            val newMp = char.currentMp - skill.mpCost
            val logs = _combatState.value.combatLogs.toMutableList()

            if (skill.effectType == "HEAL") {
                val healAmt = (char.maxHp * 0.45f).toInt()
                val newHp = (char.currentHp + healAmt).coerceAtMost(char.maxHp)
                repository.updateCharacterHpMp(newHp, newMp)
                logs.add("Kamu merapalkan [${skill.name}]! Menyembuhkan $healAmt HP untuk pahlawan.")
                _combatState.value = _combatState.value.copy(combatLogs = logs, isPlayerTurn = false)
            } else if (skill.effectType == "SHIELD") {
                repository.updateCharacterHpMp(char.currentHp, newMp)
                logs.add("Kamu mengaktifkan [${skill.name}]! Perisai suci melindungimu dari 50% damage berikutnya.")
                _combatState.value = _combatState.value.copy(
                    combatLogs = logs,
                    isGuarding = true,
                    isPlayerTurn = false
                )
            } else {
                // Damage skill
                val weaponPower = weapon?.basePower ?: 12
                val runePower = weapon?.runePower ?: 0
                val totalPower = ((char.baseAtk + weaponPower + runePower) * skill.powerMultiplier).toInt()
                val finalDmg = (totalPower - monster.def / 2).coerceAtLeast(18)

                val newMonsterHp = (monster.currentHp - finalDmg).coerceAtLeast(0)
                monster.currentHp = newMonsterHp
                repository.updateCharacterHpMp(char.currentHp, newMp)

                logs.add("Kamu melancarkan jurus [${skill.name}]! Ledakan energi menghantam ${monster.name} sebesar $finalDmg damage!")
                _combatState.value = _combatState.value.copy(
                    monster = monster,
                    combatLogs = logs,
                    isPlayerTurn = false
                )
            }

            triggerCompanionAssists(monster, logs)

            if (monster.currentHp <= 0) {
                handleVictory(monster, logs)
            } else {
                delay(600)
                handleMonsterTurn(monster, logs)
            }
        }
    }

    fun performGuard() {
        val char = character.value ?: return
        val monster = _combatState.value.monster ?: return
        if (!_combatState.value.isPlayerTurn || _combatState.value.isVictory || _combatState.value.isDefeat) return

        viewModelScope.launch {
            val logs = _combatState.value.combatLogs.toMutableList()
            val recoveredMp = (char.currentMp + 12).coerceAtMost(char.maxMp)
            repository.updateCharacterHpMp(char.currentHp, recoveredMp)
            logs.add("Kamu bersiap menahan serangan! Pertahanan meningkat drastis (+12 MP).")

            _combatState.value = _combatState.value.copy(
                combatLogs = logs,
                isGuarding = true,
                isPlayerTurn = false
            )

            triggerCompanionAssists(monster, logs)

            if (monster.currentHp <= 0) {
                handleVictory(monster, logs)
            } else {
                delay(600)
                handleMonsterTurn(monster, logs)
            }
        }
    }

    private suspend fun triggerCompanionAssists(monster: Monster, logs: MutableList<String>) {
        val companions = activeParty.value
        for (c in companions) {
            if (monster.currentHp <= 0) break
            val cDmg = (c.atk * 0.9f).toInt().coerceAtLeast(8)
            val newHp = (monster.currentHp - cDmg).coerceAtLeast(0)
            monster.currentHp = newHp
            logs.add("[Rekan] ${c.name} membantu dengan [${c.skillName}], memberi $cDmg damage!")
        }
    }

    private suspend fun handleMonsterTurn(monster: Monster, logs: MutableList<String>) {
        val char = repository.getCharacterOnce() ?: return
        val isGuarding = _combatState.value.isGuarding

        val rawAtk = monster.atk - (char.baseDef / 2)
        var dmg = rawAtk.coerceAtLeast(6)
        if (isGuarding) {
            dmg = (dmg * 0.5f).toInt().coerceAtLeast(3)
        }

        val newHp = (char.currentHp - dmg).coerceAtLeast(0)
        repository.updateCharacterHpMp(newHp, char.currentMp)

        val guardNote = if (isGuarding) " (Tertahan oleh perisai)" else ""
        logs.add("${monster.name} menyerang balik! Menimbulkan $dmg damage.$guardNote")

        if (newHp <= 0) {
            logs.add("Kamu pingsan dalam pertempuran... Tim penyelamat membawamu kembali ke checkpoint terdekat.")
            _combatState.value = _combatState.value.copy(
                combatLogs = logs,
                isPlayerTurn = false,
                isDefeat = true
            )
        } else {
            _combatState.value = _combatState.value.copy(
                combatLogs = logs,
                isPlayerTurn = true,
                isGuarding = false
            )
        }
    }

    private suspend fun handleVictory(monster: Monster, logs: MutableList<String>) {
        val floor = _combatState.value.floor
        val isBoss = _combatState.value.isBoss
        val expGained = monster.expReward.toLong()
        val goldGained = monster.goldReward

        logs.add("Kemenangan! ${monster.name} berhasil ditundukkan!")
        logs.add("Hadiah: +$expGained EXP, +$goldGained Gold, dan bahan crafting!")

        val outcome = repository.onFloorVictory(floor, isBoss, expGained, goldGained)

        if (outcome.droppedEquipment != null) {
            logs.add("★ BOS MENJATUHKAN PERALATAN [${outcome.droppedEquipment.rarity}]: ${outcome.droppedEquipment.name} (Tier ${outcome.droppedEquipment.tier}, ${outcome.droppedEquipment.basePower} Power)!")
        }
        if (outcome.droppedArtifact != null) {
            logs.add("✦ ARTEFAK LANGKA BERHASIL DIDAPATKAN: ${outcome.droppedArtifact.name} (${outcome.droppedArtifact.statBonusDesc})!")
        }
        if (outcome.isFloor100Completed) {
            logs.add("👑 SUPREMASI LANTAI 100: Kamu telah menyelesaikan Dungeon 100 Lantai dan memperoleh Artefak Senjata Tertinggi!")
        }

        val summary = if (floor == 100) {
            "SELAMAT! KAMU MENAKLUKKAN SELURUH 100 LANTAI DUNGEON! Artefak Tertinggi [${outcome.droppedArtifact?.name ?: "Bilah Mahkota"}] telah menjadi milikmu!"
        } else if (floor % 5 == 0) {
            val lootNote = outcome.droppedEquipment?.let { " • Hadiah Peralatan ${it.rarity}: ${it.name}" } ?: ""
            "Selamat! Kamu mengalahkan Boss Lantai $floor dan membuka Checkpoint Camp Lantai $floor!$lootNote"
        } else {
            "Lantai $floor berhasil ditaklukkan! Siap melangkah ke lantai berikutnya."
        }

        _combatState.value = _combatState.value.copy(
            isVictory = true,
            rewardsSummary = summary,
            combatLogs = logs,
            droppedEquipment = outcome.droppedEquipment,
            droppedArtifact = outcome.droppedArtifact,
            isFloor100Completed = outcome.isFloor100Completed
        )
    }

    fun startRivalDuel(floor: Int) {
        val rival = GameFormulas.generateRivalForFloor(floor)
        _combatState.value = CombatUiState(
            monster = null,
            rival = rival,
            isPvpDuel = true,
            floor = floor,
            isBoss = false,
            combatLogs = listOf(
                "Tantangan Duel Petualang Rival Sesama Lantai $floor!",
                "Rival: ${rival.name} (Lv. ${rival.level} ${rival.characterClass})",
                "\"${rival.quote}\"",
                "HUKUM DUNGEON: Pemain dalam party TIDAK BOLEH saling membunuh. Namun rival sesama lantai dapat MENCURI artefakmu jika kamu terbunuh!"
            ),
            isPlayerTurn = true,
            isGuarding = false,
            isVictory = false,
            isDefeat = false
        )
        _currentScreen.value = GameScreen.RivalDuel
    }

    fun performPvpAttack() {
        val rival = _combatState.value.rival ?: return
        val char = character.value ?: return
        val weapon = equippedWeapon.value
        if (!_combatState.value.isPlayerTurn || _combatState.value.isVictory || _combatState.value.isDefeat) return

        viewModelScope.launch {
            val isCrit = Random.nextInt(100) < 20
            val weaponPower = weapon?.basePower ?: 12
            val runePower = weapon?.runePower ?: 0
            val rawDmg = char.baseAtk + weaponPower + runePower - (rival.def / 2)
            val baseDmg = rawDmg.coerceAtLeast(12)
            val finalDmg = if (isCrit) (baseDmg * 1.6f).toInt() else baseDmg

            val newRivalHp = (rival.currentHp - finalDmg).coerceAtLeast(0)
            rival.currentHp = newRivalHp

            val logs = _combatState.value.combatLogs.toMutableList()
            val critText = if (isCrit) " [CRITICAL!]" else ""
            logs.add("Kamu menyerang ${rival.name}$critText, menghasilkan $finalDmg damage!")

            _combatState.value = _combatState.value.copy(
                rival = rival,
                combatLogs = logs,
                isPlayerTurn = false
            )

            // Companion assists
            val companions = activeParty.value
            for (c in companions) {
                if (rival.currentHp <= 0) break
                val cDmg = (c.atk * 0.9f).toInt().coerceAtLeast(8)
                rival.currentHp = (rival.currentHp - cDmg).coerceAtLeast(0)
                logs.add("[Rekan Party] ${c.name} membantumu menyerang ${rival.name} sebesar $cDmg damage!")
            }

            if (rival.currentHp <= 0) {
                val outcome = repository.executePvpDuel(rival, playerWon = true)
                logs.add(outcome.message)
                _combatState.value = _combatState.value.copy(
                    isVictory = true,
                    rewardsSummary = outcome.message,
                    combatLogs = logs,
                    reclaimedArtifactFromRival = outcome.reclaimedArtifact
                )
            } else {
                delay(600)
                handleRivalTurn(rival, logs)
            }
        }
    }

    fun performPvpSkill(skill: Skill) {
        val rival = _combatState.value.rival ?: return
        val char = character.value ?: return
        val weapon = equippedWeapon.value
        if (!_combatState.value.isPlayerTurn || _combatState.value.isVictory || _combatState.value.isDefeat) return

        if (char.currentMp < skill.mpCost) {
            showMessage("MP tidak cukup! Butuh ${skill.mpCost} MP.")
            return
        }

        viewModelScope.launch {
            val newMp = char.currentMp - skill.mpCost
            val logs = _combatState.value.combatLogs.toMutableList()

            if (skill.effectType == "HEAL") {
                val healAmt = (char.maxHp * 0.45f).toInt()
                val newHp = (char.currentHp + healAmt).coerceAtMost(char.maxHp)
                repository.updateCharacterHpMp(newHp, newMp)
                logs.add("Kamu merapalkan [${skill.name}]! Memulihkan $healAmt HP.")
                _combatState.value = _combatState.value.copy(combatLogs = logs, isPlayerTurn = false)
            } else if (skill.effectType == "SHIELD") {
                repository.updateCharacterHpMp(char.currentHp, newMp)
                logs.add("Kamu mengaktifkan [${skill.name}]! Menangkis 50% damage serangan rival berikutnya.")
                _combatState.value = _combatState.value.copy(combatLogs = logs, isGuarding = true, isPlayerTurn = false)
            } else {
                val weaponPower = weapon?.basePower ?: 12
                val runePower = weapon?.runePower ?: 0
                val totalPower = ((char.baseAtk + weaponPower + runePower) * skill.powerMultiplier).toInt()
                val finalDmg = (totalPower - rival.def / 2).coerceAtLeast(18)
                val newHp = (rival.currentHp - finalDmg).coerceAtLeast(0)
                rival.currentHp = newHp
                repository.updateCharacterHpMp(char.currentHp, newMp)
                logs.add("Kamu melancarkan [${skill.name}] ke ${rival.name}! Memberikan $finalDmg damage!")
                _combatState.value = _combatState.value.copy(rival = rival, combatLogs = logs, isPlayerTurn = false)
            }

            // Companion assists
            val companions = activeParty.value
            for (c in companions) {
                if (rival.currentHp <= 0) break
                val cDmg = (c.atk * 0.9f).toInt().coerceAtLeast(8)
                rival.currentHp = (rival.currentHp - cDmg).coerceAtLeast(0)
                logs.add("[Rekan Party] ${c.name} membantumu menyerang ${rival.name} sebesar $cDmg damage!")
            }

            if (rival.currentHp <= 0) {
                val outcome = repository.executePvpDuel(rival, playerWon = true)
                logs.add(outcome.message)
                _combatState.value = _combatState.value.copy(
                    isVictory = true,
                    rewardsSummary = outcome.message,
                    combatLogs = logs,
                    reclaimedArtifactFromRival = outcome.reclaimedArtifact
                )
            } else {
                delay(600)
                handleRivalTurn(rival, logs)
            }
        }
    }

    private suspend fun handleRivalTurn(rival: RivalAdventurer, logs: MutableList<String>) {
        val char = repository.getCharacterOnce() ?: return
        val isGuarding = _combatState.value.isGuarding

        val rawAtk = rival.atk - (char.baseDef / 2)
        var dmg = rawAtk.coerceAtLeast(8)
        if (isGuarding) {
            dmg = (dmg * 0.5f).toInt().coerceAtLeast(4)
        }

        val newHp = (char.currentHp - dmg).coerceAtLeast(0)
        repository.updateCharacterHpMp(newHp, char.currentMp)

        val guardNote = if (isGuarding) " (Tertahan oleh perisaimu)" else ""
        logs.add("${rival.name} menyerang balik dengan tebasan cepat! Menimbulkan $dmg damage.$guardNote")

        if (newHp <= 0) {
            // Player killed by rival on same floor!
            val outcome = repository.executePvpDuel(rival, playerWon = false)
            logs.add("FATAL! Kamu dikalahkan oleh ${rival.name} di Lantai ${rival.floor}!")
            logs.add(outcome.message)
            _combatState.value = _combatState.value.copy(
                combatLogs = logs,
                isPlayerTurn = false,
                isDefeat = true,
                stolenArtifactByRival = outcome.stolenArtifact,
                rewardsSummary = outcome.message
            )
        } else {
            _combatState.value = _combatState.value.copy(
                combatLogs = logs,
                isPlayerTurn = true,
                isGuarding = false
            )
        }
    }

    fun continueToNextFloor() {
        val char = character.value ?: return
        val nextFloor = _combatState.value.floor + 1
        if (nextFloor > 100) {
            showMessage("SELAMAT! Kamu telah menaklukkan seluruh 100 Lantai Dungeon!")
            _currentScreen.value = GameScreen.MainDungeon
            return
        }

        if (_combatState.value.floor % 5 == 0) {
            // Reached checkpoint floor! Open checkpoint screen
            _currentScreen.value = GameScreen.Checkpoint
        } else {
            startDungeonFloor(nextFloor)
        }
    }

    fun returnToSanctuary() {
        _currentScreen.value = GameScreen.Checkpoint
    }

    fun restAtCheckpointSanctuary() {
        viewModelScope.launch {
            val msg = repository.restAtSanctuary()
            showMessage(msg)
        }
    }

    fun buyShopItem(item: com.example.model.CheckpointShopItem, qty: Int = 1) {
        viewModelScope.launch {
            val res = repository.buyShopItem(item, qty)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal membeli barang") }
        }
    }

    fun usePotion(itemId: String) {
        viewModelScope.launch {
            val res = repository.usePotion(itemId)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal memakai ramuan") }
        }
    }

    fun upgradeWeapon(weapon: WeaponEntity) {
        viewModelScope.launch {
            val res = repository.upgradeWeapon(weapon)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal memperkuat senjata") }
        }
    }

    fun combineMaterialUpgrade(weapon: WeaponEntity, materialId: String) {
        viewModelScope.launch {
            val res = repository.combineMaterialUpgrade(weapon, materialId)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal menggabungkan bahan") }
        }
    }

    fun infuseWeaponRune(weapon: WeaponEntity, runeType: String) {
        viewModelScope.launch {
            val res = repository.infuseRune(weapon, runeType)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal menginfus runa") }
        }
    }

    fun craftNewWeapon(
        name: String,
        type: String,
        tier: Int,
        power: Int,
        desc: String,
        goldCost: Int,
        materialId: String,
        materialQty: Int
    ) {
        viewModelScope.launch {
            val res = repository.forgeNewWeapon(name, type, tier, power, desc, goldCost, materialId, materialQty)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal menempa") }
        }
    }

    fun performPvpGuard() {
        val char = character.value ?: return
        val rival = _combatState.value.rival ?: return
        if (!_combatState.value.isPlayerTurn || _combatState.value.isVictory || _combatState.value.isDefeat) return

        viewModelScope.launch {
            val logs = _combatState.value.combatLogs.toMutableList()
            val recoveredMp = (char.currentMp + 12).coerceAtMost(char.maxMp)
            repository.updateCharacterHpMp(char.currentHp, recoveredMp)
            logs.add("Kamu bersiap menahan tebasan ${rival.name}! Pertahanan meningkat drastis (+12 MP).")
            _combatState.value = _combatState.value.copy(
                isGuarding = true,
                combatLogs = logs,
                isPlayerTurn = false
            )
            delay(600)
            handleRivalTurn(rival, logs)
        }
    }

    fun performPvpPotion(itemId: String) {
        val rival = _combatState.value.rival ?: return
        val char = character.value ?: return
        if (!_combatState.value.isPlayerTurn || _combatState.value.isVictory || _combatState.value.isDefeat) return

        viewModelScope.launch {
            val res = repository.usePotion(itemId)
            res.onSuccess { msg ->
                val logs = _combatState.value.combatLogs.toMutableList()
                logs.add("Kamu menggunakan ramuan saat duel! $msg")
                _combatState.value = _combatState.value.copy(combatLogs = logs, isPlayerTurn = false)
                delay(600)
                handleRivalTurn(rival, logs)
            }.onFailure { err ->
                showMessage(err.message ?: "Gagal memakai ramuan")
            }
        }
    }

    fun equipWeapon(weaponId: Long) {
        viewModelScope.launch {
            repository.equipWeapon(weaponId)
            showMessage("Senjata berhasil dipasang!")
        }
    }

    fun recruitCompanion(member: PartyMemberEntity) {
        viewModelScope.launch {
            val res = repository.recruitPartyMember(member)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal merekrut") }
        }
    }

    fun toggleParty(member: PartyMemberEntity) {
        viewModelScope.launch {
            val res = repository.togglePartyMember(member)
            res.onSuccess { showMessage(it) }
                .onFailure { showMessage(it.message ?: "Gagal mengatur party") }
        }
    }

    fun teleportToCheckpoint(floor: Int) {
        viewModelScope.launch {
            repository.warpToCheckpoint(floor)
            showMessage("Berpindah ke Checkpoint Lantai $floor!")
            _currentScreen.value = GameScreen.Checkpoint
        }
    }
}
