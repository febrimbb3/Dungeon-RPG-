package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ArtifactEntity
import com.example.data.local.entity.CharacterEntity
import com.example.model.ArtifactDefinition
import com.example.model.GameFormulas
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactsScreen(
    character: CharacterEntity?,
    allArtifacts: List<ArtifactEntity>,
    onBack: () -> Unit,
    onChallengeRival: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val artifactMap = allArtifacts.associateBy { it.artifactId }
    val activeArtifacts = allArtifacts.filter { !it.isStolen }

    val totalAtk = activeArtifacts.sumOf { it.atkBonus }
    val totalDef = activeArtifacts.sumOf { it.defBonus }
    val totalHp = activeArtifacts.sumOf { it.hpBonus }
    val totalMp = activeArtifacts.sumOf { it.mpBonus }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DungeonDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "10 Artefak Sakral Dungeon",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_artifacts")) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DungeonSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Rules and Lore Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurfaceVariant),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Rules",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hukum Artefak & Aturan Dungeon",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• 10 Artefak TIDAK BISA DIPERJUALBELIKAN di toko manapun.\n" +
                                    "• Jatuh dari Bos Lantai 50-75 (0.1%) & Bos Lantai 75-95 (0.2%).\n" +
                                    "• Mengalahkan Bos Lantai 100 otomatis mendapat Artefak Senjata Tertinggi (Penanda Tamat Dungeon)!\n" +
                                    "• Pemain dalam party TIDAK BOLEH saling membunuh.\n" +
                                    "• AWAS: Artefak bisa dicuri oleh pemain rival sesama lantai jika kamu terbunuh dalam duel. Jika terbunuh, kamu akan bangun di checkpoint terakhir!",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Stat Buffs Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.dp, DungeonCardStroke),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Berkah Artefak Aktif",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${activeArtifacts.size} / 10 Terkumpul",
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BuffBadge(label = "ATK", value = "+$totalAtk", color = GoldPrimary, modifier = Modifier.weight(1f))
                            BuffBadge(label = "DEF", value = "+$totalDef", color = SanctuaryGreen, modifier = Modifier.weight(1f))
                            BuffBadge(label = "HP", value = "+$totalHp", color = HealthRed, modifier = Modifier.weight(1f))
                            BuffBadge(label = "MP", value = "+$totalMp", color = ManaBlue, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Daftar 10 Artefak Legendaris",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // List of 10 Artifacts
            items(GameFormulas.TEN_ARTIFACTS) { artifactDef ->
                val entity = artifactMap[artifactDef.id]
                ArtifactItemCard(
                    definition = artifactDef,
                    entity = entity,
                    currentFloor = character?.currentFloor ?: 1,
                    onChallengeRival = onChallengeRival
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun BuffBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DungeonSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = TextSecondary, fontSize = 9.sp)
            Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ArtifactItemCard(
    definition: ArtifactDefinition,
    entity: ArtifactEntity?,
    currentFloor: Int,
    onChallengeRival: (Int) -> Unit
) {
    val isOwned = entity != null && !entity.isStolen
    val isStolen = entity != null && entity.isStolen
    val isFloor100 = definition.slotNumber == 10

    val borderColor = when {
        isStolen -> HealthRed
        isOwned && isFloor100 -> GoldPrimary
        isOwned -> SanctuaryGreen
        else -> DungeonCardStroke
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("artifact_card_${definition.slotNumber}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned) DungeonSurface else DungeonDarkBg
        ),
        border = BorderStroke(if (isOwned || isStolen) 1.5.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isStolen -> HealthRed.copy(alpha = 0.2f)
                                isOwned && isFloor100 -> GoldPrimary.copy(alpha = 0.3f)
                                isOwned -> ArcanePurple.copy(alpha = 0.25f)
                                else -> DungeonSurfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isFloor100 -> Icons.Default.EmojiEvents
                            isStolen -> Icons.Default.Dangerous
                            isOwned -> Icons.Default.MilitaryTech
                            else -> Icons.Default.Lock
                        },
                        contentDescription = "Artifact icon",
                        tint = when {
                            isStolen -> HealthRed
                            isOwned && isFloor100 -> GoldPrimary
                            isOwned -> GoldPrimary
                            else -> TextSecondary
                        },
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
                            text = "Artefak #${definition.slotNumber}: ${definition.name}",
                            color = if (isOwned) GoldPrimary else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    when {
                                        isStolen -> HealthRed.copy(alpha = 0.2f)
                                        isOwned -> SanctuaryGreen.copy(alpha = 0.2f)
                                        else -> DungeonSurfaceVariant
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when {
                                    isStolen -> "DICURI"
                                    isOwned -> "AKTIF"
                                    else -> "TERKUNCI"
                                },
                                color = when {
                                    isStolen -> HealthRed
                                    isOwned -> SanctuaryGreen
                                    else -> TextSecondary
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = definition.title,
                        color = if (isFloor100) GoldPrimary else ArcanePurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = definition.description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bonus Stat Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DungeonSurfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Bonus",
                        tint = GoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = definition.statBonusDesc,
                        color = if (isOwned) GoldPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Location & Drop Rate Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lokasi: ${definition.dropFloorRange}",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = "Peluang: ${definition.dropChanceText}",
                    color = if (isFloor100) GoldPrimary else ManaBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Stolen Alert & Reclaim Action
            if (isStolen) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = HealthRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, HealthRed.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = HealthRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dicuri oleh Rival: ${entity.stolenByRivalName ?: "Rival Penjarah"}",
                                color = HealthRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { onChallengeRival(currentFloor) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("btn_reclaim_artifact_${definition.slotNumber}"),
                            colors = ButtonDefaults.buttonColors(containerColor = HealthRed, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Duel Rival di Lantai $currentFloor untuk Rebut Kembali!", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
