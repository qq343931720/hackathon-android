package com.webviander.hackathonapp.view

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.webviander.hackathonapp.R
import com.webviander.hackathonapp.databinding.ActivityDetailsBinding
import com.webviander.hackathonapp.model.Comment
import com.webviander.hackathonapp.model.FeedItem
import com.webviander.hackathonapp.viewmodel.DetailPageViewModel
import com.webviander.hackathonapp.viewmodel.DetailsPageCallback
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {


    lateinit var binding: ActivityDetailsBinding
    lateinit var feedItem: FeedItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        populateFromIntent(intent)
        initLayouts()
        setUpViewModel()
        setUpComments(comments_list)
        binding.viewModel?.loadComments()

//        binding.viewModel?.let { setUpObserver(it) }
    }

    private fun populateFromIntent(intent: Intent?) {
        feedItem = intent?.getSerializableExtra(FEED_ITEM_EXTRA) as FeedItem
    }

    fun initLayouts() {
        binding = DataBindingUtil.setContentView<ActivityDetailsBinding>(this, R.layout.activity_details)
    }

    fun setUpViewModel() {
        binding.viewModel = DetailPageViewModel(feedItem, this, object : DetailsPageCallback {
            override fun onBackPressed() {
                onBack()
            }

            override fun onCommentsLoaded(commentsList: ArrayList<Comment>, newCommentAdded: Boolean) {
                val adapter = binding.commentsList.adapter as CommentsAdapter
                adapter.commentsList = commentsList
                adapter.feedItem = feedItem
                val view: TextView = comment_text_entry
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                if(newCommentAdded) {
                    comments_list.smoothScrollToPosition(commentsList.size+1)
                }
            }
        })
    }

    fun setUpComments(recyclerView: RecyclerView) {
        val adapter = CommentsAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun onBack() {
        onBackPressed()
    }

    override fun onBackPressed() {
        startActivity(FeedActivity.getIntent(this))
        finish()
    }


    companion object {
        val FEED_ITEM_EXTRA = "Feed_Item_Extra"
        fun getIntent(context: Context, feedItem: FeedItem): Intent {
            val i = Intent(context, DetailsActivity::class.java)
            i.putExtra(FEED_ITEM_EXTRA, feedItem)
            return i
        }
    }
}
