package com.example.byespy.libsignal

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.whispersystems.libsignal.IdentityKey
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

class IdentityKeySerializer: JsonSerializer<IdentityKey> {
    override fun serialize(
        src: IdentityKey?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if(src != null) {
            return JsonPrimitive(LibsignalHelper.byteArrayToUString(src.publicKey.serialize()))
        } else {
            throw IllegalArgumentException("It cannot be null!")
        }
    }
}