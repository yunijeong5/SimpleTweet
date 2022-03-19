package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.button.MaterialButton
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: MaterialButton
    lateinit var tvTweetCounter: TextView

    lateinit var client: TwitterClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        supportActionBar?.hide()

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvTweetCounter = findViewById(R.id.tvTweetCounter)

        client = TwitterApplication.getRestClient(this)

        btnTweet.isEnabled = false

        etCompose.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(s: Editable) {
            }
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                tvTweetCounter.text = etCompose.text.length.toString() + " / 280"
                btnTweet.isEnabled = etCompose.text.toString().isNotEmpty() && etCompose.text.length <= 280
            }

        })

        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener{

            // Gran the content of the etCompose
            val tweetContent = etCompose.text.toString()

            // 1. make sure the tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
            } else {
                // 2. make sure the tweet is under character count
                if (tweetContent.length == 280) {
                    Toast.makeText(this, "Tweet reached max length! Limit is 280 characters", Toast.LENGTH_SHORT).show()
                } else {
                    // Make an api call to Twitter to publish tweet
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "Failed to publish tweet", throwable)
                        }

                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                            // Send the tweet back to TimelineActivity
                            Log.i(TAG, "publish success!")

                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish() // finish compose activity and go back to timeline activity
                        }


                    })
                }


            }
        }





    }
    companion object {
        val TAG = "ComposeActivity"
    }
}