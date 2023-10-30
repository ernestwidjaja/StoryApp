package com.example.mystoryapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapp.R
import com.example.mystoryapp.ViewModelFactory
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.databinding.ActivityRegisterBinding
import com.example.mystoryapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()

        binding.btnRegister.setOnClickListener {
            val name = binding.edtRegisterName.text.toString()
            val email = binding.edtRegisterEmail.text.toString()
            val password = binding.edtRegisterPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showToast(getString(R.string.isEdtEmpty))
            } else {
                register(name, email, password)
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        viewModel.register(name, email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder
                            .setCancelable(false)
                            .setMessage(getString(R.string.register_success))
                            .setTitle(getString(R.string.success))
                            .setPositiveButton(getString(R.string.login)) { _, _ ->
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(700)
        val name = ObjectAnimator.ofFloat(binding.edtRegisterNameLayout, View.ALPHA, 1f).setDuration(700)
        val email = ObjectAnimator.ofFloat(binding.edtRegisterEmailLayout, View.ALPHA, 1f).setDuration(700)
        val password = ObjectAnimator.ofFloat(binding.edtRegisterPasswordLayout, View.ALPHA, 1f).setDuration(700)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(700)

        AnimatorSet().apply {
            playSequentially(title, name, email, password, register)
            start()
        }
    }
}