package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.blocks.IEBlock
import me.ethanbrews.experience.items.IEItem
import me.ethanbrews.experience.registry.BlockRegistry
import me.ethanbrews.experience.registry.ItemRegistry.defaultItemSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SentientStand : BlockWithEntity(Settings.of(Material.STONE).hardness(4.0F)), IEBlock {
    override val id = "sentient_stand"
    override val item: IEItem
        get() = SentientStandBlockItem(this, defaultItemSettings)

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return SentientStandEntity(pos, state)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult?
    ): ActionResult {
        if(hand == Hand.OFF_HAND) {
            return ActionResult.PASS
        }
        if (!world.isClient) {
            val e = world.getBlockEntity(pos) as SentientStandEntity
            return e.playerUse(player)
        }
        return ActionResult.PASS
    }

    override fun getRenderType(state: BlockState?): BlockRenderType = BlockRenderType.MODEL

    override fun <T : BlockEntity?> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return checkType(
            type, BlockRegistry.sentient_stand_entity
        ) { world1: World, pos: BlockPos, state1: BlockState, be1: BlockEntity ->
            SentientStandEntity.tick(
                world1,
                pos,
                state1,
                be1 as SentientStandEntity
            )
        }
    }
}