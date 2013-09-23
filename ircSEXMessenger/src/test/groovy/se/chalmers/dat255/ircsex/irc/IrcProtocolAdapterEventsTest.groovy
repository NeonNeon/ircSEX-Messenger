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
}
