package com.yuijuhn.egg.extensions

import android.view.accessibility.AccessibilityNodeInfo
import com.yuijuhn.egg.models.EggNode

fun AccessibilityNodeInfo.firstOrNull(isMatch: (AccessibilityNodeInfo) -> Boolean): AccessibilityNodeInfo? {
    this.toList().forEach {
        if (isMatch(it)) {
            return it
        } else {
            val child = it.firstOrNull(isMatch)
            if (child != null) {
                return child
            }
        }
    }

    return null
}

fun AccessibilityNodeInfo.any(isMatch: (AccessibilityNodeInfo) -> Boolean): Boolean {
    this.toList().forEach {
        if (isMatch(it)) {
            return true
        } else {
            if (it.any(isMatch)) {
                return true
            }
        }
    }

    return false
}

fun AccessibilityNodeInfo.toEggNode(): EggNode {
    val childrenNodes = mutableListOf<EggNode>()

    this.toList().forEach {
        childrenNodes.add(it.toEggNode())
    }

    return EggNode(
        text = text?.toString(),
        resourceId = viewIdResourceName,
        className = className?.toString(),
        packageName = packageName?.toString(),
        contentDesc = contentDescription?.toString(),
        checkable = isCheckable,
        checked = isChecked,
        clickable = isClickable,
        enabled = isEnabled,
        focusable = isFocusable,
        focused = isFocused,
        scrollable = isScrollable,
        longClickable = isLongClickable,
        password = isPassword,
        selected = isSelected,
        children = childrenNodes
    )
}

fun AccessibilityNodeInfo.toList(): List<AccessibilityNodeInfo> {
    val children = mutableListOf<AccessibilityNodeInfo>()

    for (i in 0 until childCount) {
        val child = getChild(i)
        if (child != null) {
            children.add(child)
        }
    }

    return children
}