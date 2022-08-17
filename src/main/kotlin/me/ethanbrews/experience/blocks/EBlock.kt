package me.ethanbrews.experience.blocks

import me.ethanbrews.experience.items.SimpleBlockItem
import me.ethanbrews.experience.registry.ItemRegistry.defaultItemSettings
import net.minecraft.block.Block

abstract class EBlock(settings: Settings) : Block(settings), IEBlock {
    override val item : SimpleBlockItem
        get() = SimpleBlockItem(this, defaultItemSettings)
}