package org.rsbot.loader.script;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class CodeReader {
	static interface Opcodes {
		int INSN = 1;
		int INT_INSN = 2;
		int VAR_INSN = 3;
		int TYPE_INSN = 4;
		int FIELD_INSN = 5;
		int METHOD_INSN = 6;
		int JUMP_INSN = 7;
		int LDC_INSN = 8;
		int IINC_INSN = 9;
		int TABLESWITCH_INSN = 10;
		int LOOKUPSWITCH_INSN = 11;
		int MULTIANEWARRAY_INSN = 12;
		int TRY_CATCH_BLOCK = 13;
		int LOCAL_VARIABLE = 14;
		int LABEL = 15;
	}

	private final Scanner code;

	public CodeReader(final byte[] code) {
		this.code = new Scanner(code);
	}

	public void accept(final MethodVisitor v) {
		int len = code.readShort();
		final Label[] labels = new Label[code.readByte()];
		for (int i = 0, l = labels.length; i < l; ++i) {
			labels[i] = new Label();
		}
		while (len-- > 0) {
			final Label dflt;
			final Label[] lbls;
			int n, ptr = 0;
			switch (code.readByte()) {
				case Opcodes.INSN:
					v.visitInsn(code.readByte());
					break;
				case Opcodes.INT_INSN:
					v.visitIntInsn(code.readByte(), code.readShort());
					break;
				case Opcodes.VAR_INSN:
					v.visitVarInsn(code.readByte(), code.readByte());
					break;
				case Opcodes.TYPE_INSN:
					v.visitTypeInsn(code.readByte(), code.readString());
					break;
				case Opcodes.FIELD_INSN:
					v.visitFieldInsn(code.readByte(), code.readString(), code.readString(), code.readString());
					break;
				case Opcodes.METHOD_INSN:
					v.visitMethodInsn(code.readByte(), code.readString(), code.readString(), code.readString());
					break;
				case Opcodes.JUMP_INSN:
					v.visitJumpInsn(code.readByte(), labels[code.readByte()]);
					break;
				case Opcodes.LDC_INSN:
					final int type = code.readByte();
					if (type == 1) {
						v.visitLdcInsn(code.readInt());
					} else if (type == 2) {
						v.visitLdcInsn(Float.parseFloat(code.readString()));
					} else if (type == 3) {
						v.visitLdcInsn(code.readLong());
					} else if (type == 4) {
						v.visitLdcInsn(Double.parseDouble(code.readString()));
					} else if (type == 5) {
						v.visitLdcInsn(code.readString());
					}
					break;
				case Opcodes.IINC_INSN:
					v.visitIincInsn(code.readByte(), code.readByte());
					break;
				case Opcodes.TABLESWITCH_INSN:
					final int min = code.readShort();
					final int max = code.readShort();
					dflt = labels[code.readByte()];
					n = code.readByte();
					lbls = new Label[n];
					while (ptr < n) {
						lbls[ptr++] = labels[code.readByte()];
					}
					v.visitTableSwitchInsn(min, max, dflt, lbls);
					break;
				case Opcodes.LOOKUPSWITCH_INSN:
					dflt = labels[code.readByte()];
					n = code.readByte();
					final int[] keys = new int[n];
					while (ptr < n) {
						keys[ptr++] = code.readShort();
					}
					n = code.readByte();
					ptr = 0;
					lbls = new Label[n];
					while (ptr < n) {
						lbls[ptr++] = labels[code.readByte()];
					}
					v.visitLookupSwitchInsn(dflt, keys, lbls);
					break;
				case Opcodes.MULTIANEWARRAY_INSN:
					v.visitMultiANewArrayInsn(code.readString(), code.readByte());
					break;
				case Opcodes.TRY_CATCH_BLOCK:
					v.visitTryCatchBlock(labels[code.readByte()], labels[code.readByte()], labels[code.readByte()], code.readString());
					break;
				case Opcodes.LOCAL_VARIABLE:
					v.visitLocalVariable(code.readString(), code.readString(), code.readString(), labels[code.readByte()], labels[code.readByte()], code.readByte());
					break;
				case Opcodes.LABEL:
					v.visitLabel(labels[code.readByte()]);
					break;
			}
		}
	}
}