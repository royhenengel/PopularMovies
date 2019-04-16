package com.example.popularmovies.ui.common.scrollingthumbnail.view

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popularmovies.ui.common.scrollingthumbnail.model.ScrollingThumbnailClickListener
import com.example.popularmovies.ui.common.scrollingthumbnail.model.ThumbnailUiEntity
import com.example.popularmovies.ui.common.scrollingthumbnail.viewmodel.ScrollingThumbnailsViewUiModel

class ScrollingThumbnailsView @JvmOverloads constructor(

        context: Context,

        attrs: AttributeSet? = null,

        defStyleAttr: Int = 0

) : RecyclerView(context, attrs, defStyleAttr) {

    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var uiModel: ScrollingThumbnailsViewUiModel

    private var thumbnailClickListener: ScrollingThumbnailClickListener? = null

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        setHasFixedSize(true)

        background = null
    }

    fun setUiModel(scrollingThumbnailsViewUiModel: ScrollingThumbnailsViewUiModel, lifecycleOwner: LifecycleOwner) {

        this.lifecycleOwner = lifecycleOwner
        this.uiModel = scrollingThumbnailsViewUiModel

        observe()
    }

    fun setClickListener(scrollingThumbnailClickListener: ScrollingThumbnailClickListener){

        thumbnailClickListener = scrollingThumbnailClickListener
    }

    private fun observe() {

        uiModel.thumbnailsLiveData.observe(lifecycleOwner, Observer { handleThumbnailsData(it) })
        uiModel.clickListenerLiveEvent.observe(lifecycleOwner, Observer { handleThumbnailClickedEvent(it) })
    }

    private fun handleThumbnailsData(thumbnailUiEntities: List<ThumbnailUiEntity>) {

        adapter = ScrollingThumbnailAdapter(thumbnailUiEntities, uiModel)
    }

    private fun handleThumbnailClickedEvent(positing: Int) {

        thumbnailClickListener?.onThumbnailClicked(positing)
    }

}