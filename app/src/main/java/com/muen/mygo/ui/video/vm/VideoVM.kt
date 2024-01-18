package com.muen.mygo.ui.video.vm

import androidx.lifecycle.ViewModel
import com.muen.mygo.repo.AppServiceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoVM @Inject constructor(private val repo: AppServiceRepo):ViewModel() {

}