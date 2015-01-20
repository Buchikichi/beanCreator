package to.kit.data.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import to.kit.data.schema.AttrInfo;
import to.kit.data.schema.EntityInfo;
import to.kit.data.schema.Schema;
import to.kit.util.NameUtils;

/**
 * DataBeanCreator.
 * @author H.Sasai
 */
public final class DataBeanCreator {
	private static final String INDENT = "\t";
	private static final String LF = "\n";

	private String makeSetter(AttrInfo attr) {
		StringBuilder buff = new StringBuilder();
		String name = attr.getName();
		String argName = "value";

		buff.append(INDENT);
		buff.append("/** ");
		buff.append(attr.getComment());
		buff.append("[");
		buff.append(attr.getType());
		String size = attr.getSize();
		if (!StringUtils.isEmpty(size)) {
			buff.append(String.format("(%s)", size));
		}
		buff.append("]");
		buff.append(". */");
		buff.append(LF);
		buff.append(INDENT);
		buff.append("public void set");
		buff.append(NameUtils.toPascal(name));
		buff.append("(");
		buff.append(attr.getJavaType());
		buff.append(" ");
		buff.append(argName);
		buff.append(") {");
		buff.append(LF);
		buff.append(INDENT);
		buff.append(INDENT);
		buff.append("this.");
		buff.append(NameUtils.toCamel(name));
		buff.append(" = ");
		buff.append(argName);
		buff.append(";");
		buff.append(LF);
		buff.append(INDENT);
		buff.append("}");
		buff.append(LF);
		return buff.toString();
	}

	private String makeGetter(AttrInfo attr) {
		StringBuilder buff = new StringBuilder();
		String name = attr.getName();

		buff.append(INDENT);
		buff.append("/** ");
		buff.append(attr.getComment());
		buff.append("[");
		buff.append(attr.getType());
		String size = attr.getSize();
		if (!StringUtils.isEmpty(size)) {
			buff.append(String.format("(%s)", size));
		}
		buff.append("]");
		buff.append(". */");
		buff.append(LF);
		buff.append(INDENT);
		buff.append("public ");
		buff.append(attr.getJavaType());
		buff.append(" get");
		buff.append(NameUtils.toPascal(name));
		buff.append("() {");
		buff.append(LF);
		buff.append(INDENT);
		buff.append(INDENT);
		buff.append("return this.");
		buff.append(NameUtils.toCamel(name));
		buff.append(";");
		buff.append(LF);
		buff.append(INDENT);
		buff.append("}");
		buff.append(LF);
		return buff.toString();
	}

	private String makeFields(EntityInfo entity) {
		StringBuilder buff = new StringBuilder();

		for (AttrInfo attr : entity) {
			String name = attr.getName();

			buff.append(INDENT);
			buff.append("/** ");
			buff.append(attr.getComment());
			buff.append(". */");
			buff.append(LF);
			// annotation
			if (attr.isPk()) {
				buff.append(INDENT);
				buff.append("@Id");
				buff.append(LF);
			}
			buff.append(INDENT);
			buff.append("@Column(name=\"");
			buff.append(name);
			buff.append("\", columnDefinition=\"");
			buff.append(attr.getType());
			buff.append("\")");
			buff.append(LF);
			// definition
			buff.append(INDENT);
			buff.append("private ");
			buff.append(attr.getJavaType());
			buff.append(" ");
			buff.append(NameUtils.toCamel(name));
			buff.append(";");
			buff.append(LF);
		}
		return buff.toString();
	}

	private String makeImport(String fields) {
		StringBuilder buff = new StringBuilder();
		List<String> list = new ArrayList<>();

		if (fields.indexOf(" Date ") != -1) {
			list.add("java.sql.Date");
		}
		if (fields.indexOf(" Timestamp ") != -1) {
			list.add("java.sql.Timestamp");
		}
		if (!list.isEmpty()) {
			Collections.sort(list);
			for (String pkg : list) {
				buff.append("import ");
				buff.append(pkg);
				buff.append(";");
				buff.append(LF);
			}
			buff.append(LF);
		}
		buff.append("import javax.persistence.Column;");
		buff.append(LF);
		buff.append("import javax.persistence.Entity;");
		buff.append(LF);
		buff.append("import javax.persistence.Id;");
		buff.append(LF);
		return buff.toString();
	}

	private String make(EntityInfo entity) {
		StringBuilder buff = new StringBuilder();
		String name = entity.getName();
		String className = NameUtils.toPascal(name);
		Schema schema = entity.getSchema();
		String nameSpace = schema.getNameSpace();
		String interfaces = schema.getInterfaces();
		String fields = makeFields(entity);
//Set<String> typeSet = new HashSet<>();

		buff.append("package ");
		buff.append(nameSpace);
		buff.append(";");
		buff.append(LF);
		buff.append(LF);
		buff.append(makeImport(fields));
		buff.append(LF);
		buff.append("/**");
		buff.append(LF);
		buff.append(" * `");
		buff.append(entity.getComment());
		buff.append("`.");
		buff.append(LF);
		buff.append(" */");
		buff.append(LF);
		buff.append("@Entity(name=\"");
		buff.append(name);
		buff.append("\")");
		buff.append(LF);
		buff.append(String.format("public final class %s", className));
		if (StringUtils.isNotEmpty(interfaces)) {
			buff.append(String.format(" implements %s", interfaces));
		}
		buff.append(" {");
		buff.append(LF);
		buff.append(fields);
		buff.append(LF);
		for (AttrInfo attr : entity) {
			buff.append(makeGetter(attr));
			buff.append(makeSetter(attr));
//typeSet.add(attr.getType());
		}
		buff.append("}");
		buff.append(LF);
//for (String type : typeSet) System.out.println(type);
		return buff.toString();
	}

	private String makePackagePath(Schema schema) {
		StringBuilder buff = new StringBuilder();
		String nameSpace = schema.getNameSpace();
		String pkg = nameSpace.replace('.', File.separatorChar);

		buff.append(schema.getPath());
		buff.append(File.separator);
		buff.append(pkg);
		return buff.toString();
	}

	private String makeFilename(EntityInfo entity) {
		StringBuilder buff = new StringBuilder();
		String className = NameUtils.toPascal(entity.getName());

		buff.append(makePackagePath(entity.getSchema()));
		buff.append(File.separator);
		buff.append(className);
		buff.append(".java");
		return buff.toString();
	}

	public void create(Schema schema) throws IOException {
		File dir = new File(makePackagePath(schema));

		dir.mkdirs();
		for (EntityInfo entity : schema) {
			String content = make(entity);
			String fileName = makeFilename(entity);
			File file = new File(fileName);

			try (FileWriter out = new FileWriter(file)) {
				out.write(content);
			}
		}
	}
}
