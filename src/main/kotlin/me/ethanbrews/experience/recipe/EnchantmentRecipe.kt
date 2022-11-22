package me.ethanbrews.experience.recipe

import me.ethanbrews.experience.logger
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class EnchantmentRecipe(data: EnchantmentRecipeData) {
    fun testItemStack(descriptor: ItemDescriptor, stack: ItemStack): Boolean {
        descriptor.enchantment?.let {
            val enchantment = Registry.ENCHANTMENT.get(Identifier(it))
            stack.enchantments.forEach {
                logger.info(it.nbtType.toString())
            }
        }
        return true
    }
}