package com.tonentreprise.wms.model

import java.time.LocalDate

data class Operation(
    val type: String, // "Entrée" ou "Sortie"
    val produit: String,
    val fournisseur: String,
    val quantite: Int,
    val motif: String,
    val date: LocalDate
)
