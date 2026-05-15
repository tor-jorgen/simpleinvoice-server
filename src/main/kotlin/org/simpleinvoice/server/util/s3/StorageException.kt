package org.simpleinvoice.server.util.s3

class StorageException(
    msg: String,
    cause: Throwable,
) : Exception(msg, cause)
