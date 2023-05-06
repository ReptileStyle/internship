package com.kuzevanov.filemanager.ui.common

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.adriankuta.datastructure.tree.TreeNode
import com.kuzevanov.filemanager.ui.common.model.DropdownMenuTreeNodes
import com.kuzevanov.filemanager.ui.theme.MyApplicationTheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TreeDropDownMenu(
    modifier: Modifier = Modifier,
    tree: TreeNode<DropdownMenuTreeNodes>,
            onEvent
    :(DropdownMenuTreeNodes)->Unit,

    ) {
    var isMenuOpen by remember { mutableStateOf(false) }
    IconButton(onClick = { isMenuOpen = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "OpenActionMenu"
        )
    }
    MyApplicationTheme(
        shapes = Shapes(
            small = RoundedCornerShape(7),
            medium = RoundedCornerShape(7),
            large = RoundedCornerShape(7)
        )
    ) {
        var currentNode by remember { mutableStateOf(tree) }
        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = {
                val parent = currentNode.parent
                if (parent != null)
                    currentNode = parent
                else
                    isMenuOpen = false
            },
            modifier = modifier.width(IntrinsicSize.Max)
        ) {
            currentNode.children.forEach {
                Surface(
                    onClick = {
                        if (it.children.isNotEmpty())
                            currentNode = it
                        else {
                            onEvent(it.value)
                            isMenuOpen = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it.value.title,
                        modifier = Modifier.padding(
                            vertical = 4.dp,
                            horizontal = 8.dp
                        )
                    )
                }
            }
        }
    }
}