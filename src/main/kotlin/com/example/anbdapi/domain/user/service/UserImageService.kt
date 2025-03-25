package com.example.anbdapi.domain.user.service

import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.domain.user.exception.UserImageDeleteException
import com.example.anbdapi.domain.user.exception.UserImageUploadException
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class UserImageService (
    private val storage: Storage,
    private val bucketName: String,
    private val userRepository: UserRepository
) {

    @Transactional
    fun uploadUserImage(userId: Long, file: MultipartFile): String {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        validateImage(file)

        val imageUrl = uploadToStorage(file)

        user.profileImage?.let {
            if (it.contains("storage.googleapis.com")) {
                deleteImage(it)
            }
        }

        user.profileImage = imageUrl
        userRepository.save(user)

        return imageUrl
    }

    @Transactional
    fun uploadSharePostImage(user: User, file: MultipartFile): String {
        validateImage(file)

        // TODO: Image upload시 user profile image 변경 제거
        return uploadToStorage(file, "share-post-images")
    }

    @Transactional
    fun deleteImage(imageUrl: String): Boolean {

        try {
            val objectName = imageUrl.substringAfter("$bucketName/")

            val blobId = BlobId.of(bucketName, objectName)
            return storage.delete(blobId)
        } catch (e: Exception) {
            throw UserImageDeleteException("User image delete error")
        }
    }

    private fun uploadToStorage(file: MultipartFile, directory: String = "profile-images"): String {
        try {
            val extension = FilenameUtils.getExtension(file.originalFilename)

            val fileName = "${UUID.randomUUID()}.$extension"
            val objectName = "$directory/$fileName"
            val contentType = file.contentType ?: "application/octet-stream"

            val blobId = BlobId.of(bucketName, objectName)
            val blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build()

            storage.create(blobInfo, file.bytes)

            return "https://storage.googleapis.com/$bucketName/$objectName"
        } catch (e: Exception) {
            throw UserImageUploadException("User image upload error")
        }
    }

    private fun validateImage(file: MultipartFile) {
        if (file.isEmpty) {
            throw UserImageUploadException("Empty file.")
        }

        val contentType = file.contentType

        if (contentType == null) {
            throw UserImageUploadException("Image upload error.")
        }
    }
}