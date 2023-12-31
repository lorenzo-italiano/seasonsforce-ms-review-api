package fr.polytech.service

import fr.polytech.model.UserDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.util.*

@Service
class UserService @Autowired constructor(
    private val apiService: ApiService
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    /**
     * Get a Recruiter by id.
     *
     * @param id the id of the Recruiter to get
     * @param token the token of the user
     * @throws HttpClientErrorException if an error occurs while getting the Recruiter
     */
    @Throws(HttpClientErrorException::class)
    fun getRecruiterById(id: UUID, token: String): UserDTO? {
        val uri = System.getenv("USER_API_URI") + "/" + id
        return apiService.makeApiCall(uri, HttpMethod.GET, UserDTO::class.java, token)
    }
}