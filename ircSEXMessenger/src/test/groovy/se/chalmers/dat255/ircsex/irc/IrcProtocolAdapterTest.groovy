package se.chalmers.dat255.ircsex.irc

import se.chalmers.dat255.ircsex.mock.MockIrcServer
import spock.lang.Specification

/**
 * Created by Wilhelm on 2013-09-22.
 */
class IrcProtocolAdapterTest extends Specification {
    IrcProtocolAdapter ircProtocolAdapter
    MockIrcServer mockIrcServer

    def setup() {
        ircProtocolAdapter = new IrcProtocolAdapter("localhost", 80)
        mockIrcServer = new MockIrcServer()
        ircProtocolAdapter.output = mockIrcServer.getAdapterOutputStream()
        ircProtocolAdapter.input = mockIrcServer.getAdapterInputStream()
        ircProtocolAdapter.socket = mockIrcServer.getAdapterSocket()
    }

    def "test connect"() {
        when: "IPA connects to the server"
        ircProtocolAdapter.connect("Hest", "Fest", "Alko Hest")

        then: "NICK and USER is sent to the socket"
        mockIrcServer.readLine().startsWith("NICK")
        mockIrcServer.readLine().startsWith("USER")
        mockIrcServer.readLine() == null
    }

    def "test disconnect"() {
        when: "IPA disconnects from the server"
        ircProtocolAdapter.disconnect("Bye")

        then: "QUIT is sent to the socket"
        mockIrcServer.readLine().startsWith("QUIT")
        mockIrcServer.readLine() == null
    }
}
