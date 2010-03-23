package net.sourceforge.vizant;

import java.io.File;import java.io.FileInputStream;import java.io.IOException;import java.util.Enumeration;import java.util.StringTokenizer;import java.util.Vector;import javax.xml.parsers.ParserConfigurationException;import javax.xml.parsers.SAXParser;import javax.xml.parsers.SAXParserFactory;import org.apache.tools.ant.BuildException;import org.xml.sax.Attributes;import org.xml.sax.InputSource;import org.xml.sax.SAXException;import org.xml.sax.helpers.DefaultHandler;

/**
 * An implementation of VizProjectLoader.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizProjectLoaderImpl implements VizProjectLoader {
    protected File file;
    protected boolean uniqueref;
    private boolean ignoreAnt;
    private boolean ignoreAntcall;
    protected boolean ignoreDepends;

    public VizProjectLoaderImpl() {
	uniqueref = false;
	ignoreAnt = false;
	ignoreAntcall = false;
	ignoreDepends = false;
    }

    public void uniqueRef(boolean uniqueref) {
	this.uniqueref = uniqueref;
    }

    public void ignoreAnt(boolean ignoreAnt) {
	this.ignoreAnt = ignoreAnt;
    }

    public void ignoreAntcall(boolean ignoreAntcall) {
	this.ignoreAntcall = ignoreAntcall;
    }

    public void ignoreDepends(boolean ignoreDepends) {
	this.ignoreDepends = ignoreDepends;
    }

    public void setFile(File file) {
	this.file = file;
    }

    public Vector getProjects() throws BuildException {
	try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
	    SAXHandler handler = new SAXHandler();
	    parser.parse(new InputSource(new FileInputStream(file)), handler);
	    return handler.getProjects();
	} catch(SAXException e) {
	    throw new BuildException(e);
	} catch(ParserConfigurationException e) {
	    throw new BuildException(e);
	} catch(IOException e) {
	    throw new BuildException(e);
	}
    }

    private class SAXHandler extends DefaultHandler {
	private String defaultName;
	private Vector projects;
	private VizTarget target;
	private VizProject baseProject;

	public SAXHandler() {
	    defaultName = "main";
	    baseProject = new VizProject();
	}

	public Vector getProjects() {
	    return projects;
	}

	public void startDocument() {
	    projects = new Vector();
	    projects.addElement(baseProject);
	}

	public void startElement (String uri, 
				  String name, 
				  String qName, 
				  Attributes atts) {
	    if ("project".equals(qName)) {
		String def = atts.getValue("default");
		String dir = atts.getValue("dir");
		if (def != null)
		    defaultName = def;
		if (dir != null)
		    baseProject.setDir(dir);
	    } else if ("target".equals(qName)) {
		String targetName = atts.getValue("name");
		String depends = atts.getValue("depends");
		target = baseProject.getTarget(targetName);
		target.setDefault(targetName.equals(defaultName));
		baseProject.appendTarget(target);
		if (null != depends && ! ignoreDepends)
		    addDepends(target, depends);
	    } else if ("antcall".equals(qName)) {
		if (! ignoreAntcall)
		    addAntCall(target, atts.getValue("target"));
	    } else if ("ant".equals(qName)) {
		if (! ignoreAnt)
		    addAnt(target, 
			   atts.getValue("dir"),
			   atts.getValue("antfile"),
			   atts.getValue("target"));
	    }
	}
	
	private void addReference(VizTarget from, VizTarget to, int type) {
	    VizReference reference = new VizReference();
	    reference.setFrom(from);
	    reference.setTo(to);
	    reference.setType(type);
	    from.addReferenceOut(reference, uniqueref);
	    to.addReferenceIn(reference, uniqueref);
	}

	private void addDepends(VizTarget from, String toNames) {
	    StringTokenizer st = new StringTokenizer(toNames, ",");
	    while (st.hasMoreTokens()) {
		VizTarget to = baseProject.getTarget(st.nextToken().trim());
		addReference(from, to, VizReference.DEPENDS);
	    }
	}

	private void addAntCall(VizTarget from, String toName) {
	    VizTarget to = baseProject.getTarget(toName);
	    addReference(from, to, VizReference.ANTCALL);
	}

	private void addAnt(VizTarget from,
			    String toDir, String toFile, String toName) {
	    toDir = (toDir != null) ? toDir : "";
	    toFile = (toFile != null) ? toFile : "";
	    toName = (toName != null) ? toName : "";
	    VizProject toProject = getProject(toDir, toFile);
	    VizTarget to = toProject.getTarget(toName);
	    if ("".equals(toName))
		to.setDefault(true);
	    toProject.appendTarget(to);
	    addReference(from, to, VizReference.ANT);
	}

	private VizProject getProject(String dir, String file) {
	    Enumeration projEnum = projects.elements();
	    while(projEnum.hasMoreElements()) {
		VizProject project = (VizProject)projEnum.nextElement();
		if (dir.equals(project.getDir()) && file.equals(project.getFile()))
		    return project;
	    }
	    VizProject project = new VizProject();
	    project.setDir(dir);
	    project.setFile(file);
	    projects.addElement(project);
	    return project;
	}
    } 
}




