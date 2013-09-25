package se.chalmers.dat255.ircsex.irc

import se.chalmers.dat255.ircsex.mock.MockIrcServer
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by Wilhelm on 2013-09-22.
 */
class IrcProtocolAdapterTest extends Specification {
    IrcProtocolAdapter ipa
    IrcProtocolListener subscriber = Mock()
    MockIrcServer mockIrcServer

    def setup() {
        ipa = new IrcProtocolAdapter("localhost", 80, subscriber)
        mockIrcServer = new MockIrcServer()
        ipa.output = mockIrcServer.getAdapterOutputStream()
    }

    def "test connect"() {
        when: "IPA connects to the server"
        ipa.connect("Hest", "Fest", "Alko Hest")

        then: "NICK and USER is sent to the socket"
        mockIrcServer.readLine().startsWith("NICK")
        mockIrcServer.readLine().startsWith("USER")
        mockIrcServer.readLine() == null
    }

    @Ignore("Socket does not work on Linux. Try ExpandoMetaClass runtime change for IPA.")
    def "test disconnect"() {
        ipa.input = mockIrcServer.getAdapterInputStream()
        ipa.socket = mockIrcServer.getAdapterSocket()

        when: "IPA disconnects from the server"
        ipa.disconnect("Bye")

        then: "QUIT is sent to the socket"
        mockIrcServer.readLine().startsWith("QUIT")
        mockIrcServer.readLine() == null
    }

    def "test whois"() {
        when:
        ipa.whois(nick)

        then:
        mockIrcServer.readLine().equals("WHOIS " + nick + "\r\n")

        where:
        nick << ["Heissman", "Rascal", "oed"]
    }
}
