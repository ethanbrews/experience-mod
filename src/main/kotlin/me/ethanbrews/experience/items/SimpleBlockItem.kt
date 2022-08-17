package me.ethanbrews.experience.items

import me.ethanbrews.experience.blocks.EBlock
import me.ethanbrews.experience.blocks.IEBlock
import net.minecraft.item.BlockItem

class SimpleBlockItem(block: EBlock, settings: Settings) : BlockItem(block, settings), IEItem {
    override val id: String
        get() = (block as EBlock).id
}