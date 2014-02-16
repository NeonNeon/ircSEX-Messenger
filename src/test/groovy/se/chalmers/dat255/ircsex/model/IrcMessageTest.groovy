package se.chalmers.dat255.ircsex.model

import spock.lang.Specification

/**
 * Created by oed on 2/16/14.
 */
class IrcMessageTest extends Specification {
    Date date = new Date()
    IrcMessage ircMessage = new IrcMessage("fest", "oed", date, false);

    def "GetMessage"() {
        expect:
        ircMessage.getMessage() == "fest"
    }

    def "GetOwner"() {
        expect:
        ircMessage.getOwner() == "oed"
    }

    def "GetFormattedTimestamp"() {
        expect:
        ircMessage.getFormattedTimestamp() == date.toString();
    }

    def "GetTimestamp"() {
        expect:
        ircMessage.getTimestamp() == date
    }

    def "IsHilight"() {
        expect:
        !ircMessage.isHilight()
    }
}
