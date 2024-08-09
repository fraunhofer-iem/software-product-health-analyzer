package de.fraunhofer.iem.kpiCalculator.model.adapter.occmd

enum class Checks(val checkName: String) {
    SastUsageBasic("SastUsageBasic"),
    Secrets("Secrets"),
    CheckedInBinaries("CheckedInBinaries"),
    CommentsInCode("CommentsInCode"),
    DocumentationInfrastructure("ExistenceOfDocumentationInfrastructure");

    companion object {
        fun fromString(value: String): Checks? {
            return Checks.entries.find { it.checkName.equals(value, ignoreCase = true) }
        }
    }
}
