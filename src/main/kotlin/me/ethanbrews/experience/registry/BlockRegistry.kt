package me.ethanbrews.experience.registry

import me.ethanbrews.experience.blocks.ExperienceSimpleBlock
import me.ethanbrews.experience.utility.id
import net.minecraft.util.registry.Registry

object BlockRegistry {

    val sentient_stone = ExperienceSimpleBlock("sentient_stone")

    fun register() {
        listOf(sentient_stone).forEach {
            Registry.register(Registry.BLOCK, id(it.id), it)
        }
    }
}