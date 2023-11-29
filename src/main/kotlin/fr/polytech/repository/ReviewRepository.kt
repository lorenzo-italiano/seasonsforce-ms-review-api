package fr.polytech.repository

import fr.polytech.model.Review
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface ReviewRepository : MongoRepository<Review, UUID> {
    fun findBySenderId(senderId: UUID): List<Review>
}