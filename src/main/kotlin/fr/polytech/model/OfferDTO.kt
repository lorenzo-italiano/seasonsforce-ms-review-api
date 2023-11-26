package fr.polytech.model

import java.util.*

data class OfferDTO (
    val id: UUID,
    val job_title: String,
    val job_description: String,
    val contract_type: String,
    val companyId: UUID,
    val salary: Double,
    val addressId: UUID,
    val hours_per_week: Float,
    val benefits: List<String>,
    val offer_language: String,
    val publication_date: Date,
    val offer_status: String,
    val contact_information: String,
    val required_degree: String,
    val required_experience: String,
    val required_skills: List<String>,
    val jobCategoryId: UUID,
    val creatorId: UUID,
    val recruitedId: UUID,
    val startDate: Date,
    val endDate: Date,
)