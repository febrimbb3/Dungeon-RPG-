package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.InventoryItemEntity
import com.example.data.local.entity.PartyMemberEntity
import com.example.data.local.entity.WeaponEntity
import com.example.model.GameFormulas
import com.example.model.Skill
import com.example.ui.components.StatusHeader
import com.example.ui.theme.ArcanePurple
import com.example.ui.theme.DungeonCardStroke
import com.example.ui.theme.DungeonDarkBg
import com.example.ui.theme.DungeonSurface
import com.example.ui.theme.DungeonSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HealthRed
import com.example.ui.theme.ManaBlue
import com.example.ui.theme.SanctuaryGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CombatUiState

@Composable
fun DungeonCombatScreen(
    character: CharacterEntity?,
    equippedWeapon: WeaponEntity?,
    activeParty: List<PartyMemberEntity>,
    inventory: List<InventoryItemEntity>,
    combatState: CombatUiState,
    onAttack: () -> Unit,
    onSkill: (Skill) -> Unit,
    onGuard: () -> Unit,
    onUsePotion: (String) -> Unit,
    onEscape: () -> Unit,
    onNextFloor: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSkillDialog by remember { mutableStateOf(false) }
    var showPotionDialog by remember { mutableStateOf(false) }

    val monster = combatState.monster
    val charClass = character?.characterClass ?: "PEDANG"
    val skills = if (charClass == "PEDANG") GameFormulas.getSwordsmanSkills() else GameFormulas.getMageSkills()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DungeonDarkBg)
    ) {
        StatusHeader(character = character)

        // Floor and Boss indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(if (combatState.isBoss) HealthRed else GoldPrimary, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (combatState.isBoss) "BOSS CHECKPOINT" else "LANTAI ${combatState.floor} / 100",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                if (combatState.floor % 5 == 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Checkpoint Camp Tersedia",
                        color = SanctuaryGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            OutlinedButton(
                onClick = onEscape,
                modifier = Modifier
                    .height(30.dp)
                    .testTag("btn_combat_escape"),
                border = BorderStroke(1.dp, DungeonCardStroke),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(Icons.Default.DirectionsRun, contentDescription = "Kabur", modifier = Modifier.size(14.dp), tint = TextSecondary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kabur", color = TextSecondary, fontSize = 11.sp)
            }
        }

        // Monster Card
        if (monster != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (combatState.isBoss) HealthRed else DungeonCardStroke)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (combatState.isBoss) {
                                Icon(Icons.Default.Warning, contentDescription = "Boss", tint = HealthRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = monster.name,
                                fontWeight = FontWeight.Bold,
                                color = if (combatState.isBoss) HealthRed else TextPrimary,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "ATK ${monster.atk} • DEF ${monster.def}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    if (monster.isDragonBoss || (combatState.isBoss && combatState.floor >= 65)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = HealthRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, GoldPrimary)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Whatshot, contentDescription = "Naga", tint = GoldPrimary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "RAS NAGA KELAS ATAS (PENGUASA TERTINGGI)",
                                    color = GoldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Monster HP bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("HP Monster", color = HealthRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${monster.currentHp} / ${monster.maxHp}", color = TextPrimary, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { (monster.currentHp.toFloat() / monster.maxHp).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HealthRed,
                        trackColor = DungeonSurfaceVariant
                    )
                }
            }
        }

        // Active Party Members bar
        if (activeParty.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activeParty.forEach { member ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = DungeonSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, DungeonCardStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ArcanePurple.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(member.name.take(1), color = ArcanePurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = member.name.substringBefore(" "),
                                    color = TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = member.skillName,
                                    color = GoldPrimary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weapon Banner Indicator
        if (equippedWeapon != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Senjata: ${equippedWeapon.name}${if (equippedWeapon.upgradeLevel > 0) " +${equippedWeapon.upgradeLevel}" else ""}",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (equippedWeapon.runeType != "NONE") {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(ArcanePurple.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("Runa ${equippedWeapon.runeType}", color = ArcanePurple, fontSize = 9.sp)
                    }
                }
            }
        }

        // Combat Logs
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            color = DungeonSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DungeonCardStroke)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                reverseLayout = true
            ) {
                items(combatState.combatLogs.reversed()) { log ->
                    Text(
                        text = "• $log",
                        color = when {
                            log.contains("CRITICAL") || log.contains("Kemenangan") -> GoldPrimary
                            log.contains("Kamu menyerang") || log.contains("jurus") -> ManaBlue
                            log.contains("menyerang balik") || log.contains("pingsan") -> HealthRed
                            log.contains("[Rekan]") -> ArcanePurple
                            else -> TextSecondary
                        },
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        // Bottom Controls
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            color = DungeonSurface,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, DungeonCardStroke)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (combatState.isVictory) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = combatState.rewardsSummary,
                            color = SanctuaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        // Boss Equipment Drop (Epik, Legend, Mythic, Divine/Godly)
                        if (combatState.droppedEquipment != null) {
                            val equip = combatState.droppedEquipment
                            val rarityColor = when (equip.rarity) {
                                "DIVINE" -> GoldPrimary
                                "MYTHIC" -> HealthRed
                                "LEGEND" -> ArcanePurple
                                else -> ManaBlue
                            }
                            val rarityBadgeText = when (equip.rarity) {
                                "DIVINE" -> "HADIAH BOS: [DIVINE / GODLY]"
                                "MYTHIC" -> "HADIAH BOS: [MYTHIC]"
                                "LEGEND" -> "HADIAH BOS: [LEGEND]"
                                else -> "HADIAH BOS: [EPIK]"
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = rarityColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, rarityColor)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = rarityBadgeText, color = rarityColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = equip.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Tier ${equip.tier} • ${equip.basePower} ATK Power • ${equip.description}", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }

                        // Dropped Artifact Display (0.1%, 0.2%, or Floor 100 Guarantee)
                        if (combatState.droppedArtifact != null) {
                            val art = combatState.droppedArtifact
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = GoldPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, GoldPrimary)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoFixHigh, contentDescription = "Artifact", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "ARTEFAK LANGKA BERHASIL DIDAPATKAN!", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(text = "Artefak #${art.slotNumber}: ${art.name} (${art.title})", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Efek: ${art.statBonusDesc}", color = SanctuaryGreen, fontSize = 10.sp)
                                    Text(text = "Catatan: Tidak dapat diperjualbelikan. Berhati-hatilah terhadap rival sesama lantai!", color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                        }

                        // Floor 100 Mastery Banner
                        if (combatState.isFloor100Completed) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = ArcanePurple.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, GoldPrimary)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "👑 TAMAT: PENAKLUK 100 LANTAI DUNGEON! 👑", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                    Text(text = "Kamu telah menyelesaikan seluruh 100 lantai dungeon dan menerima Artefak Senjata Tertinggi!", color = TextPrimary, fontSize = 10.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (combatState.floor % 5 == 0) {
                                Button(
                                    onClick = onNextFloor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("btn_enter_checkpoint"),
                                    colors = ButtonDefaults.buttonColors(containerColor = SanctuaryGreen, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = "Camp")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Masuk Toko Checkpoint", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = onNextFloor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("btn_next_floor"),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Lanjut Lantai ${combatState.floor + 1}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                                }
                            }
                        }
                    }
                } else if (combatState.isDefeat) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pahlawan Terjatuh!",
                            color = HealthRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tim penyelamat mengevakuasimu kembali ke camp checkpoint aman.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onEscape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_defeat_recover"),
                            colors = ButtonDefaults.buttonColors(containerColor = HealthRed, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Bangkit & Kembali ke Checkpoint", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                } else {
                    // Turn Actions
                    val isPlayerTurn = combatState.isPlayerTurn
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAttack,
                            enabled = isPlayerTurn,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_combat_attack"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = "Serang", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Serang", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showSkillDialog = true },
                            enabled = isPlayerTurn,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_combat_skill"),
                            colors = ButtonDefaults.buttonColors(containerColor = ManaBlue, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Skill", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skill", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onGuard,
                            enabled = isPlayerTurn,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_combat_guard"),
                            colors = ButtonDefaults.buttonColors(containerColor = DungeonSurfaceVariant, contentColor = TextPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = "Tangkis", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tangkis", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showPotionDialog = true },
                            enabled = isPlayerTurn,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_combat_potions"),
                            colors = ButtonDefaults.buttonColors(containerColor = DungeonSurfaceVariant, contentColor = TextPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.LocalHospital, contentDescription = "Ramuan", modifier = Modifier.size(16.dp), tint = HealthRed)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ramuan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Skill Selection Dialog
    if (showSkillDialog) {
        AlertDialog(
            onDismissRequest = { showSkillDialog = false },
            title = { Text("Pilih Jurus & Mantra", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    skills.forEach { skill ->
                        val canCast = (character?.currentMp ?: 0) >= skill.mpCost
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = canCast) {
                                    showSkillDialog = false
                                    onSkill(skill)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (canCast) DungeonSurfaceVariant else DungeonDarkBg.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(1.dp, if (canCast) ManaBlue else DungeonCardStroke),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = skill.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canCast) TextPrimary else TextSecondary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${skill.mpCost} MP",
                                        color = ManaBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = skill.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSkillDialog = false }) {
                    Text("Tutup", color = TextSecondary)
                }
            },
            containerColor = DungeonSurface
        )
    }

    // Potion Selection Dialog
    if (showPotionDialog) {
        val potionItems = inventory.filter { it.itemType == "POTION" && it.quantity > 0 }
        AlertDialog(
            onDismissRequest = { showPotionDialog = false },
            title = { Text("Pilih Ramuan Pemulih", color = HealthRed, fontWeight = FontWeight.Bold) },
            text = {
                if (potionItems.isEmpty()) {
                    Text("Tidak ada ramuan di tas! Kamu dapat membelinya di toko checkpoint lantai.", color = TextSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        potionItems.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showPotionDialog = false
                                        onUsePotion(item.itemId)
                                    },
                                colors = CardDefaults.cardColors(containerColor = DungeonSurfaceVariant),
                                border = BorderStroke(1.dp, HealthRed.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(item.description, color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(HealthRed.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("${item.quantity}x", color = HealthRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPotionDialog = false }) {
                    Text("Tutup", color = TextSecondary)
                }
            },
            containerColor = DungeonSurface
        )
    }
}
