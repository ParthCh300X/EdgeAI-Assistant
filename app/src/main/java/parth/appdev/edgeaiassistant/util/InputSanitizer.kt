package parth.appdev.edgeaiassistant.util

object InputSanitizer {

    private const val MAX_LENGTH = 200

    fun sanitize(raw: String): String? {
        val trimmed = raw.trim()

        if (trimmed.isBlank()) return null

        // Strip control characters and null bytes
        val noControl = trimmed.replace(Regex("[\\p{Cc}\\p{Cf}\\x00]"), "")

        // Keep letters, digits, common punctuation and spaces
        // Strips emojis, RTL markers, zero-width chars, HTML tags
        val cleaned = noControl
            .replace(Regex("<[^>]*>"), "")               // strip HTML tags
            .replace(Regex("[^\\p{L}\\p{N}\\s'\":.,!?@#%&()\\-+*/=]"), " ")
            .replace(Regex("\\s{2,}"), " ")              // collapse multiple spaces
            .trim()

        if (cleaned.isBlank()) return null

        // Hard cap — anything above this is almost certainly not a real command
        return if (cleaned.length > MAX_LENGTH) cleaned.substring(0, MAX_LENGTH).trim()
        else cleaned
    }
}