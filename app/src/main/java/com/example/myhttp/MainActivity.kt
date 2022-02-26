package com.example.myhttp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myhttp.databinding.ActivityMainBinding
import com.example.myhttp.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    // Retrofit本体
    private val retrofit = Retrofit.Builder().apply {
        baseUrl("http://localhost:3000/posts/")
    }.build()

    // サービスクラスの実装オブジェクト取得
    private val service = retrofit.create(MyService::class.java)

    // 通信全体で利用するMediaType
    private val mediaType: MediaType = "application/json; charset=utf-8".toMediaTypeOrNull()!!

    private val myViewModel: MyViewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(MyViewModel::class.java)
    }

    private val scope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.model = myViewModel
        binding.lifecycleOwner = this
    }

    /**
     * POSTボタンが押された時の処理
     *
     * @param view
     */
    fun onPostButtonClick(view: View) {

        if (!myViewModel.author.value.isNullOrBlank() && !myViewModel.title.value.isNullOrBlank()) {


            val json = Util.createJson(
                title = myViewModel.title.value!!,
                author = myViewModel.author.value!!
            )

            val requestBody = RequestBody.create(mediaType, json)

            val post = service.postRawRequestForPosts(requestBody)

            scope.launch {
                val responseBody = post.execute()

                responseBody.body()?.let {

                    myViewModel.result.postValue(it.string())

                }
            }
        }

    }

    /**
     * PUTボタンが押された時の処理
     *
     * @param view
     */
    fun onPutButtonClick(view: View) {

        if ((!myViewModel.id.value.isNullOrBlank()
                    && !myViewModel.title.value.isNullOrBlank()
                    && !myViewModel.author.value.isNullOrBlank())
        ) {

            val json = Util.createJson(
                id = myViewModel.id.value!!.toString(),
                title = myViewModel.title.value!!.toString(),
                author = myViewModel.author.value!!.toString()
            )

            val requestBody = RequestBody.create(mediaType, json)

            val put = service.putRawRequestForPosts(myViewModel.id.value!!.toString(), requestBody)

            scope.launch {
                val responseBody = put.execute()

                responseBody.body()?.let {

                    myViewModel.result.postValue(it.string())

                }
            }
        }

    }

    /**
     * DELETEボタンが押された時の処理
     *
     * @param view
     */
    fun onDeleteButtonClick(view: View) {


        if (!myViewModel.id.value.isNullOrBlank()) {

            val delete = service.deletePathParam(myViewModel.id.value!!)

            scope.launch {

                if (delete.execute().isSuccessful) {
                    withContext(Dispatchers.Main) {

                        Toast.makeText(this@MainActivity, "削除成功", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    /**
     * GETボタンが押された時の処理
     *
     * @param view
     */
    fun onGetButtonClick(view: View) {


        val get = service.getRawResponseForPosts()

        scope.launch {

            val responseBody = get.execute()

            responseBody.body()?.let {

                myViewModel.result.postValue(it.string())

            }
        }
    }
}