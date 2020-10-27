package com.blazingtech.amakasamtv.model

data class UserData(
    val name: String,
    val email: String,
    val password: String,
    val uid: String
) {
    constructor() : this("", "", "", "")
}