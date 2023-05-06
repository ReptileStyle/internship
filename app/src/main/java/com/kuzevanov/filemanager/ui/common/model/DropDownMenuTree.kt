package com.kuzevanov.filemanager.ui.common.model



sealed class DropdownMenuTreeNodes{
    abstract val title:String
    abstract val event:Any?
    data class FolderNode(override val title:String):DropdownMenuTreeNodes(){
        override val event: Any? = null
    }
    data class ActionNode(override val title:String, override val event: Any):DropdownMenuTreeNodes()
}