package me.ethanbrews.experience.recipe

import com.beust.klaxon.Json
import java.util.Dictionary

data class EnchantmentRecipeData (
    @Json(name="type")
    val type: String,

    @Json(name="group")
    val group: String,

    @Json(name="pattern")
    val pattern: List<String>?,

    @Json(name="key")
    val key: Dictionary<String, ItemDescriptor>,

    @Json(name="enchantment")
    val result: String
)

data class ItemDescriptor (
    @Json(name="item")
    public val item: String?,

    @Json(name="tag")
    public val tag: String?,

    @Json(name="enchantment")
    public val enchantment: String?,
)