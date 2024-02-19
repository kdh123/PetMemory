package com.dohyun.petmemory.ui.home

import androidx.lifecycle.viewModelScope
import com.dohyun.domain.common.CommonResult
import com.dohyun.domain.diary.DiaryData
import com.dohyun.domain.diary.GetDiaryUseCase
import com.dohyun.domain.pet.PetDto
import com.dohyun.domain.pet.PetRepository
import com.dohyun.domain.weather.WeatherDto
import com.dohyun.domain.weather.WeatherRepository
import com.dohyun.petmemory.base.StateViewModel
import com.dohyun.petmemory.extension.handle
import com.dohyun.petmemory.ui.diary.DiaryEvent
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val getDiaryUseCase: GetDiaryUseCase,
    private val weatherRepository: WeatherRepository
) : StateViewModel<HomeState>(HomeState.None) {

    private val _weatherState: MutableStateFlow<WeatherState> = MutableStateFlow(WeatherState.None)
    val weatherState = _weatherState.asStateFlow()

    private var _petList = listOf<PetDto>()
    val petList
        get() = _petList

    var currentDiaryList = listOf<DiaryData>()
    val currentLocationList
        get() = currentDiaryList.filter { it.lat != 0.0 && it.lng != 0.0 }

    private val _sheetAlpha = MutableStateFlow(0f)
    val sheetAlpha = _sheetAlpha.asStateFlow()

    private var currentStartIndex = 0
    private var currentOffset = 18

    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            homeUiState.combine(getDiaries()) { state, diaries ->
                when (state) {
                    is HomeUiState.Loading -> {
                        HomeUiState.Success(diaries, listOf(), diaries
                                .filter { it.lat != 0.0 && it.lng != 0.0 }
                            .map { LatLng(it.lat!!, it.lng!!) })
                    }

                    is HomeUiState.Success -> {
                        state.copy(diaries = diaries, locations = diaries
                            .filter { it.lat != 0.0 && it.lng != 0.0 }
                            .map { LatLng(it.lat!!, it.lng!!) })
                    }

                    is HomeUiState.Fail -> {
                        state
                    }
                }
            }.catch {

            }.collect {
                _homeUiState.value = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            homeUiState.combine(getPets()) { state, pets ->
                when (state) {
                    is HomeUiState.Loading -> {
                        HomeUiState.Success(listOf(), pets, listOf())
                    }

                    is HomeUiState.Success -> {
                        state.copy(pets = pets)
                    }

                    is HomeUiState.Fail -> {
                        state
                    }
                }
            }.catch {

            }.collect {
                _homeUiState.value = it
            }
        }
    }

    fun setSheetOffset(offset: Float) {
        _sheetAlpha.value = offset
    }
    private suspend fun getDiaries(isPaging: Boolean = false): StateFlow<List<DiaryData>> {
        getDiaryUseCase(
            currentDiaryListSize = currentDiaryList.size,
            currentIndex = currentStartIndex,
            offset = currentOffset,
            isPaging = isPaging
        )?.run {
            currentStartIndex = indexAndOffset.index
            currentOffset = indexAndOffset.offset
            currentDiaryList = currentDiaryList + diaryList

            val isLoadMore = currentDiaryList.size >= currentOffset
        }

        return flow {
            emit(currentDiaryList)
        }.stateIn(viewModelScope)
    }

    fun getDiary(isPaging: Boolean = false) {
        _state.value = HomeState.Loading

        viewModelScope.handle(
            dispatcher = Dispatchers.IO,
            block = {
                getDiaryUseCase(
                    currentDiaryListSize = currentDiaryList.size,
                    currentIndex = currentStartIndex,
                    offset = currentOffset,
                    isPaging = isPaging
                )?.run {
                    currentStartIndex = indexAndOffset.index
                    currentOffset = indexAndOffset.offset
                    currentDiaryList = currentDiaryList + diaryList

                    val isLoadMore = currentDiaryList.size >= currentOffset

                    _state.value = HomeState.Load(diaryList = currentDiaryList, isLoadMore = isLoadMore)
                } ?: kotlin.run {
                    _state.value = HomeState.Load(diaryList = currentDiaryList, isLoadMore = false)
                }
            })
    }

    private suspend fun getPets(): StateFlow<List<PetDto>> {
        return flow {
            emit(petRepository.getAllPet().reversed())
        }.flowOn(Dispatchers.IO).stateIn(viewModelScope)
    }

    suspend fun getPetList(): List<PetDto> {
        _petList = petRepository.getAllPet().reversed()

        return _petList
    }

    fun commitSync(diaryEvent: DiaryEvent, diaryData: DiaryData) {
        _state.value = HomeState.Loading

        when (diaryEvent) {
            DiaryEvent.Edit -> {
                currentDiaryList = currentDiaryList.map {
                    if (it.id == diaryData.id) {
                        diaryData.copy()
                    } else {
                        it.copy()
                    }
                }
            }

            DiaryEvent.Save -> {
                currentDiaryList = listOf(diaryData) + currentDiaryList.map { it.copy() }
            }

            DiaryEvent.Delete -> {
                currentDiaryList = currentDiaryList.filter { it.id != diaryData.id }
            }

            else -> {
            }
        }
    }

    fun getWeather() {
        _state.value = HomeState.Loading

        val date = Date()
        val todayDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
        val currentTime = SimpleDateFormat("HH", Locale.getDefault()).format(date).let {
            val hour = it.toInt() + 1
            if (hour >= 24) {
                "0000"
            } else if (hour < 10) {
                "0${hour}00"
            } else {
                "${hour}00"
            }
        }

        viewModelScope.handle(
            block = {
                val result = weatherRepository.getTodayWeather(todayDate = todayDate, currentTime = currentTime)

                when (result) {
                    is CommonResult.Success -> {
                        result.data?.run {
                            _weatherState.value = WeatherState.Success(getWeatherState(sky = sky, pty = pty))
                        }
                    }

                    is CommonResult.Fail -> {
                        _weatherState.value = WeatherState.Fail("날씨 정보를 불러올 수 없습니다")
                    }
                }
            },
            error = {
                _weatherState.value = WeatherState.Fail("날씨 정보를 불러올 수 없습니다")
            })
    }
}

private fun getWeatherState(sky: String, pty: String): String {
    val isRainy = pty != WeatherDto.PTY_NO

    if (isRainy) {
        return "오늘은 비가 와서 산책 가기 어려움"
    }

    return when (sky) {
        WeatherDto.SKY_GOOD -> {
            "산책 가기 좋은 날씨"
        }

        WeatherDto.SKY_BAD,
        WeatherDto.SKY_CLOUDY -> {
            "구름이 많은 날씨"
        }

        else -> {
            "날씨 정보 없음"
        }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val diaries: List<DiaryData>,
        val pets: List<PetDto>,
        val locations: List<LatLng>
    ) : HomeUiState
    data class Fail(val message: String?) : HomeUiState
}