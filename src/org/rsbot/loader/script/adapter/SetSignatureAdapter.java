package org.rsbot.loader.script.adapter;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class SetSignatureAdapter extends ClassAdapter {
	public static class Signature {
		public String name;
		public String desc;
		public int new_access;
		public String new_name;
		public String new_desc;
	}

	private final Signature[] signatures;

	public SetSignatureAdapter(final ClassVisitor delegate, final Signature[] signatures) {
		super(delegate);
		this.signatures = signatures;
	}

	@Override
	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String desc,
			final String signature,
			final String[] exceptions) {
		for (final Signature s : signatures) {
			if (s.name.equals(name) && (s.desc.equals("") || s.desc.equals(desc))) {
				return cv.visitMethod(
						s.new_access == -1 ? access : s.new_access,
						s.new_name,
						s.new_desc.equals("") ? desc : s.new_desc,
						signature,
						exceptions);
			}
		}
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}
}
