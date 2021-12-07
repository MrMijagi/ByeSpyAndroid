package com.example.byespy.libsignal

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.ecc.ECPublicKey
import java.lang.IllegalArgumentException
import java.lang.reflect.Type
import java.nio.charset.Charset

class IdentityKeyDeserializer: JsonDeserializer<IdentityKey> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): IdentityKey {
        if(json != null) {
            if(json is JsonPrimitive) {
                return IdentityKey(ECPublicKey(LibsignalHelper.ustringToByteArray(json.asString)))
            } else {
                throw IllegalArgumentException("It must be JsonPrimitive!")
            }
        } else {
            throw IllegalArgumentException("It cannot be null!")
        }
    }

}