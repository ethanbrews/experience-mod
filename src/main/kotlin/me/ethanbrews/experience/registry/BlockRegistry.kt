package me.ethanbrews.experience.registry

import me.ethanbrews.experience.blocks.SimpleBlock
import me.ethanbrews.experience.blocks.sentientStand.SentientStand
import me.ethanbrews.experience.blocks.sentientStand.SentientStandEntity
import me.ethanbrews.experience.utility.id
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

object BlockRegistry {

    val sentient_stone = SimpleBlock("sentient_stone")
    val sentient_stand = SentientStand()
    lateinit var sentient_stand_entity: BlockEntityType<SentientStandEntity>

    fun register() {
        listOf(sentient_stone, sentient_stand).forEach {
            Registry.register(Registry.BLOCK, id(it.id), it)
        }

        sentient_stand_entity = FabricBlockEntityTypeBuilder.create({ x: BlockPos, y: BlockState -> SentientStandEntity(x, y)}, sentient_stand).build()

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("sentient_stand_entity"), sentient_stand_entity)
    }
}