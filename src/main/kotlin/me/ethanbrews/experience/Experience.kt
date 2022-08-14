package me.ethanbrews.experience

import me.ethanbrews.experience.registry.BlockRegistry
import me.ethanbrews.experience.registry.ItemRegistry
import org.apache.logging.log4j.LogManager

val modid = "experience"
val logger = LogManager.getLogger(modid)

fun init() {
    logger.info("Loading $modid")
    BlockRegistry.register()
    ItemRegistry.register()
}