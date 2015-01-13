package to.kit.data.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Definition of Schema.
 * @author H.Sasai
 */
public final class Schema implements Iterable<EntityInfo> {
	/** Entity list. */
	private List<EntityInfo> entityList = new ArrayList<>();
	/** NameSpace. */
	private String nameSpace;
	/** Physical directory path. */
	private String path;
	/** Interface. */
	private String interfaces;

	@Override
	public Iterator<EntityInfo> iterator() {
		return this.entityList.iterator();
	}

	public void addEntity(EntityInfo entity) {
		entity.setSchema(this);
		this.entityList.add(entity);
	}

	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return this.nameSpace;
	}

	/**
	 * @param val the nameSpace to set
	 */
	public void setNameSpace(String val) {
		this.nameSpace = val;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @param val the path to set
	 */
	public void setPath(String val) {
		this.path = val;
	}

	/**
	 * @return the interfaces
	 */
	public String getInterfaces() {
		return this.interfaces;
	}

	/**
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}
}
