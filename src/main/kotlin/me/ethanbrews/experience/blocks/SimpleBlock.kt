package me.ethanbrews.experience.blocks

import net.minecraft.block.Material

class SimpleBlock(override val id: String) : EBlock(Settings.of(Material.STONE).hardness(4.0F))