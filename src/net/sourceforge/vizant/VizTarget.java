package net.sourceforge.vizant;

import java.util.Iterator;
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

//    public String refVectorToString(Vector v) {
//    	if (v == null) return "[]";
//    	StringBuilder stringBuilder = new StringBuilder("[");
//    	for (Iterator iterator = v.iterator(); iterator.hasNext();) {
//			VizReference ref = (VizReference) iterator.next();
//			stringBuilder.append(ref.toString()).append(",");
//		}
//    	return stringBuilder.toString().substring(0, stringBuilder.length() - 1) + "]";
//    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (defaultTarget ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((project == null) ? 0 : (project.getDir() + project.getFile()).hashCode());
		result = prime * result
				+ ((referencesIn == null) ? 0 : referencesIn.hashCode());
		result = prime * result
				+ ((referencesOut == null) ? 0 : referencesOut.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VizTarget other = (VizTarget) obj;
		if (defaultTarget != other.defaultTarget) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (project == null) {
			if (other.project != null) {
				return false;
			}
		} else if (!(project.getDir() + project.getFile()).equals(other.project.getDir() + other.project.getFile())) {
			return false;
		}
		if (referencesIn == null) {
			if (other.referencesIn != null) {
				return false;
			}
		} else if (!referencesIn.equals(other.referencesIn)) {
			return false;
		}
		if (referencesOut == null) {
			if (other.referencesOut != null) {
				return false;
			}
		} else if (!referencesOut.equals(other.referencesOut)) {
			return false;
		}
		return true;
	}
}

