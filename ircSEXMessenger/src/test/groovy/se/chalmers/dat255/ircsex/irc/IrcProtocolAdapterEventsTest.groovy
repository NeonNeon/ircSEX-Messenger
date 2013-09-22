package se.chalmers.dat255.ircsex.irc

import spock.lang.Specification

/**
 * Created by Wilhelm on 2013-09-22.
 */
class IrcProtocolAdapterEventsTest extends Specification {
    IrcProtocolAdapter ipa = new IrcProtocolAdapter("localhost", 80)
    IrcProtocolAdapter.IrcProtocolServerListener subscriber = Mock()

    def setup() {
        ipa.addIrcProtocolServerListener(subscriber)
    }

    def "test mock"() {
        when:
        def command = ":tord!~banned@smurf-EAF5674.dynamic.se.alltele.net JOIN :"
        def channel = "#fest"
        ipa.handleReply(command + channel)

        then:
        1 * subscriber.fireEvent(IrcProtocolAdapter.MessageType.JOIN, "#fest")
    }
}
