package com.dongyang.android.youdongknowme.ui.view.cafeteria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dongyang.android.youdongknowme.R
import com.dongyang.android.youdongknowme.data.remote.entity.Cafeteria
import com.dongyang.android.youdongknowme.data.repository.CafeteriaRepository
import com.dongyang.android.youdongknowme.standard.base.BaseViewModel
import com.dongyang.android.youdongknowme.standard.network.NetworkResult
import com.dongyang.android.youdongknowme.standard.util.Weekdays
import com.dongyang.android.youdongknowme.ui.view.util.Event
import com.dongyang.android.youdongknowme.ui.view.util.ResourceProvider
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate

class CafeteriaViewModel(
    private val cafeteriaRepository: CafeteriaRepository,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel() {

    private val _errorState: MutableLiveData<Event<Int>> = MutableLiveData()
    val errorState: LiveData<Event<Int>> = _errorState

    private val _isError: MutableLiveData<Boolean> = MutableLiveData()
    val isError: LiveData<Boolean> = _isError

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedDate: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _cafeteriaList: MutableLiveData<List<Cafeteria>> = MutableLiveData()
    val cafeteriaList: LiveData<List<Cafeteria>> = _cafeteriaList

    private val _koreaMenus: MutableLiveData<List<String>> = MutableLiveData()
    val koreaMenus: LiveData<List<String>> = _koreaMenus

    private val _daysMenus: MutableLiveData<List<String>> = MutableLiveData()
    val daysMenus: LiveData<List<String>> = _daysMenus

    private val emptyMenu = listOf(resourceProvider.getString(R.string.cafeteria_no_menu))

    init {
        fetchCafeteria()
    }

    fun fetchCafeteria() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            when (val result = cafeteriaRepository.fetchMenuList()) {
                is NetworkResult.Success -> {
                    val menuList = result.data
                    _cafeteriaList.value = menuList
                    _selectedDate.value = LocalDate.now()
                    selectedDate.value?.let { updateMenuList(it) }
                    _isError.postValue(false)
                    _isLoading.postValue(false)
                }

                is NetworkResult.Error -> {
                    handleError(result, _errorState)
                    _isError.postValue(true)
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun updateMenuList(selectedDate: LocalDate) {
        val cafeteriaList = _cafeteriaList.value ?: emptyList()
        _selectedDate.value = selectedDate
        val selectedMenu = cafeteriaList.find { it.date == selectedDate.toString() }?.menus
        _koreaMenus.postValue(
            if (selectedMenu.isNullOrEmpty()) {
                emptyMenu
            } else {
                selectedMenu
            }
        )
    }

    fun updateDaysMenu(selectedDate: LocalDate) {
        viewModelScope.launch {
            val dateToWeekday: Weekdays = Weekdays.from(selectedDate.dayOfWeek)
            runCatching {
                cafeteriaRepository.fetchDaysMenus(dateToWeekday)
            }.onSuccess { daysMenus ->
                val formatter = DecimalFormat("#,###")
                val formattedMenuWithPrice = daysMenus.map { "${it.menuNameKr} ${formatter.format(it.price)}원" }
                _daysMenus.value = formattedMenuWithPrice
            }.onFailure {
                _isError.value = true
            }
        }
    }
}
