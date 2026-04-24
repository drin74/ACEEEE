package com.example.a2ace.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a2ace.databinding.ActivityAuthBinding
import com.example.a2ace.MainActivity  // ← ПРАВИЛЬНЫЙ ИМПОРТ!
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()


        if (auth.currentUser != null) {
            goToMain()
            return
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        if (isLoginMode) {
            binding.tvTitle.text = "🎬 Ace Player"
            binding.btnSubmit.text = "Войти"
            binding.tvToggleMode.text = "Нет аккаунта? Зарегистрироваться"
        } else {
            binding.tvTitle.text = "📝 Регистрация"
            binding.btnSubmit.text = "Зарегистрироваться"
            binding.tvToggleMode.text = "Уже есть аккаунт? Войти"
        }
    }

    private fun setupClickListeners() {
        binding.tvToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            setupUI()
            binding.etEmail.text?.clear()
            binding.etPassword.text?.clear()
        }

        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Пароль минимум 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setLoading(true)

            if (isLoginMode) {

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        setLoading(false)
                        if (task.isSuccessful) {
                            goToMain()
                        } else {
                            Toast.makeText(this,
                                task.exception?.message ?: "Ошибка входа",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        setLoading(false)
                        if (task.isSuccessful) {
                            Toast.makeText(this, "✅ Регистрация успешна!", Toast.LENGTH_SHORT).show()
                            goToMain()
                        } else {
                            Toast.makeText(this,
                                task.exception?.message ?: "Ошибка регистрации",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.tvToggleMode.isEnabled = !isLoading
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)  // ← ЯВНОЕ СОЗДАНИЕ INTENT
        startActivity(intent)
        finish()
    }
}