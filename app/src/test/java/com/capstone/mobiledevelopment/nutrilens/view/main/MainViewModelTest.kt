package com.capstone.mobiledevelopment.nutrilens.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.capstone.mobiledevelopment.nutrilens.DataDummy
import com.capstone.mobiledevelopment.nutrilens.MainDispatcherRule
import com.capstone.mobiledevelopment.nutrilens.data.pref.UserModel
import com.capstone.mobiledevelopment.nutrilens.data.reponse.ListStoryItem
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.getOrAwaitValue
import com.capstone.mobiledevelopment.nutrilens.view.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.logging.Level
import java.util.logging.Logger

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        // Suppress logging during tests
        val logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
        logger.level = Level.OFF

        viewModel = MainViewModel(userRepository, storyRepository)
    }

    @org.junit.jupiter.api.Test
    fun testFetchToken() = runTest {
        val email = "dummy_email"
        val token = "dummy_token"
        val userModel = UserModel(email, token)
        `when`(userRepository.getSession()).thenReturn(flowOf(userModel))

        viewModel.fetchToken()

        assertEquals(token, viewModel.token.getOrAwaitValue())
    }

    @org.junit.jupiter.api.Test
    fun testGetStories_success() = runTest {
        val email = "dummy_email"
        val token = "dummy_token"
        val userModel = UserModel(email, token)
        val storyItems = DataDummy.generateDummyStoriesResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(storyItems)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        `when`(userRepository.getSession()).thenReturn(flowOf(userModel))
        `when`(storyRepository.getStories(token)).thenReturn(expectedStories)

        viewModel.fetchToken()
        val actualStoriesPagingData: PagingData<ListStoryItem> = viewModel.storiesPagingData.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStoriesPagingData)

        assertNotNull(differ.snapshot())
        assertEquals(storyItems.size, differ.snapshot().size)
        assertEquals(storyItems[0], differ.snapshot()[0])
    }

    @org.junit.jupiter.api.Test
    fun testGetStories_noData() = runTest {
        val email = "dummy_email"
        val token = "dummy_token"
        val userModel = UserModel(email, token)
        val emptyPagingData: PagingData<ListStoryItem> = PagingData.empty()

        `when`(userRepository.getSession()).thenReturn(flowOf(userModel))
        `when`(storyRepository.getStories(token)).thenReturn(MutableLiveData(emptyPagingData))

        viewModel.fetchToken()
        val actualStoriesPagingData: PagingData<ListStoryItem> = viewModel.storiesPagingData.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStoriesPagingData)

        assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
            return state.anchorPosition
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
            return LoadResult.Page(emptyList(), null, null)
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}