package me.ethanbrews.experience.items

import me.ethanbrews.experience.registry.ItemRegistry.defaultItemSettings
import net.minecraft.block.Material
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolMaterial
import net.minecraft.item.ToolMaterials

class SentientStick : SwordItem(ToolMaterials.WOOD, 2, 2.0f, defaultItemSettings), IItemWithId {
    override val id: String
        get() = "sentient_stick"
}