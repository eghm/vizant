package net.sourceforge.vizant;

public class BufferedWriter implements VizWriter {
    StringBuffer buffer = new StringBuffer();
    
    public void print(String str) {
	buffer.append(str);
    }
    
    public void println(String str) {
	buffer.append(str).append("\n");
    }
    
    public String getString() {
	return buffer.toString();
    }
}
