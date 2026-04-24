package com.example.a2ace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a2ace.Link
import com.google.android.material.appbar.MaterialToolbar
import android.content.Intent

class LinksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var adapter: LinkAdapter


    private val presetLinks = listOf(
        Link(
            id = "1",
            title = "Спорт 1",
            aceStreamId = "acestream://abcd1234abcd1234abcd1234abcd1234abcd1234",
            iconResId = R.drawable.ic_sports
        ),
        Link(
            id = "2",
            title = "Кино HD",
            aceStreamId = "http://localhost:6878/ace/getstream?infohash=42832fbf9a1d3bdcca043e09951d6769fbbe04e7",
            iconResId = R.drawable.ic_movie
        ),
        Link(
            id = "3",
            title = "Новости 24",
            aceStreamId = "http://localhost:6878/ace/getstream?infohash=01dcc1ea3387c2b73953efe3f52286a770737d7c",
            iconResId = R.drawable.ic_news
        ),
        Link(
            id = "4",
            title = "Музыка",
            aceStreamId = "http://localhost:6878/ace/getstream?infohash=0dca83a7cc9184893ff96ee2355874342fc8fd45",
            iconResId = R.drawable.ic_music
        ),
        Link(
            id = "5",
            title = "Документальный",
            aceStreamId = "http://localhost:6878/ace/getstream?infohash=2605c201c8c9cf6684ee1de562d3b4c7e17ac915",
            iconResId = R.drawable.ic_documentary
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        initViews()
        setupRecyclerView()
    }


    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun setupRecyclerView() {
        adapter = LinkAdapter(presetLinks) { link ->
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("extra_ace_link", link.getFullUrl())
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@LinksActivity)
            adapter = this@LinksActivity.adapter

            setHasFixedSize(true)
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}