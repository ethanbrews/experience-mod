package me.ethanbrews.experience.recipe

import me.ethanbrews.experience.error.InvalidFormatException

abstract class RecipeComponent {
    abstract fun getConfigurationFailureReason(): String?

    fun isConfigurationValid(): Boolean = getConfigurationFailureReason() == null

    fun validateConfigurationOrThrow() {
        getConfigurationFailureReason()?.let {
            throw InvalidFormatException(it)
        }
    }
}