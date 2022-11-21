package me.ethanbrews.experience.recipe

import com.beust.klaxon.Json

data class EnchantmentRecipeData (
    @Json(name="pattern")
    val pattern: List<String>

    @Json(name="key")
    val key: Key,

    @Json(name="pattern")
    val result: Result
)