package com.replyflow.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SimpleFactory<T : ViewModel>(private val create: () -> T) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <V : ViewModel> create(modelClass: Class<V>): V = create() as V
}
