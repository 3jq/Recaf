package me.coley.recaf.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A wrapper around {@link org.objectweb.asm.Type}.
 *
 * @author Matt Coley
 */
public class Types {
	public static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
	private static final Type[] PRIMITIVES = new Type[]{
			Type.VOID_TYPE,
			Type.BOOLEAN_TYPE,
			Type.BYTE_TYPE,
			Type.CHAR_TYPE,
			Type.SHORT_TYPE,
			Type.INT_TYPE,
			Type.FLOAT_TYPE,
			Type.DOUBLE_TYPE,
			Type.LONG_TYPE
	};

	/**
	 * @param desc
	 * 		Some internal type descriptor.
	 *
	 * @return {@code true} if it matches a reserved primitive type.
	 */
	public static boolean isPrimitive(String desc) {
		if (desc.length() != 1)
			return false;
		char c = desc.charAt(0);
		switch (c) {
			case 'V':
			case 'Z':
			case 'C':
			case 'B':
			case 'S':
			case 'I':
			case 'F':
			case 'J':
			case 'D':
				return true;
			default:
				return false;
		}
	}

	/**
	 * ASM likes to throw {@link IllegalArgumentException} in cases where it can't parse type descriptors.
	 * This lets us check beforehand if its valid.
	 *
	 * @param desc
	 * 		Descriptor to check.
	 *
	 * @return {@code true} when its parsable.
	 */
	@SuppressWarnings("all")
	public static boolean isValidDesc(String desc) {
		if (desc == null)
			return false;
		if (desc.length() == 0)
			return false;
		char first = desc.charAt(0);
		if (first == '(') {
			try {
				Type.getMethodType(desc);
				return true;
			} catch (Throwable t) {
				return false;
			}
		} else {
			try {
				Type type = Type.getType(desc);
				if (type.getSort() == Type.OBJECT && !desc.endsWith(";"))
					return false;
				return true;
			} catch (Throwable t) {
				return false;
			}
		}
	}

	/**
	 * @param opcode
	 * 		Some instruction opcode.
	 *
	 * @return The implied variable type, or {@code null} if the passed opcode does not imply a type.
	 */
	public static Type fromVarOpcode(int opcode) {
		switch (opcode) {
			case Opcodes.IINC:
			case Opcodes.ILOAD:
			case Opcodes.ISTORE:
				return Type.INT_TYPE;
			case Opcodes.ALOAD:
			case Opcodes.ASTORE:
				return Types.OBJECT_TYPE;
			case Opcodes.FLOAD:
			case Opcodes.FSTORE:
				return Type.FLOAT_TYPE;
			case Opcodes.DLOAD:
			case Opcodes.DSTORE:
				return Type.DOUBLE_TYPE;
			case Opcodes.LLOAD:
			case Opcodes.LSTORE:
				return Type.LONG_TYPE;
			default:
				return null;
		}
	}

	/**
	 * @param sort
	 * 		Some type sort.
	 *
	 * @return Normalized sort. This is in the context of runtime expectations.
	 * Any type smaller than {@code int} is treated as an {@code int}.
	 * Array types are essentially drop-in replaceable with object types in most cases.
	 */
	public static int getNormalizedSort(int sort) {
		if (sort == Type.ARRAY)
			sort = Type.OBJECT;
		else if (sort < Type.INT)
			sort = Type.INT;
		return sort;
	}

	/**
	 * @param sort
	 *        {@link Type#getSort()}.
	 *
	 * @return Name of sort.
	 */
	public static String getSortName(int sort) {
		switch (sort) {
			case Type.VOID:
				return "void";
			case Type.BOOLEAN:
				return "boolean";
			case Type.CHAR:
				return "char";
			case Type.BYTE:
				return "byte";
			case Type.SHORT:
				return "short";
			case Type.INT:
				return "int";
			case Type.FLOAT:
				return "float";
			case Type.LONG:
				return "long";
			case Type.DOUBLE:
				return "double";
			case Type.ARRAY:
				return "array";
			case Type.OBJECT:
				return "object";
			case Type.METHOD:
				return "method";
			case -1:
				return "<undefined>";
			default:
				return "<UNKNOWN>";
		}
	}

	/**
	 * @param desc
	 * 		Some class name.
	 *
	 * @return {@code true} if it matches the class name of a primitive type.
	 */
	public static boolean isPrimitiveClassName(String desc) {
		for (Type prim : PRIMITIVES)
			if (prim.getClassName().equals(desc))
				return true;
		return false;
	}

	/**
	 * @param desc
	 * 		Must be a primitive class name. See {@link #isPrimitiveClassName(String)}.
	 *
	 * @return Internal name of primitive.
	 */
	public static String classToPrimitive(String desc) {
		for (Type prim : PRIMITIVES)
			if (prim.getClassName().equals(desc))
				return prim.getInternalName();
		throw new IllegalArgumentException("Descriptor was not a primitive class name!");
	}
}
