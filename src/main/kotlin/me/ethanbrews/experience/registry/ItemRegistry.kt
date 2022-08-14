package me.ethanbrews.experience.registry

import me.ethanbrews.experience.items.ExperienceItemBase
import me.ethanbrews.experience.items.ExperienceSimpleItem
import me.ethanbrews.experience.items.SentientStaff
import me.ethanbrews.experience.items.SentientStick
import me.ethanbrews.experience.utility.id
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.*
import net.minecraft.util.registry.Registry

object ItemRegistry {
    val item_group: ItemGroup = FabricItemGroupBuilder.create(id("general"))
        .icon { ItemStack(Items.DIAMOND_BLOCK) }
        .build()

    val defaultItemSettings = Item.Settings().group(item_group)

    val sentientStick = SentientStick()
    val sentientStaff = SentientStaff()
    val brain = ExperienceSimpleItem("brain")

    fun register() {
        listOf(sentientStaff, brain, BlockRegistry.sentient_stone.item, sentientStick).forEach {
            Registry.register(Registry.ITEM, id(it.id), it)
        }
    }
}