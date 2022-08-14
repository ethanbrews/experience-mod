package me.ethanbrews.experience.items

import me.ethanbrews.experience.blocks.ExperienceBlockBase
import net.minecraft.item.BlockItem

class ExperienceBlockItem(override val id: String, block: ExperienceBlockBase, settings: Settings) : BlockItem(block, settings), IItemWithId {
}