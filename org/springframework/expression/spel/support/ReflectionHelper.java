/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.expression.spel.support;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

/**
 * Utility methods used by the reflection resolver code to discover the appropriate
 * methods/constructors and fields that should be used in expressions.
 *
 * <p>
 *  反射解析器代码使用的实用方法来发现应在表达式中使用的适当方法/构造函数和字段
 * 
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ReflectionHelper {

	/**
	 * Compare argument arrays and return information about whether they match.
	 * A supplied type converter and conversionAllowed flag allow for matches to take
	 * into account that a type may be transformed into a different type by the converter.
	 * <p>
	 * 比较参数数组并返回关于它们是否匹配的信息A提供的类型转换器和conversionAllowed标志允许匹配考虑到转换器可能将类型转换为不同类型
	 * 
	 * 
	 * @param expectedArgTypes the types the method/constructor is expecting
	 * @param suppliedArgTypes the types that are being supplied at the point of invocation
	 * @param typeConverter a registered type converter
	 * @return a MatchInfo object indicating what kind of match it was,
	 * or {@code null} if it was not a match
	 */
	static ArgumentsMatchInfo compareArguments(
			List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {

		Assert.isTrue(expectedArgTypes.size() == suppliedArgTypes.size(),
				"Expected argument types and supplied argument types should be arrays of same length");

		ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;
		for (int i = 0; i < expectedArgTypes.size() && match != null; i++) {
			TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
			TypeDescriptor expectedArg = expectedArgTypes.get(i);
			if (!expectedArg.equals(suppliedArg)) {
				// The user may supply null - and that will be ok unless a primitive is expected
				if (suppliedArg == null) {
					if (expectedArg.isPrimitive()) {
						match = null;
					}
				}
				else {
					if (suppliedArg.isAssignableTo(expectedArg)) {
						if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
							match = ArgumentsMatchKind.CLOSE;
						}
					}
					else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
						match = ArgumentsMatchKind.REQUIRES_CONVERSION;
					}
					else {
						match = null;
					}
				}
			}
		}
		return (match != null ? new ArgumentsMatchInfo(match) : null);
	}

	/**
	 * Based on {@link MethodInvoker#getTypeDifferenceWeight(Class[], Object[])} but operates on TypeDescriptors.
	 * <p>
	 *  基于{@link MethodInvoker#getTypeDifferenceWeight(Class [],Object [])},但在TypeDescriptors
	 * 
	 */
	public static int getTypeDifferenceWeight(List<TypeDescriptor> paramTypes, List<TypeDescriptor> argTypes) {
		int result = 0;
		for (int i = 0; i < paramTypes.size(); i++) {
			TypeDescriptor paramType = paramTypes.get(i);
			TypeDescriptor argType = (i < argTypes.size() ? argTypes.get(i) : null);
			if (argType == null) {
				if (paramType.isPrimitive()) {
					return Integer.MAX_VALUE;
				}
			}
			else {
				Class<?> paramTypeClazz = paramType.getType();
				if (!ClassUtils.isAssignable(paramTypeClazz, argType.getType())) {
					return Integer.MAX_VALUE;
				}
				if (paramTypeClazz.isPrimitive()) {
					paramTypeClazz = Object.class;
				}
				Class<?> superClass = argType.getType().getSuperclass();
				while (superClass != null) {
					if (paramTypeClazz.equals(superClass)) {
						result = result + 2;
						superClass = null;
					}
					else if (ClassUtils.isAssignable(paramTypeClazz, superClass)) {
						result = result + 2;
						superClass = superClass.getSuperclass();
					}
					else {
						superClass = null;
					}
				}
				if (paramTypeClazz.isInterface()) {
					result = result + 1;
				}
			}
		}
		return result;
	}

	/**
	 * Compare argument arrays and return information about whether they match.
	 * A supplied type converter and conversionAllowed flag allow for matches to
	 * take into account that a type may be transformed into a different type by the
	 * converter. This variant of compareArguments also allows for a varargs match.
	 * <p>
	 *  比较参数数组并返回关于它们是否匹配的信息A提供的类型转换器和conversionAllowed标志允许匹配考虑到转换器可能会将类型转换为不同类型compareArguments的这种变体还允许使用va
	 * rargs匹配。
	 * 
	 * 
	 * @param expectedArgTypes the types the method/constructor is expecting
	 * @param suppliedArgTypes the types that are being supplied at the point of invocation
	 * @param typeConverter a registered type converter
	 * @return a MatchInfo object indicating what kind of match it was,
	 * or {@code null} if it was not a match
	 */
	static ArgumentsMatchInfo compareArgumentsVarargs(
			List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {

		Assert.isTrue(expectedArgTypes != null && expectedArgTypes.size() > 0,
				"Expected arguments must at least include one array (the vargargs parameter)");
		Assert.isTrue(expectedArgTypes.get(expectedArgTypes.size() - 1).isArray(),
				"Final expected argument should be array type (the varargs parameter)");

		ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;

		// Check up until the varargs argument:

		// Deal with the arguments up to 'expected number' - 1 (that is everything but the varargs argument)
		int argCountUpToVarargs = expectedArgTypes.size() - 1;
		for (int i = 0; i < argCountUpToVarargs && match != null; i++) {
			TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
			TypeDescriptor expectedArg = expectedArgTypes.get(i);
			if (suppliedArg == null) {
				if (expectedArg.isPrimitive()) {
					match = null;
				}
			}
			else {
				if (!expectedArg.equals(suppliedArg)) {
					if (suppliedArg.isAssignableTo(expectedArg)) {
						if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
							match = ArgumentsMatchKind.CLOSE;
						}
					}
					else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
						match = ArgumentsMatchKind.REQUIRES_CONVERSION;
					}
					else {
						match = null;
					}
				}
			}
		}

		// If already confirmed it cannot be a match, then return
		if (match == null) {
			return null;
		}

		if (suppliedArgTypes.size() == expectedArgTypes.size() &&
				expectedArgTypes.get(expectedArgTypes.size() - 1).equals(
						suppliedArgTypes.get(suppliedArgTypes.size() - 1))) {
			// Special case: there is one parameter left and it is an array and it matches the varargs
			// expected argument - that is a match, the caller has already built the array. Proceed with it.
		}
		else {
			// Now... we have the final argument in the method we are checking as a match and we have 0
			// or more other arguments left to pass to it.
			TypeDescriptor varargsDesc = expectedArgTypes.get(expectedArgTypes.size() - 1);
			Class<?> varargsParamType = varargsDesc.getElementTypeDescriptor().getType();

			// All remaining parameters must be of this type or convertible to this type
			for (int i = expectedArgTypes.size() - 1; i < suppliedArgTypes.size(); i++) {
				TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
				if (suppliedArg == null) {
					if (varargsParamType.isPrimitive()) {
						match = null;
					}
				}
				else {
					if (varargsParamType != suppliedArg.getType()) {
						if (ClassUtils.isAssignable(varargsParamType, suppliedArg.getType())) {
							if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
								match = ArgumentsMatchKind.CLOSE;
							}
						}
						else if (typeConverter.canConvert(suppliedArg, TypeDescriptor.valueOf(varargsParamType))) {
							match = ArgumentsMatchKind.REQUIRES_CONVERSION;
						}
						else {
							match = null;
						}
					}
				}
			}
		}

		return (match != null ? new ArgumentsMatchInfo(match) : null);
	}


	// TODO could do with more refactoring around argument handling and varargs
	/**
	 * Convert a supplied set of arguments into the requested types. If the parameterTypes are related to
	 * a varargs method then the final entry in the parameterTypes array is going to be an array itself whose
	 * component type should be used as the conversion target for extraneous arguments. (For example, if the
	 * parameterTypes are {Integer, String[]} and the input arguments are {Integer, boolean, float} then both
	 * the boolean and float must be converted to strings). This method does *not* repackage the arguments
	 * into a form suitable for the varargs invocation - a subsequent call to setupArgumentsForVarargsInvocation handles that.
	 * <p>
	 * 将提供的一组参数转换为请求的类型如果parameterTypes与varargs方法相关,则parameterTypes数组中的最终条目将是一个数组本身,其组件类型应用作无关参数的转换目标(例如,如果p
	 * arameterTypes是{Integer,String []},输入参数是{Integer,boolean,float},则布尔值和浮点数都必须转换为字符串)这个方法不会*将*重新包装成适合vara
	 * rgs调用 - 随后调用setupArgumentsForVarargsInvocation来处理。
	 * 
	 * 
	 * @param converter the converter to use for type conversions
	 * @param arguments the arguments to convert to the requested parameter types
	 * @param method the target Method
	 * @return true if some kind of conversion occurred on the argument
	 * @throws SpelEvaluationException if there is a problem with conversion
	 */
	public static boolean convertAllArguments(TypeConverter converter, Object[] arguments, Method method)
			throws SpelEvaluationException {

		Integer varargsPosition = (method.isVarArgs() ? method.getParameterTypes().length - 1 : null);
		return convertArguments(converter, arguments, method, varargsPosition);
	}

	/**
	 * Takes an input set of argument values and converts them to the types specified as the
	 * required parameter types. The arguments are converted 'in-place' in the input array.
	 * <p>
	 *  获取输入参数值集,并将它们转换为指定为必需参数类型的类型参数在输入数组中转换为"in-place"
	 * 
	 * 
	 * @param converter the type converter to use for attempting conversions
	 * @param arguments the actual arguments that need conversion
	 * @param methodOrCtor the target Method or Constructor
	 * @param varargsPosition the known position of the varargs argument, if any
	 * ({@code null} if not varargs)
	 * @return {@code true} if some kind of conversion occurred on an argument
	 * @throws EvaluationException if a problem occurs during conversion
	 */
	static boolean convertArguments(TypeConverter converter, Object[] arguments, Object methodOrCtor,
			Integer varargsPosition) throws EvaluationException {

		boolean conversionOccurred = false;
		if (varargsPosition == null) {
			for (int i = 0; i < arguments.length; i++) {
				TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forMethodOrConstructor(methodOrCtor, i));
				Object argument = arguments[i];
				arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
				conversionOccurred |= (argument != arguments[i]);
			}
		}
		else {
			// Convert everything up to the varargs position
			for (int i = 0; i < varargsPosition; i++) {
				TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forMethodOrConstructor(methodOrCtor, i));
				Object argument = arguments[i];
				arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
				conversionOccurred |= (argument != arguments[i]);
			}
			MethodParameter methodParam = MethodParameter.forMethodOrConstructor(methodOrCtor, varargsPosition);
			if (varargsPosition == arguments.length - 1) {
				// If the target is varargs and there is just one more argument
				// then convert it here
				TypeDescriptor targetType = new TypeDescriptor(methodParam);
				Object argument = arguments[varargsPosition];
				TypeDescriptor sourceType = TypeDescriptor.forObject(argument);
				arguments[varargsPosition] = converter.convertValue(argument, sourceType, targetType);
				// Three outcomes of that previous line:
				// 1) the input argument was already compatible (ie. array of valid type) and nothing was done
				// 2) the input argument was correct type but not in an array so it was made into an array
				// 3) the input argument was the wrong type and got converted and put into an array
				if (argument != arguments[varargsPosition] &&
						!isFirstEntryInArray(argument, arguments[varargsPosition])) {
					conversionOccurred = true; // case 3
				}
			}
			else {
				// Convert remaining arguments to the varargs element type
				TypeDescriptor targetType = new TypeDescriptor(methodParam).getElementTypeDescriptor();
				for (int i = varargsPosition; i < arguments.length; i++) {
					Object argument = arguments[i];
					arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
					conversionOccurred |= (argument != arguments[i]);
				}
			}
		}
		return conversionOccurred;
	}

	/**
	 * Check if the supplied value is the first entry in the array represented by the possibleArray value.
	 * <p>
	 * 检查提供的值是否是可能的数组表示的数组中的第一个条目
	 * 
	 * 
	 * @param value the value to check for in the array
	 * @param possibleArray an array object that may have the supplied value as the first element
	 * @return true if the supplied value is the first entry in the array
	 */
	private static boolean isFirstEntryInArray(Object value, Object possibleArray) {
		if (possibleArray == null) {
			return false;
		}
		Class<?> type = possibleArray.getClass();
		if (!type.isArray() || Array.getLength(possibleArray) == 0 ||
				!ClassUtils.isAssignableValue(type.getComponentType(), value)) {
			return false;
		}
		Object arrayValue = Array.get(possibleArray, 0);
		return (type.getComponentType().isPrimitive() ? arrayValue.equals(value) : arrayValue == value);
	}

	/**
	 * Package up the arguments so that they correctly match what is expected in parameterTypes.
	 * For example, if parameterTypes is {@code (int, String[])} because the second parameter
	 * was declared {@code String...}, then if arguments is {@code [1,"a","b"]} then it must be
	 * repackaged as {@code [1,new String[]{"a","b"}]} in order to match the expected types.
	 * <p>
	 *  打包参数,使其正确匹配parameterTypes中的预期值例如,如果参数是{@code(int,String [])},因为第二个参数被声明为{@code String},那么如果参数是{@code [1,"a","b"]}
	 * 然后必须重新打包为{@code [1,new String [] {"a","b"}]},以匹配预期的类型。
	 * 
	 * 
	 * @param requiredParameterTypes the types of the parameters for the invocation
	 * @param args the arguments to be setup ready for the invocation
	 * @return a repackaged array of arguments where any varargs setup has been done
	 */
	public static Object[] setupArgumentsForVarargsInvocation(Class<?>[] requiredParameterTypes, Object... args) {
		// Check if array already built for final argument
		int parameterCount = requiredParameterTypes.length;
		int argumentCount = args.length;

		// Check if repackaging is needed...
		if (parameterCount != args.length ||
				requiredParameterTypes[parameterCount - 1] !=
						(args[argumentCount - 1] != null ? args[argumentCount - 1].getClass() : null)) {

			int arraySize = 0;  // zero size array if nothing to pass as the varargs parameter
			if (argumentCount >= parameterCount) {
				arraySize = argumentCount - (parameterCount - 1);
			}

			// Create an array for the varargs arguments
			Object[] newArgs = new Object[parameterCount];
			System.arraycopy(args, 0, newArgs, 0, newArgs.length - 1);

			// Now sort out the final argument, which is the varargs one. Before entering this method,
			// the arguments should have been converted to the box form of the required type.
			Class<?> componentType = requiredParameterTypes[parameterCount - 1].getComponentType();
			Object repackagedArgs = Array.newInstance(componentType, arraySize);
			for (int i = 0; i < arraySize; i++) {
				Array.set(repackagedArgs, i, args[parameterCount - 1 + i]);
			}
			newArgs[newArgs.length - 1] = repackagedArgs;
			return newArgs;
		}
		return args;
	}


	static enum ArgumentsMatchKind {

		/** An exact match is where the parameter types exactly match what the method/constructor is expecting */
		EXACT,

		/** A close match is where the parameter types either exactly match or are assignment-compatible */
		CLOSE,

		/** A conversion match is where the type converter must be used to transform some of the parameter types */
		REQUIRES_CONVERSION
	}


	/**
	 * An instance of ArgumentsMatchInfo describes what kind of match was achieved
	 * between two sets of arguments - the set that a method/constructor is expecting
	 * and the set that are being supplied at the point of invocation. If the kind
	 * indicates that conversion is required for some of the arguments then the arguments
	 * that require conversion are listed in the argsRequiringConversion array.
	 * <p>
	 * ArgumentsMatchInfo的一个实例描述了两组参数之间实现了什么样的匹配 - 方法/构造函数所期望的集合以及在调用时提供的集合如果该类型表示需要转换一些参数然后需要转换的参数列在argsReq
	 * uiringConversion数组中。
	 */
	static class ArgumentsMatchInfo {

		private final ArgumentsMatchKind kind;

		ArgumentsMatchInfo(ArgumentsMatchKind kind) {
			this.kind = kind;
		}

		public boolean isExactMatch() {
			return (this.kind == ArgumentsMatchKind.EXACT);
		}

		public boolean isCloseMatch() {
			return (this.kind == ArgumentsMatchKind.CLOSE);
		}

		public boolean isMatchRequiringConversion() {
			return (this.kind == ArgumentsMatchKind.REQUIRES_CONVERSION);
		}

		@Override
		public String toString() {
			return "ArgumentMatchInfo: " + this.kind;
		}
	}

}
