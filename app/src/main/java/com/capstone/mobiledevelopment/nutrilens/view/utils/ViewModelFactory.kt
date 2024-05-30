package com.capstone.mobiledevelopment.nutrilens.view.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.di.Injection
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginViewModel
import com.capstone.mobiledevelopment.nutrilens.view.main.MainViewModel
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsViewModel
import com.capstone.mobiledevelopment.nutrilens.view.signup.SignupViewModel
import com.capstone.mobiledevelopment.nutrilens.view.utils.step.StepCounter

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val stepCounter: StepCounter
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository, stepCounter) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    val userRepository = Injection.provideUserRepository(context)
                    val storyRepository = Injection.provideStoryRepository(context)
                    val stepCounter = Injection.provideStepCounter(context)
                    INSTANCE = ViewModelFactory(userRepository, storyRepository, stepCounter)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}