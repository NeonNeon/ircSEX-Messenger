package se.chalmers.dat255.ircsex.irc

import spock.lang.Specification

/**
 * Created by Wilhelm on 2013-09-22.
 */
class IrcProtocolAdapterEventsTest extends Specification {
    IrcProtocolListener subscriber = Mock()
    IrcProtocolAdapter ipa = new IrcProtocolAdapter("localhost", 80, subscriber)

    def setup() {
    }

    def "test join event sent"() {
        when:
        def command = ":tord!~banned@smurf-EAF5674.dynamic.se.alltele.net JOIN :"
        ipa.handleReply(command + channel)

        then:
        1 * subscriber.joinedChannel(channel)

        where:
        channel << ["#fest", "#svinstia", "#party"]
    }

    def "test part event sent"() {
        when:
        def command = "PART "
        ipa.handleReply(command + channel)

        then:
        1 * subscriber.partedChannel(channel)

        where:
        channel << ["#fest", "#svinstia", "#party"]
    }

    def "test messageReceived event sent"() {
        when:
        def command = "!~anon@smurf-BA46BB40.edstud.chalmers.se PRIVMSG "
        ipa.handleReply(":" + nick + command + channel + " :" + message)

        then:
        1* subscriber.messageReceived(channel, nick, message)

        where:
        channel << ["#fest", "#svinstia", "#party"]
        nick << ["oed", "Heissman", "Rascal"]
        message << ["hejhej", "fulefan", "Nej men.."]
    }
}
