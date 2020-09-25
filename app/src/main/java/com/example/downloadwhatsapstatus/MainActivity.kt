package com.example.downloadwhatsapstatus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.downloadwhatsapstatus.adapter.StatusAdapter
import com.example.downloadwhatsapstatus.service.MyService
import com.example.downloadwhatsapstatus.service.OnStatusFoundListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnStatusFoundListener {

    private lateinit var adapter: StatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            checkPermissions()
    }

    private fun startServiceAct() {


        val intent = Intent(this, MyService::class.java)
        MyService.listener=this
        intent.action = MyService.ACTION_START_FOREGROUND_SERVICE
        startService(intent)

    }

    private fun checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SETTINGS
                ),
                1001
            )
        }

    }

    fun getListOfPathsAct() {
        val list: ArrayList<String> = ArrayList()

        val fileToStatus =
            File(Environment.getExternalStorageDirectory().toString() + MyService.WHATSAPP_STATUS_FOLDER_PATH)
        val listFile = fileToStatus.listFiles()
        Log.e("FilesInFolder>>", "${listFile} <<<SizE<<<")

        if (listFile != null && listFile.isNullOrEmpty()) {
            Arrays.sort(listFile)
        }

        if (listFile != null) {
            for (imgFile in listFile) {
                val model = imgFile.absolutePath
                list.add(model)

            }
        }


        adapter = StatusAdapter()
        adapter.setList(list)
        rvWhatsAppStatus.layoutManager = GridLayoutManager(this, 3)
        rvWhatsAppStatus.adapter = adapter

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission Granted!!", Toast.LENGTH_LONG).show()
            getListOfPathsAct()
            startServiceAct()
        }

    }

    override fun getListOfPaths(listOfPaths: ArrayList<String>) {

        adapter = StatusAdapter()
        adapter.setList(listOfPaths)
        rvWhatsAppStatus.layoutManager = GridLayoutManager(this, 3)
        rvWhatsAppStatus.adapter = adapter


    }
}