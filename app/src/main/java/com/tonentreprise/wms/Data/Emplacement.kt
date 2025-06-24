package com.tonentreprise.wms.model

data class Emplacement(
    val code: String,
    val type: String,
    val capaciteMax: Int,
    val capaciteOccupe: Int,
    val categorieProd: String,
    val statut: Boolean
)
