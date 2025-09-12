package com.tonentreprise.wms.model

data class ProductDTO(
    val artnum: String,  // Numéro de l'article
    val artdes: String,  // Description de l'article
    val sku: String,     // SKU de l'article
    val barcode: String?, // Code-barres, peut être nul
    val category: CategoryDTO, // Catégorie du produit
    val categoryName: String? = null,  // Nom de la catégorie (TCLNAM)
    val categoryCode: String? = null,  // Code de la catégorie (TCLCOD)
    val stockQty: Int? = null,


    // Champs ignorés, ils ne seront pas envoyés à partir du backend

    val isUniqueToWarehouse: Boolean? = null  // Est unique à l'entrepôt
)