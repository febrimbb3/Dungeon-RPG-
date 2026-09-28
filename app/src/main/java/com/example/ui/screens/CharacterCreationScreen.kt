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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.GameFormulas
import com.example.model.RaceDefinition
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
fun CharacterCreationScreen(
    onCreateCharacter: (name: String, characterClass: String, characterRace: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("PEDANG") }
    var selectedRace by remember { mutableStateOf("MANUSIA") }

    val availableRaces = if (selectedClass == "PEDANG") {
        GameFormulas.SWORDSMAN_RACES
    } else {
        GameFormulas.MAGE_RACES
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DungeonDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DungeonCardStroke, RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_dungeon_hero),
                contentDescription = "Dungeon Entrance Hero",
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
                    .padding(16.dp)
            ) {
                Text(
                    text = "DUNGEON 100",
                    color = GoldPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Pendaftaran Petualang Baru • Puncak Level 100",
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, DungeonCardStroke)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Peraturan Guild & Sistem Ras",
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Pengguna Pedang dapat memilih 2 Ras: Ras Manusia atau Ras Beastkin.\n" +
                            "• Pengguna Penyihir dapat memilih 2 Ras: Ras Elf atau Ras Demonkin.\n" +
                            "• Ras Naga dikunci dan TIDAK DAPAT dipilih oleh pemain — Ras Naga hanya berkuasa sebagai Bos Dungeon Kelas Atas (Lantai 65 - 100)!",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 16) name = it },
            label = { Text("Nama Petualang") },
            placeholder = { Text("cth: Kael, Arthur, Elenor") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_character_name"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = DungeonCardStroke,
                focusedLabelColor = GoldPrimary,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Section 1: Select Class (Pedang vs Sihir)
        Text(
            text = "1. Pilih Tipe Karakter",
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Class 1: Pedang
        val isSwordSelected = selectedClass == "PEDANG"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectedClass = "PEDANG"
                    if (selectedRace != "MANUSIA" && selectedRace != "BEASTKIN") {
                        selectedRace = "MANUSIA"
                    }
                }
                .testTag("card_class_sword"),
            colors = CardDefaults.cardColors(
                containerColor = if (isSwordSelected) DungeonSurfaceVariant else DungeonSurface
            ),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(if (isSwordSelected) 2.dp else 1.dp, if (isSwordSelected) GoldPrimary else DungeonCardStroke)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(HealthRed.copy(alpha = 0.2f))
                        .border(1.dp, HealthRed, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Pedang",
                        tint = HealthRed,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ksatria Pedang (2 Pilihan Ras)",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        if (isSwordSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Terpilih",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ahli tebasan jarak dekat, pertahanan kokoh, benteng perisai, dan tempa pedang +15.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Class 2: Sihir
        val isMageSelected = selectedClass == "SIHIR"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectedClass = "SIHIR"
                    if (selectedRace != "ELF" && selectedRace != "DEMONKIN") {
                        selectedRace = "ELF"
                    }
                }
                .testTag("card_class_mage"),
            colors = CardDefaults.cardColors(
                containerColor = if (isMageSelected) DungeonSurfaceVariant else DungeonSurface
            ),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(if (isMageSelected) 2.dp else 1.dp, if (isMageSelected) ManaBlue else DungeonCardStroke)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ManaBlue.copy(alpha = 0.2f))
                        .border(1.dp, ManaBlue, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = "Sihir",
                        tint = ManaBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Penyihir Mantra (2 Pilihan Ras)",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        if (isMageSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Terpilih",
                                tint = ManaBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Penguasa elemen api neraka, badai es beku, dan meteor kehancuran dengan infusi runa mistis.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 2: Select Race (2 Ras for Selected Class)
        Text(
            text = "2. Pilih Ras untuk Pengguna ${if (isSwordSelected) "Pedang" else "Sihir"}",
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        availableRaces.forEach { raceDef ->
            val isRaceSelected = selectedRace == raceDef.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedRace = raceDef.id }
                    .testTag("card_race_${raceDef.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isRaceSelected) DungeonSurfaceVariant else DungeonSurface
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    if (isRaceSelected) 2.dp else 1.dp,
                    if (isRaceSelected) (if (isSwordSelected) GoldPrimary else ManaBlue) else DungeonCardStroke
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (raceDef.id) {
                                    "BEASTKIN" -> Icons.Default.Pets
                                    "DEMONKIN" -> Icons.Default.Whatshot
                                    "ELF" -> Icons.Default.AutoAwesome
                                    else -> Icons.Default.Shield
                                },
                                contentDescription = raceDef.name,
                                tint = if (isRaceSelected) GoldPrimary else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = raceDef.name,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        if (isRaceSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Terpilih",
                                tint = if (isSwordSelected) GoldPrimary else ManaBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = raceDef.title,
                        color = if (isSwordSelected) GoldPrimary else ManaBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = raceDef.description,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = DungeonDarkBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Bonus Ras: ${raceDef.statBonusSummary}",
                            color = SanctuaryGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Section 3: Dragon Race Locked / High Tier Boss Showcase
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DungeonSurface.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HealthRed.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(HealthRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = HealthRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Ras Naga (Terkunci • Bos Kelas Atas)",
                                color = HealthRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "Hanya untuk Bos Dungeon Kelas Atas (Lantai 65 - 100)",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan Sistem: Ras Naga adalah ras tertinggi terkuat dan TIDAK DAPAT dipilih oleh petualang biasa. Mereka hanya muncul sebagai Penguasa Bos Tertinggi di kedalaman dungeon!",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Submit Button
        Button(
            onClick = {
                val finalName = if (name.isBlank()) (if (selectedClass == "PEDANG") "Arthur" else "Merlin") else name.trim()
                onCreateCharacter(finalName, selectedClass, selectedRace)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_create_character"),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Daftar Petualang & Mulai Misi Pertama",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
