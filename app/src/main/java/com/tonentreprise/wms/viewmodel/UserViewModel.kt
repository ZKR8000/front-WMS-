package com.tonentreprise.wms.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.tonentreprise.wms.model.UserRole

class UserViewModel : ViewModel() {

    // 🔥 État stockant le rôle de l'utilisateur (valeur par défaut : USER)
    private val _userRole = MutableStateFlow(UserRole.USER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    // ✅ Définit le rôle de l'utilisateur
    fun setUserRole(role: UserRole?) {
        _userRole.value = role ?: UserRole.USER
    }

    // ✅ Vérifie si l'utilisateur est un ADMIN
    fun isAdmin(): Boolean {
        return _userRole.value == UserRole.ADMIN
    }

    // ✅ Récupère le rôle actuel de l'utilisateur
    fun getUserRole(): UserRole {
        return userRole.value
    }
}
