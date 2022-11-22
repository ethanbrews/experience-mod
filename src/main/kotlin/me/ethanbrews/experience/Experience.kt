package me.ethanbrews.experience

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.ethanbrews.experience.registry.BlockRegistry
import me.ethanbrews.experience.registry.BlockRenderers
import me.ethanbrews.experience.registry.ItemRegistry
import me.ethanbrews.experience.test.InGameTests
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.ServerCommandSource
import org.apache.logging.log4j.LogManager

val modid = "experience"
val logger = LogManager.getLogger(modid)

fun init() {
    logger.info("Loading $modid")
    ItemRegistry.register()
    BlockRegistry.register()

    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        dispatcher.register(LiteralArgumentBuilder.literal<ServerCommandSource?>("runTests").executes {
            if (InGameTests.runAll()) 0 else 1
        })
    }
}

fun client() {
    BlockRenderers.register()
}