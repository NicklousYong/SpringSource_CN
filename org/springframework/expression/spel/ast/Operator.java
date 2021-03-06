/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

/**
 * Common supertype for operators that operate on either one or two operands.
 * In the case of multiply or divide there would be two operands, but for
 * unary plus or minus, there is only one.
 *
 * <p>
 *  在一个或两个操作数上操作的操作符的公共超类在乘法或除法的情况下,将有两个操作数,但对于一元加或减,只有一个
 * 
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @author Giovanni Dall'Oglio Risso
 * @since 3.0
 */
public abstract class Operator extends SpelNodeImpl {

	private final String operatorName;
	
	// The descriptors of the runtime operand values are used if the discovered declared
	// descriptors are not providing enough information (for example a generic type
	// whose accessors seem to only be returning 'Object' - the actual descriptors may
	// indicate 'int')

	protected String leftActualDescriptor;

	protected String rightActualDescriptor;


	public Operator(String payload,int pos,SpelNodeImpl... operands) {
		super(pos, operands);
		this.operatorName = payload;
	}


	public SpelNodeImpl getLeftOperand() {
		return this.children[0];
	}

	public SpelNodeImpl getRightOperand() {
		return this.children[1];
	}

	public final String getOperatorName() {
		return this.operatorName;
	}

	/**
	 * String format for all operators is the same '(' [operand] [operator] [operand] ')'
	 * <p>
	 * 所有运算符的字符串格式是相同的('[operand] [operator] [operand]')'
	 * 
	 */
	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder("(");
		sb.append(getChild(0).toStringAST());
		for (int i = 1; i < getChildCount(); i++) {
			sb.append(" ").append(getOperatorName()).append(" ");
			sb.append(getChild(i).toStringAST());
		}
		sb.append(")");
		return sb.toString();
	}

	protected boolean isCompilableOperatorUsingNumerics() {
		SpelNodeImpl left = getLeftOperand();
		SpelNodeImpl right= getRightOperand();
		if (!left.isCompilable() || !right.isCompilable()) {
			return false;
		}

		// Supported operand types for equals (at the moment)
		String leftDesc = left.exitTypeDescriptor;
		String rightDesc = right.exitTypeDescriptor;
		DescriptorComparison dc = DescriptorComparison.checkNumericCompatibility(leftDesc, rightDesc,
				this.leftActualDescriptor, this.rightActualDescriptor);
		return (dc.areNumbers && dc.areCompatible);
	}

	/** 
	 * Numeric comparison operators share very similar generated code, only differing in 
	 * two comparison instructions.
	 * <p>
	 *  数字比较运算符共享非常类似的生成代码,仅在两个比较指令中不同
	 * 
	 */
	protected void generateComparisonCode(MethodVisitor mv, CodeFlow cf, int compInstruction1, int compInstruction2) {
		String leftDesc = getLeftOperand().exitTypeDescriptor;
		String rightDesc = getRightOperand().exitTypeDescriptor;
		
		boolean unboxLeft = !CodeFlow.isPrimitive(leftDesc);
		boolean unboxRight = !CodeFlow.isPrimitive(rightDesc);
		DescriptorComparison dc = DescriptorComparison.checkNumericCompatibility(leftDesc, rightDesc,
				this.leftActualDescriptor, this.rightActualDescriptor);
		char targetType = dc.compatibleType;//CodeFlow.toPrimitiveTargetDesc(leftDesc);
		
		getLeftOperand().generateCode(mv, cf);
		if (unboxLeft) {
			CodeFlow.insertUnboxInsns(mv, targetType, leftDesc);
		}
	
		cf.enterCompilationScope();
		getRightOperand().generateCode(mv, cf);
		cf.exitCompilationScope();
		if (unboxRight) {
			CodeFlow.insertUnboxInsns(mv, targetType, rightDesc);
		}

		// assert: SpelCompiler.boxingCompatible(leftDesc, rightDesc)
		Label elseTarget = new Label();
		Label endOfIf = new Label();
		if (targetType=='D') {
			mv.visitInsn(DCMPG);
			mv.visitJumpInsn(compInstruction1, elseTarget);
		}
		else if (targetType=='F') {
			mv.visitInsn(FCMPG);		
			mv.visitJumpInsn(compInstruction1, elseTarget);
		}
		else if (targetType=='J') {
			mv.visitInsn(LCMP);		
			mv.visitJumpInsn(compInstruction1, elseTarget);
		}
		else if (targetType=='I') {
			mv.visitJumpInsn(compInstruction2, elseTarget);
		}
		else {
			throw new IllegalStateException("Unexpected descriptor "+leftDesc);
		}

		// Other numbers are not yet supported (isCompilable will not have returned true)
		mv.visitInsn(ICONST_1);
		mv.visitJumpInsn(GOTO,endOfIf);
		mv.visitLabel(elseTarget);
		mv.visitInsn(ICONST_0);
		mv.visitLabel(endOfIf);
		cf.pushDescriptor("Z");
	}

	protected boolean equalityCheck(ExpressionState state, Object left, Object right) {
		if (left instanceof Number && right instanceof Number) {
			Number leftNumber = (Number) left;
			Number rightNumber = (Number) right;

			if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
				BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
				BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
				return (leftBigDecimal == null ? rightBigDecimal == null : leftBigDecimal.compareTo(rightBigDecimal) == 0);
			}
			else if (leftNumber instanceof Double || rightNumber instanceof Double) {
				return (leftNumber.doubleValue() == rightNumber.doubleValue());
			}
			else if (leftNumber instanceof Float || rightNumber instanceof Float) {
				return (leftNumber.floatValue() == rightNumber.floatValue());
			}
			else if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
				BigInteger leftBigInteger = NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
				BigInteger rightBigInteger = NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
				return (leftBigInteger == null ? rightBigInteger == null : leftBigInteger.compareTo(rightBigInteger) == 0);
			}
			else if (leftNumber instanceof Long || rightNumber instanceof Long) {
				return (leftNumber.longValue() == rightNumber.longValue());
			}
			else if (leftNumber instanceof Integer || rightNumber instanceof Integer) {
				return (leftNumber.intValue() == rightNumber.intValue());
			}
			else if (leftNumber instanceof Short || rightNumber instanceof Short) {
				return (leftNumber.shortValue() == rightNumber.shortValue());
			}
			else if (leftNumber instanceof Byte || rightNumber instanceof Byte) {
				return (leftNumber.byteValue() == rightNumber.byteValue());
			}
			else {
				// Unknown Number subtypes -> best guess is double comparison
				return (leftNumber.doubleValue() == rightNumber.doubleValue());
			}
		}

		if (left instanceof CharSequence && right instanceof CharSequence) {
			return left.toString().equals(right.toString());
		}

		if (ObjectUtils.nullSafeEquals(left, right)) {
			return true;
		}

		if (left instanceof Comparable && right instanceof Comparable) {
			Class<?> ancestor = ClassUtils.determineCommonAncestor(left.getClass(), right.getClass());
			if (ancestor != null && Comparable.class.isAssignableFrom(ancestor)) {
				return (state.getTypeComparator().compare(left, right) == 0);
			}
		}

		return false;
	}
	

	/**
	 * A descriptor comparison encapsulates the result of comparing descriptor for two operands and
	 * describes at what level they are compatible.
	 * <p>
	 *  描述符比较封装比较两个操作数的描述符的结果,并描述它们在什么级别上兼容
	 * 
	 */
	protected static class DescriptorComparison {

		static DescriptorComparison NOT_NUMBERS = new DescriptorComparison(false, false, ' ');

		static DescriptorComparison INCOMPATIBLE_NUMBERS = new DescriptorComparison(true, false, ' ');

		final boolean areNumbers; // Were the two compared descriptor both for numbers?

		final boolean areCompatible; // If they were numbers, were they compatible?

		final char compatibleType; // When compatible, what is the descriptor of the common type
		
		private DescriptorComparison(boolean areNumbers, boolean areCompatible, char compatibleType) {
			this.areNumbers = areNumbers;
			this.areCompatible = areCompatible;
			this.compatibleType = compatibleType;
		}
		
		/**
		 * Returns an object that indicates whether the input descriptors are compatible. A declared descriptor
		 * is what could statically be determined (e.g. from looking at the return value of a property accessor
		 * method) whilst an actual descriptor is the type of an actual object that was returned, which may differ.
		 * For generic types with unbound type variables the declared descriptor discovered may be 'Object' but
		 * from the actual descriptor it is possible to observe that the objects are really numeric values (e.g.
		 * ints).
		 * <p>
		 * 返回指示输入描述符是否兼容的对象。
		 * 声明的描述符是静态确定的(例如从查看属性访问器方法的返回值),而实际描述符是返回的实际对象的类型,可能不同对于具有未绑定类型变量的通用类型,发现的声明的描述符可能是"Object",但是从实际描述符可以
		 * 观察到对象是真正的数值(例如int)。
		 * 
		 * @param leftDeclaredDescriptor the statically determinable left descriptor
		 * @param rightDeclaredDescriptor the statically determinable right descriptor
		 * @param leftActualDescriptor the dynamic/runtime left object descriptor
		 * @param rightActualDescriptor the dynamic/runtime right object descriptor
		 * @return a DescriptorComparison object indicating the type of compatibility, if any
		 */
		public static DescriptorComparison checkNumericCompatibility(String leftDeclaredDescriptor,
				String rightDeclaredDescriptor, String leftActualDescriptor, String rightActualDescriptor) {

			String ld = leftDeclaredDescriptor;
			String rd = rightDeclaredDescriptor;

			boolean leftNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(ld);
			boolean rightNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(rd);
			
			// If the declared descriptors aren't providing the information, try the actual descriptors
			if (!leftNumeric && !ld.equals(leftActualDescriptor)) {
				ld = leftActualDescriptor;
				leftNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(ld);
			}
			if (!rightNumeric && !rd.equals(rightActualDescriptor)) {
				rd = rightActualDescriptor;
				rightNumeric = CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(rd);
			}
			
			if (leftNumeric && rightNumeric) {
				if (CodeFlow.areBoxingCompatible(ld, rd)) {
					return new DescriptorComparison(true, true, CodeFlow.toPrimitiveTargetDesc(ld));
				}
				else {
					return DescriptorComparison.INCOMPATIBLE_NUMBERS;
				}
			}
			else {
				return DescriptorComparison.NOT_NUMBERS;
			}		
		}
	}

}
