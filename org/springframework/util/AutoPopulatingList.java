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

package org.springframework.util;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Simple {@link List} wrapper class that allows for elements to be
 * automatically populated as they are requested. This is particularly
 * useful for data binding to {@link List Lists}, allowing for elements
 * to be created and added to the {@link List} in a "just in time" fashion.
 *
 * <p>Note: This class is not thread-safe. To create a thread-safe version,
 * use the {@link java.util.Collections#synchronizedList} utility methods.
 *
 * <p>Inspired by {@code LazyList} from Commons Collections.
 *
 * <p>
 * 简单的{@link List}包装器类,允许在请求时自动填充元素这对于绑定到{@link列表列表}的数据特别有用,允许创建元素并添加到{@link列表}在"及时"的时尚
 * 
 *  <p>注意：此类不是线程安全要创建线程安全版本,请使用{@link javautilCollections#synchronizedList}实用程序方法
 * 
 *  <p>灵感来自于Commons Collections的{@code LazyList}
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AutoPopulatingList<E> implements List<E>, Serializable {

	/**
	 * The {@link List} that all operations are eventually delegated to.
	 * <p>
	 *  所有操作最终被委派的{@link列表}
	 * 
	 */
	private final List<E> backingList;

	/**
	 * The {@link ElementFactory} to use to create new {@link List} elements
	 * on demand.
	 * <p>
	 *  用于按需创建新的{@link List}元素的{@link ElementFactory}
	 * 
	 */
	private final ElementFactory<E> elementFactory;


	/**
	 * Creates a new {@code AutoPopulatingList} that is backed by a standard
	 * {@link ArrayList} and adds new instances of the supplied {@link Class element Class}
	 * to the backing {@link List} on demand.
	 * <p>
	 * 创建一个由标准的{@link ArrayList}支持的新的{@code AutoPopulatingList},并根据需要将新提供的{@link Class元素Class}实例添加到后台{@link列表}
	 * 。
	 * 
	 */
	public AutoPopulatingList(Class<? extends E> elementClass) {
		this(new ArrayList<E>(), elementClass);
	}

	/**
	 * Creates a new {@code AutoPopulatingList} that is backed by the supplied {@link List}
	 * and adds new instances of the supplied {@link Class element Class} to the backing
	 * {@link List} on demand.
	 * <p>
	 *  创建一个新的{@code AutoPopulatingList},由所提供的{@link List}支持,并根据需要将新提供的{@link Class元素Class}实例添加到后台{@link List}
	 * 。
	 * 
	 */
	public AutoPopulatingList(List<E> backingList, Class<? extends E> elementClass) {
		this(backingList, new ReflectiveElementFactory<E>(elementClass));
	}

	/**
	 * Creates a new {@code AutoPopulatingList} that is backed by a standard
	 * {@link ArrayList} and creates new elements on demand using the supplied {@link ElementFactory}.
	 * <p>
	 *  创建一个新的{@code AutoPopulatingList},它由标准的{@link ArrayList}支持,并使用提供的{@link ElementFactory}创建新的元素,
	 * 
	 */
	public AutoPopulatingList(ElementFactory<E> elementFactory) {
		this(new ArrayList<E>(), elementFactory);
	}

	/**
	 * Creates a new {@code AutoPopulatingList} that is backed by the supplied {@link List}
	 * and creates new elements on demand using the supplied {@link ElementFactory}.
	 * <p>
	 *  创建一个新的{@code AutoPopulatingList},由所提供的{@link列表}支持,并使用提供的{@link ElementFactory}创建新的元素,
	 * 
	 */
	public AutoPopulatingList(List<E> backingList, ElementFactory<E> elementFactory) {
		Assert.notNull(backingList, "Backing List must not be null");
		Assert.notNull(elementFactory, "Element factory must not be null");
		this.backingList = backingList;
		this.elementFactory = elementFactory;
	}


	@Override
	public void add(int index, E element) {
		this.backingList.add(index, element);
	}

	@Override
	public boolean add(E o) {
		return this.backingList.add(o);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return this.backingList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return this.backingList.addAll(index, c);
	}

	@Override
	public void clear() {
		this.backingList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.backingList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.backingList.containsAll(c);
	}

	/**
	 * Get the element at the supplied index, creating it if there is
	 * no element at that index.
	 * <p>
	 * 在提供的索引处获取元素,如果该索引中没有元素,则创建它
	 * 
	 */
	@Override
	public E get(int index) {
		int backingListSize = this.backingList.size();
		E element = null;
		if (index < backingListSize) {
			element = this.backingList.get(index);
			if (element == null) {
				element = this.elementFactory.createElement(index);
				this.backingList.set(index, element);
			}
		}
		else {
			for (int x = backingListSize; x < index; x++) {
				this.backingList.add(null);
			}
			element = this.elementFactory.createElement(index);
			this.backingList.add(element);
		}
		return element;
	}

	@Override
	public int indexOf(Object o) {
		return this.backingList.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return this.backingList.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return this.backingList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.backingList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return this.backingList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return this.backingList.listIterator(index);
	}

	@Override
	public E remove(int index) {
		return this.backingList.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return this.backingList.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.backingList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.backingList.retainAll(c);
	}

	@Override
	public E set(int index, E element) {
		return this.backingList.set(index, element);
	}

	@Override
	public int size() {
		return this.backingList.size();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return this.backingList.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return this.backingList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.backingList.toArray(a);
	}


	@Override
	public boolean equals(Object other) {
		return this.backingList.equals(other);
	}

	@Override
	public int hashCode() {
		return this.backingList.hashCode();
	}


	/**
	 * Factory interface for creating elements for an index-based access
	 * data structure such as a {@link java.util.List}.
	 * <p>
	 *  用于为基于索引的访问数据结构创建元素的工厂界面,例如{@link javautilList}
	 * 
	 */
	public interface ElementFactory<E> {

		/**
		 * Create the element for the supplied index.
		 * <p>
		 *  创建提供的索引的元素
		 * 
		 * 
		 * @return the element object
		 * @throws ElementInstantiationException if the instantiation process failed
		 * (any exception thrown by a target constructor should be propagated as-is)
		 */
		E createElement(int index) throws ElementInstantiationException;
	}


	/**
	 * Exception to be thrown from ElementFactory.
	 * <p>
	 *  要从ElementFactory抛出异常
	 * 
	 */
	public static class ElementInstantiationException extends RuntimeException {

		public ElementInstantiationException(String msg) {
			super(msg);
		}

		public ElementInstantiationException(String message, Throwable cause) {
			super(message, cause);
		}
	}


	/**
	 * Reflective implementation of the ElementFactory interface,
	 * using {@code Class.newInstance()} on a given element class.
	 * <p>
	 *  在给定的元素类上使用{@code ClassnewInstance()}的ElementFactory接口的反射实现
	 */
	private static class ReflectiveElementFactory<E> implements ElementFactory<E>, Serializable {

		private final Class<? extends E> elementClass;

		public ReflectiveElementFactory(Class<? extends E> elementClass) {
			Assert.notNull(elementClass, "Element class must not be null");
			Assert.isTrue(!elementClass.isInterface(), "Element class must not be an interface type");
			Assert.isTrue(!Modifier.isAbstract(elementClass.getModifiers()), "Element class cannot be an abstract class");
			this.elementClass = elementClass;
		}

		@Override
		public E createElement(int index) {
			try {
				return this.elementClass.newInstance();
			}
			catch (InstantiationException ex) {
				throw new ElementInstantiationException(
						"Unable to instantiate element class: " + this.elementClass.getName(), ex);
			}
			catch (IllegalAccessException ex) {
				throw new ElementInstantiationException(
						"Could not access element constructor: " + this.elementClass.getName(), ex);
			}
		}
	}

}
