package com.drivenote.app.domain.model

enum class NoteCategory(val label: String) {
    WORK("업무"),
    DEV("개발"),
    INVEST("투자"),
    TODO("할일"),
    RESEARCH("조사"),
    UNCLASSIFIED("미분류");

    companion object {
        fun fromLabel(label: String): NoteCategory {
            return entries.firstOrNull { it.label == label } ?: UNCLASSIFIED
        }
    }
}
