package com.kuzevanov.filemanager.fileSystem.model

sealed class SpecialFolderTypes {
    object Images : SpecialFolderTypes()
    object Videos : SpecialFolderTypes()
    object Music : SpecialFolderTypes()
    object Apps : SpecialFolderTypes()
    object Zip : SpecialFolderTypes()
    object Docs : SpecialFolderTypes()
    object Downloads : SpecialFolderTypes()
    object AddNew : SpecialFolderTypes() // not working atm

    fun toInt(): Int {
        return when (this) {
            AddNew -> 0
            Apps -> 1
            Docs -> 2
            Downloads -> 3
            Images -> 4
            Music -> 5
            Videos -> 6
            Zip -> 7
        }
    }

    companion object {
        fun getTypeFromInt(number: Int):SpecialFolderTypes {
            return when (number) {
                0 -> AddNew
                1 -> Apps
                2 -> Docs
                3 -> Downloads
                4 -> Images
                5 -> Music
                6 -> Videos
                7 -> Zip
                else-> throw Exception("wrong int, must be from 0 to 7")
            }
        }
    }
}