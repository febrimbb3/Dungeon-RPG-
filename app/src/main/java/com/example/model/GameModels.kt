package com.example.model

enum class CharacterClass(val displayName: String, val description: String) {
    PEDANG("Ksatria Pedang", "Ahli pertarungan jarak dekat dengan daya tahan tinggi, tebasan pedang mematikan, dan perisai baja."),
    SIHIR("Penyihir Mantra", "Penguasa elemen alam dengan serangan sihir jarak jauh bertenaga besar, badai es, petir, dan meteor.")
}

enum class RuneType(val displayName: String, val effectText: String, val colorHex: Long) {
    NONE("Tanpa Runa", "-", 0xFF888888),
    API("Runa Api", "+25 Damage Bakaran Api", 0xFFE53935),
    ES("Runa Es", "+20 Damage Pembekuan Es", 0xFF00ACC1),
    PETIR("Runa Petir", "+30 Critical Burst Listrik", 0xFFFFB300),
    KEHIDUPAN("Runa Jiwa", "+15 Pemulihan HP per Serangan", 0xFF43A047)
}

data class Skill(
    val id: String,
    val name: String,
    val mpCost: Int,
    val powerMultiplier: Float,
    val description: String,
    val isUltimate: Boolean = false,
    val effectType: String = "DAMAGE" // DAMAGE, HEAL, SHIELD
)

data class Monster(
    val name: String,
    val maxHp: Int,
    var currentHp: Int,
    val atk: Int,
    val def: Int,
    val expReward: Int,
    val goldReward: Int,
    val isBoss: Boolean = false,
    val floor: Int,
    val element: String = "NETRAL",
    val description: String = "",
    val race: String = "MONSTER",
    val isDragonBoss: Boolean = false
)

data class RaceDefinition(
    val id: String,
    val name: String,
    val title: String,
    val suitableClass: String, // "PEDANG", "SIHIR", "BOS_NAGA"
    val description: String,
    val statBonusSummary: String,
    val hpBonus: Int = 0,
    val mpBonus: Int = 0,
    val atkBonus: Int = 0,
    val defBonus: Int = 0,
    val isPlayable: Boolean = true
)

data class CheckpointShopItem(
    val id: String,
    val name: String,
    val type: String, // POTION, MATERIAL, RUNE
    val description: String,
    val price: Int,
    val quantityPerBuy: Int = 1
)

data class ArtifactDefinition(
    val id: String,
    val slotNumber: Int,
    val name: String,
    val title: String,
    val description: String,
    val statBonusDesc: String,
    val atkBonus: Int = 0,
    val defBonus: Int = 0,
    val hpBonus: Int = 0,
    val mpBonus: Int = 0,
    val dropFloorRange: String,
    val dropChanceText: String,
    val isFloor100Mastery: Boolean = false
)

data class RivalAdventurer(
    val id: String,
    val name: String,
    val title: String,
    val characterClass: String,
    val level: Int,
    val floor: Int,
    var currentHp: Int,
    val maxHp: Int,
    val atk: Int,
    val def: Int,
    val isPartyMember: Boolean = false,
    val heldArtifactId: String? = null,
    val quote: String = ""
)

object GameFormulas {
    val SWORDSMAN_RACES = listOf(
        RaceDefinition(
            id = "MANUSIA",
            name = "Ras Manusia",
            title = "Ksatria Manusia Pelindung",
            suitableClass = "PEDANG",
            description = "Bangsa manusia yang tangguh dan memiliki pertahanan tubuh seimbang, stamina kokoh, dan pertahanan solid.",
            statBonusSummary = "+20 Max HP • +5 DEF",
            hpBonus = 20,
            defBonus = 5,
            isPlayable = true
        ),
        RaceDefinition(
            id = "BEASTKIN",
            name = "Ras Beastkin",
            title = "Pendekar Cakar Liar",
            suitableClass = "PEDANG",
            description = "Bangsa manusia setengah binatang dengan cakar tajam dan insting bertarung buas yang melipatgandakan daya serang fisik.",
            statBonusSummary = "+30 Max HP • +6 ATK",
            hpBonus = 30,
            atkBonus = 6,
            isPlayable = true
        )
    )

    val MAGE_RACES = listOf(
        RaceDefinition(
            id = "ELF",
            name = "Ras Elf",
            title = "Peri Suci Aliran Mana",
            suitableClass = "SIHIR",
            description = "Bangsa peri berumur panjang dengan aliran mana alami yang murni, memungkinkan kapasitas dan regenerasi energi magis lebih tinggi.",
            statBonusSummary = "+35 Max MP • +4 ATK",
            mpBonus = 35,
            atkBonus = 4,
            isPlayable = true
        ),
        RaceDefinition(
            id = "DEMONKIN",
            name = "Ras Demonkin",
            title = "Penyihir Kehancuran Abyssal",
            suitableClass = "SIHIR",
            description = "Keturunan iblis kuno yang menguasai sihir kutukan gelap berdaya ledak fatal dan kehancuran ekstrem.",
            statBonusSummary = "+15 Max HP • +20 Max MP • +8 ATK",
            hpBonus = 15,
            mpBonus = 20,
            atkBonus = 8,
            isPlayable = true
        )
    )

    val DRAGON_BOSS_RACE = RaceDefinition(
        id = "NAGA",
        name = "Ras Naga Kuno (Ancient Dragon)",
        title = "Penguasa Bos Dungeon Kelas Atas",
        suitableClass = "BOS_NAGA",
        description = "Ras tertinggi penguasa langit dan jurang terdalam abyss. Ras ini TIDAK DAPAT DIPILIH OLEH PEMAIN. Ras Naga HANYA ADA SEBAGAI BOS DUNGEON KELAS ATAS (Lantai 65 s/d 100)!",
        statBonusSummary = "Eksklusif Bos Dungeon Kelas Atas (Lantai 65 - 100)",
        isPlayable = false
    )

    val TEN_ARTIFACTS = listOf(
        ArtifactDefinition(
            id = "art_1",
            slotNumber = 1,
            name = "Cincin Mata Naga Kuno",
            title = "Artefak Jiwa Naga",
            description = "Relik kuno tertanam dengan iris mata naga purba penghuni lantai 50. Memancarkan aura panas tak tertandingi. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+85 ATK • +150 HP",
            atkBonus = 85,
            hpBonus = 150,
            dropFloorRange = "Lantai 50 - 75",
            dropChanceText = "0.1% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_2",
            slotNumber = 2,
            name = "Mahkota Abadi Raja Elv",
            title = "Artefak Hutan Purba",
            description = "Mahkota suci bertatahkan zamrud kehidupan yang tidak pernah layu. Menjaga aliran vitalitas abadi. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+320 Max HP • +40 DEF",
            hpBonus = 320,
            defBonus = 40,
            dropFloorRange = "Lantai 50 - 75",
            dropChanceText = "0.1% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_3",
            slotNumber = 3,
            name = "Jimat Jiwa Phoenix Nether",
            title = "Artefak Api Abadi",
            description = "Jimat kristal api berkobar yang meregenerasi energi jiwa dan mana tak terhingga saat di dalam dungeon. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+140 Max MP • +50 ATK",
            mpBonus = 140,
            atkBonus = 50,
            dropFloorRange = "Lantai 50 - 75",
            dropChanceText = "0.1% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_4",
            slotNumber = 4,
            name = "Cermin Ilusi Dimensi Bayangan",
            title = "Artefak Kehampaan",
            description = "Cermin perak misterius yang membengkokkan ruang dan waktu, menangkis pukulan mematikan dari monster jurang. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+65 DEF • +120 HP • +30 MP",
            defBonus = 65,
            hpBonus = 120,
            mpBonus = 30,
            dropFloorRange = "Lantai 50 - 75",
            dropChanceText = "0.1% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_5",
            slotNumber = 5,
            name = "Teras Kristal Arcana Purba",
            title = "Artefak Inti Sihir",
            description = "Kristal misterius dari inti lantai 80 yang mengalirkan esensi sihir primordial dan memperkuat mantra. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+160 ATK • +180 Max MP",
            atkBonus = 160,
            mpBonus = 180,
            dropFloorRange = "Lantai 75 - 95",
            dropChanceText = "0.2% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_6",
            slotNumber = 6,
            name = "Sayap Kematian Malaikat Jatuh",
            title = "Artefak Sayap Bayangan",
            description = "Sayap hitam berbulu baja yang memberikan kecepatan kilat dan ketajaman tebasan mutlak. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+175 ATK • +60 DEF",
            atkBonus = 175,
            defBonus = 60,
            dropFloorRange = "Lantai 75 - 95",
            dropChanceText = "0.2% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_7",
            slotNumber = 7,
            name = "Perisai Aegis Sang Pencipta",
            title = "Artefak Pertahanan Absolut",
            description = "Perisai emas sakral penangkal segala bencana dan kutukan dungeon abyssal. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+130 DEF • +450 Max HP",
            defBonus = 130,
            hpBonus = 450,
            dropFloorRange = "Lantai 75 - 95",
            dropChanceText = "0.2% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_8",
            slotNumber = 8,
            name = "Piala Darah Dewa Titan",
            title = "Artefak Keperkasaan Titan",
            description = "Piala batu kuno berukir runa titan yang melipatgandakan daya tahan fisik petualang secara permanen. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+600 Max HP • +100 ATK",
            hpBonus = 600,
            atkBonus = 100,
            dropFloorRange = "Lantai 75 - 95",
            dropChanceText = "0.2% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_9",
            slotNumber = 9,
            name = "Jam Pasir Waktu Kronos",
            title = "Artefak Penguasa Waktu",
            description = "Jam pasir dengan butiran pasir bintang yang mempercepat refleks tempur dan memberi serangan ganda tak terelakkan. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+210 ATK • +90 DEF • +150 MP",
            atkBonus = 210,
            defBonus = 90,
            mpBonus = 150,
            dropFloorRange = "Lantai 75 - 95",
            dropChanceText = "0.2% Drop Rate"
        ),
        ArtifactDefinition(
            id = "art_10",
            slotNumber = 10,
            name = "Bilah Mahkota Penakluk Lantai 100",
            title = "Artefak Senjata Tertinggi (Supremasi Dungeon 100)",
            description = "Artefak senjata legendaris puncak yang hanya bisa didapatkan dengan membunuh Raja Iblis Malakar Lantai 100! Bukti nyata pahlawan sejati yang telah menuntaskan seluruh 100 lantai dungeon. Tidak dapat diperjualbelikan.",
            statBonusDesc = "+380 ATK • +200 DEF • +800 HP • +300 MP",
            atkBonus = 380,
            defBonus = 200,
            hpBonus = 800,
            mpBonus = 300,
            dropFloorRange = "Lantai 100 (Boss Malakar)",
            dropChanceText = "100% GARANSI DROP",
            isFloor100Mastery = true
        )
    )

    fun generateBossEquipmentDrop(floor: Int, playerClass: String): com.example.data.local.entity.WeaponEntity {
        val isSwordsman = playerClass == "PEDANG"
        val weaponType = if (isSwordsman) "PEDANG" else "TONGKAT"

        return when {
            // Lantai 95 - 100: Divine / Godly Equipment
            floor >= 95 -> {
                val name = if (isSwordsman) "Pedang Dewa Bintang Genesis" else "Tongkat Mahkota Dewa Arcana"
                val desc = "Peralatan tingkat Divine/Godly yang dijatuhkan langsung oleh Raja Iblis penjaga lantai $floor puncak dungeon."
                com.example.data.local.entity.WeaponEntity(
                    name = name,
                    weaponType = weaponType,
                    tier = 5,
                    rarity = "DIVINE",
                    basePower = 340 + (floor - 95) * 15,
                    upgradeLevel = 0,
                    runeType = if (isSwordsman) "PETIR" else "API",
                    runePower = 40,
                    isEquipped = false,
                    description = desc
                )
            }
            // Lantai 65 - 90: Mythic Equipment
            floor >= 65 -> {
                val name = if (isSwordsman) "Pedang Pembelah Dimensi Mythic" else "Tongkat Galaksi Void Mythic"
                val desc = "Peralatan tingkat Mythic langka dari bos lantai $floor dengan aliran energi misterius berkekuatan tinggi."
                com.example.data.local.entity.WeaponEntity(
                    name = name,
                    weaponType = weaponType,
                    tier = 4,
                    rarity = "MYTHIC",
                    basePower = 185 + ((floor - 65) / 5) * 18,
                    upgradeLevel = 0,
                    runeType = "NONE",
                    runePower = 0,
                    isEquipped = false,
                    description = desc
                )
            }
            // Lantai 30 - 60: Legend Equipment
            floor >= 30 -> {
                val name = if (isSwordsman) "Pedang Ksatria Kegelapan Legend" else "Tongkat Badai Es Purba Legend"
                val desc = "Peralatan tingkat Legend dari bos monster lantai $floor yang memiliki aura kekuatan tak tergoyahkan."
                com.example.data.local.entity.WeaponEntity(
                    name = name,
                    weaponType = weaponType,
                    tier = 3,
                    rarity = "LEGEND",
                    basePower = 95 + ((floor - 30) / 5) * 12,
                    upgradeLevel = 0,
                    runeType = "NONE",
                    runePower = 0,
                    isEquipped = false,
                    description = desc
                )
            }
            // Lantai 1 - 25: Epic Equipment
            else -> {
                val name = if (isSwordsman) "Pedang Baja Naga Epik" else "Tongkat Kristal Teratai Epik"
                val desc = "Peralatan tingkat Epik yang dijatuhkan oleh bos lantai $floor, cocok untuk menjelajah lebih dalam ke dungeon."
                com.example.data.local.entity.WeaponEntity(
                    name = name,
                    weaponType = weaponType,
                    tier = 2,
                    rarity = "EPIK",
                    basePower = 48 + (floor / 5) * 8,
                    upgradeLevel = 0,
                    runeType = "NONE",
                    runePower = 0,
                    isEquipped = false,
                    description = desc
                )
            }
        }
    }

    fun generateRivalForFloor(floor: Int): RivalAdventurer {
        val names = listOf(
            "Vexen si Pedang Hitam", "Selena si Pencuri Runa", "Kallum Pembantai Petualang",
            "Morgath Penyihir Darah", "Raven sang Pemburu Artefak", "Ignatius si Bilah Petir"
        )
        val name = names[(floor * 7) % names.size]
        val isMage = (floor % 2 == 0)
        val scaling = 1.0 + (floor - 1) * 0.11

        return RivalAdventurer(
            id = "rival_floor_$floor",
            name = name,
            title = "Penjelajah Rival Lantai $floor",
            characterClass = if (isMage) "SIHIR" else "PEDANG",
            level = (floor.coerceIn(1, 100)),
            floor = floor,
            currentHp = (110 * scaling).toInt(),
            maxHp = (110 * scaling).toInt(),
            atk = (16 * scaling).toInt(),
            def = (7 * scaling).toInt(),
            isPartyMember = false,
            quote = "Artefak dan perbekalanmu di lantai $floor akan menjadi milikku!"
        )
    }
    fun expForNextLevel(level: Int): Long {
        return (level * 65L) + (level * level * 12L)
    }

    fun getRankName(floor: Int): String {
        return when {
            floor >= 95 -> "Rank SS - Legenda Abadi"
            floor >= 80 -> "Rank S - Pahlawan Kerajaan"
            floor >= 65 -> "Rank A - Ahli Dungeon"
            floor >= 50 -> "Rank B - Ksatria Elit"
            floor >= 35 -> "Rank C - Petualang Teruji"
            floor >= 20 -> "Rank D - Penjelajah Gua"
            floor >= 10 -> "Rank E - Pengelana Baru"
            else -> "Rank F - Pemula Guild"
        }
    }

    fun getFloorBossName(floor: Int): String {
        return when (floor) {
            5 -> "Kapten Goblin Bertaring (Ras Goblin)"
            10 -> "Raja Skeleton Necromancer (Ras Undead)"
            15 -> "Ratu Arachne Racun Kuno (Ras Serangga Kuno)"
            20 -> "Titan Golem Batu Raksasa (Ras Konstruk Elemental)"
            25 -> "Cerberus Gerbang Neraka (Ras Beastkin Neraka)"
            30 -> "Iblis Api Pyro Demon (Ras Demonkin)"
            35 -> "Gargoyle Sayap Baja (Ras Gargoyle Purba)"
            40 -> "Lich Penguasa Jiwa Beku (Ras Undead Abyssal)"
            45 -> "Ksatria Bayangan Kematian (Ras Hantu Terkutuk)"
            50 -> "Jenderal Iblis Azazel (Ras Demonkin Tinggi)"
            55 -> "Behemoth Pemecah Bumi (Ras Beastkin Raksasa)"
            60 -> "Archon Bayangan Hampa (Ras Makhluk Dimensi)"
            // Bos Dungeon Kelas Atas (Lantai 65 - 100): HANYA RAS NAGA
            65 -> "Naga Magma Ignis Wyrm (Ras Naga Kelas Atas)"
            70 -> "Naga Es Netherfrost (Ras Naga Kelas Atas)"
            75 -> "Naga Badai Tempest Wyvern (Ras Naga Kelas Atas)"
            80 -> "Naga Hitam Abyssal Dreadwyrm (Ras Naga Kelas Atas)"
            85 -> "Naga Cahaya Bintang Astral (Ras Naga Kelas Atas)"
            90 -> "Kaisar Naga Abyssal Leviathan (Ras Naga Kelas Atas)"
            95 -> "Naga Suci Emas Aurelius (Ras Naga Kelas Atas)"
            100 -> "Raja Naga Tertinggi Bahamut Ouroboros (Ras Naga Tertinggi Lantai 100)"
            else -> if (floor >= 65) "Naga Abyssal Penjaga (Ras Naga Kelas Atas)" else "Penjaga Lantai $floor"
        }
    }

    fun generateMonsterForFloor(floor: Int): Monster {
        val isBossFloor = (floor % 5 == 0)
        val scaling = 1.0 + (floor - 1) * 0.12 + Math.pow((floor - 1) / 15.0, 1.4)
        
        if (isBossFloor) {
            val isDragon = floor >= 65
            val bossHp = (220 * scaling * 2.2).toInt()
            val bossAtk = (18 * scaling * 1.45).toInt()
            val bossDef = (8 * scaling * 1.3).toInt()
            val bossExp = (80 * scaling * 2.5).toInt()
            val bossGold = (100 * scaling * 2.0).toInt()
            val bossRace = if (isDragon) "Ras Naga (Bos Kelas Atas)" else "Monster Penjaga"
            val bossDesc = if (isDragon) {
                "PENGUASA RAS NAGA KELAS ATAS! Entitas naga purba terkuat di puncak Abyss Lantai $floor yang menjaga relik sakral dungeon!"
            } else {
                "Penguasa penjaga checkpoint lantai $floor. Kalahkan dia untuk membuka jalan ke checkpoint!"
            }

            return Monster(
                name = "[BOSS] " + getFloorBossName(floor),
                maxHp = bossHp,
                currentHp = bossHp,
                atk = bossAtk,
                def = bossDef,
                expReward = bossExp,
                goldReward = bossGold,
                isBoss = true,
                floor = floor,
                description = bossDesc,
                race = bossRace,
                isDragonBoss = isDragon
            )
        }

        val monsterTypes = listOf(
            "Goblin Pengintai", "Kelelawar Darah", "Prajurit Kerangka", "Serigala Berduri",
            "Mantis Raksasa", "Mumi Terkutuk", "Ksatria Bayangan", "Penyihir Sesat",
            "Kalajengking Magma", "Orc Pembawa Gada", "Gargoyle Batu", "Roh Api Liar"
        )
        val name = monsterTypes[(floor - 1) % monsterTypes.size] + " (Lt. $floor)"
        val hp = (80 * scaling).toInt()
        val atk = (14 * scaling).toInt()
        val def = (5 * scaling).toInt()
        val exp = (35 * scaling).toInt()
        val gold = (25 * scaling).toInt()

        return Monster(
            name = name,
            maxHp = hp,
            currentHp = hp,
            atk = atk,
            def = def,
            expReward = exp,
            goldReward = gold,
            isBoss = false,
            floor = floor,
            description = "Monster penghuni lantai $floor dungeon."
        )
    }

    fun getSwordsmanSkills(): List<Skill> {
        return listOf(
            Skill(
                id = "sword_slash",
                name = "Tebasan Baja",
                mpCost = 12,
                powerMultiplier = 1.45f,
                description = "Tebasan pedang yang menghantam titik lemah musuh."
            ),
            Skill(
                id = "sword_whirlwind",
                name = "Putaran Pedang Badai",
                mpCost = 25,
                powerMultiplier = 2.1f,
                description = "Berputar cepat menebas berkali-kali dengan kecepatan tinggi."
            ),
            Skill(
                id = "sword_iron_aegis",
                name = "Benteng Besi",
                mpCost = 20,
                powerMultiplier = 0.5f,
                description = "Mengangkat perisai suci, memulihkan 40 HP dan mengurangi 50% damage berikutnya.",
                effectType = "SHIELD"
            ),
            Skill(
                id = "sword_dragon_blade",
                name = "Tebasan Naga Pamungkas",
                mpCost = 45,
                powerMultiplier = 3.4f,
                description = "Jurusan pamungkas ksatria: memanggil aura naga emas untuk tebasan fatal!",
                isUltimate = true
            )
        )
    }

    fun getMageSkills(): List<Skill> {
        return listOf(
            Skill(
                id = "mage_fireball",
                name = "Bola Api Neraka",
                mpCost = 15,
                powerMultiplier = 1.6f,
                description = "Menembakkan bola api berkobar yang membakar musuh."
            ),
            Skill(
                id = "mage_blizzard",
                name = "Badai Es Beku",
                mpCost = 28,
                powerMultiplier = 2.2f,
                description = "Membekukan monster dengan kristal es tajam dan menurunkan pertahanan musuh."
            ),
            Skill(
                id = "mage_arcane_heal",
                name = "Pemulihan Jiwa Arcana",
                mpCost = 22,
                powerMultiplier = 0.8f,
                description = "Memusatkan mana murni untuk memulihkan 65 HP karakter & party.",
                effectType = "HEAL"
            ),
            Skill(
                id = "mage_meteor",
                name = "Kiamat Meteor Kosmik",
                mpCost = 50,
                powerMultiplier = 3.6f,
                description = "Mantra pemusnah terdahsyat: menjatuhkan meteor kosmik dari langit!",
                isUltimate = true
            )
        )
    }
}
