package me.ethanbrews.experience.items

import me.ethanbrews.experience.registry.ItemRegistry.defaultItemSettings
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolMaterials

class SentientStick : SwordItem(ToolMaterials.WOOD, 2, 2.0f, defaultItemSettings), IEItem {
    override val id = "sentient_stick"
}