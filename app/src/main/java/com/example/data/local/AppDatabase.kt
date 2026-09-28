package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ArtifactDao
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.InventoryDao
import com.example.data.local.dao.LeaderboardDao
import com.example.data.local.dao.PartyMemberDao
import com.example.data.local.dao.WeaponDao
import com.example.data.local.entity.ArtifactEntity
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.InventoryItemEntity
import com.example.data.local.entity.LeaderboardEntity
import com.example.data.local.entity.PartyMemberEntity
import com.example.data.local.entity.WeaponEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CharacterEntity::class,
        WeaponEntity::class,
        InventoryItemEntity::class,
        PartyMemberEntity::class,
        LeaderboardEntity::class,
        ArtifactEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao
    abstract fun weaponDao(): WeaponDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun partyMemberDao(): PartyMemberDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun artifactDao(): ArtifactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dungeon_100_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedInitialData(database)
                    }
                }
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            val partyDao = database.partyMemberDao()
            partyDao.insertMembers(
                listOf(
                    PartyMemberEntity(
                        name = "Aria si Pendekar Kilat",
                        role = "PEDANG",
                        level = 1,
                        hp = 95,
                        maxHp = 95,
                        atk = 19,
                        def = 8,
                        skillName = "Tebasan Cepat",
                        skillDesc = "Menyerang musuh dengan tebasan kilat beruntun (+35% DMG)",
                        isRecruited = false,
                        isInParty = false,
                        recruitCost = 150
                    ),
                    PartyMemberEntity(
                        name = "Eldrin Penyihir Badai",
                        role = "SIHIR",
                        level = 1,
                        hp = 80,
                        maxHp = 80,
                        atk = 24,
                        def = 6,
                        skillName = "Petir Arcana",
                        skillDesc = "Menyambar musuh dengan badai sihir listrik (+50% MATK)",
                        isRecruited = false,
                        isInParty = false,
                        recruitCost = 180
                    ),
                    PartyMemberEntity(
                        name = "Lyra sang Tabib Suci",
                        role = "TABIB",
                        level = 1,
                        hp = 90,
                        maxHp = 90,
                        atk = 12,
                        def = 10,
                        skillName = "Doa Pemulihan",
                        skillDesc = "Memulihkan 45 HP untuk pahlawan utama & party",
                        isRecruited = false,
                        isInParty = false,
                        recruitCost = 200
                    ),
                    PartyMemberEntity(
                        name = "Garrick si Ksatria Perisai",
                        role = "TANK",
                        level = 1,
                        hp = 140,
                        maxHp = 140,
                        atk = 15,
                        def = 18,
                        skillName = "Tembok Besi",
                        skillDesc = "Memasang perisai baja pelindung dan mengurangi damage musuh",
                        isRecruited = false,
                        isInParty = false,
                        recruitCost = 250
                    )
                )
            )

            // Seed Guild Rivals on Leaderboard
            val leaderboardDao = database.leaderboardDao()
            leaderboardDao.insertAll(
                listOf(
                    LeaderboardEntity(
                        rankNumber = 1,
                        name = "Zephyr sang Penakluk Abyss",
                        className = "PEDANG",
                        level = 98,
                        floorReached = 97,
                        score = 295000,
                        title = "Pahlawan Legendaris SS"
                    ),
                    LeaderboardEntity(
                        rankNumber = 2,
                        name = "Archmage Selena",
                        className = "SIHIR",
                        level = 94,
                        floorReached = 92,
                        score = 265000,
                        title = "Penyihir Bintang S"
                    ),
                    LeaderboardEntity(
                        rankNumber = 3,
                        name = "Valerius Bilah Hitam",
                        className = "PEDANG",
                        level = 88,
                        floorReached = 85,
                        score = 220000,
                        title = "Pendekar Rank S"
                    ),
                    LeaderboardEntity(
                        rankNumber = 4,
                        name = "Morrigan Ratu Petir",
                        className = "SIHIR",
                        level = 81,
                        floorReached = 79,
                        score = 195000,
                        title = "Penyihir Rank A"
                    ),
                    LeaderboardEntity(
                        rankNumber = 5,
                        name = "Kael Pembantai Naga",
                        className = "PEDANG",
                        level = 75,
                        floorReached = 72,
                        score = 170000,
                        title = "Pendekar Rank A"
                    ),
                    LeaderboardEntity(
                        rankNumber = 6,
                        name = "Ignis Penyulut Api",
                        className = "SIHIR",
                        level = 68,
                        floorReached = 64,
                        score = 142000,
                        title = "Penyihir Rank B"
                    ),
                    LeaderboardEntity(
                        rankNumber = 7,
                        name = "Boran si Penjaga Gerbang",
                        className = "PEDANG",
                        level = 58,
                        floorReached = 55,
                        score = 115000,
                        title = "Pendekar Rank B"
                    ),
                    LeaderboardEntity(
                        rankNumber = 8,
                        name = "Luna si Ahli Mantra",
                        className = "SIHIR",
                        level = 45,
                        floorReached = 42,
                        score = 85000,
                        title = "Penyihir Rank C"
                    ),
                    LeaderboardEntity(
                        rankNumber = 9,
                        name = "Doran si Pemburu Goblin",
                        className = "PEDANG",
                        level = 32,
                        floorReached = 30,
                        score = 54000,
                        title = "Petualang Rank D"
                    ),
                    LeaderboardEntity(
                        rankNumber = 10,
                        name = "Elise si Pelajar Sihir",
                        className = "SIHIR",
                        level = 20,
                        floorReached = 18,
                        score = 28000,
                        title = "Pemula Rank E"
                    )
                )
            )
        }
    }
}
