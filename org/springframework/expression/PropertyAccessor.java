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

package org.springframework.expression;


/**
 * A property accessor is able to read from (and possibly write to) an object's properties.
 * This interface places no restrictions, and so implementors are free to access properties
 * directly as fields or through getters or in any other way they see as appropriate.
 *
 * <p>A resolver can optionally specify an array of target classes for which it should be
 * called. However, if it returns {@code null} from {@link #getSpecificTargetClasses()},
 * it will be called for all property references and given a chance to determine if it
 * can read or write them.
 *
 * <p>Property resolvers are considered to be ordered and each will be called in turn.
 * The only rule that affects the call order is that any naming the target class directly
 * in {@link #getSpecificTargetClasses()} will be called first, before the general resolvers.
 *
 * <p>
 * 属性访问器能够从对象的属性(并且可能写入)读取对象的属性该接口没有限制,因此,实现者可以直接访问属性作为字段或通过getter或以任何其他方式他们看得到
 * 
 *  <p>解析器可以可选地指定一个目标类的数组,但是,如果从{@link #getSpecificTargetClasses()}返回{@code null}),将会调用所有属性引用,并给出确定是否可以读
 * 取或写入它们的机会。
 * 
 * <p>属性解析器被认为是有序的,每个都将被调用。影响调用顺序的唯一规则是在{@link #getSpecificTargetClasses()}之前直接命名目标类的任何命令都将首先被调用解析器
 * 
 * 
 * @author Andy Clement
 * @since 3.0
 */
public interface PropertyAccessor {

	/**
	 * Return an array of classes for which this resolver should be called.
	 * <p>>Returning {@code null} indicates this is a general resolver that
	 * can be called in an attempt to resolve a property on any type.
	 * <p>
	 *  返回一个类的数组,该解析器应该被调用<p >>返回{@code null}表示这是一个通用解析器,可以调用它来尝试解析任何类型的属性
	 * 
	 * 
	 * @return an array of classes that this resolver is suitable for
	 * (or {@code null} if a general resolver)
	 */
	Class<?>[] getSpecificTargetClasses();

	/**
	 * Called to determine if a resolver instance is able to access a specified property
	 * on a specified target object.
	 * <p>
	 *  调用以确定解析器实例是否能够访问指定的目标对象上的指定属性
	 * 
	 * 
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @param name the name of the property being accessed
	 * @return true if this resolver is able to read the property
	 * @throws AccessException if there is any problem determining whether the property can be read
	 */
	boolean canRead(EvaluationContext context, Object target, String name) throws AccessException;

	/**
	 * Called to read a property from a specified target object.
	 * Should only succeed if {@link #canRead} also returns {@code true}.
	 * <p>
	 *  调用从指定的目标对象读取属性如果{@link #canRead}也返回{@code true},则只能成功
	 * 
	 * 
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @param name the name of the property being accessed
	 * @return a TypedValue object wrapping the property value read and a type descriptor for it
	 * @throws AccessException if there is any problem accessing the property value
	 */
	TypedValue read(EvaluationContext context, Object target, String name) throws AccessException;

	/**
	 * Called to determine if a resolver instance is able to write to a specified
	 * property on a specified target object.
	 * <p>
	 * 调用以确定解析器实例是否能够写入指定的目标对象上的指定属性
	 * 
	 * 
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @param name the name of the property being accessed
	 * @return true if this resolver is able to write to the property
	 * @throws AccessException if there is any problem determining whether the
	 * property can be written to
	 */
	boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException;

	/**
	 * Called to write to a property on a specified target object.
	 * Should only succeed if {@link #canWrite} also returns {@code true}.
	 * <p>
	 *  调用写入指定目标对象上的属性如果{@link #canWrite}也返回{@code true},则只能成功
	 * 
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @param name the name of the property being accessed
	 * @param newValue the new value for the property
	 * @throws AccessException if there is any problem writing to the property value
	 */
	void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException;

}
