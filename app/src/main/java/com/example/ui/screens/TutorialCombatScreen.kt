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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsKabaddi
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
import androidx.compose.runtime.Composable
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
import com.example.ui.components.StatusHeader
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
fun TutorialCombatScreen(
    character: CharacterEntity?,
    combatState: CombatUiState,
    onExecuteAction: (actionType: String) -> Unit,
    onFinishTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monster = combatState.monster
    val step = combatState.tutorialStep

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DungeonDarkBg)
    ) {
        // Status header
        StatusHeader(character = character)

        // Step Guidance Banner
        val guideTitle = when (step) {
            1 -> "MISI PEMULA 1/4: SERANGAN DASAR"
            2 -> "MISI PEMULA 2/4: JURUS & MANTRA (SKILL)"
            3 -> "MISI PEMULA 3/4: BERTAHAN (GUARD)"
            4 -> "MISI PEMULA 4/4: PENGGUNAAN RAMUAN"
            else -> "MISI PERTAMA SELESAI!"
        }

        val guideInstruction = when (step) {
            1 -> "Tekan tombol [SERANG DASAR] di bawah untuk memukul target boneka latihan."
            2 -> "Skill mengonsumsi MP untuk memberikan damage berlipat ganda! Tekan tombol [GUNAKAN SKILL]."
            3 -> "Bertahan menangkis 50% serangan musuh dan memulihkan sebagian MP. Tekan tombol [BERTAHAN]."
            4 -> "Ramuan memulihkan HP atau MP saat sekarat. Tekan tombol [MINUM RAMUAN]."
            else -> "Kamu telah lulus pelatihan tempur dasar Guild Petualang! Hadiah siap diklaim."
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = if (step >= 5) SanctuaryGreen.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, if (step >= 5) SanctuaryGreen else GoldPrimary)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (step >= 5) SanctuaryGreen else GoldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (step >= 5) Icons.Default.CardGiftcard else Icons.Default.SportsKabaddi,
                        contentDescription = "Tutorial",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = guideTitle,
                        color = if (step >= 5) SanctuaryGreen else GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = guideInstruction,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Target Monster Card
        if (monster != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DungeonCardStroke)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = monster.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = monster.description,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Monster HP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("HP Target", color = HealthRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("${monster.currentHp}/${monster.maxHp}", color = TextPrimary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (monster.currentHp.toFloat() / monster.maxHp).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = HealthRed,
                        trackColor = DungeonSurfaceVariant
                    )
                }
            }
        }

        // Combat Logs
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
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
                        color = if (log.contains("Bagus") || log.contains("Luar") || log.contains("Hebat") || log.contains("Sempurna")) GoldPrimary
                               else if (log.contains("damage")) HealthRed
                               else TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }

        // Action Buttons Grid
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            color = DungeonSurface,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, DungeonCardStroke)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (step < 5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Attack
                        Button(
                            onClick = { onExecuteAction("ATTACK") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_tutorial_attack"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (step == 1) GoldPrimary else DungeonSurfaceVariant,
                                contentColor = if (step == 1) Color.Black else TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = "Serang", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Serang", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Skill
                        Button(
                            onClick = { onExecuteAction("SKILL") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_tutorial_skill"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (step == 2) ManaBlue else DungeonSurfaceVariant,
                                contentColor = if (step == 2) Color.Black else TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Skill", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skill", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Guard
                        Button(
                            onClick = { onExecuteAction("GUARD") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_tutorial_guard"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (step == 3) GoldPrimary else DungeonSurfaceVariant,
                                contentColor = if (step == 3) Color.Black else TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = "Bertahan", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bertahan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Potion
                        Button(
                            onClick = { onExecuteAction("POTION") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_tutorial_potion"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (step == 4) HealthRed else DungeonSurfaceVariant,
                                contentColor = if (step == 4) Color.White else TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.LocalHospital, contentDescription = "Ramuan", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ramuan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                } else {
                    // Complete Tutorial Button
                    Button(
                        onClick = onFinishTutorial,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_tutorial_finish"),
                        colors = ButtonDefaults.buttonColors(containerColor = SanctuaryGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Klaim")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Klaim Hadiah & Buka Dungeon 100 Lantai",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
