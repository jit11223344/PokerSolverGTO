package com.example.pokersolverGTO.trainer.data

import androidx.room.TypeConverter
import com.example.pokersolverGTO.trainer.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromPokerPosition(value: PokerPosition): String = value.name

    @TypeConverter
    fun toPokerPosition(value: String): PokerPosition = PokerPosition.valueOf(value)

    @TypeConverter
    fun fromStreet(value: Street): String = value.name

    @TypeConverter
    fun toStreet(value: String): Street = Street.valueOf(value)

    @TypeConverter
    fun fromPokerAction(value: PokerAction): String = value.name

    @TypeConverter
    fun toPokerAction(value: String): PokerAction = PokerAction.valueOf(value)

    @TypeConverter
    fun fromTrainingMode(value: TrainingMode): String = value.name

    @TypeConverter
    fun toTrainingMode(value: String): TrainingMode = TrainingMode.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}

