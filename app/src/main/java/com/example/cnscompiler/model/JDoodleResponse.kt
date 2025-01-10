package com.example.cnscompiler.model

data class JDoodleResponse(
    val output: String,
    val statusCode: Int,
    val memory: String?,
    val cpuTime: String?,
    val error: String?
)

