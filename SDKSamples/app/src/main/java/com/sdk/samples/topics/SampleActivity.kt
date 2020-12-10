package com.sdk.samples.topics

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sdk.samples.SamplesViewModel
import com.sdk.samples.SingletonSamplesViewModelFactory

abstract class SampleActivity  : AppCompatActivity() {

    private val singletonSamplesViewModelFactory =  SingletonSamplesViewModelFactory(SamplesViewModel.getInstance())
    lateinit var viewModel: SamplesViewModel
    
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        viewModel = ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java)
    }
}