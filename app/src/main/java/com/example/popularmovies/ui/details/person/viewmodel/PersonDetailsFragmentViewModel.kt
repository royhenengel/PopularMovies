package com.example.popularmovies.ui.details.person.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.popularmovies.BuildConfig
import com.example.popularmovies.data.MoviesRepository
import com.example.popularmovies.data.details.entity.cast.PersonDetailsEntity
import com.example.popularmovies.data.details.entity.movie.MovieActorInEntity
import com.example.popularmovies.ui.common.scrollingthumbnail.entity.ThumbnailUiEntity
import com.example.popularmovies.ui.common.scrollingthumbnail.viewmodel.ScrollingThumbnailsViewUiModel
import com.example.popularmovies.ui.details.person.entity.PersonDetailsUiEntity
import com.example.popularmovies.ui.details.person.entity.mapper.MovieActorInEntityToThumbnailUiEntityMapper
import com.example.popularmovies.ui.details.person.entity.mapper.PersonDetailsEntityToUiEntityMapper
import com.example.popularmovies.viewmodel.SingleLiveEvent
import kotlinx.coroutines.*
import javax.inject.Inject

class PersonDetailsFragmentViewModel @Inject constructor(

        private val repository: MoviesRepository,

        private val personDetailsEntityToUiEntityMapper: PersonDetailsEntityToUiEntityMapper,

        private val movieActorInEntityToThumbnailUiEntityMapper: MovieActorInEntityToThumbnailUiEntityMapper

) : ViewModel() {

    val personDetailsUiEntityLiveData = MutableLiveData<PersonDetailsUiEntity>()
    val movieThumbnailsUiModelLiveData = MutableLiveData<ScrollingThumbnailsViewUiModel>()

    val movieActorInClickedLiveEvent = SingleLiveEvent<MovieActorInEntity>()
    val openInBrowserLiveEvent = SingleLiveEvent<String>()

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val getPersonDetailsJob: Job = Job()

    private lateinit var personDetailsEntity: PersonDetailsEntity
    private lateinit var movieActorInList: List<MovieActorInEntity>

    fun start(personId: Int) {

        uiScope.launch {

            val detailsTask = async(Dispatchers.IO) { repository.getCastDetails(personId) }
            val moviesActorInTask = async(Dispatchers.IO) { repository.getCastMovies(personId) }

            personDetailsEntity = detailsTask.await()
            val uiEntity = personDetailsEntityToUiEntityMapper.apply(personDetailsEntity)

            movieActorInList = moviesActorInTask.await()
            val thumbnails = mapMoviesActorInToThumbnails(movieActorInList)
            val scrollingThumbnailsViewUiModel = ScrollingThumbnailsViewUiModel(thumbnails)

            movieThumbnailsUiModelLiveData.value = scrollingThumbnailsViewUiModel
            personDetailsUiEntityLiveData.value = uiEntity
        }
    }

    override fun onCleared() {
        super.onCleared()

        getPersonDetailsJob.cancel()
    }

    fun onThumbnailClicked(position: Int) {

        val thumbnailClicked = movieActorInList[position]
        movieActorInClickedLiveEvent.value = thumbnailClicked
    }

    fun onOpenInBrowserClicked() {

        val url = "${BuildConfig.IMDB_BASE_URL}${personDetailsEntity.imdbId}"
        openInBrowserLiveEvent.value = url
    }

    private fun mapMoviesActorInToThumbnails(movieActorInList: List<MovieActorInEntity>): List<ThumbnailUiEntity> {

        val thumbnails = arrayListOf<ThumbnailUiEntity>()
        for (item in movieActorInList) {
            thumbnails.add(movieActorInEntityToThumbnailUiEntityMapper.apply(item))
        }

        return thumbnails
    }

}
