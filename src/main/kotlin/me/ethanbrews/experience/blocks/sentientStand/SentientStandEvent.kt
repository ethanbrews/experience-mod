package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.recipe.SentientRitualRecipe
import me.ethanbrews.experience.utility.BlockPosHelper
import me.ethanbrews.experience.utility.toList
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*

class SentientStandEvent(
    var playerUuid: String?,
    var masterPos: BlockPos,
    var tickCounter: Int,
    var recipe: SentientRitualRecipe?
) {

    var initialTickCounter: Int = tickCounter

    fun getPlayer(world: World): PlayerEntity? {
        return world.getPlayerByUuid(UUID.fromString(playerUuid))
    }

    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putIntArray("masterPos", masterPos.toList())
        nbt.putInt("tickCounter", tickCounter)
        nbt.putInt("iTickCounter", initialTickCounter)
        playerUuid?.let { nbt.putString("playerUuid", it) }
        return nbt
    }

    companion object {
        fun fromNbt(nbt: NbtCompound): SentientStandEvent {
            val playerUuid: String? = if (nbt.contains("playerUuid"))
                nbt.getString("playerUuid")
            else
                null
            val e = SentientStandEvent(
                playerUuid,
                BlockPosHelper.blockPosFromArray(nbt.getIntArray("masterPos")),
                nbt.getInt("tickCounter"),
                null
            )
            e.initialTickCounter = nbt.getInt("iTickCounter")
            return e
        }
    }
}