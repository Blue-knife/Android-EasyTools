package com.business.tools.utils.download

import java.io.InputStream
import java.io.OutputStream

/**
 * @name Android Business Toos
 * @class name：com.business.tools.utils.download
 * @author 345 QQ:1831712732
 * @time 2020/3/16 20:32
 * @description
 */

inline fun InputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE, progress: (Long) -> Unit): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)

        progress(bytesCopied)
    }
    return bytesCopied
}