package com.drivenote.app.ml

import com.drivenote.app.domain.model.NoteCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FallbackKeywordClassifier @Inject constructor() : Classifier {
    private val keywordMap = mapOf(
        NoteCategory.WORK to listOf("회의", "팀장", "보고", "업무", "고객"),
        NoteCategory.DEV to listOf("코드", "버그", "앱", "배포", "스프린트", "API"),
        NoteCategory.INVEST to listOf("주식", "ETF", "투자", "포트폴리오", "매수"),
        NoteCategory.TODO to listOf("해야", "할일", "체크", "준비", "제출"),
        NoteCategory.RESEARCH to listOf("조사", "리서치", "검토", "비교", "분석")
    )

    override suspend fun classify(text: String): ClassificationResult {
        val normalized = text.lowercase()
        val matched = keywordMap
            .mapValues { (_, keywords) -> keywords.count { normalized.contains(it.lowercase()) } }
            .maxByOrNull { it.value }

        val category = if (matched == null || matched.value == 0) {
            NoteCategory.UNCLASSIFIED
        } else {
            matched.key
        }

        val tags = keywordMap[category]
            ?.filter { normalized.contains(it.lowercase()) }
            ?.distinct()
            ?.take(3)
            .orEmpty()

        return ClassificationResult(
            category = category,
            tags = tags,
            confidence = if (category == NoteCategory.UNCLASSIFIED) 0.2f else 0.65f
        )
    }

    override fun isAvailable(): Boolean = true
}
