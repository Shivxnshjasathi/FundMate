package com.zincstate.fundmate.data.model

import com.google.gson.annotations.SerializedName

// Response for GET /mf/latest
data class SchemeDto(
    @SerializedName("schemeCode") val schemeCode: Int,
    @SerializedName("schemeName") val schemeName: String,
    @SerializedName("fundHouse") val fundHouse: String?,
    @SerializedName("nav") val nav: String,
    @SerializedName("date") val date: String
)
data class MetaDto(
    @SerializedName("fund_house") val fundHouse: String,
    @SerializedName("scheme_name") val schemeName: String,
    @SerializedName("scheme_category") val category: String,
    @SerializedName("scheme_code") val schemeCode: Int
)
data class NavDataDto(
    val date: String,
    val nav: String
)

// Response for GET /mf/{code}/latest
// Note: The API structure changes slightly for details
data class FundDetailResponse(
    val meta: MetaDto,
    val data: List<NavDataDto>,
    val status: String
)
