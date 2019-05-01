package com.group2.pacepal

import java.util.*

interface TextMessageTest {
    val time: Date
    val text: String
    val senderId: String
    val recipientId: String
    val senderName: String
    val type: String

    fun getTimes() : String

    fun getMessageText() : String
}