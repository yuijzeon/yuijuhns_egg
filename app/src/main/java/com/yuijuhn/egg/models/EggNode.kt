package com.yuijuhn.egg.models

data class EggNode(
    val text: String?,
    val resourceId: String?,
    val className: String?,
    val packageName: String?,
    val contentDesc: String?,
    val checkable: Boolean,
    val checked: Boolean,
    val clickable: Boolean,
    val enabled: Boolean,
    val focusable: Boolean,
    val focused: Boolean,
    val scrollable: Boolean,
    val longClickable: Boolean,
    val password: Boolean,
    val selected: Boolean,
    val children: List<EggNode>?,
)