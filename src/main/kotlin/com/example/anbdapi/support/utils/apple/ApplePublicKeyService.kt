package com.example.anbdapi.support.utils.apple

import com.example.anbdapi.domain.auth.exception.AppleAuthException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Service
class ApplePublicKeyService(
    private val objectMapper: ObjectMapper
) {
    companion object {
        private const val APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys"
    }

    @Cacheable("applePublicKeys", unless = "#result == null")
    fun getPublicKey(kid: String, alg: String): PublicKey {
        val httpClient = HttpClients.createDefault()
        val request = HttpGet(APPLE_PUBLIC_KEYS_URL)

        return try {
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)

            if (response.statusLine.statusCode != 200) {
                throw AppleAuthException("Failed to fetch Apple public keys: $content")
            }

            val keysResponse: ApplePublicKeysResponse = objectMapper.readValue(content)

            val key = keysResponse.keys.find { it.kid == kid && it.alg == alg }
                ?: throw AppleAuthException("No matching public key found for kid: $kid, alg: $alg")

            // RSA 공개키 생성
            createPublicKey(key)
        } catch (e: AppleAuthException) {
            throw e
        } catch (e: Exception) {
            throw AppleAuthException("Failed to get Apple public key: ${e.message}")
        } finally {
            httpClient.close()
        }
    }

    private fun createPublicKey(key: ApplePublicKey): PublicKey {
        val nBytes = Base64.getUrlDecoder().decode(key.n)
        val eBytes = Base64.getUrlDecoder().decode(key.e)

        val modulus = BigInteger(1, nBytes)
        val exponent = BigInteger(1, eBytes)

        val spec = RSAPublicKeySpec(modulus, exponent)
        val factory = KeyFactory.getInstance(key.kty)

        return factory.generatePublic(spec)
    }
}