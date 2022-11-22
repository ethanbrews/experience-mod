package me.ethanbrews.experience.test

import me.ethanbrews.experience.recipe.EnchantmentRecipe
import me.ethanbrews.experience.recipe.EnchantmentRecipeData
import me.ethanbrews.experience.recipe.ItemDescriptor
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.Dictionary
import java.util.Hashtable

object InGameTests {
    fun runAll(): Boolean {
        return testRecipeLoading()
    }

    fun testRecipeLoading(): Boolean {
        val dct: Dictionary<String, ItemDescriptor> = Hashtable()
        val descriptor = ItemDescriptor(item="minecraft:obsidian", tag=null, enchantment = "minecraft:sharpness")
        dct.put("O", descriptor)
        val recipeData = EnchantmentRecipeData(type="enchantment:enchanting", group = "enchantment:enchanting", pattern = listOf(" O ", "O O", " O "), key = dct, result = "minecraft:sharpness")
        val er = EnchantmentRecipe(recipeData)

        val item = ItemStack(Items.DIAMOND_SWORD)
        item.addEnchantment(Registry.ENCHANTMENT.get(Identifier("minecraft", "sharpness")), 1)

        er.testItemStack(descriptor, item)

        return true
    }
}