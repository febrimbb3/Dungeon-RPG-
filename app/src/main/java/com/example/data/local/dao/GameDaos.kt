package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ArtifactEntity
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.InventoryItemEntity
import com.example.data.local.entity.LeaderboardEntity
import com.example.data.local.entity.PartyMemberEntity
import com.example.data.local.entity.WeaponEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM character WHERE id = 1 LIMIT 1")
    fun getCharacter(): Flow<CharacterEntity?>

    @Query("SELECT * FROM character WHERE id = 1 LIMIT 1")
    suspend fun getCharacterOnce(): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(character: CharacterEntity)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("UPDATE character SET currentHp = :hp, currentMp = :mp WHERE id = 1")
    suspend fun updateHpMp(hp: Int, mp: Int)

    @Query("UPDATE character SET currentFloor = :floor, maxFloorReached = MAX(maxFloorReached, :floor) WHERE id = 1")
    suspend fun updateFloor(floor: Int)

    @Query("UPDATE character SET tutorialCompleted = 1 WHERE id = 1")
    suspend fun completeTutorial()

    @Query("DELETE FROM character")
    suspend fun deleteAll()
}

@Dao
interface WeaponDao {
    @Query("SELECT * FROM weapons ORDER BY tier DESC, upgradeLevel DESC")
    fun getAllWeapons(): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons WHERE isEquipped = 1 LIMIT 1")
    fun getEquippedWeapon(): Flow<WeaponEntity?>

    @Query("SELECT * FROM weapons WHERE isEquipped = 1 LIMIT 1")
    suspend fun getEquippedWeaponOnce(): WeaponEntity?

    @Query("SELECT * FROM weapons WHERE id = :id LIMIT 1")
    suspend fun getWeaponById(id: Long): WeaponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapon(weapon: WeaponEntity): Long

    @Update
    suspend fun updateWeapon(weapon: WeaponEntity)

    @Query("UPDATE weapons SET isEquipped = 0")
    suspend fun unequipAll()

    @Query("UPDATE weapons SET isEquipped = 1 WHERE id = :id")
    suspend fun equipWeapon(id: Long)

    @Query("DELETE FROM weapons WHERE id = :id")
    suspend fun deleteWeapon(id: Long)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY itemType, name")
    fun getAllInventory(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory WHERE itemId = :itemId LIMIT 1")
    suspend fun getItem(itemId: String): InventoryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: InventoryItemEntity)

    @Update
    suspend fun updateItem(item: InventoryItemEntity)

    @Query("DELETE FROM inventory WHERE id = :id")
    suspend fun deleteItem(id: Long)
}

@Dao
interface PartyMemberDao {
    @Query("SELECT * FROM party_members")
    fun getAllMembers(): Flow<List<PartyMemberEntity>>

    @Query("SELECT * FROM party_members WHERE isInParty = 1")
    fun getActiveParty(): Flow<List<PartyMemberEntity>>

    @Query("SELECT * FROM party_members WHERE isInParty = 1")
    suspend fun getActivePartyOnce(): List<PartyMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<PartyMemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: PartyMemberEntity)

    @Delete
    suspend fun deleteMember(member: PartyMemberEntity)

    @Update
    suspend fun updateMember(member: PartyMemberEntity)
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY floorReached DESC, level DESC, score DESC")
    fun getAllLeaderboard(): Flow<List<LeaderboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<LeaderboardEntity>)

    @Query("DELETE FROM leaderboard WHERE isPlayer = 1")
    suspend fun deletePlayerEntry()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LeaderboardEntity)
}

@Dao
interface ArtifactDao {
    @Query("SELECT * FROM artifacts ORDER BY slotNumber ASC")
    fun getAllArtifacts(): Flow<List<ArtifactEntity>>

    @Query("SELECT * FROM artifacts WHERE isStolen = 0 ORDER BY slotNumber ASC")
    fun getActiveArtifacts(): Flow<List<ArtifactEntity>>

    @Query("SELECT * FROM artifacts WHERE isStolen = 0")
    suspend fun getActiveArtifactsOnce(): List<ArtifactEntity>

    @Query("SELECT * FROM artifacts WHERE artifactId = :id LIMIT 1")
    suspend fun getArtifactById(id: String): ArtifactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(artifact: ArtifactEntity)

    @Query("UPDATE artifacts SET isStolen = 1, stolenByRivalName = :rivalName WHERE artifactId = :id")
    suspend fun markArtifactStolen(id: String, rivalName: String)

    @Query("UPDATE artifacts SET isStolen = 0, stolenByRivalName = NULL WHERE artifactId = :id")
    suspend fun reclaimArtifact(id: String)
}

