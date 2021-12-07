package com.example.byespy.libsignal

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.byespy.R
import com.example.byespy.network.Api
import com.example.byespy.network.SessionManager
import com.example.byespy.network.requests.CiphertextMessage
import com.example.byespy.network.requests.MessageToSave
import com.example.byespy.network.requests.SaveMessageRequest
import com.example.byespy.network.requests.SendPreKeysBundleRequest
import com.example.byespy.network.response.DeviceResponse
import com.example.byespy.network.response.MessageSentInvalidDevicesResponse
import com.example.byespy.network.response.PreKey
import com.example.byespy.network.response.SignedPreKey
import com.example.byespy.ui.chat.ChatActivity
import com.google.gson.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.whispersystems.libsignal.*
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECPublicKey
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.protocol.SignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignalProtocolStore
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import org.whispersystems.libsignal.util.KeyHelper
import java.lang.IllegalArgumentException
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

public final class LibsignalHelper {

    companion object {
        const val STORE_KEY = "ProtocolStorev2"

        //region SignalProtocolStore

        public suspend fun getStore(applicationContext: Context): SignalProtocolStore {
            val sharedPrefs = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.app_name), Context.MODE_PRIVATE
            )
            val storeJson = sharedPrefs.getString(STORE_KEY, null)
            return if(storeJson == null) {
                Log.d("abc", "There is no store, creating new one...")
                val store = createStore(applicationContext)
                saveStore(applicationContext, store, sharedPrefs)
                store
            } else {
                Log.d("abc", "There is store, loading from memory...")
                val store = getGson().fromJson(storeJson, InMemorySignalProtocolStore::class.java)
                store
            }
        }

        private suspend fun createStore(applicationContext: Context): SignalProtocolStore {
            val registrationId = 1 //TODO
            val identityKeyPair = createIdentityKeyPair()
            val preKeys = createPreKeyRecords(2)
            val signedPreKey = createSignedPreKeyRecord(identityKeyPair)

            val store = createStore(identityKeyPair, registrationId)
            preKeys.forEach {
                store.storePreKey(it.id, it)
            }
            store.storeSignedPreKey(signedPreKey.id, signedPreKey)

            sendPreKeysToServer(
                identityKeyPair,
                preKeys,
                signedPreKey,
                applicationContext
            )
            return store
        }

        private fun saveStore(applicationContext: Context, signalProtocolStore: SignalProtocolStore) {
            val sharedPrefs = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.app_name), Context.MODE_PRIVATE
            )
            saveStore(applicationContext, signalProtocolStore, sharedPrefs)
        }

        private fun saveStore(applicationContext: Context, signalProtocolStore: SignalProtocolStore, sharedPrefs: SharedPreferences) {
            val editor = sharedPrefs.edit()
            editor.putString(STORE_KEY, getGson().toJson(signalProtocolStore))
            editor.apply()
        }

        //region Creating stuff

        private fun createRegistrationId(): Int {
            return KeyHelper.generateRegistrationId(false)
        }

        private fun createIdentityKeyPair(): IdentityKeyPair {
            val keyPair = Curve.generateKeyPair()
            return IdentityKeyPair(IdentityKey(keyPair.publicKey), keyPair.privateKey)
        }

        private fun createPreKeyRecords(amount: Int): ArrayList<PreKeyRecord> {
            val preKeyRecords = ArrayList<PreKeyRecord>();
            for(i in 0..amount ) {
                preKeyRecords.add(PreKeyRecord(i + 1, Curve.generateKeyPair()));
            }
            return preKeyRecords;
        }

        private fun createSignedPreKeyRecord(identityKeyPair: IdentityKeyPair): SignedPreKeyRecord {
            val keyPair = Curve.generateKeyPair();
            val signature = Curve.calculateSignature(identityKeyPair.privateKey, keyPair.publicKey.serialize());
            return SignedPreKeyRecord(100, System.currentTimeMillis(), keyPair, signature);
        }

        private fun createStore(identityKeyPair: IdentityKeyPair, registrationId: Int): SignalProtocolStore {
            return InMemorySignalProtocolStore(identityKeyPair, registrationId);
        }

        //endregion

        private suspend fun sendPreKeysToServer(identityKeyPair: IdentityKeyPair,
                                                preKeys: List<PreKeyRecord>,
                                                signedPreKey: SignedPreKeyRecord,
                                                applicationContext: Context)
        {
            val sendPreKeysBundleRequest = SendPreKeysBundleRequest(
                byteArrayToUString(identityKeyPair.publicKey.serialize()),
                listOf(
                    PreKey(preKeys[0].id, byteArrayToUString(preKeys[0].keyPair.publicKey.serialize())),
                    PreKey(preKeys[1].id, byteArrayToUString(preKeys[1].keyPair.publicKey.serialize())),
                ),
                SignedPreKey(signedPreKey.id, byteArrayToUString(signedPreKey.keyPair.publicKey.serialize()), byteArrayToUString(signedPreKey.signature))
            )
            val response = Api.getApiService(applicationContext).sendPreKeysBundle(sendPreKeysBundleRequest)
            val deviceId = response.body()?.deviceId
            if(deviceId != null) {
                SessionManager(applicationContext).saveDeviceId(deviceId)
            }
        }

        //endregion

        //region Sending messages

        suspend fun sendMessage(plaintextMessage: String, receiverUserId: Int, applicationContext: Context) {
            val store = getStore(applicationContext)

            ensureSession(receiverUserId, store, applicationContext)
            val encryptedMessage = encryptMessage(plaintextMessage, receiverUserId, store, applicationContext)
            sendMessageToServer(receiverUserId, listOf(encryptedMessage), applicationContext)

            saveStore(applicationContext, store)
        }

        private suspend fun ensureSession(receiverUserId: Int, protocolStore: SignalProtocolStore, applicationContext: Context) {
            val receiverAddress = SignalProtocolAddress(receiverUserId.toString(), 1)
            if(protocolStore.containsSession(receiverAddress)) {
                Log.d("abc", "Session is present!")
                return
            }
            Log.d("abc", "Session is not present, creating request...")

            //TODO make it better
            val getSessionDataRequest = SaveMessageRequest(
                senderDeviceId = SessionManager(applicationContext).fetchDeviceId(),
                receiverUserId = receiverUserId,
                messageToSaves = listOf()
            )
            val serverResponse = Api.getApiService(applicationContext).saveMessage(getSessionDataRequest)

            val messageSentInvalidDevicesResponse = getMoshi().adapter(
                MessageSentInvalidDevicesResponse::class.java).fromJson(Gson().toJson(serverResponse.body()))
            if(messageSentInvalidDevicesResponse != null) {
                val sessionBuilder = SessionBuilder(protocolStore, receiverAddress)
                sessionBuilder.process(createPreKeyBundle(messageSentInvalidDevicesResponse.devicesResponse[0]))
            } else {
                throw IllegalArgumentException("This response shouldn't be null!")
            }
        }

        private fun createPreKeyBundle(deviceResponse: DeviceResponse): PreKeyBundle {
            val preKeyPublicKey = ECPublicKey(ustringToByteArray(deviceResponse.preKeyBundle.preKey.publicKey))
            val signedPreKeyPublicKey = ECPublicKey(ustringToByteArray(deviceResponse.preKeyBundle.signedKey.publicKey))

            val signedPreKeySignature = ustringToByteArray(deviceResponse.preKeyBundle.signedKey.signature)
            val identityPublicKey = IdentityKey(ustringToByteArray(deviceResponse.preKeyBundle.identityKey))

            return PreKeyBundle(1, deviceResponse.deviceId, deviceResponse.preKeyBundle.preKey.keyId, preKeyPublicKey, deviceResponse.preKeyBundle.signedKey.keyId, signedPreKeyPublicKey, signedPreKeySignature, identityPublicKey)
        }

        private fun encryptMessage(plaintextMessage: String, receiverUserId: Int, protocolStore: SignalProtocolStore, applicationContext: Context): MessageToSave {
            val receiverAddress = SignalProtocolAddress(receiverUserId.toString(), 1)

            val sessionCipher = SessionCipher(protocolStore, receiverAddress)

            var userEmail = SessionManager(applicationContext).fetchUserEmail()
            val messageWithEmail = "$userEmail/$plaintextMessage"
            val encryptedMessage = sessionCipher.encrypt(messageWithEmail.toByteArray())
            val encryptedMessageString = encryptedMessage.serialize().toUByteArray()
                .joinToString("", "", "", -1, "...") { uByte ->
                    uByte.toInt().toChar().toString()
                }

            return MessageToSave(
                1,  //TODO
                "type",
                Calendar.getInstance().time,
                Gson().toJson(CiphertextMessage(encryptedMessage.type, encryptedMessageString, 1))  //TODO
            )
        }

        private suspend fun sendMessageToServer(receiverUserId: Int, encryptedMessages: List<MessageToSave>, applicationContext: Context) {
            val requestToServer = SaveMessageRequest(
                senderDeviceId = SessionManager(applicationContext).fetchDeviceId(),
                receiverUserId = receiverUserId,
                messageToSaves = encryptedMessages
            )
            val responseFromServer = Api.getApiService(applicationContext).saveMessage(requestToServer)
        }

        //endregion

        //region Receiving messages

        fun decryptMessage(ciphertextMessage: CiphertextMessage, senderAddress: SignalProtocolAddress, protocolStore: SignalProtocolStore): ByteArray {
            var sessionCipher = SessionCipher(protocolStore, senderAddress)
            var ciphertextSerialized = ciphertextMessage.body.toCharArray().map { char -> char.code.toUByte() }.toUByteArray().toByteArray()

            if(ciphertextMessage.type == 3) {
                return sessionCipher.decrypt(PreKeySignalMessage(ciphertextSerialized))
            } else if(ciphertextMessage.type == 1) {
                return sessionCipher.decrypt(SignalMessage(ciphertextSerialized))
            } else {
                throw IllegalArgumentException("Unknown ciphertext type! Type " + ciphertextMessage.type.toString())
            }
        }

        suspend fun onMessageReceived(message: String, applicationContext: Context): Pair<String, Date>? {
            val protocolStore = getStore(applicationContext)

            val receivedMessageAdapter = getMoshi().adapter(ReceivedMessage::class.java)
            val receivedMessage = receivedMessageAdapter.fromJson(message)
            if(receivedMessage != null) {
                if (receivedMessage.sender.userId == SessionManager(applicationContext).fetchUserId()) {
                    Log.d("abc", "Message to me so skipping!")
                    return null
                }

                val ciphertextAdapter = getMoshi().adapter(CiphertextMessage::class.java)
                val ciphertext = ciphertextAdapter.fromJson(receivedMessage.content)
                if (ciphertext != null) {
                    val senderAddress = SignalProtocolAddress(
                        receivedMessage.sender.userId.toString(),
                        receivedMessage.sender.deviceId
                    )

                    val plaintextMessage = String(
                        decryptMessage(ciphertext, senderAddress, protocolStore),
                        Charsets.UTF_8
                    )
                    saveStore(applicationContext, protocolStore)
                    return Pair(plaintextMessage, receivedMessage.sentAt)
                }
            }
            return null
        }

        //endregion

        //region Utils

        private fun getMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .build()
        }

        private fun getGson(): Gson {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(SignalProtocolAddress::class.java,
                SignalProtocolAddressSerializer()
            )
            gsonBuilder.registerTypeAdapter(SignalProtocolAddress::class.java,
                SignalProtocolAddressDeserializer()
            )
            return gsonBuilder.create()
        }

        private fun ustringToByteArray(string: String): ByteArray {
            val split = string.substring(1, string.length - 1).replace(" ", "").split(',')
            val uByteArray = UByteArray(split.size) { index -> split[index].toInt().toUByte() }
            return uByteArray.toByteArray()
        }

        private fun byteArrayToUString(byteArray: ByteArray): String {
            val uByteArray = byteArray.toUByteArray()
            return uByteArray.contentToString()
        }

        //endregion
    }

}