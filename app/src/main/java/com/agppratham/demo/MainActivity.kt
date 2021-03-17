package com.agppratham.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDay1.setOnClickListener {
            var intent  = Intent(this, SecondActivity::class.java)
            intent.putExtra("isFrom", "sat")
            startActivity(intent)
        }

        btnDay2.setOnClickListener {
            var intent  = Intent(this, SecondActivity::class.java)
            intent.putExtra("isFrom", "mon")
            startActivity(intent)
        }

        btnDay3.setOnClickListener {
            var intent  = Intent(this, SecondActivity::class.java)
            intent.putExtra("isFrom", "tue")
            startActivity(intent)
        }

        btnDay4.setOnClickListener {
            var intent  = Intent(this, SecondActivity::class.java)
            intent.putExtra("isFrom", "wed")
            startActivity(intent)
        }

        btnDay5.setOnClickListener {
            var intent  = Intent(this, SecondActivity::class.java)
            intent.putExtra("isFrom", "thu")
            startActivity(intent)
        }

        btnDay6.setOnClickListener {
            var intent  = Intent(this, SecondActivity::class.java)
            intent.putExtra("isFrom", "fri")
            startActivity(intent)
        }

        btnDay7.setOnClickListener {
            var intent  = Intent(this, SecondActivity::class.java)
            intent.putExtra("isFrom", "sat")
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        finishAffinity()
    }
}