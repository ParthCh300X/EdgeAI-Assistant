package parth.appdev.edgeaiassistant.domain.command

interface Command {
    fun execute(): String
}