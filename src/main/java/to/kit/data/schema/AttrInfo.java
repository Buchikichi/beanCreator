package to.kit.data.schema;

/**
 * Definition of attribute.
 * @author H.Sasai
 */
public final class AttrInfo {
	/** Name of field. */
	private String name;
	/** Kanji name of field. */
	private String comment;
	/** Type of field. */
	private String type;
	/** Size of field. */
	private String size;
	/** Null. */
	private boolean nullable;
	/** Primary key. */
	private boolean pk;

	public String getJavaType() {
		String result = "String";

		if ("int".equals(this.type)) {
			result = "int";
		} else {
			result = "String";
		}
		return result;
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
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return this.size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return this.nullable;
	}
	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	/**
	 * @return
	 */
	public boolean isPk() {
		return this.pk;
	}
	/**
	 * @param b
	 */
	public void setPk(boolean b) {
		this.pk = b;
	}
}
