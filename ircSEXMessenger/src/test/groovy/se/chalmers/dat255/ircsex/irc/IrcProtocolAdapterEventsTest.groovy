package se.chalmers.dat255.ircsex.irc

import spock.lang.Specification

/**
 * Created by Wilhelm on 2013-09-22.
 */
class IrcProtocolAdapterEventsTest extends Specification {
    IrcProtocolListener subscriber = Mock()
    IrcProtocolAdapter ipa = new IrcProtocolAdapter("irc.chalmers.it", 80, subscriber)

    def setup() {
    }


    def "test userJoined event sent"() {
        when:
        def command = "!~anon@smurf-BA46BB40.edstud.chalmers.se JOIN :"
        ipa.handleReply(":" + user + command + channel)

        then:
        1 * subscriber.userJoined(channel, user)

        where:
        channel << ["#fest", "#svinstia", "#party"]
        user << ["oed", "Heissman", "Rascal"]
    }

    def "test userParted event sent"() {
        when:
        def command = "!~anon@smurf-BA46BB40.edstud.chalmers.se PART "
        ipa.handleReply(":" + user + command + channel)

        then:
        1 * subscriber.userParted(channel, user)

        where:
        channel << ["#fest", "#svinstia", "#party"]
        user << ["oed", "Heissman", "Rascal"]
    }

    def "test usersInChannel event sent"() {
        def channel = "#ircSEX-asp"

        when:
        def command1 = ":irc.chalmers.it 353 tord = "
        def command2 = ":irc.chalmers.it 366 tord "
        ipa.handleReply(command1 + channel + " :" + users)
        ipa.handleReply(command2 + channel + " :End of /NAMES list.")

        then:
        1 * subscriber.usersInChannel(channel, usersList)

        where:
        users << ["tord Micro rekoil",
                "@Norrland @Heissman @Hultner @oed @Tuna @Roras",
                "+Rascal ~stefan"]
        usersList << [["tord", "Micro", "rekoil"],
                    ["@Norrland", "@Heissman", "@Hultner", "@oed", "@Tuna", "@Roras"],
                    ["+Rascal", "~stefan"]]
    }

    def "test nickChanged event sent"() {
        when:
        def command = "!anon@smurf-BA46BB40.edstud.chalmers.se NICK :"
        ipa.handleReply(":" + oldNick + command + newNick)

        then:
        1 * subscriber.nickChanged(oldNick, newNick)

        where:
        oldNick << ["oed", "Heissman", "Rascal"]
        newNick << ["tord", "hestpojken", "OXi"]

    }

    def "test whoisRealname event sent"() {
        when:
        def command1 = ":irc.chalmers.it 311 tord "
        def command2 = " ~anon smurf-BA46BB40.edstud.chalmers.se * :"
        ipa.handleReply(command1 + nick + command2 + realname)

        then:
        1 * subscriber.whoisRealname(nick, realname)

        where:
        nick << ["oed", "Heissman", "Rascal"]
        realname << ["Joel Torstensson", "Jonathan Hedman", "Oskar Nyberg"]
    }

    def "test whoisChannels event sent"() {
        when:
        def command1 = ":irc.chalmers.it 319 tord "
        def command2 = " :"
        ipa.handleReply(command1 + nick + command2+ channels)

        then:
        1 * subscriber.whoisChannels(nick, channelsList)

        where:
        nick << ["oed"]
        channels << ["#Rascal #Hultner #SEproject @#oed #opk #olämpligphadder +#br0st #a-laget #haqKIT #!ordfITs +#ircsex #sommar13 @#ircSEX-asp #it13 @#prit @#prit13 #bättre13 #sektionsmöte #hookit #idol11 @#it12 #itstud"]
        channelsList << [["#Rascal", "#Hultner", "#SEproject", "@#oed", "#opk", "#olämpligphadder", "+#br0st", "#a-laget", "#haqKIT", "#!ordfITs", "+#ircsex", "#sommar13", "@#ircSEX-asp", "#it13", "@#prit", "@#prit13", "#bättre13", "#sektionsmöte", "#hookit", "#idol11", "@#it12", "#itstud"]]
    }

    def "test whoisIdleTime event sent"() {
        when:
        def command1 = ":irc.chalmers.it 317 tord "
        def command2 = " 1371050325 :seconds idle, signon time"
        ipa.handleReply(command1 + nick + " " + time + command2)

        then:
        1 * subscriber.whoisIdleTime(nick, time)

        where:
        nick << ["oed", "Heissman", "Rascal"]
        time << [14495, 20, 23423523]
    }
}
