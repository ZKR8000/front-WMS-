package com.tonentreprise.wms.model

import java.time.LocalDate

data class SalesOrderDetailLightDTO(
    val product: ProductDTO,
    val productName: String,
    val quantityToPrepare: Int,
    val lineStatus: String,
    val dlvdat: String, // Utilise String si tu reçois une date ISO, sinon adapte à `LocalDate`
    val uom: String
)
