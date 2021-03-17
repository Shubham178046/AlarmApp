package com.agppratham.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_music_list.*
import java.io.File


class MusicListActivity : AppCompatActivity() {

    var list: ArrayList<String> = ArrayList()
    var musicList : ArrayList<MusicList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_list)
        GetAllMediaMp3Files()
        storagePermissen()

    }

    private fun storagePermissen() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        } else {
            rvMusic.layoutManager = LinearLayoutManager(this)
            var adp = MusicAdapter(this, musicList, object : MusicAdapter.OnClick {
                override fun onClick(text: String) {
                    PrefUtils.setURI(this@MusicListActivity, text)
                    val resultIntent = Intent()
                    resultIntent.putExtra("text", text)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

            })
            rvMusic.adapter = adp
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please grant permissen", Toast.LENGTH_LONG).show()
                } else {
                    rvMusic.layoutManager = LinearLayoutManager(this)
                    var adp = MusicAdapter(this, musicList, object : MusicAdapter.OnClick {
                        override fun onClick(text: String) {
                            PrefUtils.setURI(this@MusicListActivity, text)
                            val resultIntent = Intent()
                            resultIntent.putExtra("text", text)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }

                    })
                    rvMusic.adapter = adp
                }
            }
        }
    }

    fun GetAllMediaMp3Files() {
        var contentResolver = getContentResolver()
        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var cursor = contentResolver.query(
            uri,  // Uri
            null,
            null,
            null,
            null
        )
        if (cursor == null) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_LONG)
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(this, "No Music Found on SD Card.", Toast.LENGTH_LONG)
        } else {
            val Title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)

            //Getting Song ID From Cursor.
            //int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do {

                // You can also get the Song ID using cursor.getLong(id).
                //long SongID = cursor.getLong(id);
                val SongTitle: String = cursor.getString(Title)
                val songUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                Log.d("SONGPATH", "GetAllMediaMp3Files: " + songUrl)
                list.add(SongTitle)
                var music : MusicList = MusicList()
                music.title = SongTitle
                music.path = songUrl
                musicList.add(music)
                // Adding Media File Names to ListElementsArrayList.

            } while (cursor.moveToNext())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}