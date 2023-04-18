package com.example.unsplash.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.unsplash.adapters.AdaptersListener
import com.example.unsplash.adapters.FeedAdapter
import com.example.unsplash.adapters.PhotoComparator
import com.example.unsplash.adapters.PhotoLoadStateAdapter
import com.example.unsplash.databinding.FragmentFeedBinding
import com.example.unsplash.models.Photo
import com.example.unsplash.ui.viewmodels.FeedViewModel
import com.example.unsplash.ui.viewmodels.UiAction
import com.example.unsplash.ui.viewmodels.UiState
import com.example.unsplash.utils.ViewBindingFragment
import com.example.unsplash.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class FeedFragment: ViewBindingFragment<FragmentFeedBinding>(FragmentFeedBinding::inflate), AdaptersListener {
    private val viewModel by viewModels<FeedViewModel>()
    private lateinit var thisView: View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )
        thisView = view
    }

    override fun onClickItem(photo: Photo) {
        val action = FeedFragmentDirections.actionFeedFragmentToPhotoDetailsFragment(photo.id)
        findNavController().navigate(action)
    }

    override fun onClickLike(photo: Photo, position: Int) {
        if (!photo.liked) {
            postLike(photo.id, position)
        } else {
            deleteLike(photo.id, position)
        }
    }

    override fun onClickInfo(photo: Photo) {
        openAuthorProfile(photo)
    }

    private fun openAuthorProfile(photo: Photo) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://unsplash.com/@${photo.author.username}?utm_source=proba77701&utm_medium=referral")
        )
        startActivity( browserIntent, null)
    }

    private fun postLike(id: String, position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val code = viewModel.postLikeToPhoto(id, viewLifecycleOwner.lifecycleScope)
            if (code == 201) {
            } else {
                showSnackServerConnectError(thisView, requireContext())
            }
        }
    }

    private fun deleteLike(id: String,  position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val code = viewModel.deleteLikeToPhoto(id, viewLifecycleOwner.lifecycleScope)
            if (code == 200) {
            } else {
                showSnackServerConnectError(thisView, requireContext())
            }
        }
    }

    private fun FragmentFeedBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        uiActions: (UiAction) -> Unit
    ) {
        val feedAdapter = FeedAdapter(
        PhotoComparator
        )
        feedAdapter.setOnClickListener(this@FeedFragment)
        val header = PhotoLoadStateAdapter { feedAdapter.retry() }
        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = feedAdapter.withLoadStateHeaderAndFooter(
                header = header,
                footer = PhotoLoadStateAdapter { feedAdapter.retry() }
            )
        }
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            header = header,
            feedAdapter = feedAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
        updatePhotoListFromInput(uiActions)
    }

    private fun FragmentFeedBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchPhotoEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatePhotoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchPhotoEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatePhotoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchPhotoEditText::setText)
        }
    }

    private fun FragmentFeedBinding.updatePhotoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchPhotoEditText.text.trim().let {
            if (it.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun FragmentFeedBinding.bindList(
        header: PhotoLoadStateAdapter,
        feedAdapter: FeedAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { feedAdapter.retry() }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = feedAdapter.loadStateFlow
            .distinctUntilChangedBy { it.source.refresh }
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(feedAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) recyclerView.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            feedAdapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && feedAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && feedAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                recyclerView.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && feedAdapter.itemCount == 0
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}