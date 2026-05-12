package com.pitiq.app.ui.screen.layout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pitiq.app.data.repository.LayoutRepository
import com.pitiq.app.domain.model.Layout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LayoutSelectionViewModel @Inject constructor(
    private val layoutRepository: LayoutRepository,
) : ViewModel() {

    private val _layouts = MutableStateFlow<List<Layout>>(emptyList())
    val layouts: StateFlow<List<Layout>> = _layouts.asStateFlow()

    private val _selected = MutableStateFlow<Layout?>(null)
    val selected: StateFlow<Layout?> = _selected.asStateFlow()

    init {
        viewModelScope.launch {
            layoutRepository.getLayouts().collect { list ->
                _layouts.value = list
                if (_selected.value == null) _selected.value = list.firstOrNull()
            }
        }
    }

    fun select(layout: Layout) {
        _selected.value = layout
    }
}
