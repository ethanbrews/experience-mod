package me.ethanbrews.experience.blocks

import me.ethanbrews.experience.items.ExperienceItemBase
import me.ethanbrews.experience.items.ExperienceSimpleItem
import net.minecraft.block.Block

abstract class ExperienceBlockBase(settings: Settings) : Block(settings) {
    abstract val id: String

    val item : ExperienceItemBase
        get() = ExperienceSimpleItem(id)
}