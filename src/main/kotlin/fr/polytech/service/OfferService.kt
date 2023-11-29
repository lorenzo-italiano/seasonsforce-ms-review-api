package fr.polytech.service

import fr.polytech.model.OfferDTO
import fr.polytech.model.RecruiterDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.util.*

@Service
class OfferService {
    private val logger = LoggerFactory.getLogger(OfferService::class.java)

    @Autowired
    private val apiService: ApiService? = null

    /**
     * Get a Recruiter by id.
     */
    @Throws(HttpClientErrorException::class)
    fun getOfferById(id: UUID, token: String): OfferDTO? {
        val uri = System.getenv("OFFER_API_URI") + "/" + id
        return apiService!!.makeApiCall(
            uri, HttpMethod.GET, OfferDTO::class.java,
            token
        )
    }
}