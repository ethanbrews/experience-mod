package me.ethanbrews.experience.utility

import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.registry.Registry
import kotlin.math.pow

typealias Points = Int
typealias Levels = Int

object ExperienceHelper {
    fun getLevels(xp: Points): Levels {
        return getLevelsCumulative(1, xp)
    }

    fun canEnchantOrUpgrade(stack: ItemStack, enchantment: Enchantment): Boolean {
        val index = stack.enchantments.indexOfFirst { (it as NbtCompound).getString("id") == Registry.ENCHANTMENT.getId(enchantment).toString() }
        val correctItem = enchantment.isAcceptableItem(stack)
        val isMaxEnchant = (index >= 0 && (stack.enchantments[index] as NbtCompound).getInt("lvl") >= enchantment.maxLevel)
        return correctItem && !isMaxEnchant
    }

    private fun getLevelsCumulative(acc: Levels, remainingXp: Points): Levels {
        val pointsForNextLevel = getExperience(acc + 1)
        return if (remainingXp >= pointsForNextLevel) {
            getLevelsCumulative(acc + 1, remainingXp - pointsForNextLevel)
        } else {
            acc
        }
    }


    fun getExperience(level: Levels): Points {
        return if (level in 1..16) {
            (level.toDouble().pow(2.0) + 6 * level).toInt()
        } else if (level in 17..31) {
            (2.5 * level.toDouble().pow(2.0) - 40.5 * level + 360).toInt()
        } else if (level >= 32) {
            (4.5 * level.toDouble().pow(2.0) - 162.5 * level + 2220).toInt()
        } else {
            0
        }
    }

    fun getExperienceCost(cost: Levels, totalLevels: Levels): Points {
        return getExperience(totalLevels) - getExperience(totalLevels - cost)
    }
}

fun PlayerEntity.getTotalExperiencePoints(): Int {
    return ExperienceHelper.getExperience(this.experienceLevel) + this.totalExperience
}

fun PlayerEntity.setTotalExperiencePoints(value: Int) {
    this.experienceLevel = ExperienceHelper.getLevels(value)
    this.totalExperience = value - ExperienceHelper.getExperience(this.experienceLevel)
}