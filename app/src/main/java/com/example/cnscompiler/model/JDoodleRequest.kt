package com.example.cnscompiler.model

data class JDoodleRequest(
    val script: String,
    val language: String,
    val versionIndex: String,
    val clientId: String,
    val clientSecret: String
)
