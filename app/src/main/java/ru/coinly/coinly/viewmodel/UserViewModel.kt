/* package ru.coinly.coinly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.coinly.coinly.data.entity.User
import ru.coinly.coinly.repository.CoinlyRepository

class UserViewModel(private val repository: CoinlyRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Регистрация пользователя
    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        return try {
            // Проверяем, существует ли пользователь
            val usernameExists = repository.checkUsernameExists(username) > 0
            val emailExists = repository.checkEmailExists(email) > 0

            if (usernameExists || emailExists) {
                false // Пользователь уже существует
            } else {
                // Создаем нового пользователя
                val user = User(
                    username = username,
                    email = email,
                    passwordHash = password // Пока без шифрования, добавим позже
                )
                repository.insertUser(user)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    // Авторизация пользователя
    suspend fun loginUser(username: String, password: String): Boolean {
        return try {
            val user = repository.getUserByUsername(username)
            if (user != null && user.passwordHash == password) {
                _currentUser.value = user
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Выход пользователя
    fun logout() {
        _currentUser.value = null
    }
}

 */