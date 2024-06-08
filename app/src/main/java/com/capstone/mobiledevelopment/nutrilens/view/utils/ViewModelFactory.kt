package com.capstone.mobiledevelopment.nutrilens.view.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.mobiledevelopment.nutrilens.data.repository.SleepRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.StepRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.StoryRepository
import com.capstone.mobiledevelopment.nutrilens.data.repository.UserRepository
import com.capstone.mobiledevelopment.nutrilens.di.Injection.provideSleepCountRepository
import com.capstone.mobiledevelopment.nutrilens.di.Injection.provideStepCountRepository
import com.capstone.mobiledevelopment.nutrilens.di.Injection.provideStoryRepository
import com.capstone.mobiledevelopment.nutrilens.di.Injection.provideUserRepository
import com.capstone.mobiledevelopment.nutrilens.view.login.LoginViewModel
import com.capstone.mobiledevelopment.nutrilens.view.main.MainViewModel
import com.capstone.mobiledevelopment.nutrilens.view.settings.SettingsViewModel
import com.capstone.mobiledevelopment.nutrilens.view.signup.SignupViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val stepRepository: StepRepository,
    private val sleepRepository: SleepRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository, sleepRepository ,stepRepository) as T
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
            return INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    provideUserRepository(context),
                    provideStoryRepository(context),
                    provideStepCountRepository(context),
                    provideSleepCountRepository(context)
                ).also { INSTANCE = it }
            }
        }
    }
}