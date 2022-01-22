package com.dahun.androidsidepanel_demo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dahun.androidsidepanel_demo.databinding.ActivityMainBinding
import com.dahun.sidepanel.widget.SidePanelLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.panelLayout.bindSlider(binding.panelSlider)

        binding.btnLink.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dddh1221/AndroidSidePanel")).also {
                startActivity(it)
            }
        }

        binding.btnOpen.setOnClickListener {
            binding.panelLayout.showPanel()
        }

        binding.btnClose.setOnClickListener {
            binding.panelLayout.dismissPanel()
        }
    }

}