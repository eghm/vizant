package net.sourceforge.vizant;

import org.apache.tools.ant.BuildException;

/**
 * &lt;subgraph&gt; nested element handler.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizSubgraph {
    VizPrinter printer = null;

    public void setPrinter(VizPrinter printer) {
        this.printer = printer;
    }

    public void addConfiguredAttrstmt(VizAttrStmt attrstmt) 
        throws BuildException{
        attrstmt.checkConfiguration();
        printer.addSubgraphAttributeStatement(attrstmt);
    }
}
