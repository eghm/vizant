package net.sourceforge.vizant;

/**
 * Target dependency.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizReference {
    public static final int DEPENDS = 0;
    public static final int ANTCALL = 1;
    public static final int ANT = 2;
    
    private int type = DEPENDS;
    private VizTarget from = null;
    private VizTarget to = null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFrom(VizTarget from) {
        this.from = from;
    }

    public VizTarget getFrom() {
        return from;
    }

    public void setTo(VizTarget to) {
        this.to = to;
    }

    public VizTarget getTo() {
        return to;
    }

    public boolean equals(Object o) {
        if (! (o instanceof VizReference))
            return false;
        VizReference ref = (VizReference)o;
        if (getType() != ref.getType())
            return false;
        if (getFrom() == null && ref.getFrom() != null)
            return false;
        if (getTo() == null && ref.getTo() != null) 
            return false;
        return (getFrom().equals(ref.getFrom()) &&
                getTo().equals(ref.getTo()));
    }

    public int hashCode() {
        int ret = 17;
        ret = 37 * ret + getType();
        ret = 37 * ret + ((getFrom() == null) ? 0 : getFrom().hashCode());
        ret = 37 * ret + ((getTo() == null) ? 0 : getTo().hashCode());
        return ret;
    }
}
