package com.example.byespy.network.response

import com.squareup.moshi.Json

data class MessageSentInvalidDevicesResponse(
    @Json(name = "invalid_devices") val devicesResponse: List<DeviceResponse>
)

data class DeviceResponse(
    @Json(name = "device_id") val deviceId: Int,
    @Json(name = "prekey_bundle") val preKeyBundle: PreKeyBundle
)

data class PreKeyBundle(
    @Json(name="identity_key") val identityKey: String,
    @Json(name="prekey") val preKey: PreKey,
    @Json(name="signed_key") val signedKey: SignedPreKey
)

data class PreKey(
    @Json(name = "keyId") val keyId: Int,
    @Json(name = "publicKey") val publicKey: String
)

data class SignedPreKey(
    @Json(name = "keyId") val keyId: Int,
    @Json(name = "publicKey") val publicKey: String,
    @Json(name = "signature") val signature: String
)