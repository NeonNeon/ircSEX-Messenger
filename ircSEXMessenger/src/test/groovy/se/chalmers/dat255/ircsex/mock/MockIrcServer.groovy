package se.chalmers.dat255.ircsex.mock

/**
 * Created by Wilhelm on 2013-09-22.
 */
class MockIrcServer {
    private ByteArrayOutputStream outputStream
    private ByteArrayLineReader lineReader

    MockIrcServer() {
        outputStream = new ByteArrayOutputStream()
        lineReader = new ByteArrayLineReader(outputStream)
    }

    BufferedWriter getAdapterOutputStream() {
        new BufferedWriter(new OutputStreamWriter(outputStream))
    }

    BufferedReader getAdapterInputStream() {
        new BufferedReader(new InputStreamReader(new ByteArrayInputStream()))
    }

    Socket getAdapterSocket() {
        new Socket("localhost", 80)
    }

    String readLine() {
        lineReader.readLine()
    }
}
