package org.rsbot.loader.script.adapter;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;

public class AddInterfaceAdapter extends ClassAdapter {
	private final String inter;

	public AddInterfaceAdapter(final ClassVisitor delegate, final String inter) {
		super(delegate);
		this.inter = inter;
	}

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		final String[] inters = new String[interfaces.length + 1];
		System.arraycopy(interfaces, 0, inters, 0, interfaces.length);
		inters[interfaces.length] = inter;
		cv.visit(version, access, name, signature, superName, inters);
	}
}
