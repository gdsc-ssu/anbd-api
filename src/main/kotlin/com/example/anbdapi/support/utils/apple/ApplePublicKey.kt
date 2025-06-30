package com.example.anbdapi.support.utils.apple

data class ApplePublicKey(
    val kty: String,  // Key Type (RSA)
    val kid: String,  // Key ID
    val use: String,  // Key Use (sig)
    val alg: String,  // Algorithm (RS256)
    val n: String,    // Modulus
    val e: String     // Exponent
)