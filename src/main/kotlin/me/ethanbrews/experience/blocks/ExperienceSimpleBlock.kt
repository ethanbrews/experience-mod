package me.ethanbrews.experience.blocks

import net.minecraft.block.Material

class ExperienceSimpleBlock(override val id: String) : ExperienceBlockBase(Settings.of(Material.STONE).hardness(4.0F)) {
}