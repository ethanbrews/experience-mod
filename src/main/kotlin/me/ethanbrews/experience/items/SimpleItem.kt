package me.ethanbrews.experience.items

import me.ethanbrews.experience.registry.ItemRegistry

class SimpleItem(override val id: String) : EItem(ItemRegistry.defaultItemSettings)