package net.sourceforge.vizant;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.tools.ant.BuildException;

/**
 * &lt;attrstmt&gt; nested element handler. &lt;attrstmt&gt; represents
 * attr_stmt in <a href="http://www.research.att.com/~erg/graphviz/info/lang.html" target="_blank">the DOT language</a>.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizAttrStmt {
    /** attribute statement type */
    private VizASType type = null;
    /** attributes table */
    private Vector attrs = new Vector();

    /**
     * set attribute statement type.
     */
    public void setType(String type) throws BuildException {
        this.type = VizASType.get(type);
    }

    /**
     * get attribute statement type.
     */
    public VizASType getType() {
        return type;
    }

    /**
     * add &lt;attr&gt; nested element.
     */
    public void addConfiguredAttr(VizAttr attr) {
	addAttribute(attr);
    }

    /**
     * add attribute.
     */
    public void addAttribute(VizAttr attr) {
	Enumeration enum = attrs.elements();
	while (enum.hasMoreElements()) {
	    VizAttr a = (VizAttr)enum.nextElement();
	    if (a.getName().equals(attr.getName())) {
		a.setValue(attr.getValue());
		return;
	    }
	}
	attrs.addElement(attr);
    }

    /**
     * add attribute.
     *
     * @param name Name of the attribute.
     * @param value Value of the attribute.
     */
    public void addAttribute(String name, String value) {
	VizAttr attr = new VizAttr();
	attr.setName(name);
	attr.setValue(value);
	addAttribute(attr);
    }

    /**
     * add attribute(s) from the other attrstmt.
     */
    public void addAttribute(VizAttrStmt attrstmt) {
	if (! type.equals(attrstmt.getType()))
	    return;
        Enumeration enum = attrstmt.getAttributes();
        while (enum.hasMoreElements()) {
            addAttribute((VizAttr)enum.nextElement());
        }
    }

    /**
     * get attributes as Enumeration.
     */
    public Enumeration getAttributes() {
        return attrs.elements();
    }

    protected void checkConfiguration() throws BuildException {
        if (this.type == null)
            throw new BuildException("attrstmt: type attribute is required.");
    }

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append(type).append(" [ ");
	Enumeration enum = getAttributes();
	while (enum.hasMoreElements()) {
	    buffer.append(enum.nextElement().toString()).append(" ");
	}
	buffer.append("]");
	return buffer.toString();
    }
}





