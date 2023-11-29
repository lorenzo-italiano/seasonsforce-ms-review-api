package fr.polytech.service

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import fr.polytech.model.*
import fr.polytech.model.request.DeleteReviewDTO
import fr.polytech.model.request.PatchReviewDTO
import fr.polytech.model.request.ResponseDTO
import fr.polytech.model.request.ReviewDTO
import fr.polytech.repository.ReviewRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.util.*

@Service
class ReviewService @Autowired constructor(
    private val reviewRepository: ReviewRepository,
    private val userService: UserService,
    private val offerService: OfferService,
    private val apiService: ApiService
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
    @Throws(HttpClientErrorException::class)
    fun createReview(review: ReviewDTO): Review {
        logger.info("Creating review")
        validateGrade(review.grade)
        val reviewToCreate: Review = Review(
            UUID.randomUUID(),
            review.grade,
            review.message,
            review.senderId,
            review.userId,
            listOf(),
            Date(),
            review.offerId
        )
        return reviewRepository.save(reviewToCreate)
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
        validateGrade(review.grade)
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
            Date(),
            response.message,
            response.senderId
        )
        val reviewUpdated: Review = reviewToUpdate
        reviewUpdated.responseList = reviewToUpdate.responseList.plus(responseToAdd)
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
        reviewUpdated.responseList = reviewToUpdate.responseList.map { r ->
            if (r.id == response.id) {
                response
            } else {
                r
            }
        }
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
        reviewUpdated.responseList = reviewToUpdate.responseList.filter { r ->
            r.id != response.id
        }
        return reviewRepository.save(reviewUpdated)
    }

    /**
     * Check if the user is the sender of the review.
     * @param id the id of the review
     * @param bearerToken the token of the user
     * @return true if the user is the sender of the review, false otherwise
     */
    fun checkUser(id: UUID, bearerToken: String): Boolean {
        val token: String = bearerToken.split(" ")[1]
        val decodedToken: DecodedJWT? = JWT.decode(token)
        return if (decodedToken == null) {
            false
        } else {
            decodedToken.getClaim("sub").asString() == id.toString()
        }
    }

    /**
     * Validate the grade of the review.
     * @param grade the grade to validate
     * @throws HttpClientErrorException if the grade is not valid
     */
    @Throws(HttpClientErrorException::class)
    fun validateGrade(grade: Int) {
        if (grade < 0 || grade > 5) {
            throw HttpClientErrorException(HttpStatus.BAD_REQUEST, "The grade must be between 0 and 5")
        }
    }

    /**
     * Get a detailed review by its id.
     * @param id the id of the review to get
     * @param token the token of the user
     * @return the detailed review with the given id
     */
    fun getDetailedReviewById(id: UUID, token: String): DetailedReviewDTO {
        val review: Review = getReviewById(id)

        val offer: OfferDTO? = offerService.getOfferById(review.offerId, token)

        logger.info("offer: $offer")

        val sender: UserDTO? = userService.getRecruiterById(review.senderId, token)

        logger.info("user: $sender")

        val user: UserDTO? = userService.getRecruiterById(review.userId, token)

        logger.info("user: $user")

        return DetailedReviewDTO(
            review.id,
            review.grade,
            review.message,
            sender!!,
            user!!,
            review.responseList,
            review.date,
            offer!!
        )
    }

    /**
     * Get a list of detailed reviews by the id of the user.
     * @param userId the id of the user
     * @param token the token of the user
     * @return the list of detailed reviews with the given id of the user
     */
    fun getReviewListBySenderId(senderId: UUID, token: String): List<DetailedReviewDTO> {
        val findBySenderId: List<Review> = reviewRepository.findBySenderId(senderId)

        val reviewList: MutableList<DetailedReviewDTO> = mutableListOf()

        for (review in findBySenderId) {
            val detailedReview: DetailedReviewDTO = getDetailedReviewById(review.id, token)
            reviewList.add(detailedReview)
        }

        return reviewList
    }
}