package com.example.a2ace

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.a2ace.ui.auth.AuthActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {




    private lateinit var inputLink: TextInputEditText
    private lateinit var inputLayout: TextInputLayout
    private lateinit var btnPlay: MaterialButton
    private lateinit var btnLinks: MaterialButton
    private lateinit var tvStatus: TextView


    private val aceStreamPackages = listOf(
        "org.acestream.engine",
        "org.acestream.media"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (FirebaseAuth.getInstance().currentUser == null) {

            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)


        initViews()

        setupClickListeners()

        handleIncomingIntent()
    }
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }



    private fun initViews() {
        inputLink = findViewById(R.id.inputLink)
        inputLayout = findViewById(R.id.inputLayout)
        btnPlay = findViewById(R.id.btnPlay)
        btnLinks = findViewById(R.id.btnLinks)
        tvStatus = findViewById(R.id.tvStatus)

    }

    private fun checkInstalledApps() {
        val packageManager: PackageManager = packageManager
        val installedApps = packageManager.getInstalledPackages(0)

        val aceStreamApps = installedApps.filter {
            it.packageName.contains("acestream", ignoreCase = true)
        }

        if (aceStreamApps.isEmpty()) {
            tvStatus.text = "❌ Ace Stream НЕ найден среди установленных приложений!"
            Toast.makeText(this, "Ace Stream не установлен", Toast.LENGTH_LONG).show()
        } else {
            val appNames = aceStreamApps.joinToString("\n") {
                "${it.applicationInfo?.loadLabel(packageManager)}\n(${it.packageName})"
            }
            tvStatus.text = "✅ Найдено:\n$appNames"
            Toast.makeText(this, "Ace Stream найден!", Toast.LENGTH_LONG).show()
        }

    }

    private fun setupClickListeners() {
        btnPlay.setOnClickListener {
            val link = inputLink.text.toString().trim()
            if (link.isNotEmpty()) {
                playAceStream(link)
            } else {
                inputLayout.error = "Введите ссылку Ace Stream"
                tvStatus.text = "❌ Введите ссылку"
            }
        }


        btnLinks.setOnClickListener {
            startActivity(Intent(this, LinksActivity::class.java))
        }
    }

    private fun handleIncomingIntent() {
        intent?.data?.let { uri ->
            if (uri.scheme == "acestream") {
                tvStatus.text = "📥 Получена ссылка: ${uri.toString()}"
                playAceStream(uri.toString())
            }
        }
    }


    private fun playAceStream(link: String) {
        val aceUrl = if (link.startsWith("acestream://")) {
            link
        } else {
            "acestream://$link"
        }

        tvStatus.text = "🔄 Попытка запуска: $aceUrl"

        try {

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(aceUrl)

            }

            println("🚀 Запуск Intent: $aceUrl")
            startActivity(intent)
            tvStatus.text = "✅ Запущено: $aceUrl"
            Toast.makeText(this, "Ace Stream запущен", Toast.LENGTH_SHORT).show()

        } catch (e: ActivityNotFoundException) {
            println("ActivityNotFoundException: ${e.message}")
            tvStatus.text = " Не найдено приложение для acestream://"
            showInstallDialog()
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            e.printStackTrace()
            tvStatus.text = "Ошибка: ${e.message}"

            tryOpenWithPackageName(link)
        }
    }

    private fun tryOpenWithPackageName(link: String) {
        val aceUrl = if (link.startsWith("acestream://")) link else "acestream://$link"


        val packages = listOf(
            "org.acestream.engine",
            "org.acestream.media",
            "ru.acestream.engine"
        )

        for (pkg in packages) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(aceUrl)
                    setPackage(pkg)
                }
                println("🔄 Пробую пакет: $pkg")
                startActivity(intent)
                tvStatus.text = "✅ Запущено через $pkg"
                return
            } catch (e: Exception) {
                println("❌ Пакет $pkg не подошёл")
            }
        }

        showInstallDialog()
    }

    private fun convertToAceStreamUrl(input: String): String {

        if (input.startsWith("acestream://", true)) {
            return input
        }

        if (input.contains("infohash=", true)) {
            try {
                val uri = Uri.parse(input)
                val infohash = uri.getQueryParameter("infohash")
                if (!infohash.isNullOrEmpty()) {
                    return "acestream://$infohash"
                }
            } catch (e: Exception) {

            }
        }


        if (input.length == 40 && input.matches(Regex("[a-fA-F0-9]+"))) {
            return "acestream://$input"
        }


        return if (input.startsWith("acestream://")) input else "acestream://$input"
    }

    private fun isAceStreamInstalled(): Boolean {
        val packageManager: PackageManager = packageManager


        val aceStreamPackages = listOf(
            "org.acestream.engine",
            "org.acestream.media",
            "org.acestream.core",
            "ru.acestream.engine",
            "com.acestream.engine"
        )


        for (packageName in aceStreamPackages) {
            try {
                packageManager.getPackageInfo(packageName, 0)
                println("✅ Ace Stream найден: $packageName")
                return true
            } catch (e: PackageManager.NameNotFoundException) {

            }
        }


        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("acestream://0000000000000000000000000000000000000000")
            }
            val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo != null
        } catch (e: Exception) {
            false
        }
    }

    private fun showInstallDialog() {
        AlertDialog.Builder(this)
            .setTitle("Ace Stream не установлен")
            .setMessage("Для воспроизведения необходим Ace Stream Engine.\n\nУстановить сейчас?")
            .setPositiveButton("Google Play") { _, _ ->
                openGooglePlay()
            }
            .setNegativeButton("Сайт") { _, _ ->
                openOfficialSite()
            }
            .setNeutralButton("Отмена", null)
            .show()
    }

    private fun openGooglePlay() {
        try {

            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=org.acestream.engine")
            })
        } catch (e: ActivityNotFoundException) {

            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=org.acestream.engine")
            })
        }
    }

    private fun openOfficialSite() {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.acestream.org/")
        })
    }


    override fun onResume() {
        super.onResume()
        if (isAceStreamInstalled()) {
            tvStatus.text = "✅ Ace Stream установлен"
        }
    }
}