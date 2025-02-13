package com.example.photocleaner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val deleteButton: Button = findViewById(R.id.btnDeletePhotos)
        deleteButton.setOnClickListener {
            // تحقق من صلاحيات الوصول
            if (hasPermissions()) {
                deleteAllImages()
            } else {
                requestPermissions()
            }
        }
    }

    // تحقق من وجود صلاحيات القراءة والكتابة
    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
        }
    }

    // طلب الصلاحيات للمستخدم إذا لم يكن قد منحها
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            startActivityForResult(intent, 100)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        }
    }

    // مسح الصور من المجلدات المتاحة
    private fun deleteAllImages() {
        val pictureDirectory = File("/storage/emulated/0/DCIM/Camera/") // مسار الصور
        if (pictureDirectory.exists() && pictureDirectory.isDirectory) {
            val files = pictureDirectory.listFiles()
            files?.forEach { file ->
                if (file.exists() && file.isFile) {
                    file.delete()
                }
            }
            Toast.makeText(this, "تم مسح الصور بنجاح!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "لا توجد صور في هذا المجلد", Toast.LENGTH_SHORT).show()
        }
    }

    // التعامل مع نتائج طلب الصلاحيات (على الأجهزة التي تعمل بنظام Android 6.0 فما فوق)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            deleteAllImages()
        } else {
            Toast.makeText(this, "يجب منح صلاحية الوصول للصور", Toast.LENGTH_SHORT).show()
        }
    }
}
