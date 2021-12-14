package com.example.byespy.libsignal

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.byespy.R
import com.example.byespy.data.dao.ChatActivityDao
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
import com.example.byespy.ui.chat.ChatActivity.Companion.TAG
import com.example.byespy.ui.chat.ChatViewModel
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
            val preKeys = createPreKeyRecords(100)
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
            val prekeysList = mutableListOf<PreKey>()
            preKeys.forEach {
                prekeysList.add(PreKey(it.id, byteArrayToUString(it.keyPair.publicKey.serialize())))
            }

            val sendPreKeysBundleRequest = SendPreKeysBundleRequest(
                byteArrayToUString(identityKeyPair.publicKey.serialize()),
                prekeysList,
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

        suspend fun sendMessage(plaintextMessage: String, receiverUserId: Int, applicationContext: Context, chatViewModel: ChatViewModel) {
            val store = getStore(applicationContext)

            val knownDevices = chatViewModel.getDevicesByConversationId()
            val encryptedMessages = encryptAllMessages(receiverUserId, plaintextMessage, store, applicationContext, knownDevices.toMutableList())
            sendMessageToServer(receiverUserId, plaintextMessage, encryptedMessages, applicationContext, chatViewModel, store)

            saveStore(applicationContext, store)
        }

        suspend fun encryptAllMessages(
            receiverUserId: Int,
            plaintextMessage: String,
            protocolStore: SignalProtocolStore,
            applicationContext: Context,
            knownDevices: List<Long>
        ): MutableList<MessageToSave> {
            val encryptedMessages = mutableListOf<MessageToSave>()
            knownDevices.forEach { knownDevice ->
                encryptedMessages.add(encryptMessage(plaintextMessage, receiverUserId, knownDevice.toInt(), protocolStore, applicationContext))
            }
            return encryptedMessages
        }

        fun encryptAllMessagesRepeat(
            receiverUserId: Int,
            plaintextMessage: String,
            protocolStore: SignalProtocolStore,
            applicationContext: Context,
            knownDevices: List<Long>,
            previouslyEncryptedMessages: MutableList<MessageToSave>
        ): MutableList<MessageToSave> {

            //remove devices which are outdated
            val updatedMessages = previouslyEncryptedMessages.filter { message -> knownDevices.contains(message.deviceId.toLong()) }.toMutableList()

            //get new devices
            val alreadyEncryptedMessages = updatedMessages.map { message -> message.deviceId }
            val nonEncryptedDevices = knownDevices.filter { device -> !alreadyEncryptedMessages.contains(device.toInt()) }

            nonEncryptedDevices.forEach { nonEncryptedDevice ->
                updatedMessages.add(encryptMessage(plaintextMessage, receiverUserId, nonEncryptedDevice.toInt(), protocolStore, applicationContext))
            }
            return updatedMessages
        }


        private fun createPreKeyBundle(deviceResponse: DeviceResponse): PreKeyBundle {
            val preKeyBundleFromResponse = deviceResponse.preKeyBundle
            if(preKeyBundleFromResponse != null) {
                val preKeyPublicKey = ECPublicKey(ustringToByteArray(deviceResponse.preKeyBundle.preKey.publicKey))
                val signedPreKeyPublicKey = ECPublicKey(ustringToByteArray(deviceResponse.preKeyBundle.signedKey.publicKey))

                val signedPreKeySignature = ustringToByteArray(deviceResponse.preKeyBundle.signedKey.signature)
                val identityPublicKey = IdentityKey(ustringToByteArray(deviceResponse.preKeyBundle.identityKey))

                return PreKeyBundle(1, deviceResponse.deviceId, deviceResponse.preKeyBundle.preKey.keyId, preKeyPublicKey, deviceResponse.preKeyBundle.signedKey.keyId, signedPreKeyPublicKey, signedPreKeySignature, identityPublicKey)

            } else {
                throw IllegalArgumentException("Pre key bundle shouldn't be null!")
            }

        }

        private fun encryptMessage(plaintextMessage: String, receiverUserId: Int, receiverDeviceId: Int, protocolStore: SignalProtocolStore, applicationContext: Context): MessageToSave {
            val receiverAddress = getSignalAddress(receiverUserId, receiverDeviceId) //TODO

            val sessionCipher = SessionCipher(protocolStore, receiverAddress)

            var userEmail = SessionManager(applicationContext).fetchUserEmail()
            val messageWithEmail = "$userEmail/$plaintextMessage"
            val encryptedMessage = sessionCipher.encrypt(messageWithEmail.toByteArray())
            val encryptedMessageString = encryptedMessage.serialize().toUByteArray()
                .joinToString("", "", "", -1, "...") { uByte ->
                    uByte.toInt().toChar().toString()
                }

            return MessageToSave(
                receiverDeviceId,
                "type",
                Calendar.getInstance().time,
                Gson().toJson(CiphertextMessage(encryptedMessage.type, encryptedMessageString, 1))  //TODO
            )
        }

        private suspend fun sendMessageToServer(
            receiverUserId: Int,
            plaintextMessage: String,
            encryptedMessages: MutableList<MessageToSave>,
            applicationContext: Context,
            chatViewModel: ChatViewModel,
            protocolStore: SignalProtocolStore
        ) {
            val requestToServer = SaveMessageRequest(
                senderDeviceId = SessionManager(applicationContext).fetchDeviceId(),
                receiverUserId = receiverUserId,
                messageToSaves = encryptedMessages
            )
            val responseFromServer =
                Api.getApiService(applicationContext).saveMessage(requestToServer)
            if (responseFromServer.code() == 202) {
                val messageSentInvalidDevicesResponse = getMoshi().adapter(
                    MessageSentInvalidDevicesResponse::class.java).fromJson(Gson().toJson(responseFromServer.body()))

                if(messageSentInvalidDevicesResponse != null) {
                    updateDevices(messageSentInvalidDevicesResponse, receiverUserId, protocolStore, chatViewModel)
                    val knownDevices = chatViewModel.getDevicesByConversationId()
                    Log.d("Abc", "Known devices: $knownDevices")
                    val newEncryptedMessage = encryptAllMessagesRepeat(receiverUserId, plaintextMessage, protocolStore, applicationContext, knownDevices, encryptedMessages)
                    sendMessageToServer(receiverUserId, plaintextMessage, newEncryptedMessage, applicationContext, chatViewModel, protocolStore)
                } else {
                    throw IllegalArgumentException("This response shouldn't be null!")
                }
            } else if (responseFromServer.code() == 200) {
                //TODO?
                return
            } else {
                Log.e(TAG, "Unknown response code! Code: ${responseFromServer.code()}")
            }
        }

        private fun updateDevices(messageSentInvalidDevicesResponse: MessageSentInvalidDevicesResponse, receiverUserId: Int, protocolStore: SignalProtocolStore, chatViewModel: ChatViewModel) {
            messageSentInvalidDevicesResponse.devicesResponse.forEach { deviceResponse ->
                if(deviceResponse.preKeyBundle != null) {
                    val preKeyBundle = createPreKeyBundle(deviceResponse)
                    val receiverAddress = getSignalAddress(receiverUserId, deviceResponse.deviceId)
                    val sessionBuilder = SessionBuilder(protocolStore, receiverAddress)
                    sessionBuilder.process(preKeyBundle)
                    Log.d("abc", "Adding device ${deviceResponse.deviceId}")
                    chatViewModel.addDevice(deviceResponse.deviceId.toLong())
                } else {
                    chatViewModel.removeDevice(deviceResponse.deviceId.toLong())
                }
            }
        }


        //endregion

        //region Receiving messages

        fun decryptMessage(ciphertextMessage: CiphertextMessage, senderAddress: SignalProtocolAddress, protocolStore: SignalProtocolStore): ByteArray {
            var sessionCipher = SessionCipher(protocolStore, senderAddress)
            var ciphertextSerialized = ciphertextMessage.body.toCharArray().map { char -> char.code.toUByte() }.toUByteArray().toByteArray()

            if(ciphertextMessage.type == 3) {
                return sessionCipher.decrypt(PreKeySignalMessage(ciphertextSerialized))
            } else if(ciphertextMessage.type == 1 || ciphertextMessage.type == 2) {
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
                    val senderAddress = getSignalAddress(receivedMessage.sender.userId, receivedMessage.sender.deviceId)

                    val plaintextMessage = String(
                        decryptMessage(ciphertext, senderAddress, protocolStore),
                        Charsets.UTF_8
                    )
                    val emailEndPosition = plaintextMessage.indexOf('/')
                    val email = plaintextMessage.substring(0, emailEndPosition - 1)
                    val restOfMessage = plaintextMessage.substring(emailEndPosition + 1)
                    saveStore(applicationContext, protocolStore)
                    return Pair(restOfMessage, receivedMessage.sentAt)
                }
            }
            return null
        }

        //endregion

        //region Utils

        private fun getSignalAddress(user_id: Int, device_id: Int): SignalProtocolAddress {
            return SignalProtocolAddress("$user_id;$device_id", device_id)
        }

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
            gsonBuilder.registerTypeAdapter(IdentityKey::class.java,
                IdentityKeyDeserializer()
            )
            gsonBuilder.registerTypeAdapter(IdentityKey::class.java,
                IdentityKeySerializer()
            )

            return gsonBuilder.create()
        }

        public fun ustringToByteArray(string: String): ByteArray {
            val split = string.substring(1, string.length - 1).replace(" ", "").split(',')
            val uByteArray = UByteArray(split.size) { index -> split[index].toInt().toUByte() }
            return uByteArray.toByteArray()
        }

        public fun byteArrayToUString(byteArray: ByteArray): String {
            val uByteArray = byteArray.toUByteArray()
            return uByteArray.contentToString()
        }

        //endregion
    }

}