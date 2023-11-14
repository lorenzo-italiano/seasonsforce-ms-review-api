package fr.polytech.service

import fr.polytech.model.Response
import fr.polytech.model.Review
import fr.polytech.model.request.PatchReviewDTO
import fr.polytech.model.request.ResponseDTO
import fr.polytech.repository.ReviewRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.util.*

@Service
class ReviewService @Autowired constructor(
    private val reviewRepository: ReviewRepository
) {

    /**
     * Initialize logger
     */
    private val logger = LoggerFactory.getLogger(ReviewService::class.java)

    /**
     * Get all reviews.
     * @return a list of reviews
     */
    fun getReviews(): List<Review> {
        logger.info("Getting all reviews")
        return reviewRepository.findAll()
    }

    /**
     * Get a review by its id.
     * @param id the id of the review to get
     * @return the review with the given id
     * @throws HttpClientErrorException if an error occurs while getting the review
     */
    @Throws(HttpClientErrorException::class)
    fun getReviewById(id: UUID): Review {
        logger.info("Getting review with id $id")
        val review: Optional<Review> = reviewRepository.findById(id)
        return review.orElseThrow { HttpClientErrorException(HttpStatus.NOT_FOUND) }
    }

    /**
     * Create a review.
     * @param review the review to create
     * @return the created review
     */
    fun createReview(review: Review): Review {
        logger.info("Creating review")
        return reviewRepository.save(review)
    }

    /**
     * Update a review.
     * @param id the id of the review to update
     * @param review the review to update
     * @return the updated review
     * @throws HttpClientErrorException if an error occurs while updating the review
     */
    @Throws(HttpClientErrorException::class)
    fun updateReview(id: UUID, review: PatchReviewDTO): Review {
        logger.info("Updating review with id $id")
        val reviewToUpdate: Review =
            reviewRepository.findById(id).orElseThrow { HttpClientErrorException(HttpStatus.NOT_FOUND) }
        val reviewUpdated: Review = reviewToUpdate
        reviewUpdated.grade = review.grade
        reviewUpdated.message = review.message
        return reviewRepository.save(reviewUpdated)
    }

    /**
     * Add a response to a review.
     * @param id the id of the review to update
     * @param response the response to add
     * @return the updated review
     * @throws HttpClientErrorException if an error occurs while updating the review
     */
    @Throws(HttpClientErrorException::class)
    fun addResponseToReview(id: UUID, response: ResponseDTO): Review {
        logger.info("Adding response to review with id $id")
        val reviewToUpdate: Review =
            reviewRepository.findById(id).orElseThrow { HttpClientErrorException(HttpStatus.NOT_FOUND) }
        val responseToAdd: Response = Response(
            UUID.randomUUID(),
            response.date,
            response.message,
            response.senderId
        )
        val reviewUpdated: Review = reviewToUpdate
        reviewUpdated.responseList = Optional.of(reviewToUpdate.responseList.orElse(listOf()).plus(responseToAdd))
        return reviewRepository.save(reviewUpdated)
    }

    /**
     * Modify a response of a review.
     * @param id the id of the review to update
     * @param response the response to modify
     * @return the updated review
     * @throws HttpClientErrorException if an error occurs while updating the review
     */
    @Throws(HttpClientErrorException::class)
    fun modifyResponseOfReview(id: UUID, response: Response): Review {
        logger.info("Modifying response of review with id $id")
        val reviewToUpdate: Review =
            reviewRepository.findById(id).orElseThrow { HttpClientErrorException(HttpStatus.NOT_FOUND) }
        val reviewUpdated: Review = reviewToUpdate
        reviewUpdated.responseList = Optional.of(reviewToUpdate.responseList.orElse(listOf()).map { r ->
            if (r.id == response.id) {
                response
            } else {
                r
            }
        })
        return reviewRepository.save(reviewUpdated)
    }

    /**
     * Delete a review.
     * @param id the id of the review to delete
     * @throws HttpClientErrorException if an error occurs while deleting the review
     */
    @Throws(HttpClientErrorException::class)
    fun deleteReview(id: UUID) {
        logger.info("Deleting review with id $id")
        val reviewToDelete: Review =
            reviewRepository.findById(id).orElseThrow { HttpClientErrorException(HttpStatus.NOT_FOUND) }
        reviewRepository.delete(reviewToDelete)
    }

    /**
     * Delete a response of a review.
     * @param id the id of the review to update
     * @param response the response to delete
     * @return the updated review
     * @throws HttpClientErrorException if an error occurs while updating the review
     */
    @Throws(HttpClientErrorException::class)
    fun deleteResponseOfReview(id: UUID, response: Response): Review {
        logger.info("Deleting response of review with id $id")
        val reviewToUpdate: Review =
            reviewRepository.findById(id).orElseThrow { HttpClientErrorException(HttpStatus.NOT_FOUND) }
        val reviewUpdated: Review = reviewToUpdate
        reviewUpdated.responseList = Optional.of(reviewToUpdate.responseList.orElse(listOf()).filter { r ->
            r.id != response.id
        })
        return reviewRepository.save(reviewUpdated)
    }
}