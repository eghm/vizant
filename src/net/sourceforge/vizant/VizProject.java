package net.sourceforge.vizant;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Ant project.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizProject {
    private String dir = "";
    private String file = "";
    private Hashtable allTargets = new Hashtable();
    private Vector orderedTargets = new Vector();

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void copyAttributes(VizProject project) {
        project.setDir(getDir());
        project.setFile(getFile());
    }

    public void appendTarget(VizTarget target) {
        if (! orderedTargets.contains(target)) {
            orderedTargets.addElement(target);
            if (! allTargets.contains(target))
                allTargets.put(target.getId(), target);
        }
    }

    public VizTarget getTarget(String id) {
        VizTarget target = (VizTarget)allTargets.get(id);
        if (target == null) {
            target = new VizTarget();
            target.setId(id);
            target.setProject(this);
            allTargets.put(target.getId(), target);
        }
        return target;
    }

    public Vector getOrderedTargets() {
        return orderedTargets;
    }

    public String toString() {
        return "VizProject:" 
            + " dir:" + dir
            + " file:" + file
            + " allTargets:" + allTargets
            + " orderedTargets:" + orderedTargets;
    }

}
