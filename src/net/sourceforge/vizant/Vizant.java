package net.sourceforge.vizant;

import java.util.Vector;
import java.util.Enumeration;
import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/**
 * Vizant task.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class Vizant extends Task {
    private File antfile;
    private File outfile;
    private VizProjectLoader loader;
    private VizPrinter printer;
    private VizWriter writer;

    public void init() {
	loader = getLoader();
	printer = getPrinter();
    }
    
    public void setAntfile(File antfile) throws BuildException {
	this.antfile = antfile;
	try {
	    loader.setInputStream(new FileInputStream(antfile));
	} catch (FileNotFoundException e) {
	    throw new BuildException(e);
	}
    }

    public void setOutfile(File outfile) {
	this.outfile = outfile;
    }

    public void setGraphid(String graphid) {
        printer.setGraphid(graphid);
    }

    public void setFrom(String targetName) {
        printer.setFrom(targetName);
    }

    public void setTo(String targetName) {
        printer.setTo(targetName);
    }

    public void setNocluster(boolean noclustor) {
        printer.setNocluster(noclustor);
    }

    public void setUniqueref(boolean uniqueref) {
        loader.uniqueRef(uniqueref);
    }

    public void setIgnoreant(boolean opt) {
        loader.ignoreAnt(opt);
    }

    public void setIgnoreantcall(boolean opt) {
        loader.ignoreAntcall(opt);
    }

    public void setIgnoredepends(boolean opt) {
        loader.ignoreDepends(opt);
    }

    public void addConfiguredAttrstmt(VizAttrStmt attrstmt) 
        throws BuildException {
        attrstmt.checkConfiguration();
        printer.addAttributeStatement(attrstmt);
    }

    public void addSubgraph(VizSubgraph subgraph) {
        subgraph.setPrinter(printer);
    }

    public void execute() throws BuildException {
	checkConfiguration();
	loadProjects();
	writeDotToOutfile();
    }

    protected VizPrinter getPrinter() {
	return new VizPrinter();
    }

    protected VizProjectLoader getLoader() {
	return new VizProjectLoaderImpl();
    }

    protected void checkConfiguration() throws BuildException {
	if (antfile == null) {
	    throw new BuildException("antfile attribute is required");
	}
	if (outfile == null) {
	    throw new BuildException("outfile attribute is required");
	}
    }

    protected void loadProjects() throws BuildException {
        Enumeration enum = loader.getProjects().elements();
        while (enum.hasMoreElements()) {
            printer.addProject((VizProject)enum.nextElement());
        }
    }

    protected void writeDotToOutfile() throws BuildException {
	VizFileWriter out = null;
	try {
            out = new VizFileWriter(outfile);
	    print(out);
	} catch(IOException e) {
	    throw new BuildException(e.toString());
	} finally {
	    if (out != null)
		out.close();
	}
    }

    protected void print(VizWriter out) {
	printer.setWriter(out);
	printer.print();
    }

    public class VizFileWriter implements VizWriter {
	private PrintWriter out = null;
	
	public VizFileWriter(File outfile) throws IOException {
	    out = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
	}

	public void print(String str) {
	    out.print(str);
	}

	public void println(String str) {
	    out.println(str);
	}

	public void close() {
	    if (out != null) {
		out.flush();
		out.close();
	    }
	}
    }
}
