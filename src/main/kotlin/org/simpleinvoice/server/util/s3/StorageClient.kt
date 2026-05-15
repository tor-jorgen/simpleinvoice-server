package org.simpleinvoice.server.util.s3

interface StorageClient {
    suspend fun upload(
        bucketName: String,
        keyName: String,
        fileBytes: ByteArray,
        folderName: String? = null,
    ): String

    suspend fun download(
        bucketName: String,
        keyName: String,
        folderName: String? = null,
    ): ByteArray

    suspend fun ensureBucketExists(bucketName: String)
}
