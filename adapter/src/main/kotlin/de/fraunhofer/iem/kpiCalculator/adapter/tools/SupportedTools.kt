package de.fraunhofer.iem.kpiCalculator.adapter.tools

enum class SupportedTool {
    Occmd,
    Trivy;

    companion object{
        fun fromName(name: String) : SupportedTool{
            try {
                return SupportedTool.valueOf(name)
            } catch (e: IllegalArgumentException){
                throw ToolNotFoundException("Unable to find tool result adapter for tool '$name'.");
            }
        }
    }
}

class ToolNotFoundException(message: String) : Exception(message)
