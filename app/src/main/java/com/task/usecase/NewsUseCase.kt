package com.task.usecase

import androidx.lifecycle.MutableLiveData
import com.task.data.DataRepository
import com.task.data.remote.ServiceError
import com.task.data.remote.dto.NewsItem
import com.task.data.remote.dto.NewsModel
import com.task.ui.base.listeners.BaseCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Created by AhmedEltaher on 5/12/2016
 */

class NewsUseCase @Inject
constructor(private val dataRepository: DataRepository, override val coroutineContext: CoroutineContext) : UseCase, CoroutineScope {
    private var newsModle: MutableLiveData<NewsModel>? = MutableLiveData()

    override fun getNews(callback: BaseCallback) {
        launch {
            try {
                val serviceResponse = async(Dispatchers.IO) { dataRepository.requestNews() }.await()
                if (serviceResponse?.code == ServiceError.SUCCESS_CODE) {
                    val data = serviceResponse.data
                    callback.onSuccess(data as NewsModel)
                } else {
                    callback.onFail()
                }
            } catch (e: Exception) {
                callback.onFail()
            }
        }
    }

    override fun searchByTitle(news: List<NewsItem>, keyWord: String): NewsItem? {
        for (newsItem in news) {
            if (!newsItem.title.isNullOrEmpty() && newsItem.title!!.toLowerCase().contains(keyWord.toLowerCase())) {
                return newsItem
            }
        }
        return null
    }

    private fun transformServiceResponseToDataObject(serviceResponse: Any?): MutableLiveData<NewsModel> {
        var newsModel: NewsModel = serviceResponse as NewsModel
        var newsResponse: MutableLiveData<NewsModel> = MutableLiveData()
        newsResponse.value = newsModel
        return newsResponse
    }
}
