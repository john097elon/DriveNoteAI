package com.drivenote.app.data.db.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TagsConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromTags(tags: List<String>): String = json.encodeToString(tags)

    @TypeConverter
    fun toTags(value: String): List<String> {
        return runCatching { json.decodeFromString<List<String>>(value) }.getOrDefault(emptyList())
    }
}
