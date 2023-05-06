package com.kuzevanov.filemanager.ui.common.model

sealed class SortingMode {
    object ByType:SortingMode()
    object BySize:SortingMode()
    object ByName:SortingMode()
    object ByDate:SortingMode()
}
sealed class SortingOrder{
    object Ascending:SortingOrder()
    object Descending:SortingOrder()
}