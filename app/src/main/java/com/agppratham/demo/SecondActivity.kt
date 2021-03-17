package com.agppratham.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        btn1.setOnClickListener {
            var intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("setData", "btn1")
            startActivityForResult(intent, 2)
        }

        btn2.setOnClickListener {
            var intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("setData", "btn2")
            startActivityForResult(intent, 2)
        }

        btn3.setOnClickListener {
            var intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("setData", "btn3")
            startActivityForResult(intent, 2)
        }

        btn4.setOnClickListener {
            var intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("setData", "btn4")
            startActivityForResult(intent, 2)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            if(data.getStringExtra("setData").equals("btn1")) {
                btn1.setText(data?.getStringExtra("time"))
            }else if(data.getStringExtra("setData").equals("btn2")) {
                btn2.setText(data?.getStringExtra("time"))
            }else if(data.getStringExtra("setData").equals("btn3")) {
                btn3.setText(data?.getStringExtra("time"))
            }else if(data.getStringExtra("setData").equals("btn4")) {
                btn4.setText(data?.getStringExtra("time"))
            }
        }
    }
}