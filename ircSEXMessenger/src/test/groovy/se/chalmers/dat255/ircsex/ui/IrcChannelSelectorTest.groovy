package se.chalmers.dat255.ircsex.ui

import se.chalmers.dat255.ircsex.view.IrcChannelItem
import se.chalmers.dat255.ircsex.view.IrcServerHeader
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
        hest(1, 1, header, 0)
    }

    void hest(int connectedChannels, int servers, IrcServerHeader header, int headersToChannels) {
        assert ircChannelSelector.connectedChannels.size() == connectedChannels
        assert ircChannelSelector.servers.size() == servers
        assert ircChannelSelector.headersToChannels.get(header).size() == headersToChannels
    }

    def "Add Channel"() {
        when: "A Header is added and a channel is added to it"
        def header = new IrcServerHeader("H1")
        def channel = new IrcChannelItem("#C1.1")
        ircChannelSelector.addHeader(header)
        def returnIndex = ircChannelSelector.addChannel(header.text, channel)

        then: "The Channel is associated with its Header"
        hest(2, 1, header, 1)
        returnIndex == 1;
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
        hest(3, 1, header, 2)
        returnIndex1 == 1;
        returnIndex2 == 2;
    }
}
