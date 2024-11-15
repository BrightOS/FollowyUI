package ru.bashcony.followy.ui.test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import ru.bashcony.followy.ui.FollowyToggleButton
import ru.bashcony.followy.ui.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FollowyToggleButton.synchronizeButtons(listOf(
            binding.first to {
                binding.appbar.text = "First selected"
            },
            binding.second to {
                binding.appbar.text = "Second selected"
            },
            binding.third to {
                binding.appbar.text = "Third selected"
            }
        ))

//        binding.third.isChecked = true

        binding.testCard.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}