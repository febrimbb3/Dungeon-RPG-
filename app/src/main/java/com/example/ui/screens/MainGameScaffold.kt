package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.DungeonCardStroke
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.DungeonSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun MainGameScaffold(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsStateWithLifecycle()
    val equippedWeapon by viewModel.equippedWeapon.collectAsStateWithLifecycle()
    val allWeapons by viewModel.allWeapons.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val partyMembers by viewModel.partyMembers.collectAsStateWithLifecycle()
    val activeParty by viewModel.activeParty.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val allArtifacts by viewModel.allArtifacts.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val combatState by viewModel.combatState.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissUserMessage()
        }
    }

    // Auto routing on first launch
    if (character == null) {
        CharacterCreationScreen(
            onCreateCharacter = { name, charClass, charRace ->
                viewModel.createCharacter(name, charClass, charRace)
            }
        )
        return
    }

    if (!character!!.tutorialCompleted && currentScreen != GameScreen.Tutorial) {
        viewModel.startTutorialCombat()
    }

    if (currentScreen == GameScreen.Tutorial) {
        TutorialCombatScreen(
            character = character,
            combatState = combatState,
            onExecuteAction = { action ->
                viewModel.executeTutorialAction(action)
            },
            onFinishTutorial = {
                viewModel.finishTutorialAndClaim()
            }
        )
        return
    }

    // BackHandler support
    BackHandler(enabled = currentScreen != GameScreen.MainDungeon) {
        if (currentScreen == GameScreen.Combat) {
            viewModel.navigateTo(GameScreen.MainDungeon)
        } else {
            viewModel.navigateTo(GameScreen.MainDungeon)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DungeonDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentScreen != GameScreen.Combat && currentScreen != GameScreen.RivalDuel && currentScreen != GameScreen.Artifacts) {
                NavigationBar(
                    containerColor = DungeonSurface,
                    contentColor = GoldPrimary
                ) {
                    // Tab 1: Dungeon
                    NavigationBarItem(
                        selected = currentScreen == GameScreen.MainDungeon,
                        onClick = { viewModel.navigateTo(GameScreen.MainDungeon) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == GameScreen.MainDungeon) Icons.Filled.Explore else Icons.Outlined.Explore,
                                contentDescription = "Dungeon",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Dungeon", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_dungeon")
                    )

                    // Tab 2: Checkpoint
                    NavigationBarItem(
                        selected = currentScreen == GameScreen.Checkpoint,
                        onClick = { viewModel.navigateTo(GameScreen.Checkpoint) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == GameScreen.Checkpoint) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                contentDescription = "Checkpoint",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Checkpoint", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_checkpoint")
                    )

                    // Tab 3: Crafting
                    NavigationBarItem(
                        selected = currentScreen == GameScreen.Crafting,
                        onClick = { viewModel.navigateTo(GameScreen.Crafting) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == GameScreen.Crafting) Icons.Filled.Build else Icons.Outlined.Build,
                                contentDescription = "Crafting",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Crafting", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_crafting")
                    )

                    // Tab 4: Party
                    NavigationBarItem(
                        selected = currentScreen == GameScreen.Party,
                        onClick = { viewModel.navigateTo(GameScreen.Party) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == GameScreen.Party) Icons.Filled.Group else Icons.Outlined.Group,
                                contentDescription = "Party",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Party", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_party")
                    )

                    // Tab 5: Rank
                    NavigationBarItem(
                        selected = currentScreen == GameScreen.Leaderboard,
                        onClick = { viewModel.navigateTo(GameScreen.Leaderboard) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == GameScreen.Leaderboard) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                contentDescription = "Peringkat",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Peringkat", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_rank")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                GameScreen.Creation -> {
                    CharacterCreationScreen(
                        onCreateCharacter = { name, charClass, charRace ->
                            viewModel.createCharacter(name, charClass, charRace)
                        }
                    )
                }
                GameScreen.Tutorial -> {
                    TutorialCombatScreen(
                        character = character,
                        combatState = combatState,
                        onExecuteAction = { action ->
                            viewModel.executeTutorialAction(action)
                        },
                        onFinishTutorial = {
                            viewModel.finishTutorialAndClaim()
                        }
                    )
                }
                GameScreen.MainDungeon -> {
                    MainDungeonOverviewScreen(
                        character = character,
                        equippedWeapon = equippedWeapon,
                        activeParty = activeParty,
                        onEnterDungeon = { floor ->
                            viewModel.startDungeonFloor(floor)
                        },
                        onOpenCheckpoint = {
                            viewModel.navigateTo(GameScreen.Checkpoint)
                        },
                        onOpenCrafting = {
                            viewModel.navigateTo(GameScreen.Crafting)
                        },
                        onOpenParty = {
                            viewModel.navigateTo(GameScreen.Party)
                        },
                        onOpenArtifacts = {
                            viewModel.navigateTo(GameScreen.Artifacts)
                        },
                        onStartRivalDuel = { floor ->
                            viewModel.startRivalDuel(floor)
                        }
                    )
                }
                GameScreen.Combat -> {
                    DungeonCombatScreen(
                        character = character,
                        equippedWeapon = equippedWeapon,
                        activeParty = activeParty,
                        inventory = inventory,
                        combatState = combatState,
                        onAttack = { viewModel.performPlayerAttack() },
                        onSkill = { skill -> viewModel.performPlayerSkill(skill) },
                        onGuard = { viewModel.performGuard() },
                        onUsePotion = { itemId -> viewModel.usePotion(itemId) },
                        onEscape = {
                            viewModel.navigateTo(GameScreen.MainDungeon)
                        },
                        onNextFloor = {
                            viewModel.continueToNextFloor()
                        }
                    )
                }
                GameScreen.Checkpoint -> {
                    CheckpointScreen(
                        character = character,
                        onRest = { viewModel.restAtCheckpointSanctuary() },
                        onBuyItem = { item -> viewModel.buyShopItem(item) },
                        onTeleport = { floor -> viewModel.teleportToCheckpoint(floor) },
                        onResumeDungeon = { floor -> viewModel.startDungeonFloor(floor) }
                    )
                }
                GameScreen.Crafting -> {
                    CraftingScreen(
                        character = character,
                        equippedWeapon = equippedWeapon,
                        allWeapons = allWeapons,
                        inventory = inventory,
                        onUpgradeWeapon = { weapon -> viewModel.upgradeWeapon(weapon) },
                        onCombineMaterial = { weapon, matId -> viewModel.combineMaterialUpgrade(weapon, matId) },
                        onInfuseRune = { weapon, rune -> viewModel.infuseWeaponRune(weapon, rune) },
                        onForgeNewWeapon = { name, type, tier, power, desc, gold, matId, matQty ->
                            viewModel.craftNewWeapon(name, type, tier, power, desc, gold, matId, matQty)
                        },
                        onEquipWeapon = { id -> viewModel.equipWeapon(id) }
                    )
                }
                GameScreen.Party -> {
                    PartyScreen(
                        character = character,
                        partyMembers = partyMembers,
                        activeParty = activeParty,
                        onRecruit = { member -> viewModel.recruitCompanion(member) },
                        onToggleParty = { member -> viewModel.toggleParty(member) }
                    )
                }
                GameScreen.Leaderboard -> {
                    LeaderboardScreen(
                        character = character,
                        leaderboard = leaderboard
                    )
                }
                GameScreen.Artifacts -> {
                    ArtifactsScreen(
                        character = character,
                        allArtifacts = allArtifacts,
                        onBack = { viewModel.navigateTo(GameScreen.MainDungeon) },
                        onChallengeRival = { floor ->
                            viewModel.startRivalDuel(floor)
                        }
                    )
                }
                GameScreen.RivalDuel -> {
                    RivalDuelScreen(
                        character = character,
                        equippedWeapon = equippedWeapon,
                        activeParty = activeParty,
                        inventory = inventory,
                        combatState = combatState,
                        onAttack = { viewModel.performPvpAttack() },
                        onSkill = { skill -> viewModel.performPvpSkill(skill) },
                        onGuard = { viewModel.performPvpGuard() },
                        onUsePotion = { itemId -> viewModel.performPvpPotion(itemId) },
                        onEscape = { viewModel.navigateTo(GameScreen.MainDungeon) },
                        onGoToCheckpoint = {
                            viewModel.returnToSanctuary()
                        }
                    )
                }
            }
        }
    }
}
