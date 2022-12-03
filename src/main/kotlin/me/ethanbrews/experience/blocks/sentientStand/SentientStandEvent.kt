package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.extension.blockPosFromArray
import me.ethanbrews.experience.extension.toList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

class SentientStandEvent(
    var player: PlayerEntity?,
    var masterPos: BlockPos,
    var tickCounter: Int,
) {
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putIntArray("masterPos", masterPos.toList())
        nbt.putInt("tickCounter", tickCounter)
        return nbt
    }

    companion object {
        fun fromNbt(nbt: NbtCompound): SentientStandEvent {
            return SentientStandEvent(
                null,
                blockPosFromArray(nbt.getIntArray("masterPos")),
                nbt.getInt("tickCounter")
            )
        }
    }
}