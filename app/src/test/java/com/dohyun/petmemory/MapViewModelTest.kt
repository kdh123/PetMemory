package com.dohyun.petmemory

import com.dohyun.domain.diary.Diary
import com.dohyun.domain.diary.DiaryRepository
import com.dohyun.petmemory.diary.FakeDiaryRepository
import com.dohyun.petmemory.map.MapUiState
import com.dohyun.petmemory.map.MapViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MapViewModelTest {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var diaryRepository: DiaryRepository

    @Test
    fun `지도 데이터 성공 테스트`() = runBlocking {
        diaryRepository = FakeDiaryRepository()
        mapViewModel = MapViewModel(diaryRepository = diaryRepository)

        delay(100L)
        assertTrue(mapViewModel.uiState.value is MapUiState.Map)
        assertEquals((mapViewModel.uiState.value as MapUiState.Map).diaries.size, 6)
    }

    @Test
    fun `지도 데이터 실패 테스트`() = runBlocking {
        diaryRepository = FakeDiaryRepository().apply {
            setError()
        }

        mapViewModel = MapViewModel(diaryRepository = diaryRepository)

        delay(100L)
        assertTrue(mapViewModel.uiState.value is MapUiState.Error)
    }

}