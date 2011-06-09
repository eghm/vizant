package net.sourceforge.vizant;

import junit.framework.*;

public class VizPrinterTest extends TestCase {
    VizPrinter printer;
    BufferedWriter writer;

    public VizPrinterTest(String name) {
	super(name);
    }

    public void setUp() {
	printer = new VizPrinterAntImpl();
	writer = new BufferedWriter();
	printer.setWriter(writer);
    }

    public void testPrintEmpty() {
	printer.print();
	assertEquals(printer.getClass().toString(),
			   "digraph \"G\" {\n" 
		     + "    graph [\"rankdir\"=\"LR\",];\n"
		     + "}\n", writer.getString());
    }

    public void testSetAttributes() {
	printer.setGraphid("build");

	VizAttrStmt graph = new VizAttrStmt();
	graph.setType("graph");
	graph.addAttribute("label", "test");
	graph.addAttribute("label", "test2");
	printer.addAttributeStatement(graph);

	VizAttrStmt edge = new VizAttrStmt();
	edge.setType("edge");
	edge.addAttribute("a", "3");
	edge.addAttribute("b", "2");
	edge.addAttribute("c", "1");
	printer.addAttributeStatement(edge);

	VizAttrStmt node = new VizAttrStmt();
	node.setType("node");
	node.addAttribute("c", "1");
	node.addAttribute("b", "2");
	node.addAttribute("a", "3");
	printer.addAttributeStatement(node);

	printer.print();
	assertEquals(printer.getClass().toString(), 
			   "digraph \"build\" {\n" 
		     + "    graph [\"rankdir\"=\"LR\",\"label\"=\"test2\",];\n"
		     + "    node [\"rankdir\"=\"LR\",\"label\"=\"test2\",];\n"
		     + "}\n", writer.getString());
    }

}
