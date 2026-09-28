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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.CheckpointShopItem
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
fun CheckpointScreen(
    character: CharacterEntity?,
    onRest: () -> Unit,
    onBuyItem: (CheckpointShopItem) -> Unit,
    onTeleport: (Int) -> Unit,
    onResumeDungeon: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentFloor = character?.currentFloor ?: 1
    val maxFloor = character?.maxFloorReached ?: 1
    val nearestCheckpoint = ((currentFloor / 5) * 5).coerceAtLeast(1)

    val shopItems = remember {
        listOf(
            CheckpointShopItem("potion_hp_small", "Ramuan HP Kecil", "POTION", "Memulihkan 60 HP karakter & party.", 25),
            CheckpointShopItem("potion_hp_med", "Ramuan HP Sedang", "POTION", "Memulihkan 120 HP karakter & party.", 60),
            CheckpointShopItem("potion_hp_large", "Ramuan Elixir HP Besar", "POTION", "Memulihkan 200 HP instan!", 120),
            CheckpointShopItem("potion_mp_small", "Ramuan MP Kecil", "POTION", "Memulihkan 45 MP mantra.", 25),
            CheckpointShopItem("potion_mp_med", "Ramuan MP Sedang", "POTION", "Memulihkan 85 MP mantra.", 60),
            CheckpointShopItem("potion_mp_large", "Ramuan Mana Murni", "POTION", "Memulihkan 140 MP instan!", 120),
            CheckpointShopItem("ore_iron", "Batu Besi Tempa", "MATERIAL", "Bahan utama untuk upgrade senjata di bengkel.", 35),
            CheckpointShopItem("material_mana_crystal", "Kristal Mana Halus", "MATERIAL", "Kristal untuk menempa tongkat sihir tier tinggi.", 65),
            CheckpointShopItem("rune_api", "Runa Api Neraka", "RUNE", "Infusi senjata: +25 bonus damage elemen api.", 160),
            CheckpointShopItem("rune_es", "Runa Es Kutub", "RUNE", "Infusi senjata: +20 bonus damage & pembekuan.", 160),
            CheckpointShopItem("rune_petir", "Runa Petir Badai", "RUNE", "Infusi senjata: +30 critical burst damage.", 190),
            CheckpointShopItem("rune_kehidupan", "Runa Jiwa Vampir", "RUNE", "Infusi senjata: menyerap 15 HP per serangan.", 220)
        )
    }

    // Checkpoint floors unlocked
    val checkpoints = remember(maxFloor) {
        val list = mutableListOf<Int>()
        list.add(1)
        for (f in 5..100 step 5) {
            if (f <= maxFloor) {
                list.add(f)
            }
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
                .padding(12.dp)
        ) {
            // Checkpoint Hero Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, DungeonCardStroke, RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_camp_sanctuary),
                    contentDescription = "Sanctuary Camp",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Transparent, DungeonDarkBg.copy(alpha = 0.85f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "TEMPAT AMAN CHECKPOINT",
                        color = SanctuaryGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Lantai Checkpoint Tersedia Setiap 5 Lantai (5, 10, 15, ..., 100)",
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Resume Dungeon Expedition
            Button(
                onClick = { onResumeDungeon(currentFloor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_resume_dungeon"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Lanjut")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Lanjutkan Ekspedisi (Lantai $currentFloor / 100)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 1: Rest & Recovery Sanctuary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SanctuaryGreen.copy(alpha = 0.4f))
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SanctuaryGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bedtime, contentDescription = "Rest", tint = SanctuaryGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Api Unggun & Tempat Istirahat", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Perbaiki kondisi pahlawan & seluruh rekan party", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onRest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_sanctuary_rest"),
                        colors = ButtonDefaults.buttonColors(containerColor = SanctuaryGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Healing, contentDescription = "Heal", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pulihkan 100% HP & MP (Gratis)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 2: Fast Travel Teleporter Checkpoints
            Text(
                text = "Portal Teleportasi Checkpoint",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Pilih checkpoint yang telah terbuka untuk mulai ekspedisi:",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(checkpoints) { cpFloor ->
                    val isCurrent = cpFloor == currentFloor
                    Surface(
                        modifier = Modifier
                            .clickable { onTeleport(cpFloor) }
                            .testTag("checkpoint_pill_$cpFloor"),
                        color = if (isCurrent) GoldPrimary.copy(alpha = 0.2f) else DungeonSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isCurrent) GoldPrimary else DungeonCardStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (cpFloor == 1) "Lt. 1 (Pintu Masuk)" else "Lt. $cpFloor",
                                color = if (isCurrent) GoldPrimary else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            if (isCurrent) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Check, contentDescription = "Active", tint = GoldPrimary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Checkpoint Shop (Toko Peralatan & Perbekalan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = "Toko", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Toko Perbekalan & Penempa Checkpoint",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "${character?.gold ?: 0} Gold",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            shopItems.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DungeonCardStroke)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (item.type) {
                                                "POTION" -> HealthRed.copy(alpha = 0.2f)
                                                "RUNE" -> ArcanePurple.copy(alpha = 0.2f)
                                                else -> GoldPrimary.copy(alpha = 0.2f)
                                            },
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(item.type, color = TextPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = { onBuyItem(item) },
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("btn_buy_${item.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = DungeonSurfaceVariant, contentColor = GoldPrimary),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", modifier = Modifier.size(14.dp), tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${item.price}g", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
