package no.nav.dagpenger.rapid.system

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MessageProcessorTest {
    @Test
    fun messageProcessorCanProcessMessage() {
        // Create a MessageProcessor instance (you'll implement this later)
        val messageProcessor: MessageProcessor = YourMessageProcessorImplementation()

        // Define a sample JSON message
        val jsonMessage = "{\"type\": \"knownMessageType\", \"data\": {\"key\": \"value\"}}"

        // Call the processMessage method and expect a result
        // In this test, we're not concerned with the actual processing, just that it doesn't throw an error.
        val processingResult: Boolean = messageProcessor.processMessage(jsonMessage)

        // Assert that processingResult is true, indicating successful processing
        Assertions.assertTrue(processingResult)
    }
}

