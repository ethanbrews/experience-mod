package me.ethanbrews.experience.utility

import me.ethanbrews.experience.modid
import net.minecraft.item.Item
import net.minecraft.util.Identifier

fun id(name: String): Identifier = Identifier(modid, name)