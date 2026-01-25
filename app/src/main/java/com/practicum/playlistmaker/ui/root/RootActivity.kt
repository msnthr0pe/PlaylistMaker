package com.practicum.playlistmaker.ui.root

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    //private lateinit var binding: ActivityMainBinding
    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootFragmentContainerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                this.add(R.id.rootFragmentContainerView, MainFragment())
            }
        }
//        val itemClickListener: View.OnClickListener =
//            View.OnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
//
//        binding.apply {
//            searchBtn.setOnClickListener(itemClickListener)
//            mediaBtn.setOnClickListener {
//                startActivity(Intent(this@MainActivity, MediaActivity::class.java))
//            }
//            settingsBtn.setOnClickListener {
//                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
//            }
//        }

    }
}