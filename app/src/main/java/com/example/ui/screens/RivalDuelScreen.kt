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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RivalDuelScreen(
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
    onGoToCheckpoint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rival = combatState.rival
    val charClass = character?.characterClass ?: "PEDANG"
    val skills = if (charClass == "PEDANG") GameFormulas.getSwordsmanSkills() else GameFormulas.getMageSkills()

    var showSkillDialog by remember { mutableStateOf(false) }
    var showPotionDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DungeonDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Duel Rival Petualang • Lantai ${combatState.floor}",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    if (!combatState.isDefeat && !combatState.isVictory) {
                        IconButton(onClick = onEscape, modifier = Modifier.testTag("btn_back_duel")) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kabur",
                                tint = GoldPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DungeonSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            StatusHeader(character = character)

            // Rules Reminder Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = DungeonSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Rule",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Party tidak boleh saling membunuh. Jika kalah duel rival, kamu bangun di checkpoint dan kehilangan artefak!",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            // Rival Header Card
            if (rival != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.5.dp, HealthRed),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(HealthRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Dangerous,
                                    contentDescription = "Rival",
                                    tint = HealthRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = rival.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(HealthRed, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "RIVAL SESAMA LANTAI",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                                Text(
                                    text = "Lv. ${rival.level} ${rival.characterClass} • Lantai ${rival.floor}",
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"${rival.quote}\"",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rival HP Bar
                        val hpFraction = (rival.currentHp.toFloat() / rival.maxHp.toFloat()).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "HP Rival", color = HealthRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${rival.currentHp} / ${rival.maxHp}", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { hpFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = HealthRed,
                            trackColor = DungeonDarkBg
                        )
                    }
                }
            }

            // Party Companions Backup Strip
            if (activeParty.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    color = ArcanePurple.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ArcanePurple.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = "Party",
                            tint = ArcanePurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rekan Party (${activeParty.joinToString(", ") { it.name }}) ikut bertarung membelamu!",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Combat Log Area
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
                                log.contains("Kemenangan") || log.contains("MEREBUT KEMBALI") -> GoldPrimary
                                log.contains("FATAL") || log.contains("DICURI") || log.contains("terbunuh") -> HealthRed
                                log.contains("menyerang balik") -> HealthRed
                                log.contains("[Rekan Party]") -> ArcanePurple
                                log.contains("Kamu") -> ManaBlue
                                else -> TextSecondary
                            },
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // Action Panel or Outcome Panel
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
                                text = "Kemenangan Duel!",
                                color = SanctuaryGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = combatState.rewardsSummary,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                            if (combatState.reclaimedArtifactFromRival != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = GoldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, GoldPrimary)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.MilitaryTech, contentDescription = "Reclaimed", tint = GoldPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Artefak [${combatState.reclaimedArtifactFromRival.name}] berhasil direbut kembali!",
                                            color = GoldPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onEscape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("btn_victory_finish_duel"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Lanjutkan Petualangan Dungeon", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else if (combatState.isDefeat) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Dangerous, contentDescription = "Defeat", tint = HealthRed)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Kamu Terbunuh dalam Duel!",
                                    color = HealthRed,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = combatState.rewardsSummary,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                            if (combatState.stolenArtifactByRival != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = HealthRed.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, HealthRed)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "ARTEFAK DICURI: ${combatState.stolenArtifactByRival.name}",
                                            color = HealthRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Dicuri oleh ${rival?.name ?: "Rival"}. Kamu dapat merebutnya kembali dengan mengalahkannya di Lantai ${rival?.floor ?: 1}!",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onGoToCheckpoint,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("btn_wake_at_checkpoint"),
                                colors = ButtonDefaults.buttonColors(containerColor = HealthRed, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = "Checkpoint")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bangun di Checkpoint Terakhir", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        // Action Buttons
                        val isTurn = combatState.isPlayerTurn
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAttack,
                                enabled = isTurn,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_pvp_attack"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = "Serang", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Serang", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showSkillDialog = true },
                                enabled = isTurn,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_pvp_skill"),
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
                                enabled = isTurn,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_pvp_guard"),
                                colors = ButtonDefaults.buttonColors(containerColor = DungeonSurfaceVariant, contentColor = TextPrimary),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, DungeonCardStroke)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = "Bertahan", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bertahan", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showPotionDialog = true },
                                enabled = isTurn,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_pvp_potion"),
                                colors = ButtonDefaults.buttonColors(containerColor = DungeonSurfaceVariant, contentColor = TextPrimary),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, DungeonCardStroke)
                            ) {
                                Icon(Icons.Default.LocalHospital, contentDescription = "Ramuan", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ramuan", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = onEscape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("btn_pvp_escape"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.DirectionsRun, contentDescription = "Kabur", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tarik Diri / Mundur dari Duel", fontSize = 11.sp)
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
            title = { Text("Pilih Jurus Bertarung", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    skills.forEach { skill ->
                        val canCast = (character?.currentMp ?: 0) >= skill.mpCost
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = canCast) {
                                    showSkillDialog = false
                                    onSkill(skill)
                                }
                                .testTag("btn_pvp_cast_${skill.name.replace(" ", "_")}"),
                            color = if (canCast) DungeonSurfaceVariant else DungeonDarkBg,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (canCast) ManaBlue else DungeonCardStroke)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = skill.name, color = if (canCast) TextPrimary else TextSecondary, fontWeight = FontWeight.Bold)
                                    Text(text = "${skill.mpCost} MP", color = ManaBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = skill.description, color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSkillDialog = false }) {
                    Text("Tutup", color = TextSecondary)
                }
            },
            containerColor = DungeonSurface
        )
    }

    // Potion Selection Dialog
    if (showPotionDialog) {
        val potions = inventory.filter { it.itemType == "POTION" && it.quantity > 0 }
        AlertDialog(
            onDismissRequest = { showPotionDialog = false },
            title = { Text("Gunakan Ramuan", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                if (potions.isEmpty()) {
                    Text("Kamu tidak memiliki ramuan pemulihan.", color = TextSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        potions.forEach { pot ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showPotionDialog = false
                                        onUsePotion(pot.itemId)
                                    }
                                    .testTag("btn_pvp_use_${pot.itemId}"),
                                color = DungeonSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SanctuaryGreen)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = pot.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text(text = pot.description, color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Text(text = "x${pot.quantity}", color = SanctuaryGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPotionDialog = false }) {
                    Text("Tutup", color = TextSecondary)
                }
            },
            containerColor = DungeonSurface
        )
    }
}
