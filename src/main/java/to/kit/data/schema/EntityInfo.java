package to.kit.data.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Definition of entity.
 * @author H.Sasai
 */
public final class EntityInfo implements Iterable<AttrInfo> {
	/** Name of entity. */
	private String name;
	/** Comment of entity. */
	private String comment;
	/** List of attributes. */
	private List<AttrInfo> attrList = new ArrayList<>();
	/** Parent. */
	private Schema schema;

	@Override
	public Iterator<AttrInfo> iterator() {
		return this.attrList.iterator();
	}
	public void addAttr(AttrInfo attr) {
		this.attrList.add(attr);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the kanji
	 */
	public String getComment() {
		return this.comment;
	}
	/**
	 * @param kanji the kanji to set
	 */
	public void setComment(String kanji) {
		this.comment = kanji;
	}
	/**
	 * @return the schema
	 */
	public Schema getSchema() {
		return this.schema;
	}
	/**
	 * @param obj the schema to set
	 */
	public void setSchema(Schema obj) {
		this.schema = obj;
	}
}
