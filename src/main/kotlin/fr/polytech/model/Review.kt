package fr.polytech.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "reviews")
data class Review(
    @Id
    val id: UUID,
    var grade: Int,
    var message: String,
    var senderId: UUID,
    var responseList: List<Response>,
    var date: Date
)
