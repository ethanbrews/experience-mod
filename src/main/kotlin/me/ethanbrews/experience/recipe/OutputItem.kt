package me.ethanbrews.experience.recipe

import com.beust.klaxon.Json
import me.ethanbrews.experience.error.InvalidFormatException
import me.ethanbrews.experience.utility.ExperienceHelper
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class OutputItem : RecipeComponent() {
    @Json(name = "item", ignored = false)
    private val itemName: String? = null

    @Json(name = "enchantment", ignored = false)
    private val enchantmentName: String? = null

    fun canBeAppliedTo(stack: ItemStack): Boolean {
        return if (enchantmentName != null) {
            ExperienceHelper.canEnchantOrUpgrade(stack, Registry.ENCHANTMENT[Identifier(enchantmentName)]!!)
        } else {
            true
        }
    }

    fun getOutput(stack: ItemStack): ItemStack {
        validateConfigurationOrThrow()
        return if (enchantmentName != null) {
            val enchantment = Registry.ENCHANTMENT[Identifier(enchantmentName)]
            val currentEnchantments = EnchantmentHelper.get(stack)
            if (currentEnchantments.containsKey(enchantment)) {
                currentEnchantments[enchantment] = currentEnchantments[enchantment]!! + 1
            } else {
                currentEnchantments[enchantment] = 1
            }
            EnchantmentHelper.set(currentEnchantments, stack)
            stack
        } else {
            val item = Registry.ITEM.get(Identifier(itemName))
            ItemStack(item)
        }
    }

    override fun getConfigurationFailureReason(): String? {
        return if (listOfNotNull(itemName, enchantmentName).size != 1)
            "Enchantment recipe data cannot declare multiple of 'item', 'enchantment'."
        else
            null
    }
}