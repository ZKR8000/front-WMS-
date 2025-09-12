package com.tonentreprise.wms.model


data class SalesOrderDetailLightDTO(

    val product: ProductDTO,
    val productName: String?, // Nullable, car il peut être null dans la base de données
    val quantityToPrepare: Int,
    val lineStatus: String,
    val dlvdat: String, // Reste en String si c'est comme ça dans la base de données, ou tu peux passer à LocalDate
    val uom: String
)