package org.rsbot.loader.script.adapter;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddGetterAdapter extends ClassAdapter implements Opcodes {
	public static class Field {
		public int getter_access;
		public String getter_name;
		public String getter_desc;
		public String owner;
		public String name;
		public String desc;
	}

	private final boolean virtual;
	private final Field[] fields;

	private String owner;

	public AddGetterAdapter(final ClassVisitor delegate, final boolean virtual, final Field[] fields) {
		super(delegate);
		this.virtual = virtual;
		this.fields = fields;
	}

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		owner = name;
		cv.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public void visitEnd() {
		if (virtual) {
			for (final Field f : fields) {
				visitGetter(f.getter_access, f.getter_name, f.getter_desc, f.name, f.desc);
			}
		} else {
			for (final Field f : fields) {
				visitGetter(f.getter_access, f.getter_name, f.getter_desc, f.owner, f.name, f.desc);
			}
		}
		cv.visitEnd();
	}

	private void visitGetter(
			final int getter_access,
			final String getter_name,
			final String getter_desc,
			final String name,
			final String desc) {
		final MethodVisitor mv = cv.visitMethod(getter_access, getter_name, getter_desc, null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, name, desc);
		final int op = getReturnOpcode(desc);
		mv.visitInsn(op);
		mv.visitMaxs(op == LRETURN || op == DRETURN ? 2 : 1, (getter_access & ACC_STATIC) == 0 ? 1 : 0);
		mv.visitEnd();
	}

	private void visitGetter(
			final int getter_access,
			final String getter_name,
			final String getter_desc,
			final String owner,
			final String name,
			final String desc) {
		final MethodVisitor mv = cv.visitMethod(getter_access, getter_name, getter_desc, null, null);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC, owner, name, desc);
		final int op = getReturnOpcode(desc);
		mv.visitInsn(op);
		mv.visitMaxs(op == LRETURN || op == DRETURN ? 2 : 1, (getter_access & ACC_STATIC) == 0 ? 1 : 0);
		mv.visitEnd();
	}

	private int getReturnOpcode(String desc) {
		desc = desc.substring(desc.indexOf(")") + 1);
		if (desc.length() > 1) {
			return ARETURN;
		}
		final char c = desc.charAt(0);
		switch (c) {
			case 'I':
			case 'Z':
			case 'B':
			case 'S':
			case 'C':
				return IRETURN;
			case 'J':
				return LRETURN;
			case 'F':
				return FRETURN;
			case 'D':
				return DRETURN;
		}
		throw new RuntimeException("eek");
	}
}
