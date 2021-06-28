package com.common.utils.live

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.integration.core.FileUploadInfo
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.sdkcore.model.SystemStatement
import com.nanorep.sdkcore.utils.DataStructure
import com.nanorep.sdkcore.utils.ErrorException
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.common.R
import java.net.URISyntaxException

/**
 * opens a file browsing activity for the user to select files from.
 * when selection is done `onFilesChosen` will be activated.
 */
open class FileChooser(activity: AppCompatActivity) {

    val activity = activity.weakRef()

    var onFilesChosen: ((Intent) -> Unit)? = null

    // -> New results API for handling permissions requests and activity results:
    //    https://medium.com/swlh/android-new-results-api-and-how-to-use-it-to-make-your-code-cleaner-de20d5c1fffa
    private val getPermissions = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results: Map<String, Boolean> ->
        val anyFailure = results.any { entry -> !entry.value }
        if (!anyFailure) { // if all permissions were granted
            startPickerActivity()
        }
    }

    private val fileChooser = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        result.data?.takeIf { result.resultCode == Activity.RESULT_OK }?.run {
            onFilesChosen?.invoke(this)
        } ?: kotlin.run { Log.w("FileChooser", "no file was selected to be uploaded") }
    }

    fun open() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            permissions.plus(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }
        getPermissions.launch(permissions)
    }

    private fun startPickerActivity() {
        activity.get()?.run {
            createPickerIntent {
                try {
                    fileChooser.launch(it)

                } catch (e: ActivityNotFoundException) {
                    toast(baseContext, getString(R.string.FileChooserError), Toast.LENGTH_LONG)
                }
            }
        }
    }
}

/**
 * Extends the FileChooser for files upload chooser functionality.
 * When files selection is done, the class goes over the results and creates
 * a DataStructure object for each selected file with its `FileUploadInfo` data or error.
 *
 * when processing is done, activates `onUploadsReady` with the upload info.
 */
class UploadFileChooser(activity: AppCompatActivity, private val fileSizeLimit: Int)
    : FileChooser(activity) {

    init {
        onFilesChosen = ::handleFileUploads
    }

    var onUploadsReady: ((ArrayList<DataStructure<FileUploadInfo>>) -> Unit)? = null

    private fun handleFileUploads(resultData: Intent) {
        val context = activity.get()

        if (context == null) {
            onUploadsReady?.invoke(arrayListOf())
            return
        }

        val uploadsData = ArrayList<DataStructure<FileUploadInfo>>()

        val fileUri = resultData.data

        fun addChosen(uri: Uri) {
            try {
                uri.toFileUploadInfo(context, fileSizeLimit).let { uploadsData.add(DataStructure(it)) }

            } catch (ex: ErrorException) {
                uploadsData.add(DataStructure(error = ex.error))
            }
        }

        if (fileUri == null) {
            val clipData = resultData.clipData
            if (clipData != null) {
                val itemCount = clipData.itemCount
                for (i in 0 until itemCount) {
                    addChosen(clipData.getItemAt(i).uri)
                }
            }
        } else {
            addChosen(fileUri)
        }

        onUploadsReady?.invoke(uploadsData)
    }
}


fun Activity.createPickerIntent(onIntentReady: (fileChooserIntent: Intent) -> Unit) {

    if (isFinishing) {
        Log.w("FilePicker", "request for file picker display is discarded")
        return
    }

    val intent = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        Intent(Intent.ACTION_GET_CONTENT)

    } else {
        Intent(Intent.ACTION_OPEN_DOCUMENT)

    }.apply {
        type = "*/*"
        addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }

    onIntentReady(Intent.createChooser(intent, "Select a File to Upload"))
}

/**
 * Extension method to handle upload data list: common for Samples and FullDemo
 *
 * for each selected file that matched with an error, a SystemMessage will be injected to the chat
 * for each create FileUploadInfo, an uploadFile will start.
 */
fun ChatController.onUploads(uploadsData: ArrayList<DataStructure<FileUploadInfo>>) {

    uploadsData.forEach { data ->
        data.error?.let { error ->
            if (NRError.IllegalStateError == error.reason) {
                Log.e("File Upload", "file path is invalid")
            }

            // Injects error messages for failed selected files:
            (error.description ?: error.reason
            ?: this.context?.getString(R.string.upload_failure_general))
                    ?.let { this.post(SystemStatement(it)) }


        } ?: data.data?.let {
            // Activates ChatController.uploadFile API:
            this.uploadFile(it) { results ->
                Log.i("File Upload", "got Upload results:$results")
                val error = results.error
                if (error != null) {
                    if (NRError.Canceled != error.reason) {
                        val msg = error.description
                        this.post(SystemStatement(msg ?: error.reason!!))
                    }
                }
            }
        }
    }
}


/**
 * Created by Aki on 1/7/2017.
 */

object PathUtil {
    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class, NumberFormatException::class)
    fun getPath(context: Context, pathUri: Uri): String? {
        var uri = pathUri
        val needToCheckUri = Build.VERSION.SDK_INT >= 19
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                isDownloadsDocument(uri) -> {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                }
                isMediaDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    when (split[0]) {
                        "image" -> {
                            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                }
            }
        }
        if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
            } finally {
                cursor?.close()
            }

        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}


object RealPathUtil {

    fun getRealPath(context: Context, fileUri: Uri): String? {
        return when {

            Build.VERSION.SDK_INT < 19 -> {
                // SDK >= 11 && SDK < 19
                getRealPathFromUriAPI11to18(context, fileUri)
            }

            else -> {
                // SDK > 19 (Android 4.4) and up
                getRealPathFromUriAPI19(context, fileUri)
            }
        }
    }


    private fun getRealPathFromUriAPI11to18(context: Context, contentUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null

        val cursorLoader = CursorLoader(context, contentUri, projection, null, null, null)
        val cursor = cursorLoader.loadInBackground()

        cursor?.let {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(columnIndex)
            cursor.close()
        }
        return result
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    private fun getRealPathFromUriAPI19(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        @SuppressLint("NewApi")
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    val path = StringBuilder()
                    path.append(Environment.getExternalStorageDirectory())
                    path.append("/")
                    path.append(split[1])
                    return path.toString()
                } else {
                    var path: String? = null
                    if (Build.VERSION.SDK_INT > 20) {
                        //getExternalMediaDirs() added in API 21
                        val external = context.externalMediaDirs
                        if (external.size > 1) {
                            path = external[1].absolutePath
                            path = path.substring(0, path.indexOf("Android")) + split[1]
                        }
                    } else {
                        path = "/storage/" + type + "/" + split[1]
                    }
                    return path
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "")
                    }
                    return try {
                        val contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                        getDataColumn(context, contentUri, null, null)
                    } catch (e: NumberFormatException) {
                        //(e)
                        e.printStackTrace()
                        null
                    }
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return contentUri?.let { getDataColumn(context, contentUri, selection, selectionArgs) }
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(context: Context, uri: Uri, selection: String?,
                              selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        return try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            cursor?.takeIf { it.moveToFirst() }?.let {
                val index = it.getColumnIndexOrThrow(column)
                it.getString(index)
            }
        } catch (ex: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}