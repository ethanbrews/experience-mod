package me.ethanbrews.experience

import me.ethanbrews.experience.recipe.SentientRitualRecipe
import me.ethanbrews.experience.registry.BlockRegistry
import me.ethanbrews.experience.registry.BlockRenderers
import me.ethanbrews.experience.registry.ItemRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val modid = "experience"
val logger: Logger = LogManager.getLogger(modid)

fun init() {
    logger.info("Loading $modid")
    ItemRegistry.register()
    BlockRegistry.register()
    logger.info("Loaded ${SentientRitualRecipe.recipes.size} enchantment recipes")
}

fun client() {
    BlockRenderers.register()
}