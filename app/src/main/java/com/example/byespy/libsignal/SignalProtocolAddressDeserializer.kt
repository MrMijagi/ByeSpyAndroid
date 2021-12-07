package com.example.byespy.libsignal

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.whispersystems.libsignal.SignalProtocolAddress
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

class SignalProtocolAddressDeserializer: JsonDeserializer<SignalProtocolAddress> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SignalProtocolAddress {
        if(json != null) {
            if(json is JsonPrimitive) {
                val stringVersion = json.asString
                val stringSplit = stringVersion.split(".")
                return SignalProtocolAddress(stringSplit[0], stringSplit[1].toInt())
            }
            else {
                throw IllegalArgumentException("It must be JsonPrimitive!")
            }
        } else {
            throw IllegalArgumentException("It cannot be null!")
        }
    }

}