package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.blocks.EBlock
import me.ethanbrews.experience.blocks.IEBlock
import me.ethanbrews.experience.items.IEItem
import net.minecraft.item.BlockItem

class SentientStandBlockItem(block: SentientStand, settings: Settings) : BlockItem(block, settings), IEItem {
    override val id: String
        get() = (block as IEBlock).id
}