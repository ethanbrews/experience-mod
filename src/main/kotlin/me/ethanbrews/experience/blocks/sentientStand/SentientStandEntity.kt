package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.registry.BlockRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World

class SentientStandEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockRegistry.sentient_stand_entity, pos, state) {
    private var _inventory = SimpleInventory(1)
    private var _event: SentientStandEvent? = null

    var stack: ItemStack
        get() = _inventory.getStack(0)
        set(value) {
            _inventory.setStack(0, value)
        }

    /**
     * Check the structure is valid. This must be the center block.
     * A structure is valid if it contains all tier 1 blocks or all tier 1 and 2 blocks.
     * Structures are NOT valid if a tier is partially present.
     */
    private fun isValidStructure(world: World): Boolean {
        fun checkIt(it: Vec3i): Boolean = world.getBlockEntity(this.pos.add(it)) is SentientStandEntity
        return tier1Positions.all {checkIt(it) } &&
        (
            (!tier2Positions.any { checkIt(it) }) ||
            tier2Positions.all { checkIt(it) }
        )
    }

    fun trigger(sender: Entity) {
        val event = SentientStandEvent(
            sender=sender
        )
    }

    fun tick(world: World) {
    }

    companion object {
        val tier1Positions = listOf(
            Vec3i(-2, 0, 0),
            Vec3i(2, 0, 0),
            Vec3i(0, 0, 2),
            Vec3i(0, 0, -2)
        )

        val tier2Positions = listOf(
            Vec3i(-2, 0, -2),
            Vec3i(-2, 0, 2),
            Vec3i(2, 0, -2),
            Vec3i(2, 0, 2)
        )

        fun tick(world: World, pos: BlockPos, state: BlockState, be: SentientStandEntity) {
            be.tick(world)
        }
    }
}