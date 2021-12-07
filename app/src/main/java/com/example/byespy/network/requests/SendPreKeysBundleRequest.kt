package com.example.byespy.network.requests

import com.example.byespy.network.response.PreKey
import com.example.byespy.network.response.SignedPreKey
import com.squareup.moshi.Json

data class SendPreKeysBundleRequest(
    @Json(name = "identityKey") val identityKey: String,
    @Json(name = "preKeys") val preKeys: List<PreKey>,
    @Json(name = "signedPreKey") val signedPreKey: SignedPreKey
)
