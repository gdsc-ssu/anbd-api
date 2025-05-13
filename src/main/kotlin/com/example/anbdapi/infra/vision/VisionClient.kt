package com.example.anbdapi.infra.vision

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Component
class VisionClient(private val imageAnnotatorClient: ImageAnnotatorClient) {

    fun detectText(imageFile: MultipartFile): List<String> {
        try {
            val imgBytes = imageFile.bytes
            val imgByteString = ByteString.copyFrom(imgBytes)
            val img = Image.newBuilder().setContent(imgByteString).build()

            val feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build()
            val request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build()

            val response = imageAnnotatorClient.batchAnnotateImages(listOf(request))
            val result = response.responsesList.firstOrNull()

            return result?.textAnnotationsList?.map { it.description } ?: emptyList()
        } catch (e: IOException) {
            throw RuntimeException("Image detect text error.", e)
        }
    }
}