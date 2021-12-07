package com.example.byespy.libsignal

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.whispersystems.libsignal.SignalProtocolAddress
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

class SignalProtocolAddressSerializer: JsonSerializer<SignalProtocolAddress> {
    override fun serialize(
        src: SignalProtocolAddress?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if(src != null) {
            return JsonPrimitive(src.toString())
        } else {
            throw IllegalArgumentException("It cannot be null!")
        }
    }
}