package net.sourceforge.vizant;

public interface VizLogger {

	void verbose(String string);
	void info(String string);
	void warn(String string, Throwable t);
	void debug(String string);
	void warn(String string);

}
