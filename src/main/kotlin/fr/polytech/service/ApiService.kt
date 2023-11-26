package fr.polytech.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class ApiService @Autowired constructor(
    private val restTemplate: RestTemplate
)  {
    private val logger = LoggerFactory.getLogger(ApiService::class.java)

    /**
     * Make an API call.
     *
     * @param uri          URI of the API
     * @param method       HTTP method
     * @param responseType Class of the response
     * @param token        String - Access token from the user who adds the review
     * @param <T>          Type of the response
     * @return Response
     * @throws HttpClientErrorException if an error occurs while calling the API
    </T> */
    @Throws(HttpClientErrorException::class)
    fun <T> makeApiCall(uri: String, method: HttpMethod, responseType: Class<T>, token: String): T? {
        logger.info("Making API call to {}", uri)
        val headers = createHeaders(token)
        logger.info("Headers: {}", headers)
        val entity = HttpEntity<Void>(null, headers)
        logger.info("Entity: {}", entity)
        try {
            val response: ResponseEntity<T> = restTemplate.exchange(uri, method, entity, responseType)
            logger.info("Response: {}", response)
            return if (response.statusCode === HttpStatus.OK && response.body != null) {
                logger.info("API call successful")
                logger.info("Response body: {}", response.body)
                response.body
            } else {
                logger.error("Failed to make API call to {}", uri)
                logger.error("Response status code: {}", response.statusCode)
                throw HttpClientErrorException(response.statusCode)
            }
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to make API call to {}", uri)
            logger.error("Response status code: {}", e.statusCode)
            throw e
        } catch (e: Exception) {
            logger.error("Failed to make API call to {}", uri)
            logger.error("Exception: {}", e.message)
            throw HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Create headers for API calls.
     *
     * @param token String - Access token from the user who adds the review
     * @return HttpHeaders
     * @throws HttpClientErrorException if the token is not valid
     */
    @Throws(HttpClientErrorException::class)
    private fun createHeaders(token: String?): HttpHeaders {
        logger.info("Creating headers for API call")
        logger.info("Token: {}", token)
        if (token == null || !token.startsWith("Bearer ")) {
            logger.error("Invalid token")
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
        }
        val headers = HttpHeaders()
        headers.setBearerAuth(token.replace("Bearer ", ""))
        logger.info("Headers created")
        return headers
    }
}