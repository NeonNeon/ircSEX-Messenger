ircSEX Messenger
=========

The best Android irc client.

##Introduction
IRC or Internet Relay Chat is a messaging protocol for communication
with groups or individuals over the internet. It was created in 1988
and has been used ever since. Therefore there are a plethora of clients
with 20th century thinking. The demand for a new, 21st century client,
for 21st century devices is dire.

##Product vision
For IRC users who are using their Android phones to connect to IRC,
the ircSEX messenger is the client that will revolutionize the way we
communicate on mobile devices. Drawing upon modern messaging platforms
and idioms, with our product, we show that IRC can be more than a terminal
or look like a hacker chat. The world of IRC clients for Android is in dire
need for change - and we’re here to do it!

##Dependencies
- Android SDK - API 16
- Android SDK - support-v4
- sshj - 0.9.0 

For unit tests:
- Groovy - 1.8.6
- Spock Framework - 0.6-groovy-1.8

All dependencies are declared in full in the `pom.xml`. They will be downloaded and set up automagically by Maven.

##Get it now!
The project uses Maven.

To build the project, run the Maven goal `install` with
    
    $ mvn install

To only run the unit tests, run the Maven goal `test` with
    
    $ mvn test

To deploy the application to a connected Android device, execute the task
    
    $ mvn android:deploy

To uninstall the application from a connected Android device, execute the task
    
    $ mvn android:undeploy

There are SNAPSHOT artifacts kept from every release. These are located in the `ircSEX-Messenger/apk` directory. To install an old SNAPSHOT, run
    
    $ adb install <.apk name>


##Brewers
Wilhelm Hedman  
Joel Thorstensson  
Oskar Nyberg  
Johan Magnusson  
Alexander Hultnér  
