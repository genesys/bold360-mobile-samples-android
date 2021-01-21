package com.common.utils.live

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.integration.core.FileUploadInfo
import com.integration.core.annotations.FileType
import com.nanorep.sdkcore.utils.ErrorException
import com.nanorep.sdkcore.utils.NRError
import com.sdk.common.R
import java.io.File

@FileType
fun Uri.fileType(context: Context?): String {
    val type = context?.contentResolver?.getType(this)
    return when (type) {
        "image/jpeg", "image/png" -> FileType.Picture

        "application/vnd.android.package-archive",
        "application/octet-stream",
        "application/zip" -> FileType.Archive

        else -> FileType.Default
    }
}

@Throws(ErrorException::class)
fun Uri.toFileUploadInfo(context: Context?, fileSizeLimit: Int): FileUploadInfo? =
    FileUploadInfo().apply {

        filePath = context?.let {
            RealPathUtil.getRealPath(
                context,
                this@toFileUploadInfo
            )?.takeIf { File(it).exists() }
                ?: PathUtil.getPath(
                    context,
                    this@toFileUploadInfo
                )?.takeIf { File(it).exists() }
        }
        if (filePath == null) {
            throw ErrorException(
                NRError(
                    errorCode = NRError.UploadError,
                    description = "failed to get path for file"
                )
            )
        }

        try {
            context?.run {
                contentResolver
                    ?.query(this@toFileUploadInfo, null, null, null, null, null)
                    ?.use { cursor ->

                        if (cursor.moveToFirst()) {
                            name =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                    ?: this@toFileUploadInfo.toString()

                            Log.d(
                                "Uploader",
                                "file: name: $name, size: $size bytes"
                            )
                        }

                    }

                type = this@toFileUploadInfo.fileType(this)

                content = File(filePath).takeIf {
                    if (it.length() > fileSizeLimit) {
                        throw ErrorException(
                            NRError(
                                errorCode = NRError.UploadError,
                                reason = NRError.SizeExceedsLimitError,
                                description = this.getString(R.string.upload_failure_size_exceeds_limit)
                            )
                        )
                    }
                    it.canRead()
                }?.readBytes() ?: run {
                    throw ErrorException(
                        NRError(
                            NRError.UploadError,
                            NRError.IllegalStateError,
                            "can't read file"
                        )
                    )
                }
            }

        } catch (e: Exception) {
            Log.e(
                "FileUploadInfo",
                "failed to create FileUploadInfo"
            )
            e.printStackTrace()
            throw e as? ErrorException
                ?: ErrorException(
                    NRError(
                        errorCode = NRError.UploadError,
                        reason = NRError.Unknown
                    )
                )
        } catch (oom: OutOfMemoryError) {
            Log.e(
                "FileUploadInfo",
                "failed to read file bytes due to luck of memory space"
            )
            throw ErrorException(
                NRError(
                    errorCode = NRError.UploadError,
                    reason = NRError.OutOfMemoryError
                )
            )
        }

    }