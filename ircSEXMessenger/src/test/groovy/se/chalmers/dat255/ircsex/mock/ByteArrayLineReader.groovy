package se.chalmers.dat255.ircsex.mock

/**
 * Created by Wilhelm on 2013-09-22.
 */
class ByteArrayLineReader {
    private ByteArrayOutputStream baos
    private byte[] buffer;
    private int lines;

    ByteArrayLineReader(ByteArrayOutputStream baos) {
        this.baos = baos
    }

    String readLine() {
        if (buffer == null) {
            buffer = baos.toByteArray();
            lines = getNumberOfLines() // TODO: Om det skrivs till strömmen innuti then satsen kommer antalet rader inte stämma. Kan detta uppstå?
        }
        if (lines == 0) {
            return null
        }
        else {
            String line = readNextLine()
            if (lines == 0) {
                buffer == null
                baos.reset()
            }
            line
        }
    }

    private String readNextLine() {
        int newLineIndex = 0
        for (int i = 0; i < buffer.length-1; i++) {
            if (buffer[i] == '\r' && buffer[i+1] == '\n') {
                newLineIndex = i
                break
            }
        }
        byte[] line = new byte[newLineIndex+2]
        System.arraycopy(buffer, 0, line, 0, line.length)
        lines--
        byte[] newBuffer = new byte[buffer.length-line.length]
        System.arraycopy(buffer, line.length, newBuffer, 0, newBuffer.length)
        buffer = newBuffer
        new String(line)
    }

    private int getNumberOfLines() {
        int lines = 0
        for (int i = 0; i < buffer.length-1; i++) {
            if (buffer[i] == '\r' && buffer[i+1] == '\n') {
                lines++
            }
        }
        lines
    }
}
