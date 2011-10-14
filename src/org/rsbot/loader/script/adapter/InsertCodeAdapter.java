package org.rsbot.loader.script.adapter;

import org.objectweb.asm.*;
import org.rsbot.loader.script.CodeReader;

import java.util.Map;

public class InsertCodeAdapter extends ClassAdapter {
	private final String method_name;
	private final String method_desc;
	private final Map<Integer, byte[]> fragments;
	private final int max_locals;
	private final int max_stack;

	public InsertCodeAdapter(
			final ClassVisitor delegate,
			final String method_name,
			final String method_desc,
			final Map<Integer, byte[]> fragments,
			final int max_locals,
			final int max_stack) {
		super(delegate);
		this.method_name = method_name;
		this.method_desc = method_desc;
		this.fragments = fragments;
		this.max_locals = max_locals;
		this.max_stack = max_stack;
	}

	@Override
	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String desc,
			final String signature,
			final String[] exceptions) {
		final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals(method_name) && desc.equals(method_desc)) {
			return new MethodAdapter(mv, fragments, max_locals, max_stack);
		}
		return mv;
	}

	static class MethodAdapter implements MethodVisitor {
		private final MethodVisitor mv;
		private final Map<Integer, byte[]> fragments;
		private final int max_locals;
		private final int max_stack;

		private int idx = 0;

		MethodAdapter(
				final MethodVisitor delegate,
				final Map<Integer, byte[]> fragments,
				final int max_locals,
				final int max_stack) {
			mv = delegate;
			this.fragments = fragments;
			this.max_locals = max_locals;
			this.max_stack = max_stack;
		}

		public AnnotationVisitor visitAnnotationDefault() {
			return mv.visitAnnotationDefault();
		}

		public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
			return mv.visitAnnotation(desc, visible);
		}

		public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
			return mv.visitParameterAnnotation(parameter, desc, visible);
		}

		public void visitAttribute(final Attribute attr) {
			mv.visitAttribute(attr);
		}

		public void visitCode() {
			mv.visitCode();
		}

		public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {

		}

		public void visitInsn(final int opcode) {
			checkFragments();
			mv.visitInsn(opcode);
		}

		public void visitIntInsn(final int opcode, final int operand) {
			checkFragments();
			mv.visitIntInsn(opcode, operand);
		}

		public void visitVarInsn(final int opcode, final int var) {
			checkFragments();
			mv.visitVarInsn(opcode, var);
		}

		public void visitTypeInsn(final int opcode, final String type) {
			checkFragments();
			mv.visitTypeInsn(opcode, type);
		}

		public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
			checkFragments();
			mv.visitFieldInsn(opcode, owner, name, desc);
		}

		public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
			checkFragments();
			mv.visitMethodInsn(opcode, owner, name, desc);
		}

		public void visitInvokeDynamicInsn(String name, String desc, MethodHandle bsm, Object... bsmArgs) {
		}

		public void visitJumpInsn(final int opcode, final Label label) {
			checkFragments();
			mv.visitJumpInsn(opcode, label);
		}

		public void visitLabel(final Label label) {
			checkFragments();
			mv.visitLabel(label);
		}

		public void visitLdcInsn(final Object cst) {
			checkFragments();
			mv.visitLdcInsn(cst);
		}

		public void visitIincInsn(final int var, final int increment) {
			checkFragments();
			mv.visitIincInsn(var, increment);
		}

		public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
			checkFragments();
			mv.visitTableSwitchInsn(min, max, dflt, labels);
		}

		public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
			checkFragments();
			mv.visitLookupSwitchInsn(dflt, keys, labels);
		}

		public void visitMultiANewArrayInsn(final String desc, final int dims) {
			checkFragments();
			mv.visitMultiANewArrayInsn(desc, dims);
		}

		public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
			mv.visitTryCatchBlock(start, end, handler, type);
		}

		public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
			mv.visitLocalVariable(name, desc, signature, start, end, index);
		}

		public void visitLineNumber(final int line, final Label start) {
		}

		public void visitMaxs(final int maxStack, final int maxLocals) {
			if (max_stack == -1) {
				mv.visitMaxs(maxStack, maxLocals);
			} else {
				mv.visitMaxs(max_stack, max_locals);
			}
		}

		public void visitEnd() {
			mv.visitEnd();
		}

		private void checkFragments() {
			if (fragments.containsKey(++idx)) {
				new CodeReader(fragments.get(idx)).accept(mv);
			}
		}
	}
}
