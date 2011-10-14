package org.rsbot.loader.script.adapter;

import org.objectweb.asm.*;

public class SetSuperAdapter extends ClassAdapter {
	private String superName;
	private final String newSuperName;

	public SetSuperAdapter(final ClassVisitor delegate, final String superName) {
		super(delegate);
		newSuperName = superName;
	}

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		this.superName = superName;
		cv.visit(version, access, name, signature, newSuperName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String desc,
			final String signature,
			final String[] exceptions) {
		if (name.equals("<init>")) {
			return new MethodAdapter(cv.visitMethod(access, name, desc, signature, exceptions)) {
				@Override
				public void visitMethodInsn(final int opcode, String owner, final String name, final String desc) {
					if (opcode == Opcodes.INVOKESPECIAL && owner.equals(superName)) {
						owner = newSuperName;
					}
					mv.visitMethodInsn(opcode, owner, name, desc);
				}
			};
		}
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}
}
