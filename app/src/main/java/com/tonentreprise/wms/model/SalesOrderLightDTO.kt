package com.tonentreprise.wms.model

data class SalesOrderLightDTO(
    val id: Long,
    val sohNum: String,
    val orderDate: String, // pareil ici : si la date arrive sous forme ISO
    val status: String,
    val clientName: String,
    val preparatorUsername: String,
    val siteName: String?,
    val totalItems: Int,
    val salesOrderDetails: List<SalesOrderDetailLightDTO>
)
