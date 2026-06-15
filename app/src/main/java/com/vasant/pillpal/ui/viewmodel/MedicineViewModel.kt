package com.vasant.pillpal.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasant.pillpal.data.db.Medicine
import com.vasant.pillpal.data.db.MedicineEvent
import com.vasant.pillpal.repository.MedicineRepo
import com.vasant.pillpal.ui.components.cancelAlarm
import com.vasant.pillpal.ui.components.setUpAlarm
import com.vasant.pillpal.ui.presentation.MedicineState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicineRepo: MedicineRepo,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _state = MutableStateFlow(MedicineState())
    val state = _state
    private val _medicine = medicineRepo.getMedicine()
    val meds: StateFlow<List<Medicine>> =
        _medicine.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            medicineRepo.syncWithBackend()
        }
    }

    fun onEvent(event: MedicineEvent) {
        when (event) {
            is MedicineEvent.SaveMedicine -> {
                val medicine = Medicine(
                    medName = _state.value.medicineName,
                    time = _state.value.date,
                    dosage = _state.value.dosage,
                    note = _state.value.note,
                    medType = _state.value.med_type
                )
                setUpAlarm(
                    context = event.context, medicine
                )
                viewModelScope.launch {
                    medicineRepo.addMedicine(medicine = medicine)
                }
                _state.update {
                    it.copy(
                        medicineName = "",
                        date = 0,
                        dosage = "",
                        note = null,
                        med_type = it.med_type // keep selection
                    )
                }
            }

            is MedicineEvent.DeleteMedicine -> {
                // Cancel any scheduled alarm for this medicine before deleting it
                cancelAlarm(appContext, event.medicine)
                viewModelScope.launch {
                    medicineRepo.deleteMedicine(event.medicine)
                }
            }

            is MedicineEvent.SaveMedicineName -> {
                val medicine = Medicine(
                    medName = event.medicineName,
                    time = event.date,
                    dosage = event.dosage,
                    note = event.note,
                    isCompleted = event.isCompleted,
                    medType = event.med_type
                )
                viewModelScope.launch {
                    medicineRepo.addMedicine(medicine = medicine)
                }
            }

            is MedicineEvent.AddDosageChange -> {
                _state.update {
                    it.copy(
                        dosage = event.dosage
                    )
                }
                Log.d("Viewmodel", "onEvent: Dosage Add  = ${_state.value}")
            }

            is MedicineEvent.DateChanged -> {
                _state.update {
                    it.copy(date = event.date)
                }

                Log.d("Viewmodel", "onEvent: it.date = ${_state.value}")
            }

            is MedicineEvent.PendingMedicine -> {
                val medicineData = Medicine(
                    id = event.data.id,
                    medName = event.data.medName,
                    time = event.data.time,
                    dosage = event.data.dosage,
                    note = event.data.note,
                    isCompleted = true,
                    medType = event.data.medType
                )
                viewModelScope.launch {
                    medicineRepo.updateMedicine(medicineData)
                }
            }


            is MedicineEvent.MedicineNameChanged -> {
                _state.update {
                    it.copy(medicineName = event.medicineName)
                }
                Log.d("Viewmodel", "onEvent: medicineName change = ${_state.value}")
            }

            is MedicineEvent.MedicineTypeChanged -> {
                _state.update {
                    it.copy(med_type = event.med_type)
                }
                Log.d("Viewmodel", "onEvent: med_type change = ${_state.value.med_type}")
            }

            is MedicineEvent.NoteChanged -> {
                _state.update { it.copy(note = event.note) }
                Log.d("Viewmodel", "onEvent: note change = ${_state.value.note}")
            }
        }
    }
}