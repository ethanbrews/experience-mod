package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.blocks.EBlock
import me.ethanbrews.experience.blocks.IEBlock
import me.ethanbrews.experience.items.IEItem
import me.ethanbrews.experience.items.SimpleBlockItem
import me.ethanbrews.experience.items.SimpleItem
import me.ethanbrews.experience.registry.BlockRegistry
import me.ethanbrews.experience.registry.ItemRegistry
import me.ethanbrews.experience.registry.ItemRegistry.defaultItemSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SentientStand : BlockWithEntity(Settings.of(Material.STONE).hardness(4.0F)), IEBlock {
    override val id = "sentient_stand"
    override val item: IEItem
        get() = SentientStandBlockItem(this, defaultItemSettings)

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return SentientStandEntity(pos, state)
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