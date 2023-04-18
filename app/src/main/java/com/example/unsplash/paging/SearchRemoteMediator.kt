package com.example.unsplash.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.unsplash.db.UnsplashDatabase
import com.example.unsplash.db.FeedRemoteKeys
import com.example.unsplash.models.Photo
import com.example.unsplash.networking.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val query: String,
    private val api: UnsplashApi,
    private val database: UnsplashDatabase
) : RemoteMediator<Int, Photo>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Photo>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextPage?.minus(1) ?: UNSPLASH_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevPage
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextPage
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        try {
            val apiResponse = api.searchPhotosByKey(query, page, state.config.pageSize)
            val photos = apiResponse.results
            val endOfPaginationReached = photos.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.unsplashRemoteKeysDao().deleteAllRemoteKeys()
                    database.unsplashImageDao().deleteAllImages()
                }
                val prevKey = if (page == UNSPLASH_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = photos.map {
                    FeedRemoteKeys(id = it.id, prevPage = prevKey, nextPage = nextKey)
                }
                database.unsplashRemoteKeysDao().addAllRemoteKeys(keys)
                database.unsplashImageDao().addImages(photos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Photo>
    ): FeedRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                database.unsplashRemoteKeysDao().getRemoteKeys(id = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Photo>
    ): FeedRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                database.unsplashRemoteKeysDao().getRemoteKeys(id = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Photo>
    ): FeedRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.unsplashRemoteKeysDao().getRemoteKeys(id = id)
            }
        }
    }
}