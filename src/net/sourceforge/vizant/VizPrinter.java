package net.sourceforge.vizant;

public interface VizPrinter {

	void addAttributeStatement(VizAttrStmt node);

	void addProject(VizProject nextElement);

	void addSubgraphAttributeStatement(VizAttrStmt attrstmt);

	void print();

	void setFrom(String targetName);

	void setGraphid(String graphid);

	void setNocluster(boolean noclustor);

	void setTo(String targetName);

	void setWriter(VizWriter out);

}
