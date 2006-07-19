package net.sourceforge.vizant;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Ant target.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizTarget {
    private String id;
    private Vector referencesIn = new Vector();
    private Vector referencesOut = new Vector();
    private VizProject project;
    private boolean defaultTarget = false;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDefault(boolean defaultTarget) {
        this.defaultTarget = defaultTarget;
    }

    public boolean isDefault() {
        return defaultTarget;
    }

    public void setProject(VizProject project) {
        this.project = project;
    }

    public VizProject getProject() {
        return project;
    }

    public Vector getReferencesIn() {
        return referencesIn;
    }

    public Vector getReferencesOut() {
        return referencesOut;
    }

    public void addReferenceIn(VizReference ref, boolean unique) {
        if (unique && referencesIn.contains(ref))
            return;
        if (! this.equals(ref.getTo()))
            return;
        referencesIn.addElement(ref);
    }

    public void addReferenceOut(VizReference ref, boolean unique) {
        if (unique && referencesOut.contains(ref))
            return;
        if (! this.equals(ref.getFrom()))
            return;
        referencesOut.addElement(ref);
    }

    public void filterReferences(Vector targets) {
        referencesIn = filterReferences(targets, referencesIn);
        referencesOut = filterReferences(targets, referencesOut);
    }

    private Vector filterReferences(Vector targets, 
                                    Vector references) {
        Vector ret = new Vector();
        Enumeration refEnum = references.elements();
        while (refEnum.hasMoreElements()) {
            VizReference r = (VizReference)refEnum.nextElement();
            if(targets.contains(r.getFrom()) &&
               targets.contains(r.getTo())) {
                ret.addElement(r);
            }
        }
        return ret;
    }

    public String toString() {
        return "VizTarget: id:" + id 
            + " referencesIn:" + referencesIn
            + " referencesOut:" + referencesOut
            + " default:" + defaultTarget;
    }
}

