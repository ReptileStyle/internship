package com.kuzevanov.filemanager.ui.screens.directory.component

import com.github.adriankuta.datastructure.tree.tree
import com.kuzevanov.filemanager.ui.common.model.DropdownMenuTreeNodes
import com.kuzevanov.filemanager.ui.common.model.SortingMode
import com.kuzevanov.filemanager.ui.common.model.SortingOrder


//need to think better about structure, looks stupid


val DropdownMenuTree = tree<DropdownMenuTreeNodes>(DropdownMenuTreeNodes.FolderNode("menu")){
    child(DropdownMenuTreeNodes.FolderNode("Сортировка")) {
        child(DropdownMenuTreeNodes.ActionNode("По возрастению", SortingOrder.Ascending))
        child(DropdownMenuTreeNodes.ActionNode("По убыванию",SortingOrder.Descending))
        child(DropdownMenuTreeNodes.ActionNode("По размеру",SortingMode.BySize))
        child(DropdownMenuTreeNodes.ActionNode("По типу",SortingMode.ByType))
        child(DropdownMenuTreeNodes.ActionNode("По дате",SortingMode.ByDate))
        child(DropdownMenuTreeNodes.ActionNode("По названию",SortingMode.ByName))
    }
}



