package se.chalmers.dat255.ircsex.irc

import spock.lang.Specification

/**
 * Created by Wilhelm on 2013-09-22.
 */
class IrcProtocolAdapterTest extends Specification {
    def "working spec"() {
        when: "a new stack is inited"
        Stack stack = new Stack();

        then: "it's empty"
        stack.isEmpty()
    }
}
