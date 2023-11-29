package fr.polytech.annotation

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAnyRole('client_admin', 'client_recruiter') and @reviewService.checkUser(#review.senderId, #token)")
annotation class IsRecruiterOrAdminAndSender()
