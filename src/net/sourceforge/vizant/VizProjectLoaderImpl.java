package net.sourceforge.vizant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tools.ant.BuildException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
    protected boolean ignoreImport;
    protected boolean ignoreMacrodef;
    private Map antfileMap;
    protected VizLogger vizLogger;
    protected String baseDir;
    protected static Vector allProjects = new Vector();
    protected static Map allMacrodefs = new HashMap();
//    protected Vector projects = new Vector(); cause concurrency exception when a subproject tries to add a project to the inuse static projects...
    
    public VizProjectLoaderImpl(VizLogger vizLogger) {
	uniqueref = false;
	ignoreAnt = false;
	ignoreAntcall = false;
	ignoreDepends = false;
	ignoreMacrodef = false;
	this.vizLogger = vizLogger;
    }

    protected void init(File antfileMapFile) {
    	antfileMap = new HashMap();
    	try {
    		// TODO don't require full path for antfileMaps maybe by
    		// using antfileMaps to also define ant variables and their replacements
    		if (antfileMapFile != null) {
    			vizLogger.info("Reading " + antfileMapFile.toString());
    			BufferedReader in = new BufferedReader(new FileReader(antfileMapFile));
    			String line;
    			String key;
    			String fileValue;
    			while ((line = in.readLine()) != null) {
    				if (line.indexOf("=") > -1) {
        				fileValue = line.substring(line.indexOf("=") + 1, line.length());
        				key = line.substring(0, line.indexOf("="));
        				antfileMap.put(key, fileValue);
        				vizLogger.debug("line: " + line);
        				if (!new File(fileValue).exists()) {
        					vizLogger.warn("antfileMap value: " + fileValue + " not found.  Key was " + key);
        				}    					
    				}
    			}
    			Set keys = antfileMap.keySet();
    			for (Object name :keys) {
					vizLogger.verbose("name:" + (String)name + " value:" + antfileMap.get(name));
				}
    		} else {
    			vizLogger.verbose("No antfileMapFile to process");
    		}
    		
    	} catch (Throwable t) {
    		vizLogger.warn("Problem with antfileMap " + antfileMapFile + " " + t.getMessage(), t);
    		System.err.println("Problem with antfileMap " + antfileMapFile + " " + t.getMessage());
    	}
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

	public void ignoreImport(boolean opt) {
		this.ignoreImport = opt;
	}

	public void setFile(File file) {
	    this.file = file;
	    this.baseDir = file.getParent();
    }

	public void setAntfileMap(File antfileMap) {
		if (antfileMap != null && !antfileMap.isDirectory()) 
			init(antfileMap);
	}

	public Vector getProjects() throws BuildException {
	try {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
	    SAXHandler handler = new SAXHandler(file);
	    parser.parse(new InputSource(new FileInputStream(file)), handler);
	    allProjects.addAll(handler.getProjects());
	    
        SAXParser macroParser = SAXParserFactory.newInstance().newSAXParser();
	    SAXHandler macroHandler = new SaxMacroHandler(handler.getBaseProject(), handler.getProjects());
	    macroParser.parse(new InputSource(new FileInputStream(file)), macroHandler);
	    
//	    return macroHandler.getProjects();
	    return allProjects;
//	    return handler.getProjects();
	} catch(SAXException e) {
	    throw new BuildException(e);
	} catch(ParserConfigurationException e) {
	    throw new BuildException(e);
	} catch(IOException e) {
	    throw new BuildException(e);
	}
    }

    protected class SAXHandler extends DefaultHandler {
	protected String defaultName;
	protected Vector projects;
	protected VizTarget target;
	protected VizProject baseProject;

	public SAXHandler(File file) {
		defaultName = "main";
	    baseProject = new VizProject();
	    baseProject.setDir(file.getParent());
	    baseProject.setFile(file.getName());
	}

	public SAXHandler(VizProject baseProject, Vector projects) {
		this.projects = new Vector();
		this.baseProject = baseProject;
		this.projects.addAll(projects);
	}	
	
	public SAXHandler() {
		defaultName = "main";
	    baseProject = new VizProject();
	}

	public VizProject getBaseProject() {
		return baseProject;
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
		    String projName = atts.getValue("name");
			if (def != null)
			    defaultName = def;
			if (dir != null && baseProject.getDir() == null)
			    baseProject.setDir(dir);
			if (projName != null)
			    baseProject.setName(projName);				
	    } else if ("target".equals(qName)) {
			String targetName = atts.getValue("name");
			String depends = atts.getValue("depends");
			target = baseProject.getTarget(targetName);
			target.setDefault(targetName.equals(defaultName));
			baseProject.appendTarget(target);
			if (null != depends && !ignoreDepends) {
				addDepends(target, depends);				
			}
		} else if ("antcall".equals(qName)) {
				if (! ignoreAntcall) {
				    addAntCall(target, atts.getValue("target"));					
				}
		} else if ("ant".equals(qName)) {
				if (! ignoreAnt) {
				    addAnt(target, 
					   atts.getValue("dir"),
					   atts.getValue("antfile"),
					   atts.getValue("target"));
				}
		} else if ("import".equals(qName) && !ignoreImport) {
		    	addImport(atts.getValue("file"));
		} else if ("macrodef".equals(qName) && !ignoreMacrodef) {
			String targetName = atts.getValue("name");
			target = baseProject.getTarget(targetName);
			target.setDefault(targetName.equals(defaultName));
			baseProject.appendTarget(target);
			allMacrodefs.put(targetName, target);
		}
	}
	
	protected void addReference(VizTarget from, VizTarget to, int type) {
	    VizReference reference = new VizReference();
	    reference.setFrom(from);
	    reference.setTo(to);
	    reference.setType(type);
	    from.addReferenceOut(reference, uniqueref);
	    to.addReferenceIn(reference, uniqueref);
	    vizLogger.debug("New SAX Reference (attempt) via " + this.getClass().getName() + " : " + reference.toString() + " from: " + from.toString() + " to:" + to.toString());
	}

	protected void addReferenceIfUnique(VizTarget from, VizTarget to, int type) {
	    VizReference reference = new VizReference();
	    reference.setFrom(from);
	    reference.setTo(to);
	    reference.setType(type);
	    from.addReferenceOut(reference, true);
	    to.addReferenceIn(reference, true);
	    vizLogger.debug("New Unique Reference (attempt) via " + this.getClass().getName() + " : " + reference.toString() + " from: " + from.toString() + " to:" + to.toString());
	}

	protected void addDepends(VizTarget from, String toNames) {
		vizLogger.debug("Adding depends from " + from.getId() + " to " + toNames + " of baseProjct " + baseProject);
	    StringTokenizer st = new StringTokenizer(toNames, ",");
	    while (st.hasMoreTokens()) {
			VizTarget to = baseProject.getTarget(st.nextToken().trim());
			addReference(from, to, VizReference.DEPENDS);
	    }
	}

	protected void addAntCall(VizTarget from, String toName) {
	    VizTarget to = baseProject.getTarget(toName);
	    addReference(from, to, VizReference.ANTCALL);
	}

	protected void addAnt(VizTarget from,
			    String toDir, String toFile, String toName) {
//		toDir = (toDir != null) ? toDir : baseDir;
		toDir = (toDir != null) ? toDir : "";
	    toFile = (toFile != null) ? toFile : "";
	    toName = (toName != null) ? toName : "default";
		vizLogger.debug("addAnt from:" + from + " toDir:" + toDir + " toFile:" + toFile + " toName:" + toName);
	    VizProject toProject = getProject(toDir, toFile);
	    if (toProject != null) { // exceptions reported in getProject if null
		    VizTarget to = toProject.getTarget(toName);
		    if ("".equals(toName) || "default".equals(toName)) {
		    	to.setDefault(true);
		    }
		    toProject.appendTarget(to);
		    addReference(from, to, VizReference.ANT);	    	
	    }
	}

	protected void addImport(String imported) {
		String dir, file;
		if (imported.indexOf(File.separator) > -1) {
			dir = imported.substring(0, imported.lastIndexOf(File.separator));
			file = imported.substring(imported.lastIndexOf(File.separator) + 1, imported.length());			
		} else {
			dir = "";
			file = imported;
		}		
		getProject(dir, file);
	}
	
	protected VizProject getProject(String dir, String file) {
		String origDir = dir;
		vizLogger.verbose("getProject dir:" + dir + " file:" + file);
		if (antfileMap != null && antfileMap.get(dir + File.separator + file) != null) {
			String antfilepath = (String)antfileMap.get(dir + File.separator + file);
			if (!"".equals(antfilepath)) {
				dir = antfilepath.substring(0, antfilepath.lastIndexOf(File.separator));
				file = antfilepath.substring(antfilepath.lastIndexOf(File.separator) + 1, antfilepath.length());
				vizLogger.verbose("antfileMapFile's dir " + dir + " file " + file);				
			}
		}

		if ("".equals(dir) && !"".equals(file)) { 
//			dir = baseProject.getDir();
//			dir = ((VizProject)projects.get(0)).getDir();
			dir = baseDir; // TODO?
		}

//	    VizProject cachedProject = cachedProject(dir, file, projects);
	    VizProject cachedProject = cachedProject(dir, file, allProjects);
	    if (cachedProject != null) {
	    	return cachedProject;
	    }
	    
        return parseProject(dir, file, origDir, allProjects, new SAXHandler(new VizProject(), projects));
	}

	protected VizProject cachedProject(String dir, String file, Vector allProjects) {
//	    Enumeration projEnum = projects.elements();
		Enumeration projEnum = allProjects.elements();
	    while(projEnum.hasMoreElements()) {
			VizProject project = (VizProject)projEnum.nextElement();
			vizLogger.verbose("       Checking existing projects, does dir '" + dir + "' = '" + project.getDir() + "' does file '" + file + "' = '" + project.getFile() + "'");
//			vizLogger.debug("Checking existing projects, does dir '" + dir + "' = '" + project.getDir() + "' does file '" + file + "' = '" + project.getFile() + "'");
			if (dir.equals(project.getDir()) && file.equals(project.getFile())) {
				vizLogger.verbose("Using cached project at " + project.getDir() + File.separator + project.getFile() + " set debug to view details");
				vizLogger.debug("Cached project at " + project.getDir() + File.separator + project.getFile() + " " + project.toString());
			    return project;				
			}
	    }
	    return null;
	}

	protected VizProject parseProject(String dir, String file, String origDir, Vector allProjects, SAXHandler handler) {
		SAXParser parser;
        VizProject project = null;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
		    
			parser.parse(new InputSource(new FileInputStream(dir + File.separator + file)), handler);		    	
		    for (Object vizProject : handler.getProjects()) {
		    	if (project == null) {
		    		if ((((VizProject)vizProject).getDir() == null) || "".equals(((VizProject)vizProject).getDir())) {
		    			((VizProject)vizProject).setDir(dir);
		    		}
		    		if ((((VizProject)vizProject).getFile() == null) || "".equals(((VizProject)vizProject).getFile())) {
		    			((VizProject)vizProject).setFile(file);
		    		}
		    		project = (VizProject)vizProject; // will be returned
		    	}
//		    	if (!allProjects.contains(vizProject)) {
			    	allProjects.add(vizProject);
//				    projects.addElement(vizProject);				
//		    	}
			}		    
		} catch (FileNotFoundException fnfe) {
			vizLogger.warn("FileNotFound:" + dir + File.separator + file + " antfileMap key to define file location:" + origDir + File.separator + file);
		}
		catch (Exception e) {
			vizLogger.warn("Problem parsing dir:" + dir + " file:" + file, e);
			e.printStackTrace();
		}
		return project;
	}
    }

    private class SaxMacroHandler extends SAXHandler {
    	
    	public SaxMacroHandler(VizProject baseProject, Vector projects) {
    		this.projects = new Vector();
    		this.baseProject = baseProject;
    		this.projects.addAll(projects);
    	}
    	
    	public void startElement (String uri, 
				  String name, 
				  String qName, 
				  Attributes atts) {
	    if ("target".equals(qName)) {
			String targetName = atts.getValue("name");
			target = baseProject.getTarget(targetName);
	    } else if (allMacrodefs.get(qName) != null) {
			VizTarget macroTarget = (VizTarget)allMacrodefs.get(qName);
			addReference(target, macroTarget, VizReference.MACRO);
			target = macroTarget;
	    } else if ("import".equals(qName) && !ignoreImport) {
	    	addImport(atts.getValue("file"));
		} else if ("ant".equals(qName)) {
			if (! ignoreAnt) {
			    addAnt(target, 
				   atts.getValue("dir"),
				   atts.getValue("antfile"),
				   atts.getValue("target"));
			}
		} else if ("macrodef".equals(qName) && !ignoreMacrodef) {
			String targetName = atts.getValue("name");
			target = baseProject.getTarget(targetName);
			if (allMacrodefs.get(targetName) == null) {
				allMacrodefs.put(targetName, target);
			}
		}
	}
    	protected void addReference(VizTarget from, VizTarget to, int type) {
    		addReferenceIfUnique(from, to, type);
    	}
    	
    	protected VizProject getProject(String dir, String file) {
    		String origDir = dir;
    		vizLogger.verbose("getProject dir:" + dir + " file:" + file);
    		if (antfileMap != null && antfileMap.get(dir + File.separator + file) != null) {
    			String antfilepath = (String)antfileMap.get(dir + File.separator + file);
    			if (!"".equals(antfilepath)) {
    				dir = antfilepath.substring(0, antfilepath.lastIndexOf(File.separator));
    				file = antfilepath.substring(antfilepath.lastIndexOf(File.separator) + 1, antfilepath.length());
    				vizLogger.verbose("antfileMapFile's dir " + dir + " file " + file);				
    			}
    		}

    		if ("".equals(dir) && !"".equals(file)) { 
//    			dir = ((VizProject)projects.get(0)).getDir();
    			dir = baseDir; // TODO?
    		}

    	    VizProject cachedProject = cachedProject(dir, file, allProjects);
    	    if (cachedProject != null) {
    	    	return cachedProject;
    	    }
    	    
            return parseProject(dir, file, origDir, new Vector(), new SaxMacroHandler(new VizProject(), projects));
    	}

    	protected VizProject cachedProject(String dir, String file, Vector allProjects) {
        	return null;
        }
    }
}




