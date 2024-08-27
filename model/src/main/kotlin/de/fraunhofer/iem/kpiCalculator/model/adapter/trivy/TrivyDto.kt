package de.fraunhofer.iem.kpiCalculator.model.adapter.trivy

import de.fraunhofer.iem.kpiCalculator.model.adapter.vulnerability.VulnerabilityDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

data class TrivyDto(val Vulnerabilities: Collection<VulnerabilityDto>)

@Serializable
data class TrivyDtoV1(
    val Vulnerabilities: List<TrivyVulnerabilityDto> = listOf()
)

@Serializable
data class TrivyDtoV2(
    val Results: List<Result> = listOf(),
    val SchemaVersion: Int
)

@Serializable
data class Result(
    val Vulnerabilities: List<TrivyVulnerabilityDto> = listOf()
)

@Serializable
data class TrivyVulnerabilityDto(
    // NB: Because the names of its inner elements are not fixed, this needs to be a JsonObject.
    // This way we can iterate over those it when required. Their type is always CVSSData.
    val CVSS: JsonObject?,
    val PkgName: String,
    val VulnerabilityID: String
//    val CweIDs: List<String>?,
//    val DataSource: DataSource?,
//    val Description: String?,
//    val FixedVersion: String?,
//    val InstalledVersion: String?,
//    val LastModifiedDate: String?,
//    val Layer: Layer?,
//    val PkgID: String?,
//    val PkgIdentifier: PkgIdentifier?,
//    val PrimaryURL: String?,
//    val PublishedDate: String?,
//    val References: List<String>?,
//    val Severity: String?,
//    val SeveritySource: String?,
//    val Status: String?,
//    val Title: String?,
//    val VendorIDs: List<String>?,
//    val VendorSeverity: VendorSeverity?,
)

@Serializable
data class CVSSData(
    val V2Score: Double?,
    //val V2Vector: String?,
    val V3Score: Double?,
    //val V3Vector: String?
)

//@Serializable
//data class DataSource(
//    val ID: String,
//    val Name: String,
//    val URL: String
//)
//
//@Serializable
//data class Layer(
//    val DiffID: String
//)
//
//@Serializable
//data class PkgIdentifier(
//    val PURL: String,
//    val UID: String
//)
//
//@Serializable
//data class VendorSeverity(
//    val alma: Int?,
//    val amazon: Int?,
//    val azure: Int?,
//    @SerialName("cbl-mariner")
//    val cblMariner: Int?,
//    val debian: Int?,
//    val ghsa: Int?,
//    val nvd: Int?,
//    @SerialName("oracle-oval")
//    val oracleOval: Int?,
//    val photon: Int?,
//    val redhat: Int?,
//    val rocky: Int?,
//    val ubuntu: Int?
//)
