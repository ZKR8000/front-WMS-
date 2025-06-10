package com.tonentreprise.wms.ui.screens

val transfertsOrigine = mapOf(
    "TR001" to "Site A",
    "TR002" to "Site B",
    "TR003" to "Site A",
    "TR004" to "Site C"
)
val transfertsDestination = mapOf(
    "TR001" to "Site B",
    "TR002" to "Site C",
    "TR003" to "Site D",
    "TR004" to "Site A"
)
val transfertsDates = mapOf(
    "TR001" to "11/06/2025",
    "TR002" to "08/06/2025",
    "TR003" to "09/06/2025",
    "TR004" to "07/06/2025"
)
val transfertsStatuts = mapOf(
    "TR001" to "En cours",
    "TR002" to "Terminé",
    "TR003" to "En cours",
    "TR004" to "Terminé"
)
val transfertsProduits: Map<String, List<String>> = mapOf(
    "TR001" to listOf("Produit X (Qté : 30)", "Produit Y (Qté : 50)"),
    "TR002" to listOf("Produit Z (Qté : 15)"),
    "TR003" to listOf("Produit M (Qté : 20)", "Produit N (Qté : 10)"),
    "TR004" to listOf("Produit Q (Qté : 40)")
)