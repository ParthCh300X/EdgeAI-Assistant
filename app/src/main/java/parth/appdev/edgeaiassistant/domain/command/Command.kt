package parth.appdev.edgeaiassistant.domain.command

interface Command {
    suspend fun execute(): String
}