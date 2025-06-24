package com.tonentreprise.wms.data

data class User(val nom: String, val email: String, val role: String)

val sampleUsers = listOf(
    User("Alice Dupont",   "alice@example.com",   "Admin"),
    User("Bob Martin",     "bob@example.com",     "Utilisateur"),
    User("Charlie Durand", "charlie@example.com", "Superviseur")
)
