package com.jamieadkins.nytimesdemo

class ArticleSummary {

    val id: Long = 0
    val title: String? = null
    val subtitle: String? = null
    val date: String? = null

    override fun equals(other: Any?): Boolean {
        return other is ArticleSummary && other.id == id
    }
}
