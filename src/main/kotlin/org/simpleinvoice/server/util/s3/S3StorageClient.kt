package org.simpleinvoice.server.util.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.HeadBucketRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toByteArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class S3StorageClient(
    private val s3Client: S3Client,
) : StorageClient {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun upload(
        bucketName: String,
        keyName: String,
        fileBytes: ByteArray,
        folderName: String?,
    ): String {
        val fullKey = getFullKey(folderName, keyName)
        val request =
            PutObjectRequest {
                bucket = bucketName
                key = fullKey
                body = ByteStream.fromBytes(fileBytes)
            }
        try {
            s3Client.putObject(request)
        } catch (e: S3Exception) {
            logger.error("Error uploading {} to {}", fullKey, bucketName, e)
            throw StorageException("Failed to upload file to S3 bucket: $bucketName, key: $fullKey", e)
        }

        return "/$folderName/$keyName"
    }

    override suspend fun download(
        bucketName: String,
        keyName: String,
        folderName: String?,
    ): ByteArray {
        val fullKey = getFullKey(folderName, keyName)
        logger.info("Attempting to download: bucket={}, key={}", bucketName, fullKey)
        val request =
            GetObjectRequest {
                bucket = bucketName
                key = fullKey
            }

        return try {
            s3Client.getObject(request) { response ->
                response.body?.toByteArray()
                    ?: throw S3Exception("Failed to download file from S3: bucket=$bucketName, key=$fullKey")
            }
        } catch (e: S3Exception) {
            logger.error("Error downloading {} from {}", fullKey, bucketName, e)
            throw StorageException("Failed to download file from S3 bucket: $bucketName, key: $fullKey", e)
        }
    }

    private fun getFullKey(
        folderName: String?,
        keyName: String,
    ): String =
        if (folderName != null) {
            "$folderName/$keyName"
        } else {
            keyName
        }

    private suspend fun createS3Bucket(bucketName: String): String {
        val request =
            CreateBucketRequest {
                bucket = bucketName
            }

        val response =
            try {
                s3Client.createBucket(request)
            } catch (e: S3Exception) {
                logger.error("Error creating bucket {}", bucketName, e)
                throw StorageException("Failed to create S3 bucket: $bucketName", e)
            }
        return response.location!!
    }

    override suspend fun ensureBucketExists(bucketName: String) {
        val bucketExists =
            try {
                s3Client.headBucket(HeadBucketRequest { bucket = bucketName })
                true
            } catch (e: S3Exception) {
                if (e.sdkErrorMetadata.errorCode == "NotFound") {
                    false
                } else {
                    logger.error("Error checking bucket status for bucket {}", bucketName, e)
                    throw StorageException("Failed to check status for S3 bucket: $bucketName", e)
                }
            }

        if (!bucketExists) {
            createS3Bucket(bucketName)
        }
    }
}
