package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.extension.blockPosFromArray
import me.ethanbrews.experience.extension.toList
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
    var enchantment_id: Identifier
) {

    var initialTickCounter: Int = tickCounter

    fun getPlayer(world: World): PlayerEntity? {
        return world.getPlayerByUuid(UUID.fromString(playerUuid))
    }

    val enchantment: Enchantment?
        get() { return Registry.ENCHANTMENT[enchantment_id] }

    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putIntArray("masterPos", masterPos.toList())
        nbt.putInt("tickCounter", tickCounter)
        nbt.putInt("iTickCounter", initialTickCounter)
        playerUuid?.let { nbt.putString("playerUuid", it) }
        nbt.putString("enchId", enchantment_id.toString())
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
                blockPosFromArray(nbt.getIntArray("masterPos")),
                nbt.getInt("tickCounter"),
                Identifier(nbt.getString("enchId"))
            )
            e.initialTickCounter = nbt.getInt("iTickCounter")
            return e
        }
    }
}