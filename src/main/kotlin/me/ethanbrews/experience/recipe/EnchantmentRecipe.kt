package me.ethanbrews.experience.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import me.ethanbrews.experience.logger
import me.ethanbrews.experience.modid
import me.ethanbrews.experience.utility.ExperienceHelper
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

class EnchantmentRecipe {
    @Json(name = "items")
    val recipeItems: List<InputItem> = listOf()

    @Json(name = "result", ignored = false)
    private val resultIdentifier: String? = null

    @Json(name = "experience", ignored = false)
    private val xpString: String? = null

    @Json(ignored = true)
    val result: Enchantment? get() {
        return Registry.ENCHANTMENT[Identifier(resultIdentifier)]
    }

    fun getExperienceCost(playerLevels: Int): Int {
        return xpString?.let {
            if (it.endsWith('l', true)) {
                ExperienceHelper.getExperienceCost(
                    it.slice(0 until it.length - 1).toInt(),
                    playerLevels
                )
            } else {
                it.toInt()
            }
        } ?: 0
    }

    fun validate(stacks: List<ItemStack>): Boolean {
        // The table produced is (recipeItems.matches) x (stacks)
        // Recipe is valid iff all rows contain at least one true and all columns contain at least one true
        val table = stacks.map { stack ->
            recipeItems.map { test -> test.match(stack) }
        }
        return table.all { row ->
            row.any { it }
        } && (table.indices).all { index ->
            table.map { row ->
                row[index]
            }.any { it }
        }
    }

    companion object {

        val recipes: List<EnchantmentRecipe>

        private fun onEachResource(path: String, action: (File) -> Unit) {

            fun resource2file(path: String): File {
                val resourceURL = object {}.javaClass.getResource(path)
                return File(checkNotNull(resourceURL) { "Path not found: '$path'" }.file)
            }

            with(resource2file(path)) {
                this.walk().forEach { f -> if (this != f) action(f) }
            }
        }

        init {
            val k = Klaxon()
            val files = mutableListOf<File>()
            onEachResource("/data/$modid/enchantment_recipe/") { files.add(it) }
            recipes = files.mapNotNull {
                val result = k.parse<EnchantmentRecipe>(it)
                if (result?.result == null)
                    logger.warn("The enchantment recipe, ${it.name}, is invalid or wishes to enchant with a non-existent enchantment. It will not be loaded!")
                result
            }
        }

    }
}