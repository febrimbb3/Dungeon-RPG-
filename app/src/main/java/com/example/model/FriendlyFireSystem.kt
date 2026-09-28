package com.example.model

sealed class CombatTargetEntity {
    data class MonsterTarget(
        val id: String,
        val name: String,
        val hp: Int,
        val isBoss: Boolean = false
    ) : CombatTargetEntity()

    data class PartyMemberTarget(
        val memberId: Long,
        val name: String,
        val role: String,
        val hp: Int,
        val isLeader: Boolean = false
    ) : CombatTargetEntity()
}

sealed class FriendlyFireValidationResult {
    data class DamagePermitted(
        val targetName: String,
        val finalDamage: Int,
        val message: String
    ) : FriendlyFireValidationResult()

    data class BlockedByFriendlyFireProtection(
        val allyName: String,
        val attemptedDamage: Int,
        val shieldEffect: String = "🛡️ Perisai Suci Guild Abyss",
        val logMessage: String = "Serangan mengenai area rekan [ $allyName ], tetapi sistem Friendly Fire memblokir 100% luka! Rekan tidak terluka!"
    ) : FriendlyFireValidationResult()
}

/**
 * Friendly Fire Prevention System for Dungeon Exploration
 * Under Guild Dungeon Law, party members cannot damage one another under any circumstance.
 * All AoE spells, sweeping slashes, and magic bursts automatically filter out allies.
 */
object FriendlyFireSystem {
    const val IS_PROTECTION_ACTIVE: Boolean = true

    /**
     * Evaluates damage intended for an entity in combat.
     * Returns BlockedByFriendlyFireProtection if target is an ally or party member.
     */
    fun processAttackOnTarget(
        attackerName: String,
        target: CombatTargetEntity,
        rawDamage: Int
    ): FriendlyFireValidationResult {
        return when (target) {
            is CombatTargetEntity.PartyMemberTarget -> {
                // Friendly fire strictly prohibited!
                FriendlyFireValidationResult.BlockedByFriendlyFireProtection(
                    allyName = target.name,
                    attemptedDamage = rawDamage
                )
            }
            is CombatTargetEntity.MonsterTarget -> {
                // Hostile dungeon entity, damage allowed
                FriendlyFireValidationResult.DamagePermitted(
                    targetName = target.name,
                    finalDamage = rawDamage,
                    message = "$attackerName menghantam ${target.name} sebesar $rawDamage damage!"
                )
            }
        }
    }

    /**
     * Filters an Area of Effect (AoE) blast to ensure party members take zero friendly fire.
     */
    fun filterSafeAoETargets(
        targetsInBlastRadius: List<CombatTargetEntity>
    ): Pair<List<CombatTargetEntity.MonsterTarget>, List<CombatTargetEntity.PartyMemberTarget>> {
        val monstersToDamage = mutableListOf<CombatTargetEntity.MonsterTarget>()
        val protectedAllies = mutableListOf<CombatTargetEntity.PartyMemberTarget>()

        for (target in targetsInBlastRadius) {
            when (target) {
                is CombatTargetEntity.MonsterTarget -> monstersToDamage.add(target)
                is CombatTargetEntity.PartyMemberTarget -> protectedAllies.add(target)
            }
        }

        return Pair(monstersToDamage, protectedAllies)
    }
}
