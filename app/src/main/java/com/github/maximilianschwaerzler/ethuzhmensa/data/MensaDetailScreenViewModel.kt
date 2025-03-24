package com.github.maximilianschwaerzler.ethuzhmensa.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository
import com.github.maximilianschwaerzler.ethuzhmensa.repository.MenuRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.OfferWithPrices
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.entities.Facility
import com.github.maximilianschwaerzler.ethuzhmensa.repository.FacilityRepository2
import com.github.maximilianschwaerzler.ethuzhmensa.repository.MenuRepository2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MensaDetailScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityRepository2,
    private val menuRepository: MenuRepository2,
) : ViewModel() {
    private val _menus = MutableStateFlow<OfferWithPrices?>(null)
    val menus = _menus.asStateFlow()

    private val _facility = MutableStateFlow<Facility?>(null)
    val facility = _facility.asStateFlow()

    fun loadFacilityAndMenus(facilityId: Int, date: LocalDate) = viewModelScope.launch {
        facilityInfoRepo.getFacilityById(facilityId).let {
            _facility.value = it
        }

        menuRepository.getOfferForFacilityDate(facilityId, date).let {
            _menus.value = it
        }
    }
}