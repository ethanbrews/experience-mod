package me.ethanbrews.experience.registry

import me.ethanbrews.experience.items.SimpleItem
import me.ethanbrews.experience.items.SentientStaff
import me.ethanbrews.experience.items.SentientStick
import me.ethanbrews.experience.utility.id
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.*
import net.minecraft.util.registry.Registry

object ItemRegistry {
    private val item_group: ItemGroup = FabricItemGroupBuilder.create(id("general"))
        .icon { ItemStack(brain) }
        .build()

    val defaultItemSettings: Item.Settings = Item.Settings().group(item_group)

    private val sentientStick = SentientStick()
    private val sentientStaff = SentientStaff()
    private val brain = SimpleItem("brain")

    fun register() {
        listOf(sentientStaff, brain, BlockRegistry.sentient_stone.item, BlockRegistry.sentient_stand.item, sentientStick).forEach {
            Registry.register(Registry.ITEM, id(it.id), it as Item)
        }
    }
}