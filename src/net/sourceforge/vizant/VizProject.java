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
    private String name = "";
    private static Hashtable allTargets = new Hashtable(); // when not static, 1 node gets a blank label, probably a bug somewhere
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

	public void setName(String projName) {
		this.name = projName;
	}

	public String getName() {
		return this.name;
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
    	if (id == null || "".equals(id)) {
    		throw new RuntimeException("bad id '" + id + "'");
    	}
        VizTarget target = (VizTarget)allTargets.get(id);
        if (target == null) {
//        	System.out.println("creating new target for " + id + " on project dir " + dir + " file " + file + " name " + name);
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
//            + " allTargets:" + allTargets
            + " orderedTargets:" + orderedTargets;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((allTargets == null) ? 0 : allTargets.hashCode());
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result
				+ ((orderedTargets == null) ? 0 : orderedTargets.hashCode());
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
		VizProject other = (VizProject) obj;
		if (allTargets == null) {
			if (other.allTargets != null) {
				return false;
			}
		} else if (!allTargets.equals(other.allTargets)) {
			return false;
		}
		if (dir == null) {
			if (other.dir != null) {
				return false;
			}
		} else if (!dir.equals(other.dir)) {
			return false;
		}
		if (file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		if (orderedTargets == null) {
			if (other.orderedTargets != null) {
				return false;
			}
		} else if (!orderedTargets.equals(other.orderedTargets)) {
			return false;
		}
		return true;
	}
}
