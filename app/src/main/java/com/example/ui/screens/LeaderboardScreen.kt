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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.LeaderboardEntity
import com.example.model.GameFormulas
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

@Composable
fun LeaderboardScreen(
    character: CharacterEntity?,
    leaderboard: List<LeaderboardEntity>,
    modifier: Modifier = Modifier
) {
    val playerRankName = GameFormulas.getRankName(character?.maxFloorReached ?: 1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DungeonDarkBg)
    ) {
        StatusHeader(character = character)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            item {
                // Personal Rank Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_player_rank"),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.5.dp, GoldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, GoldPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Rank",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = playerRankName,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${character?.name} • Peringkat Petualang Aktif",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = DungeonSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Lantai Tertinggi", color = TextSecondary, fontSize = 9.sp)
                                    Text("${character?.maxFloorReached ?: 1} / 100", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = DungeonSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Level Karakter", color = TextSecondary, fontSize = 9.sp)
                                    Text("Lv. ${character?.level ?: 1} / 100", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = DungeonSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Boss Kalah", color = TextSecondary, fontSize = 9.sp)
                                    Text("${character?.bossesDefeated ?: 0}", color = HealthRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rank Tier Breakdown
                Text(
                    text = "Jenjang Pangkat Petualang Dungeon 100",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Semakin dalam lantai yang berhasil kamu taklukkan, semakin tinggi pangkat guild dan kebanggaanmu!",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tiers = listOf(
                        "Rank F" to "Lt. 1-9",
                        "Rank E" to "Lt. 10-19",
                        "Rank D" to "Lt. 20-34",
                        "Rank C" to "Lt. 35-49",
                        "Rank B" to "Lt. 50-64",
                        "Rank A" to "Lt. 65-79",
                        "Rank S" to "Lt. 80-94",
                        "Rank SS" to "Lt. 95-100"
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DungeonSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, DungeonCardStroke)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text("F (1-9)", color = TextSecondary, fontSize = 10.sp)
                                Text("E (10-19)", color = TextSecondary, fontSize = 10.sp)
                                Text("D (20-34)", color = TextSecondary, fontSize = 10.sp)
                                Text("C (35-49)", color = TextSecondary, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text("B (50-64)", color = ManaBlue, fontSize = 10.sp)
                                Text("A (65-79)", color = ArcanePurple, fontSize = 10.sp)
                                Text("S (80-94)", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("SS (95-100)", color = SanctuaryGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Papan Peringkat Penakluk Dungeon",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            itemsIndexed(leaderboard) { index, entry ->
                val isSelf = entry.isPlayer
                val rankNum = index + 1
                val medalColor = when (rankNum) {
                    1 -> GoldPrimary
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> TextSecondary
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("leaderboard_item_$rankNum"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelf) GoldPrimary.copy(alpha = 0.15f) else DungeonSurface
                    ),
                    border = BorderStroke(1.dp, if (isSelf) GoldPrimary else DungeonCardStroke),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Number Badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(medalColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#$rankNum",
                                color = medalColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Class Icon
                        Icon(
                            imageVector = if (entry.className == "PEDANG") Icons.Default.Shield else Icons.Default.AutoFixHigh,
                            contentDescription = entry.className,
                            tint = if (entry.className == "PEDANG") HealthRed else ManaBlue,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = entry.name,
                                    color = if (isSelf) GoldPrimary else TextPrimary,
                                    fontWeight = if (isSelf) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (isSelf) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(GoldPrimary, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("KAMU", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "${entry.title} • Lv. ${entry.level}",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Lantai ${entry.floorReached}",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${entry.score} Poin",
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
