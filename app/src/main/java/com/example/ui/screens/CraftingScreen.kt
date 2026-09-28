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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.InventoryItemEntity
import com.example.data.local.entity.WeaponEntity
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

data class ForgeRecipe(
    val name: String,
    val type: String, // "PEDANG" or "TONGKAT"
    val tier: Int,
    val basePower: Int,
    val description: String,
    val goldCost: Int,
    val materialId: String,
    val materialName: String,
    val materialQty: Int
)

data class UpgradeMaterialOption(
    val id: String,
    val name: String,
    val description: String,
    val statUpgradeText: String,
    val icon: @Composable () -> Unit,
    val requiredQty: Int,
    val goldCost: Int,
    val powerGain: Int,
    val isLevelUpgrade: Boolean = false
)

@Composable
fun CraftingScreen(
    character: CharacterEntity?,
    equippedWeapon: WeaponEntity?,
    allWeapons: List<WeaponEntity>,
    inventory: List<InventoryItemEntity>,
    onUpgradeWeapon: (WeaponEntity) -> Unit,
    onCombineMaterial: (WeaponEntity, String) -> Unit = { weapon, matId -> onUpgradeWeapon(weapon) },
    onInfuseRune: (WeaponEntity, String) -> Unit,
    onForgeNewWeapon: (name: String, type: String, tier: Int, power: Int, desc: String, gold: Int, matId: String, matQty: Int) -> Unit,
    onEquipWeapon: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Kombinasi Bahan", "Infusi Runa", "Tempa Baru")

    // Weapon Filter: ALL, SWORDS, STAVES
    var weaponFilter by remember { mutableStateOf("ALL") }

    // Selected Weapon to Craft / Upgrade (Defaults to equipped, or first available)
    var selectedWeaponId by remember(equippedWeapon, allWeapons) {
        mutableLongStateOf(equippedWeapon?.id ?: allWeapons.firstOrNull()?.id ?: 0L)
    }

    val selectedWeapon = allWeapons.firstOrNull { it.id == selectedWeaponId } ?: equippedWeapon ?: allWeapons.firstOrNull()

    // Filtered list for selector
    val filteredWeapons = remember(allWeapons, weaponFilter) {
        when (weaponFilter) {
            "PEDANG" -> allWeapons.filter { it.weaponType == "PEDANG" }
            "TONGKAT" -> allWeapons.filter { it.weaponType == "TONGKAT" }
            else -> allWeapons
        }
    }

    // Material options
    val currentLevel = selectedWeapon?.upgradeLevel ?: 0
    val ironOreReq = (currentLevel / 2) + 1
    val ironOreGold = (currentLevel + 1) * 45 + 50
    val ironOrePowerGain = 6 + ((selectedWeapon?.tier ?: 1) * 2)

    val materialOptions = remember(selectedWeapon, currentLevel) {
        listOf(
            UpgradeMaterialOption(
                id = "ore_iron",
                name = "Batu Besi Tempa",
                description = "Bahan dasar tempa untuk meningkatkan level penguatan (+1 s/d +15).",
                statUpgradeText = "+$ironOrePowerGain ATK Power & Level +1",
                icon = { Icon(Icons.Default.Build, contentDescription = "Besi", tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                requiredQty = ironOreReq,
                goldCost = ironOreGold,
                powerGain = ironOrePowerGain,
                isLevelUpgrade = true
            ),
            UpgradeMaterialOption(
                id = "material_mana_crystal",
                name = "Kristal Mana Halus",
                description = "Kristal murni dungeon lantai 25+ untuk menyuntikkan daya sihir & serangan.",
                statUpgradeText = "+16 ATK Power Murni",
                icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = "Mana", tint = ManaBlue, modifier = Modifier.size(20.dp)) },
                requiredQty = 2,
                goldCost = 180,
                powerGain = 16
            ),
            UpgradeMaterialOption(
                id = "material_dragon_scale",
                name = "Sisik Naga Bawah Tanah",
                description = "Sisik keras naga purba lantai 50+ pelapis senjata berdaya tahan luar biasa.",
                statUpgradeText = "+32 ATK Power & Lapisan Naga",
                icon = { Icon(Icons.Default.Shield, contentDescription = "Sisik Naga", tint = HealthRed, modifier = Modifier.size(20.dp)) },
                requiredQty = 2,
                goldCost = 380,
                powerGain = 32
            ),
            UpgradeMaterialOption(
                id = "material_divine_ore",
                name = "Batu Inti Dewa",
                description = "Mineral surgawi lantai 80+ untuk kebangkitan stat ilahi dan damage tertinggi.",
                statUpgradeText = "+60 Divine ATK Power",
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Inti Dewa", tint = ArcanePurple, modifier = Modifier.size(20.dp)) },
                requiredQty = 1,
                goldCost = 750,
                powerGain = 60
            )
        )
    }

    val forgeRecipes = remember {
        listOf(
            // Swords
            ForgeRecipe(
                name = "Pedang Baja Tempa",
                type = "PEDANG",
                tier = 2,
                basePower = 34,
                description = "Pedang baja kokoh dengan ketajaman berlipat untuk penjelajah lantai 20+.",
                goldCost = 250,
                materialId = "ore_iron",
                materialName = "Batu Besi Tempa",
                materialQty = 12
            ),
            ForgeRecipe(
                name = "Bilah Api Crimson",
                type = "PEDANG",
                tier = 3,
                basePower = 70,
                description = "Bilah merah membara yang melelehkan zirah monster lantai 50+.",
                goldCost = 600,
                materialId = "material_mana_crystal",
                materialName = "Kristal Mana Halus",
                materialQty = 15
            ),
            ForgeRecipe(
                name = "Pedang Pembantai Naga",
                type = "PEDANG",
                tier = 4,
                basePower = 135,
                description = "Pedang kolosal berlapis sisik naga legendaris untuk menembus lantai 80+.",
                goldCost = 1600,
                materialId = "material_dragon_scale",
                materialName = "Sisik Naga Bawah Tanah",
                materialQty = 10
            ),
            ForgeRecipe(
                name = "Pedang Suci Excalibur Genesis",
                type = "PEDANG",
                tier = 5,
                basePower = 260,
                description = "Pedang pusaka para dewa penakluk Lantai 100 Dungeon Abyss.",
                goldCost = 4500,
                materialId = "material_divine_ore",
                materialName = "Batu Inti Dewa",
                materialQty = 8
            ),

            // Staves
            ForgeRecipe(
                name = "Tongkat Kristal Biru",
                type = "TONGKAT",
                tier = 2,
                basePower = 36,
                description = "Tongkat permata biru yang melipatgandakan daya rusak sihir es & petir.",
                goldCost = 250,
                materialId = "ore_iron",
                materialName = "Batu Besi Tempa",
                materialQty = 12
            ),
            ForgeRecipe(
                name = "Tongkat Badai Nebula",
                type = "TONGKAT",
                tier = 3,
                basePower = 75,
                description = "Tongkat magis yang mengembunkan energi kosmik untuk menghancurkan monster lantai 50+.",
                goldCost = 600,
                materialId = "material_mana_crystal",
                materialName = "Kristal Mana Halus",
                materialQty = 15
            ),
            ForgeRecipe(
                name = "Tongkat Arcana Bintang",
                type = "TONGKAT",
                tier = 4,
                basePower = 145,
                description = "Tongkat pemanggil komet yang mampu membelah pasukan monster lantai 80+.",
                goldCost = 1600,
                materialId = "material_dragon_scale",
                materialName = "Sisik Naga Bawah Tanah",
                materialQty = 10
            ),
            ForgeRecipe(
                name = "Tongkat Kehancuran Kosmik",
                type = "TONGKAT",
                tier = 5,
                basePower = 280,
                description = "Tongkat sihir terkuat di semesta untuk menundukkan Raja Naga & Penguasa Lantai 100.",
                goldCost = 4500,
                materialId = "material_divine_ore",
                materialName = "Batu Inti Dewa",
                materialQty = 8
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DungeonDarkBg)
    ) {
        StatusHeader(character = character)

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DungeonSurface,
            contentColor = GoldPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) GoldPrimary else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_crafting_$index")
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            // ==========================================
            // SECTION 1: WEAPON / STAFF SELECTION CAROUSEL
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih Pedang atau Tongkat",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${allWeapons.size} Senjata di Tas",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = weaponFilter == "ALL",
                    onClick = { weaponFilter = "ALL" },
                    label = { Text("Semua (${allWeapons.size})", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = DungeonSurface,
                        labelColor = TextSecondary
                    )
                )
                FilterChip(
                    selected = weaponFilter == "PEDANG",
                    onClick = { weaponFilter = "PEDANG" },
                    label = { Text("🗡️ Pedang (${allWeapons.count { it.weaponType == "PEDANG" }})", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HealthRed,
                        selectedLabelColor = Color.White,
                        containerColor = DungeonSurface,
                        labelColor = TextSecondary
                    )
                )
                FilterChip(
                    selected = weaponFilter == "TONGKAT",
                    onClick = { weaponFilter = "TONGKAT" },
                    label = { Text("🪄 Tongkat (${allWeapons.count { it.weaponType == "TONGKAT" }})", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ManaBlue,
                        selectedLabelColor = Color.Black,
                        containerColor = DungeonSurface,
                        labelColor = TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Weapon Carousel Cards
            if (filteredWeapons.isEmpty()) {
                Text(
                    text = "Tidak ada senjata untuk kategori ini.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredWeapons) { weapon ->
                        val isSelected = weapon.id == selectedWeapon?.id
                        val isSword = weapon.weaponType == "PEDANG"
                        val typeColor = if (isSword) HealthRed else ManaBlue

                        val rarityColor = when (weapon.rarity) {
                            "DIVINE" -> GoldPrimary
                            "MYTHIC" -> HealthRed
                            "LEGEND" -> ArcanePurple
                            "EPIC" -> ManaBlue
                            else -> TextSecondary
                        }

                        Card(
                            modifier = Modifier
                                .width(170.dp)
                                .clickable { selectedWeaponId = weapon.id }
                                .testTag("select_weapon_${weapon.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DungeonSurfaceVariant else DungeonSurface
                            ),
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) GoldPrimary else DungeonCardStroke),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(typeColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isSword) Icons.Default.Shield else Icons.Default.AutoFixHigh,
                                            contentDescription = weapon.weaponType,
                                            tint = typeColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    if (weapon.isEquipped) {
                                        Surface(
                                            color = SanctuaryGreen.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "DIPAKAI",
                                                color = SanctuaryGreen,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (isSelected) {
                                        Surface(
                                            color = GoldPrimary.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "DIPILIH",
                                                color = GoldPrimary,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = weapon.name,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "+${weapon.upgradeLevel}",
                                        color = GoldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "${weapon.basePower} ATK",
                                        color = SanctuaryGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (weapon.rarity != "COMMON") {
                                    Text(
                                        text = "[${weapon.rarity}]",
                                        color = rarityColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // SECTION 2: WORKBENCH FOR SELECTED WEAPON
            // ==========================================
            if (selectedWeapon != null) {
                val isSword = selectedWeapon.weaponType == "PEDANG"
                val typeColor = if (isSword) HealthRed else ManaBlue

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                    border = BorderStroke(1.5.dp, GoldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(typeColor.copy(alpha = 0.2f))
                                    .border(1.dp, typeColor, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSword) Icons.Default.Shield else Icons.Default.AutoFixHigh,
                                    contentDescription = selectedWeapon.weaponType,
                                    tint = typeColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedWeapon.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(GoldPrimary, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "+${selectedWeapon.upgradeLevel} / 15",
                                            color = Color.Black,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "Tier ${selectedWeapon.tier} • ${if (isSword) "Pedang Ksatria" else "Tongkat Sihir"} • [${selectedWeapon.rarity}]",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stats Summary Row
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
                                    Text("Base ATK", color = TextSecondary, fontSize = 9.sp)
                                    Text("${selectedWeapon.basePower}", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                color = DungeonSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Runa Soket", color = TextSecondary, fontSize = 9.sp)
                                    Text(
                                        text = if (selectedWeapon.runeType == "NONE") "Kosong" else selectedWeapon.runeType,
                                        color = if (selectedWeapon.runeType == "NONE") TextSecondary else ManaBlue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                color = DungeonSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Daya Runa", color = TextSecondary, fontSize = 9.sp)
                                    Text("+${selectedWeapon.runePower}", color = SanctuaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (!selectedWeapon.isEquipped) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onEquipWeapon(selectedWeapon.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("btn_equip_selected_weapon"),
                                colors = ButtonDefaults.buttonColors(containerColor = DungeonSurfaceVariant, contentColor = GoldPrimary),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Pasang", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pasang Senjata Ini Sekarang", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // SECTION 3: TABS CONTENT
            // ==========================================
            when (selectedTab) {
                0 -> {
                    // TAB 0: COMBINE COLLECTED MATERIALS TO UPGRADE STATS
                    Text(
                        text = "Gabungkan Bahan Dungeon untuk Naikkan Stat",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Pilih material hasil buruan dungeon untuk memperkuat ${selectedWeapon?.name ?: "senjata"}:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedWeapon == null) {
                        Text("Silakan pilih senjata atau tongkat terlebih dahulu.", color = TextSecondary)
                    } else {
                        materialOptions.forEach { matOption ->
                            val currentQty = inventory.firstOrNull { it.itemId == matOption.id }?.quantity ?: 0
                            val hasEnoughQty = currentQty >= matOption.requiredQty
                            val hasEnoughGold = (character?.gold ?: 0) >= matOption.goldCost
                            val isMaxLevel = matOption.isLevelUpgrade && selectedWeapon.upgradeLevel >= 15
                            val canUpgrade = hasEnoughQty && hasEnoughGold && !isMaxLevel

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                                border = BorderStroke(1.dp, if (canUpgrade) GoldPrimary.copy(alpha = 0.5f) else DungeonCardStroke),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
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
                                                    .background(DungeonSurfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                matOption.icon()
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = matOption.name,
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = matOption.description,
                                                    color = TextSecondary,
                                                    fontSize = 10.sp,
                                                    lineHeight = 14.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Stat Upgrade Preview Banner
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = SanctuaryGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, SanctuaryGreen.copy(alpha = 0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Upgrade, contentDescription = "Gain", tint = SanctuaryGreen, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Peningkatan: ${matOption.statUpgradeText}",
                                                    color = SanctuaryGreen,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "${selectedWeapon.basePower} ➜ ${selectedWeapon.basePower + matOption.powerGain} ATK",
                                                color = GoldPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Requirements & Combine Button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "Bahan: ", color = TextSecondary, fontSize = 10.sp)
                                                Text(
                                                    text = "$currentQty / ${matOption.requiredQty}",
                                                    color = if (hasEnoughQty) SanctuaryGreen else HealthRed,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "Biaya: ", color = TextSecondary, fontSize = 10.sp)
                                                Text(
                                                    text = "${matOption.goldCost} Gold",
                                                    color = if (hasEnoughGold) GoldPrimary else HealthRed,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { onCombineMaterial(selectedWeapon, matOption.id) },
                                            enabled = canUpgrade,
                                            modifier = Modifier
                                                .height(38.dp)
                                                .testTag("btn_combine_${matOption.id}"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = GoldPrimary,
                                                contentColor = Color.Black,
                                                disabledContainerColor = DungeonSurfaceVariant,
                                                disabledContentColor = TextSecondary
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (isMaxLevel) "Maksimal (+15)" else "Gabungkan",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: RUNIC INFUSION
                    Text(
                        text = "Infusi Runa Mistis Elemen",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Tanamkan Runa ke ${selectedWeapon?.name ?: "senjata"} untuk efek elemental khusus:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val runes = listOf(
                        Triple("API", "Runa Api Neraka (+25 ATK Power)", "Serangan memicu api abadi yang membakar musuh."),
                        Triple("ES", "Runa Badai Es (+20 ATK Power)", "Memperlambat musuh dan membekukan pertahanan."),
                        Triple("PETIR", "Runa Halilintar (+30 ATK Power)", "Sengatan petir berdaya rusak kritis tertinggi."),
                        Triple("KEHIDUPAN", "Runa Kehidupan (+15 ATK Power)", "Menghisap 15 HP musuh ke tubuh pemain tiap tebasan.")
                    )

                    runes.forEach { (runeType, runeName, runeDesc) ->
                        val runeItem = inventory.firstOrNull { it.itemId == "rune_" + runeType.lowercase() }
                        val runeQty = runeItem?.quantity ?: 0
                        val isSocketed = selectedWeapon?.runeType == runeType

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                            border = BorderStroke(1.dp, if (isSocketed) ManaBlue else DungeonCardStroke),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = runeName,
                                        color = if (isSocketed) ManaBlue else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(text = runeDesc, color = TextSecondary, fontSize = 10.sp)
                                    Text(
                                        text = "Dimiliki di tas: $runeQty Buah",
                                        color = if (runeQty > 0) SanctuaryGreen else HealthRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isSocketed) {
                                    Surface(
                                        color = ManaBlue.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "TERPASANG",
                                            color = ManaBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            selectedWeapon?.let { onInfuseRune(it, runeType) }
                                        },
                                        enabled = runeQty > 0 && selectedWeapon != null,
                                        modifier = Modifier.height(36.dp).testTag("btn_infuse_$runeType"),
                                        colors = ButtonDefaults.buttonColors(containerColor = ManaBlue, contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Infus", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: FORGE NEW WEAPON OR STAFF
                    Text(
                        text = "Tempa Senjata & Tongkat Tingkat Tinggi",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Gunakan bahan langka hasil ekspedisi lantai dungeon untuk menempa senjata pusaka baru:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    forgeRecipes.forEach { recipe ->
                        val matItem = inventory.firstOrNull { it.itemId == recipe.materialId }
                        val matQty = matItem?.quantity ?: 0
                        val hasMaterial = matQty >= recipe.materialQty
                        val hasGold = (character?.gold ?: 0) >= recipe.goldCost
                        val canForge = hasMaterial && hasGold

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            colors = CardDefaults.cardColors(containerColor = DungeonSurface),
                            border = BorderStroke(1.dp, if (canForge) GoldPrimary else DungeonCardStroke),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (recipe.type == "PEDANG") HealthRed.copy(alpha = 0.2f) else ManaBlue.copy(alpha = 0.2f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (recipe.type == "PEDANG") Icons.Default.Shield else Icons.Default.AutoFixHigh,
                                                contentDescription = recipe.type,
                                                tint = if (recipe.type == "PEDANG") HealthRed else ManaBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = recipe.name,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Tier ${recipe.tier} • ${if (recipe.type == "PEDANG") "Pedang" else "Tongkat Sihir"} • ${recipe.basePower} Power",
                                                color = GoldPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recipe.description,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Bahan: $matQty / ${recipe.materialQty} ${recipe.materialName}",
                                            color = if (hasMaterial) SanctuaryGreen else HealthRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Biaya: ${recipe.goldCost} Gold",
                                            color = if (hasGold) GoldPrimary else HealthRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            onForgeNewWeapon(
                                                recipe.name,
                                                recipe.type,
                                                recipe.tier,
                                                recipe.basePower,
                                                recipe.description,
                                                recipe.goldCost,
                                                recipe.materialId,
                                                recipe.materialQty
                                            )
                                        },
                                        enabled = canForge,
                                        modifier = Modifier.height(36.dp).testTag("btn_forge_${recipe.name.replace(" ", "_")}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Tempa Senjata", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
