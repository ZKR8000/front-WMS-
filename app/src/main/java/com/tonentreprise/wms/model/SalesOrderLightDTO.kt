package com.tonentreprise.wms.model


data class SalesOrderLightDTO(
    val id: Long,
    val sohNum: String,
    val orderDate: String, // Si la date arrive sous forme ISO
    val status: String,
    val clientName: String,
    val preparatorUsername: String,
    val siteName: String?,
    val totalItems: Int,
    val salesOrderDetails: List<SalesOrderDetailLightDTO> // Assurez-vous que SalesOrderDetailLightDTO est bien défini dans le même package
)