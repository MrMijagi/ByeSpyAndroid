package com.example.byespy.network.requests

import com.example.byespy.network.response.PreKey
import com.example.byespy.network.response.SignedPreKey
import com.squareup.moshi.Json

data class SendPreKeysBundleResponse(
    @Json(name = "device_id") val deviceId: Int
)




