package fr.polytech.restcontroller

import fr.polytech.model.Response
import fr.polytech.model.Review
import fr.polytech.model.request.PatchReviewDTO
import fr.polytech.model.request.ResponseDTO
import fr.polytech.model.request.ReviewDTO
import fr.polytech.service.ReviewService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.Produces
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import java.util.*

@Controller
@RequestMapping("/api/v1/review")
class ReviewController @Autowired constructor(
    private val reviewService: ReviewService
) {

    /**
     * Initialize logger
     */
    private val logger: Logger = LoggerFactory.getLogger(ReviewController::class.java)

    /**
     * Get all reviews.
     * @return a list of reviews
     */
    @GetMapping("/")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    fun getReviews(): ResponseEntity<List<Review>> {
        return try {
            val reviews: List<Review> = reviewService.getReviews()
            logger.info("Got all reviews")
            ResponseEntity(reviews, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to get all reviews", e)
            ResponseEntity(e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to get all reviews", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Get a review by its id.
     * @param id the id of the review to get
     * @return the review with the given id
     */
    @GetMapping("/{id}")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    fun getReviewById(@PathVariable id: UUID): ResponseEntity<Review> {
        return try {
            val review: Review = reviewService.getReviewById(id)
            logger.info("Got review with id $id")
            ResponseEntity(review, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to get review with id $id", e)
            ResponseEntity(e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to get review with id $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Create a review.
     * @param review the review to create
     * @return the created review
     */
    @PostMapping("/")
    @PreAuthorize("(hasRole('client_recruiter') or hasRole('client_admin')) and @reviewService.checkUser(#review.senderId, #token)")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    fun createReview(
        @RequestBody review: ReviewDTO,
        @RequestHeader("Authorization") token: String
        ): ResponseEntity<Review> {
        return try {
            val createdReview: Review = reviewService.createReview(review)
            logger.info("Created review with id ${createdReview.id}")
            ResponseEntity(createdReview, HttpStatus.CREATED)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to create review", e)
            ResponseEntity(e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to create review", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Update a review.
     * @param id the id of the review to update
     * @param review the review to update
     * @return the updated review
     */
    @PatchMapping("/{id}")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    fun updateReview(@PathVariable id: UUID, @RequestBody review: PatchReviewDTO): ResponseEntity<Review> {
        return try {
            val updatedReview: Review = reviewService.updateReview(id, review)
            logger.info("Updated review with id $id")
            ResponseEntity(updatedReview, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to update review with id $id", e)
            ResponseEntity(e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to update review with id $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Add a response to a review.
     * @param id the id of the review to update
     * @param response the response to add
     * @return the updated review
     */
    @PatchMapping("/add/response/{id}")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    fun addResponseToReview(@PathVariable id: UUID, @RequestBody response: ResponseDTO): ResponseEntity<Review> {
        return try {
            val updatedReview: Review = reviewService.addResponseToReview(id, response)
            logger.info("Added response to review with id $id")
            ResponseEntity(updatedReview, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to add response to review with id $id", e)
            ResponseEntity(e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to add response to review with id $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Modify a response of a review.
     * @param id the id of the review to update
     * @param response the response to modify
     * @return the updated review
     */
    @PutMapping("/modify/response/{id}")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    fun modifyResponseOfReview(@PathVariable id: UUID, @RequestBody response: Response): ResponseEntity<Review> {
        return try {
            val updatedReview: Review = reviewService.modifyResponseOfReview(id, response)
            logger.info("Modified response to review with id $id")
            ResponseEntity(updatedReview, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to modify response to review with id $id", e)
            ResponseEntity(e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to modify response to review with id $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Delete a review.
     * @param id the id of the review to delete
     * @return true if the review has been deleted, false otherwise
     */
    @DeleteMapping("/{id}")
    @Produces(MediaType.TEXT_PLAIN_VALUE)
    fun deleteReview(@PathVariable id: UUID): ResponseEntity<Boolean> {
        return try {
            reviewService.deleteReview(id)
            logger.info("Deleted review with id $id")
            ResponseEntity(true, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to delete review with id $id", e)
            ResponseEntity(false, e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to delete review with id $id", e)
            ResponseEntity(false, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Delete a response of a review.
     * @param id the id of the review to update
     * @param response the response to delete
     * @return the updated review
     */
    @DeleteMapping("/delete/response/{id}")
    @Produces(MediaType.TEXT_PLAIN_VALUE)
    fun deleteResponseOfReview(@PathVariable id: UUID, @RequestBody response: Response): ResponseEntity<Review> {
        return try {
            val updatedReview: Review = reviewService.deleteResponseOfReview(id, response)
            logger.info("Deleted response of review with id $id")
            ResponseEntity(updatedReview, HttpStatus.OK)
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to delete response of review with id $id", e)
            ResponseEntity(e.statusCode)
        } catch (e: Exception) {
            logger.error("Failed to delete response of review with id $id", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}