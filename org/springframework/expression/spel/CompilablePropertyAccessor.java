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

package org.springframework.expression.spel;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.PropertyAccessor;

/**
 * A compilable property accessor is able to generate bytecode that represents
 * the access operation, facilitating compilation to bytecode of expressions
 * that use the accessor.
 *
 * <p>
 *  可编译属性访问器能够生成代表访问操作的字节码,便于编译使用访问器的表达式的字节码
 * 
 * 
 * @author Andy Clement
 * @since 4.1
 */
public interface CompilablePropertyAccessor extends PropertyAccessor, Opcodes {

	/**
	 * Return {@code true} if this property accessor is currently suitable for compilation.
	 * <p>
	 * 如果此属性访问器当前适合于编译,则返回{@code true}
	 * 
	 */
	boolean isCompilable();

	/**
	 * Return the type of the accessed property - may only be known once an access has occurred.
	 * <p>
	 *  返回访问属性的类型 - 只有在访问发生后才可以知道
	 * 
	 */
	Class<?> getPropertyType();

	/**
	 * Generate the bytecode the performs the access operation into the specified MethodVisitor
	 * using context information from the codeflow where necessary.
	 * <p>
	 *  生成字节码,使用必要的代码流中的上下文信息,将访问操作执行到指定的MethodVisitor中
	 * 
	 * @param propertyName the name of the property
	 * @param mv the Asm method visitor into which code should be generated
	 * @param cf the current state of the expression compiler
	 */
	void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf);

}
