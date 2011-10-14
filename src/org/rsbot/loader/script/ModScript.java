package org.rsbot.loader.script;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.rsbot.loader.script.adapter.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ModScript {
	public static interface Opcodes {
		int ATTRIBUTE = 1;
		int GET_STATIC = 2;
		int GET_FIELD = 3;
		int ADD_FIELD = 4;
		int ADD_METHOD = 5;
		int ADD_INTERFACE = 6;
		int SET_SUPER = 7;
		int SET_SIGNATURE = 8;
		int INSERT_CODE = 9;
		int OVERRIDE_CLASS = 10;
	}

	private static final int MAGIC = 0xFADFAD;

	private String name;
	private int version;
	private Map<String, String> attributes;
	private Map<String, ClassAdapter> adapters;
	private Map<String, ClassWriter> writers;

	public ModScript(final byte[] data) throws ParseException {
		this(new ByteArrayInputStream(data));
	}

	public ModScript(final InputStream in) throws ParseException {
		load(new Scanner(in));
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public String getAttribute(final String key) {
		return attributes.get(key);
	}

	public byte[] process(final String key, final byte[] data) {
		final ClassAdapter adapter = adapters.get(key);
		if (adapter != null) {
			final ClassReader reader = new ClassReader(data);
			reader.accept(adapter, ClassReader.SKIP_FRAMES);
			return writers.get(key).toByteArray();
		}
		return data;
	}

	public byte[] process(final String key, final InputStream is) throws IOException {
		final ClassAdapter adapter = adapters.get(key);
		if (adapter != null) {
			final ClassReader reader = new ClassReader(is);
			reader.accept(adapter, ClassReader.SKIP_FRAMES);
			return writers.get(key).toByteArray();
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final byte[] buffer = new byte[4096];
		int n;
		while ((n = is.read(buffer)) != -1) {
			os.write(buffer, 0, n);
		}
		return os.toByteArray();
	}

	private void load(final Scanner scan) throws ParseException {
		if (scan.readInt() != ModScript.MAGIC) {
			throw new ParseException("invalid patch format");
		}
		attributes = new HashMap<String, String>();
		adapters = new HashMap<String, ClassAdapter>();
		writers = new HashMap<String, ClassWriter>();
		name = scan.readString();
		version = scan.readShort();
		int num = scan.readShort();
		while (num-- > 0) {
			final String clazz;
			int count = 0, ptr = 0;
			final int op = scan.readByte();
			switch (op) {
				case Opcodes.ATTRIBUTE:
					final String key = scan.readString();
					final String value = scan.readString();
					attributes.put(key, new StringBuilder(value).reverse().toString());
					break;
				case Opcodes.GET_STATIC:
				case Opcodes.GET_FIELD:
					clazz = scan.readString();
					count = scan.readShort();
					final AddGetterAdapter.Field[] fieldsGet = new AddGetterAdapter.Field[count];
					while (ptr < count) {
						final AddGetterAdapter.Field f = new AddGetterAdapter.Field();
						f.getter_access = scan.readInt();
						f.getter_name = scan.readString();
						f.getter_desc = scan.readString();
						f.owner = scan.readString();
						f.name = scan.readString();
						f.desc = scan.readString();
						fieldsGet[ptr++] = f;
					}
					adapters.put(clazz, new AddGetterAdapter(delegate(clazz), op == Opcodes.GET_FIELD, fieldsGet));
					break;
				case Opcodes.ADD_FIELD:
					clazz = scan.readString();
					count = scan.readShort();
					final AddFieldAdapter.Field[] fieldsAdd = new AddFieldAdapter.Field[count];
					while (ptr < count) {
						final AddFieldAdapter.Field f = new AddFieldAdapter.Field();
						f.access = scan.readInt();
						f.name = scan.readString();
						f.desc = scan.readString();
						fieldsAdd[ptr++] = f;
					}
					adapters.put(clazz, new AddFieldAdapter(delegate(clazz), fieldsAdd));
					break;
				case Opcodes.ADD_METHOD:
					clazz = scan.readString();
					count = scan.readShort();
					final AddMethodAdapter.Method[] methods = new AddMethodAdapter.Method[count];
					while (ptr < count) {
						final AddMethodAdapter.Method m = new AddMethodAdapter.Method();
						m.access = scan.readInt();
						m.name = scan.readString();
						m.desc = scan.readString();
						final byte[] code = new byte[scan.readInt()];
						scan.readSegment(code, code.length, 0);
						m.code = code;
						m.max_locals = scan.readByte();
						m.max_stack = scan.readByte();
						methods[ptr++] = m;
					}
					adapters.put(clazz, new AddMethodAdapter(delegate(clazz), methods));
					break;
				case Opcodes.ADD_INTERFACE:
					clazz = scan.readString();
					final String inter = scan.readString();
					adapters.put(clazz, new AddInterfaceAdapter(delegate(clazz), inter));
					break;
				case Opcodes.SET_SUPER:
					clazz = scan.readString();
					final String superName = scan.readString();
					adapters.put(clazz, new SetSuperAdapter(delegate(clazz), superName));
					break;
				case Opcodes.SET_SIGNATURE:
					clazz = scan.readString();
					count = scan.readShort();
					final SetSignatureAdapter.Signature[] signatures = new SetSignatureAdapter.Signature[count];
					while (ptr < count) {
						final SetSignatureAdapter.Signature s = new SetSignatureAdapter.Signature();
						s.name = scan.readString();
						s.desc = scan.readString();
						s.new_access = scan.readInt();
						s.new_name = scan.readString();
						s.new_desc = scan.readString();
						signatures[ptr++] = s;
					}
					adapters.put(clazz, new SetSignatureAdapter(delegate(clazz), signatures));
					break;
				case Opcodes.INSERT_CODE:
					clazz = scan.readString();
					final String name = scan.readString();
					final String desc = scan.readString();
					count = scan.readByte();
					final Map<Integer, byte[]> fragments = new HashMap<Integer, byte[]>();
					while (count-- > 0) {
						final int off = scan.readShort();
						final byte[] code = new byte[scan.readInt()];
						scan.readSegment(code, code.length, 0);
						fragments.put(off, code);
					}
					adapters.put(clazz, new InsertCodeAdapter(delegate(clazz),
							name, desc, fragments, scan.readByte(), scan.readByte()));
					break;
				case Opcodes.OVERRIDE_CLASS:
					final String old_clazz = scan.readString();
					final String new_clazz = scan.readString();
					count = scan.readByte();
					while (count-- > 0) {
						final String current_clazz = scan.readString();
						adapters.put(current_clazz, new OverrideClassAdapter(delegate(current_clazz), old_clazz, new_clazz));
					}
					break;
			}
		}
	}

	private ClassVisitor delegate(final String clazz) {
		final ClassAdapter delegate = adapters.get(clazz);
		if (delegate == null) {
			final ClassWriter writer = new ClassWriter(0);
			writers.put(clazz, writer);
			return writer;
		} else {
			return delegate;
		}
	}
}