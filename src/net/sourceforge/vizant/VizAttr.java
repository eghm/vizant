package net.sourceforge.vizant;

/**
 * &lt;attr&gt; nested element handler. &lt;attr&gt; represents an
 * attribute in the DOT language.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizAttr {
    private String name = null;
    private String value = null;

    /**
     * Set the name of this attirubte.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the value of this attirubte.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the name of this attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of this attribute.
     */
    public String getValue() {
        return value;
    }

    public String toString() {
	return name + "=" + value;
    }
}
