package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.PartyMemberEntity
import com.example.data.local.entity.WeaponEntity
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
fun MainDungeonOverviewScreen(
    character: CharacterEntity?,
    equippedWeapon: WeaponEntity?,
    activeParty: List<PartyMemberEntity>,
    onEnterDungeon: (Int) -> Unit,
    onOpenCheckpoint: () -> Unit,
    onOpenCrafting: () -> Unit,
    onOpenParty: () -> Unit,
    onOpenArtifacts: () -> Unit,
    onStartRivalDuel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentFloor = character?.currentFloor ?: 1
    val maxFloor = character?.maxFloorReached ?: 1
    val nextBossFloor = (((currentFloor - 1) / 5) + 1) * 5
    val nextBossName = GameFormulas.getFloorBossName(nextBossFloor)

    // Checkpoints list
    val checkpoints = remember(maxFloor) {
        val list = mutableListOf(1)
        for (f in 5..100 step 5) {
            if (f <= maxFloor) list.add(f)
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DungeonDarkBg)
    ) {
        StatusHeader(character = character)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            // Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, DungeonCardStroke, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_dungeon_hero),
                    contentDescription = "Dungeon Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Transparent, DungeonDarkBg.copy(alpha = 0.9f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(GoldPrimary, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LANTAI $currentFloor / 100",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = GameFormulas.getRankName(maxFloor),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gerbang Abyss Lantai 100",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Launch Button
            Button(
                onClick = { onEnterDungeon(currentFloor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_start_exploration"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Mulai", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Masuki Lantai $currentFloor Dungeon",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fast Checkpoints Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lompat ke Checkpoint Terbuka",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Toko & Camp tiap 5 lantai",
                    color = SanctuaryGreen,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(checkpoints) { cpFloor ->
                    val isCurrent = cpFloor == currentFloor
                    Surface(
                        modifier = Modifier
                            .clickable { onEnterDungeon(cpFloor) }
                            .testTag("btn_cp_select_$cpFloor"),
                        color = if (isCurrent) GoldPrimary.copy(alpha = 0.25f) else DungeonSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isCurrent) GoldPrimary else DungeonCardStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (cpFloor == 1) "Lt. 1" else "Lt. $cpFloor",
                                color = if (isCurrent) GoldPrimary else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Upcoming Boss Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (currentFloor % 5 == 0) HealthRed else DungeonCardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(HealthRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Boss", tint = HealthRed, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Penjaga Checkpoint Berikutnya (Lantai $nextBossFloor)",
                            color = HealthRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = nextBossName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Equipment & Party Quick Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Weapon Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenCrafting() },
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.dp, DungeonCardStroke),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Upgrade, contentDescription = "Craft", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Senjata Aktif", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = equippedWeapon?.name ?: "Belum Ada",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "+${equippedWeapon?.upgradeLevel ?: 0} • ${equippedWeapon?.basePower ?: 0} ATK",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Party Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenParty() },
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.dp, DungeonCardStroke),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = "Party", tint = ArcanePurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rekan Party", color = ArcanePurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activeParty.size} Rekan Membantu",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Kelola tim di Guild",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Artifacts & PvP Rival Duel Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 10 Artifacts Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenArtifacts() }
                        .testTag("btn_overview_artifacts"),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = "Artifacts", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("10 Artefak", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Koleksi Sakral",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tidak bisa dijual",
                            color = SanctuaryGreen,
                            fontSize = 10.sp
                        )
                    }
                }

                // Rival PvP Duel Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStartRivalDuel(currentFloor) }
                        .testTag("btn_overview_pvp_duel"),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.5.dp, HealthRed.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlashOn, contentDescription = "Duel", tint = HealthRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Duel Rival", color = HealthRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rival Lantai $currentFloor",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Rebut / Curian Artefak",
                            color = HealthRed,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dungeon Boss Drop Rules & Artefak Summary Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DungeonSurfaceVariant),
                border = BorderStroke(1.dp, DungeonCardStroke),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Pedoman Hadiah Bos & Artefak Dungeon",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Bos Lt. 1-25: Menjatuhkan Peralatan Epik Acak\n" +
                                "• Bos Lt. 30-60: Menjatuhkan Peralatan Legend Acak\n" +
                                "• Bos Lt. 65-90: Menjatuhkan Peralatan Mythic Acak\n" +
                                "• Bos Lt. 95-100: Menjatuhkan Peralatan Divine / Godly Acak\n" +
                                "• 10 Artefak Tidak Bisa Diperjualbelikan:\n" +
                                "   - Drop rate Bos Lt. 50-75: 0.1%\n" +
                                "   - Drop rate Bos Lt. 75-95: 0.2%\n" +
                                "   - Bos Lt. 100: Otomatis Artefak Senjata Penanda Tamat!\n" +
                                "• Hukum Party: Anggota party TIDAK BOLEH saling membunuh.\n" +
                                "• Waspada Rival: Jika terbunuh duel rival sesama lantai, kamu bangun di checkpoint terakhir & kehilangan artefak!",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
