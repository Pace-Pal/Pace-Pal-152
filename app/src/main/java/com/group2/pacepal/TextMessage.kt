package com.group2.pacepal

import java.util.*

/*
Purpose: The TextMessage class is used to define the data type that is stored in the messageAdapter.
         It is also ready to be given extended functionality in that being able to send images with
         this data type is not difficult to add.

         Key: The constructor. Without it we could not add items to Firebase's Firestore
          using this data class.

*/


data class TextMessage(val text: String,
                       override val time: Date,
                       override val senderId: String,
                       override val recipientId: String,
                       override val senderName: String,
                       override val epochTimeMilliseconds: Long,
                       override val type: String = MessageType.TEXT
                       )
    : Message {
    constructor() : this("", Date(0), "", "", "", 0)
}
