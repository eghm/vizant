package net.sourceforge.vizant;

import java.io.File;
import java.io.InputStream;
import java.util.Vector;
import org.apache.tools.ant.BuildException;

/**
 * Project loader.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public interface VizProjectLoader {
    public void uniqueRef(boolean opt);
    public void ignoreAnt(boolean opt);
    public void ignoreAntcall(boolean opt);
    public void ignoreDepends(boolean opt);
    public void setFile(File file);
    public Vector getProjects() throws BuildException;
}
