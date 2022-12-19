package me.ethanbrews.experience.recipe

import com.beust.klaxon.Json
import me.ethanbrews.experience.error.ImpossibleException
import me.ethanbrews.experience.error.InvalidFormatException
import me.ethanbrews.experience.utility.ExperienceHelper
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class InputItem {
    @Json(name = "item", ignored = false)
    private val item: String? = null

    @Json(name = "enchantment", ignored = false)
    private val enchantment: String? = null

    @Json(name = "tag", ignored = false)
    private val tag: String? = null

    @Json(name = "consume")
    val consume: Boolean = true

    private fun validateOrThrow() {
        val all = listOf(item, enchantment, tag)
        if (all.filter { it == null }.size != 2)
            throw InvalidFormatException("Enchantment recipe data cannot declare multiple of 'item', 'enchantment', 'tag'.")

    }

    private fun matchItem(stack: ItemStack): Boolean {
        return Identifier(item) == Registry.ITEM.getId(stack.item)
    }

    private fun matchEnchantment(stack: ItemStack): Boolean {
        return EnchantmentHelper.getLevel(Registry.ENCHANTMENT[Identifier(enchantment)], stack) > 0
    }

    private fun matchTag(stack: ItemStack): Boolean {
        return stack.streamTags().anyMatch {
            it.id == Identifier(tag)
        }
    }

    fun match(stack: ItemStack): Boolean {
        validateOrThrow()
        if (item != null)
            return matchItem(stack)
        else if (enchantment != null)
            return matchEnchantment(stack)
        else if (tag != null)
            return matchTag(stack)
        throw ImpossibleException("Failure to match a valid InputItem")
    }
}