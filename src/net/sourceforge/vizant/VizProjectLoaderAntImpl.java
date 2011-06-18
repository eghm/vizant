package net.sourceforge.vizant;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;

/**
 * Vizant build xml loader using Ant's ProjectHelper.
 * 
 * @author kpothula (Krishna Rao Pothula)
 * 
 */
public class VizProjectLoaderAntImpl extends VizProjectLoaderImpl {
	private Vector projects = new Vector();
	private VizProject baseProject;
	private String defaultName;

	public VizProjectLoaderAntImpl(VizLogger vizLogger) {
		super(vizLogger);
	}
	
	public Vector getProjects() throws BuildException {
		Project project = new Project();
		project.init();
		try {
			ProjectHelper.configureProject(project, file);
		} catch (Throwable t) {
			vizLogger.warn("Problem with Ant Project configuration of " + file, t);
		}

		configureProject(project);

		Hashtable targets = project.getTargets();

		for (Iterator iterator = targets.values().iterator(); iterator
				.hasNext();) {
			Target targetItem = (Target) iterator.next();
			String targetName = targetItem.getName();
			Enumeration depends = targetItem.getDependencies();
			VizTarget target = baseProject.getTarget(targetName);
			target.setDefault(targetName.equals(defaultName));
			baseProject.appendTarget(target);
			if (null != depends && !ignoreDepends)
				addDepends(target, depends);

		}

		return projects;

	}

	private void configureProject(Project project) {
		baseProject = new VizProject();
		String def = project.getDefaultTarget();
		if (def != null)
			defaultName = def;
		projects.add(baseProject);
	}

	private void addReference(VizTarget from, VizTarget to, int type) {
		VizReference reference = new VizReference();
		reference.setFrom(from);
		reference.setTo(to);
		reference.setType(type);
		from.addReferenceOut(reference, uniqueref);
		to.addReferenceIn(reference, uniqueref);
	}

	private void addDepends(VizTarget from, Enumeration toNames) {
		while (toNames.hasMoreElements()) {
			String name = (String) toNames.nextElement();
			VizTarget to = baseProject.getTarget(name);
			addReference(from, to, VizReference.DEPENDS);
		}
	}

	private VizProject getProject(String dir, String file) {
		Enumeration projEnum = projects.elements();
		while (projEnum.hasMoreElements()) {
			VizProject project = (VizProject) projEnum.nextElement();
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