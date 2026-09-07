package com.viafourasample.src.services

import com.google.firebase.messaging.FirebaseMessagingService

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
