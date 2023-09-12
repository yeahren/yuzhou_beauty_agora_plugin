package com.sc.jojo.json_load


import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.text.TextUtils
import android.util.Log
import com.sc.jojo.AppContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ConfigLoader {
    companion object {
        val MMCV_ROOT = File(AppContext.filesDir, "model-all")
    }

    private val TAG = "ConfigLoader"
    lateinit var rootDir: String
    private lateinit var context: Context
    fun setContext(context: Context) {
        this.context = context
        rootDir = context.filesDir.absolutePath
    }

    /**
     * 将 cosmos 复制到{data/files}文件夹里，并解压
     */
    private val INVALID_ZIP_ENTRY_NAME = arrayOf("../", "~/")
    private val unZipSuccess = 1
    private val copySuccess = 1

    private fun copyAssets(context: Context, fileName: String, destFile: File?): Boolean {
        return if (!TextUtils.isEmpty(fileName) && destFile != null) {
            var isSuccess = false
            val assetManager = context.assets
            var inputStream: InputStream? = null
            var fileOutputStream: FileOutputStream? = null
            try {
                inputStream = assetManager.open(fileName)
                fileOutputStream = FileOutputStream(destFile)
                copyFile(inputStream, fileOutputStream as OutputStream)
                isSuccess = true
            } catch (e: IOException) {
                Log.w(TAG, "copyAssets: 文件准备失败")
            } finally {
                try {
                    inputStream?.close()
                    fileOutputStream?.close()
                } catch (e: IOException) {
                    Log.w(TAG, "copyAssets: 流关闭失败")
                }
            }
            isSuccess
        } else {
            false
        }
    }


    @Throws(IOException::class)
    private fun copyFile(inputStream: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    private fun unzip(zipFile: String, targetDir: String): Boolean {
        var unzipSuccess: Boolean
        try {
            unzipSuccess = true
            unzipWithException(zipFile, targetDir)
        } catch (e: Exception) {
            Log.w(TAG, "unzip: 解压失败")
            unzipSuccess = false
        }
        return unzipSuccess
    }

    @Throws(Exception::class)
    private fun unzipWithException(zipFile: String, targetDir: String) {
        val bufferSize = 4096
        val fileInputStream = FileInputStream(zipFile)
        var bufferedOutputStream: BufferedOutputStream
        val zipInputStream = ZipInputStream(BufferedInputStream(fileInputStream))
        var entry: ZipEntry?
        zipInputStream.use { zis ->
            while (zis.nextEntry.also { entry = it } != null) {
                val data = ByteArray(bufferSize)
                val strEntry = entry!!.name
                require(validEntry(strEntry)) {
                    "unsecurity zipfile!"
                }
                val entryFile = File(targetDir, strEntry)
                if (entry!!.isDirectory) {
                    if (!entryFile.exists()) {
                        entryFile.mkdirs()
                    }
                } else {
                    val entryDir = File(entryFile.parent!!)
                    if (!entryDir.exists()) {
                        entryDir.mkdirs()
                    }
                    bufferedOutputStream =
                        BufferedOutputStream(FileOutputStream(entryFile), bufferSize)
                    bufferedOutputStream.use { bos ->
                        var count: Int
                        while (zis.read(data, 0, bufferSize).also { count = it } != -1) {
                            bos.write(data, 0, count)
                        }
                        bos.flush()
                    }
                }
            }
        }
    }

    private fun validEntry(name: String): Boolean {
        var i = 0
        val l: Int = INVALID_ZIP_ENTRY_NAME.size
        while (i < l) {
            if (name.contains(INVALID_ZIP_ENTRY_NAME[i])) {
                return false
            }
            ++i
        }
        return true
    }

    private fun deleteDir(dir: File?) {
        if (dir != null) {
            val files = dir.listFiles()
            if (files != null) {
                val var3 = files.size
                for (var4 in 0 until var3) {
                    val f = files[var4]
                    if (f.isDirectory) {
                        deleteDir(f)
                    } else {
                        f.delete()
                    }
                }
            }
            dir.delete()
        }
    }

    fun loadCosmosZip(callback: (copySuccess: Boolean, unZipSuccess: Boolean) -> Unit) {
        var copySuccess: Boolean
        var unZipSuccess: Boolean
        when {
            !checkCopySuccessFromSharePreferences() -> {
                deleteCosmosDir()
                deleteCosmosZip()
                copySuccess = copyCosmosZipToFilesDir()
                copySuccess = copySuccess && copyModelsZipToFilesDir()
                if (copySuccess) {
                    writeCopySuccessToSharePreferences()
                }
                unZipSuccess = unZipCosmosZipToFilesDir()
                unZipSuccess = unZipSuccess && unZipModelsZipToFilesDir()
                if (unZipSuccess) {
                    writeUnzipSuccessToSharePreferences()
                }
            }
            !checkUnzipSuccessFromSharePreferences() -> {
                deleteCosmosDir()
                copySuccess = true
                unZipSuccess = unZipCosmosZipToFilesDir()
                if (unZipSuccess) {
                    writeUnzipSuccessToSharePreferences()
                }
            }
            else -> {
                copySuccess = true
                unZipSuccess = true
            }
        }
        callback(copySuccess, unZipSuccess)
    }

    private fun copyCosmosZipToFilesDir(): Boolean =
        copyAssets(context.applicationContext, "cosmos.zip", File("$rootDir/cosmos.zip"))

    private fun copyModelsZipToFilesDir(): Boolean =
        copyAssets(context.applicationContext, "model-all.zip", File("$rootDir/model-all.zip"))

    private fun unZipCosmosZipToFilesDir(): Boolean =
        unzip(File("$rootDir/cosmos.zip").absolutePath, rootDir)

    private fun unZipModelsZipToFilesDir(): Boolean =
        unzip(File("$rootDir/model-all.zip").absolutePath, "$rootDir/model-all")

    private fun checkCopySuccessFromSharePreferences(): Boolean {
        val sp = context.applicationContext.getSharedPreferences("configInit", MODE_PRIVATE)
        return sp.getInt("copySuccess", 0) == copySuccess
    }

    private fun writeCopySuccessToSharePreferences() {
        val spEditor =
            context.applicationContext.getSharedPreferences("configInit", MODE_PRIVATE).edit()
        spEditor.putInt("copySuccess", copySuccess)
        spEditor.apply()
    }

    private fun checkUnzipSuccessFromSharePreferences(): Boolean {
        val sp = context.applicationContext.getSharedPreferences("unZip", MODE_PRIVATE)
        return sp.getInt("unzipSuccess", 0) == copySuccess
    }

    private fun writeUnzipSuccessToSharePreferences() {
        val spEditor = context.applicationContext.getSharedPreferences("unZip", MODE_PRIVATE).edit()
        spEditor.putInt("unzipSuccess", unZipSuccess)
        spEditor.apply()
    }

    private fun deleteCosmosDir() {
        deleteDir(File("$rootDir/cosmos"))
    }

    private fun deleteCosmosZip() {
        File("$rootDir/cosmos.zip").delete()
    }
}