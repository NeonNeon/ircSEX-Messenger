package se.chalmers.dat255.ircsex.ui

import se.chalmers.dat255.ircsex.view.IrcChannelItem
import se.chalmers.dat255.ircsex.view.IrcServerHeader
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author: wilhelm
 * @date: 9/25/13
 */
class IrcChannelSelectorTest extends Specification {
    IrcChannelSelector ircChannelSelector = new IrcChannelSelector(null);

    def "Add Header"() {
        when: "A single Header is added to the list"
        def header = new IrcServerHeader("H1")
        ircChannelSelector.addHeader(header)

        then: "It's alone in the lists and has no children"
        assertStatus(1, 1, header, 0)
        ircChannelSelector.isIndexHeading(0)
    }

    def "Add Channel"() {
        when: "A Header is added and a channel is added to it"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)

        then: "The Channel is associated with its Header"
        assertStatus(2, 1, header, 1)
        returnIndex == 1;
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)
    }

    def "Add two Channels"() {
        when: "A Header is added and two channels are added to it"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        def channel2 = new IrcChannelItem("#C1.2")
        ircChannelSelector.addHeader(header)
        def returnIndex1 = ircChannelSelector.addChannel(header.text, channel)
        def returnIndex2 = ircChannelSelector.addChannel(header.text, channel2)

        then: "The Channels are associated with its Header"
        assertStatus(3, 1, header, 2)
        returnIndex1 == 1;
        returnIndex2 == 2;
    }

    def "Remove a Channel"() {
        when: "A Header is added and a Channel is added"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)

        then:
        assertStatus(2, 1, header, 1)
        returnIndex == 1

        when: "The channel is removed"
        int newIndex = ircChannelSelector.removeChannel(1)

        then:
        assertStatus(1, 1, header, 0)
        newIndex == 0
        ircChannelSelector.isIndexHeading(0)
    }

    def "Remove the last Channel of two"() {
        when: "A Header is added and two Channels are added"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        def channel2 = new IrcChannelItem("#C1.2")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)
        def returnIndex2 = ircChannelSelector.addChannel(header.text, channel2)

        then:
        assertStatus(3, 1, header, 2)
        returnIndex == 1
        returnIndex2 == 2

        when: "The channel is removed"
        int newIndex = ircChannelSelector.removeChannel(2)

        then:
        assertStatus(2, 1, header, 1)
        newIndex == 1
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)
    }

    def "Remove the first Channel of two"() {
        when: "A Header is added and two Channels are added"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        def channel2 = new IrcChannelItem("#C1.2")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)
        def returnIndex2 = ircChannelSelector.addChannel(header.text, channel2)

        then:
        assertStatus(3, 1, header, 2)
        returnIndex == 1
        returnIndex2 == 2

        when: "The channel is removed"
        int newIndex = ircChannelSelector.removeChannel(1)

        then:
        assertStatus(2, 1, header, 1)
        newIndex == 1
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)
    }

    def "Add two servers and one channel each"() {
        when: "A Header is added and a channel is added to it"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)

        then: "The Channel is associated with its Header"
        assertStatus(2, 1, header, 1)
        returnIndex == 1;
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)

        when: "The second Header is added and a channel is added to it"
        def header2 = new IrcServerHeader("H2")
        def channel2 = new IrcChannelItem("#C2.1")
        ircChannelSelector.addHeader(header2)
        def returnIndex2 = ircChannelSelector.addChannel(header2.text, channel2)

        then: "The Channel is associated with its Header"
        assertStatus(4, 2, header, 1)
        ircChannelSelector.headersToChannels.get(header2).size() == 1
        returnIndex2 == 3;
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)
        ircChannelSelector.isIndexHeading(2)
        !ircChannelSelector.isIndexHeading(3)
    }

    def "Add two servers and add more channels to the first"() {
        when: "A Header is added and a channel is added to it"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        def channel12 = new IrcChannelItem("#C1.2")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)

        then: "The Channel is associated with its Header"
        assertStatus(2, 1, header, 1)
        returnIndex == 1;
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)

        when: "The second Header is added and a channel is added to it"
        def header2 = new IrcServerHeader("H2")
        def channel21 = new IrcChannelItem("#C2.1")
        ircChannelSelector.addHeader(header2)
        def returnIndex21 = ircChannelSelector.addChannel(header2.text, channel21)

        then: "The Channels are associated with its respective Header"
        assertStatus(4, 2, header, 1)
        ircChannelSelector.headersToChannels.get(header2).size() == 1
        returnIndex21 == 3;

        when: "A channel is added to the first header"
        def returnIndex12 = ircChannelSelector.addChannel(header.text, channel12)

        then:
        assertStatus(5, 2, header, 2)
        assertStatus(5, 2, header2, 1)
        returnIndex12 == 2;
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)
        !ircChannelSelector.isIndexHeading(2)
        ircChannelSelector.isIndexHeading(3)
        !ircChannelSelector.isIndexHeading(4)
    }

    def "Add two servers and add more channels to the last"() {
        when: "A Header is added and a channel is added to it"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)

        then: "The Channel is associated with its Header"
        assertStatus(2, 1, header, 1)
        returnIndex == 1;
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)

        when: "The second Header is added and a channel is added to it"
        def header2 = new IrcServerHeader("H2")
        def channel21 = new IrcChannelItem("#C2.1")
        def channel22 = new IrcChannelItem("#C2.2")
        ircChannelSelector.addHeader(header2)
        def returnIndex21 = ircChannelSelector.addChannel(header2.text, channel21)

        then: "The Channels are associated with its respective Header"
        assertStatus(4, 2, header, 1)
        ircChannelSelector.headersToChannels.get(header2).size() == 1
        returnIndex21 == 3;

        when: "A channel is added to the first header"
        def returnIndex22 = ircChannelSelector.addChannel(header2.text, channel22)

        then:
        assertStatus(5, 2, header, 1)
        assertStatus(5, 2, header2, 2)
        returnIndex22 == 4;
        ircChannelSelector.isIndexHeading(0)
        !ircChannelSelector.isIndexHeading(1)
        ircChannelSelector.isIndexHeading(2)
        !ircChannelSelector.isIndexHeading(3)
        !ircChannelSelector.isIndexHeading(4)
    }

    void assertStatus(int connections, int servers, IrcServerHeader header, int headersToChannels) {
        assert ircChannelSelector.connections.size() == connections
        assert ircChannelSelector.servers.size() == servers
        assert ircChannelSelector.headersToChannels.get(header).size() == headersToChannels
    }
}
