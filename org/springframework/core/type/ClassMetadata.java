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

package org.springframework.core.type;

/**
 * Interface that defines abstract metadata of a specific class,
 * in a form that does not require that class to be loaded yet.
 *
 * <p>
 *  界面定义了特定类的抽象元数据,不需要加载该类的表单
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see StandardClassMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getClassMetadata()
 * @see AnnotationMetadata
 */
public interface ClassMetadata {

	/**
	 * Return the name of the underlying class.
	 * <p>
	 *  返回底层类的名称
	 * 
	 */
	String getClassName();

	/**
	 * Return whether the underlying class represents an interface.
	 * <p>
	 * 返回底层类是否代表一个接口
	 * 
	 */
	boolean isInterface();

	/**
	 * Return whether the underlying class represents an annotation.
	 * <p>
	 *  返回基础类是否表示注释
	 * 
	 * 
	 * @since 4.1
	 */
	boolean isAnnotation();

	/**
	 * Return whether the underlying class is marked as abstract.
	 * <p>
	 *  返回底层类是否被标记为抽象
	 * 
	 */
	boolean isAbstract();

	/**
	 * Return whether the underlying class represents a concrete class,
	 * i.e. neither an interface nor an abstract class.
	 * <p>
	 *  返回底层是否代表一个具体的类,即既不是接口也不是抽象类
	 * 
	 */
	boolean isConcrete();

	/**
	 * Return whether the underlying class is marked as 'final'.
	 * <p>
	 *  返回底层类是否被标记为'final'
	 * 
	 */
	boolean isFinal();

	/**
	 * Determine whether the underlying class is independent,
	 * i.e. whether it is a top-level class or a nested class
	 * (static inner class) that can be constructed independent
	 * from an enclosing class.
	 * <p>
	 *  确定底层类是否是独立的,即是顶级类还是嵌套类(静态内类),可以独立于封闭类来构建
	 * 
	 */
	boolean isIndependent();

	/**
	 * Return whether the underlying class has an enclosing class
	 * (i.e. the underlying class is an inner/nested class or
	 * a local class within a method).
	 * <p>If this method returns {@code false}, then the
	 * underlying class is a top-level class.
	 * <p>
	 *  返回底层类是否包含封闭类(即底层类是方法内部/嵌套类或本地类)<p>如果此方法返回{@code false},则底层类是顶层类
	 * 
	 */
	boolean hasEnclosingClass();

	/**
	 * Return the name of the enclosing class of the underlying class,
	 * or {@code null} if the underlying class is a top-level class.
	 * <p>
	 * 如果基础类是顶级类,则返回底层类的封装类的名称,或{@code null}
	 * 
	 */
	String getEnclosingClassName();

	/**
	 * Return whether the underlying class has a super class.
	 * <p>
	 *  返回底层类是否有超类
	 * 
	 */
	boolean hasSuperClass();

	/**
	 * Return the name of the super class of the underlying class,
	 * or {@code null} if there is no super class defined.
	 * <p>
	 *  返回底层类的超级类的名称,如果没有定义超类,则返回{@code null}
	 * 
	 */
	String getSuperClassName();

	/**
	 * Return the names of all interfaces that the underlying class
	 * implements, or an empty array if there are none.
	 * <p>
	 *  返回基础类实现的所有接口的名称,如果没有,则返回一个空数组
	 * 
	 */
	String[] getInterfaceNames();

	/**
	 * Return the names of all classes declared as members of the class represented by
	 * this ClassMetadata object. This includes public, protected, default (package)
	 * access, and private classes and interfaces declared by the class, but excludes
	 * inherited classes and interfaces. An empty array is returned if no member classes
	 * or interfaces exist.
	 * <p>
	 *  返回被声明为由此ClassMetadata对象表示的类的成员的所有类的名称这包括public,protected,default(package)访问以及该类声明的私有类和接口,但不包括继承的类和接口
	 * 返回一个空数组如果没有成员类或接口存在。
	 * 
	 * @since 3.1
	 */
	String[] getMemberClassNames();

}
