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

package org.springframework.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Factory for collections that is aware of Java 5, Java 6, and Spring
 * collection types.
 * <p>Mainly for internal use within the framework.
 * <p>The goal of this class is to avoid runtime dependencies on a specific
 * Java version, while nevertheless using the best collection implementation
 * that is available at runtime.
 *
 * <p>
 * 意识到Java 5,Java 6和Spring集合类型的集合的工厂<p>主要用于框架内部使用<p>此类的目标是避免对特定Java版本的运行时依赖,同时使用运行时可用的最佳集合实现
 * 
 * 
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Oliver Gierke
 * @author Sam Brannen
 * @since 1.1.1
 */
public abstract class CollectionFactory {

	private static final Set<Class<?>> approximableCollectionTypes = new HashSet<Class<?>>(11);

	private static final Set<Class<?>> approximableMapTypes = new HashSet<Class<?>>(7);


	static {
		// Standard collection interfaces
		approximableCollectionTypes.add(Collection.class);
		approximableCollectionTypes.add(List.class);
		approximableCollectionTypes.add(Set.class);
		approximableCollectionTypes.add(SortedSet.class);
		approximableCollectionTypes.add(NavigableSet.class);
		approximableMapTypes.add(Map.class);
		approximableMapTypes.add(SortedMap.class);
		approximableMapTypes.add(NavigableMap.class);

		// Common concrete collection classes
		approximableCollectionTypes.add(ArrayList.class);
		approximableCollectionTypes.add(LinkedList.class);
		approximableCollectionTypes.add(HashSet.class);
		approximableCollectionTypes.add(LinkedHashSet.class);
		approximableCollectionTypes.add(TreeSet.class);
		approximableCollectionTypes.add(EnumSet.class);
		approximableMapTypes.add(HashMap.class);
		approximableMapTypes.add(LinkedHashMap.class);
		approximableMapTypes.add(TreeMap.class);
		approximableMapTypes.add(EnumMap.class);
	}


	/**
	 * Determine whether the given collection type is an <em>approximable</em> type,
	 * i.e. a type that {@link #createApproximateCollection} can approximate.
	 * <p>
	 *  确定给定的集合类型是否是可接近的</em>类型,即{@link #createApproximateCollection}可以近似的类型
	 * 
	 * 
	 * @param collectionType the collection type to check
	 * @return {@code true} if the type is <em>approximable</em>
	 */
	public static boolean isApproximableCollectionType(Class<?> collectionType) {
		return (collectionType != null && approximableCollectionTypes.contains(collectionType));
	}

	/**
	 * Create the most approximate collection for the given collection.
	 * <p><strong>Warning</strong>: Since the parameterized type {@code E} is
	 * not bound to the type of elements contained in the supplied
	 * {@code collection}, type safety cannot be guaranteed if the supplied
	 * {@code collection} is an {@link EnumSet}. In such scenarios, the caller
	 * is responsible for ensuring that the element type for the supplied
	 * {@code collection} is an enum type matching type {@code E}. As an
	 * alternative, the caller may wish to treat the return value as a raw
	 * collection or collection of {@link Object}.
	 * <p>
	 * 为给定集合创建最接近的集合<p> <strong>警告</strong>：由于参数化类型{@code E}未绑定到提供的{@code集合}中包含的元素类型,请键入安全性如果提供的{@code collection}
	 * 是{@link EnumSet},则无法保证。
	 * 在这种情况下,调用方负责确保提供的{@code collection}的元素类型是枚举类型匹配类型{@code E }作为替代方法,调用者可能希望将返回值视为{@link Object}的原始集合或集合
	 * ,。
	 * 
	 * 
	 * @param collection the original collection object, potentially {@code null}
	 * @param capacity the initial capacity
	 * @return a new, empty collection instance
	 * @see #isApproximableCollectionType
	 * @see java.util.LinkedList
	 * @see java.util.ArrayList
	 * @see java.util.EnumSet
	 * @see java.util.TreeSet
	 * @see java.util.LinkedHashSet
	 */
	@SuppressWarnings({ "unchecked", "cast", "rawtypes" })
	public static <E> Collection<E> createApproximateCollection(Object collection, int capacity) {
		if (collection instanceof LinkedList) {
			return new LinkedList<E>();
		}
		else if (collection instanceof List) {
			return new ArrayList<E>(capacity);
		}
		else if (collection instanceof EnumSet) {
			// Cast is necessary for compilation in Eclipse 4.4.1.
			Collection<E> enumSet = (Collection<E>) EnumSet.copyOf((EnumSet) collection);
			enumSet.clear();
			return enumSet;
		}
		else if (collection instanceof SortedSet) {
			return new TreeSet<E>(((SortedSet<E>) collection).comparator());
		}
		else {
			return new LinkedHashSet<E>(capacity);
		}
	}

	/**
	 * Create the most appropriate collection for the given collection type.
	 * <p>Delegates to {@link #createCollection(Class, Class, int)} with a
	 * {@code null} element type.
	 * <p>
	 *  为给定的集合类型创建最合适的集合<p>使用{@code null}元素类型的{@link #createCollection(Class,Class,int)}的代理
	 * 
	 * 
	 * @param collectionType the desired type of the target collection; never {@code null}
	 * @param capacity the initial capacity
	 * @return a new collection instance
	 * @throws IllegalArgumentException if the supplied {@code collectionType}
	 * is {@code null} or of type {@link EnumSet}
	 */
	public static <E> Collection<E> createCollection(Class<?> collectionType, int capacity) {
		return createCollection(collectionType, null, capacity);
	}

	/**
	 * Create the most appropriate collection for the given collection type.
	 * <p><strong>Warning</strong>: Since the parameterized type {@code E} is
	 * not bound to the supplied {@code elementType}, type safety cannot be
	 * guaranteed if the desired {@code collectionType} is {@link EnumSet}.
	 * In such scenarios, the caller is responsible for ensuring that the
	 * supplied {@code elementType} is an enum type matching type {@code E}.
	 * As an alternative, the caller may wish to treat the return value as a
	 * raw collection or collection of {@link Object}.
	 * <p>
	 * 为给定的集合类型创建最合适的集合<p> <strong>警告</strong>：由于参数化类型{@code E}未绑定到提供的{@code elementType},所以无法保证类型的安全性所需的{@code collectionType}
	 * 是{@link EnumSet}在这种情况下,调用者负责确保提供的{@code elementType}是枚举类型匹配类型{@code E}。
	 * 作为替代,呼叫者可能希望将返回值视为{@link Object}的原始集合或集合。
	 * 
	 * 
	 * @param collectionType the desired type of the target collection; never {@code null}
	 * @param elementType the collection's element type, or {@code null} if unknown
	 * (note: only relevant for {@link EnumSet} creation)
	 * @param capacity the initial capacity
	 * @return a new collection instance
	 * @since 4.1.3
	 * @see java.util.LinkedHashSet
	 * @see java.util.ArrayList
	 * @see java.util.TreeSet
	 * @see java.util.EnumSet
	 * @throws IllegalArgumentException if the supplied {@code collectionType} is
	 * {@code null}; or if the desired {@code collectionType} is {@link EnumSet} and
	 * the supplied {@code elementType} is not a subtype of {@link Enum}
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public static <E> Collection<E> createCollection(Class<?> collectionType, Class<?> elementType, int capacity) {
		Assert.notNull(collectionType, "Collection type must not be null");
		if (collectionType.isInterface()) {
			if (Set.class == collectionType || Collection.class == collectionType) {
				return new LinkedHashSet<E>(capacity);
			}
			else if (List.class == collectionType) {
				return new ArrayList<E>(capacity);
			}
			else if (SortedSet.class == collectionType || NavigableSet.class == collectionType) {
				return new TreeSet<E>();
			}
			else {
				throw new IllegalArgumentException("Unsupported Collection interface: " + collectionType.getName());
			}
		}
		else if (EnumSet.class == collectionType) {
			Assert.notNull(elementType, "Cannot create EnumSet for unknown element type");
			// Cast is necessary for compilation in Eclipse 4.4.1.
			return (Collection<E>) EnumSet.noneOf(asEnumType(elementType));
		}
		else {
			if (!Collection.class.isAssignableFrom(collectionType)) {
				throw new IllegalArgumentException("Unsupported Collection type: " + collectionType.getName());
			}
			try {
				return (Collection<E>) collectionType.newInstance();
			}
			catch (Throwable ex) {
				throw new IllegalArgumentException(
					"Could not instantiate Collection type: " + collectionType.getName(), ex);
			}
		}
	}

	/**
	 * Determine whether the given map type is an <em>approximable</em> type,
	 * i.e. a type that {@link #createApproximateMap} can approximate.
	 * <p>
	 *  确定给定的地图类型是否是可接近的</em>类型,即{@link #createApproximateMap}可以近似的类型
	 * 
	 * 
	 * @param mapType the map type to check
	 * @return {@code true} if the type is <em>approximable</em>
	 */
	public static boolean isApproximableMapType(Class<?> mapType) {
		return (mapType != null && approximableMapTypes.contains(mapType));
	}

	/**
	 * Create the most approximate map for the given map.
	 * <p><strong>Warning</strong>: Since the parameterized type {@code K} is
	 * not bound to the type of keys contained in the supplied {@code map},
	 * type safety cannot be guaranteed if the supplied {@code map} is an
	 * {@link EnumMap}. In such scenarios, the caller is responsible for
	 * ensuring that the key type in the supplied {@code map} is an enum type
	 * matching type {@code K}. As an alternative, the caller may wish to
	 * treat the return value as a raw map or map keyed by {@link Object}.
	 * <p>
	 * 为给定地图创建最为近似的地图<p> <strong>警告</strong>：由于参数化类型{@code K}未绑定到提供的{@code map}中包含的键类型,请键入安全如果提供的{@code map}
	 * 是{@link EnumMap},则无法保证。
	 * 在这种情况下,调用方负责确保提供的{@code map}中的密钥类型是枚举类型匹配类型{@code K }作为替代,呼叫者可能希望将返回值视为由{@link Object}键入的原始地图或地图。
	 * 
	 * 
	 * @param map the original map object, potentially {@code null}
	 * @param capacity the initial capacity
	 * @return a new, empty map instance
	 * @see #isApproximableMapType
	 * @see java.util.EnumMap
	 * @see java.util.TreeMap
	 * @see java.util.LinkedHashMap
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <K, V> Map<K, V> createApproximateMap(Object map, int capacity) {
		if (map instanceof EnumMap) {
			EnumMap enumMap = new EnumMap((EnumMap) map);
			enumMap.clear();
			return enumMap;
		}
		else if (map instanceof SortedMap) {
			return new TreeMap<K, V>(((SortedMap<K, V>) map).comparator());
		}
		else {
			return new LinkedHashMap<K, V>(capacity);
		}
	}

	/**
	 * Create the most appropriate map for the given map type.
	 * <p>Delegates to {@link #createMap(Class, Class, int)} with a
	 * {@code null} key type.
	 * <p>
	 *  使用{@code null}键类型为给定的地图类型创建最合适的地图<p>代表{@link #createMap(Class,Class,int)}
	 * 
	 * 
	 * @param mapType the desired type of the target map
	 * @param capacity the initial capacity
	 * @return a new map instance
	 * @throws IllegalArgumentException if the supplied {@code mapType} is
	 * {@code null} or of type {@link EnumMap}
	 */
	public static <K, V> Map<K, V> createMap(Class<?> mapType, int capacity) {
		return createMap(mapType, null, capacity);
	}

	/**
	 * Create the most appropriate map for the given map type.
	 * <p><strong>Warning</strong>: Since the parameterized type {@code K}
	 * is not bound to the supplied {@code keyType}, type safety cannot be
	 * guaranteed if the desired {@code mapType} is {@link EnumMap}. In such
	 * scenarios, the caller is responsible for ensuring that the {@code keyType}
	 * is an enum type matching type {@code K}. As an alternative, the caller
	 * may wish to treat the return value as a raw map or map keyed by
	 * {@link Object}. Similarly, type safety cannot be enforced if the
	 * desired {@code mapType} is {@link MultiValueMap}.
	 * <p>
	 * 为给定的地图类型创建最合适的地图<p> <strong>警告</strong>：由于参数化类型{@code K}未绑定到提供的{@code keyType},所以不能保证如果期望的{@code mapType}
	 * 是{@link EnumMap}在这种情况下,呼叫者负责确保{@code keyType}是枚举类型匹配类型{@code K}。
	 * 作为替代,呼叫者可能希望对待返回值作为由{@link Object}键入的原始地图或地图类似地,如果所需的{@code mapType}为{@link MultiValueMap},则不能强制输入安全性
	 * 。
	 * 
	 * @param mapType the desired type of the target map; never {@code null}
	 * @param keyType the map's key type, or {@code null} if unknown
	 * (note: only relevant for {@link EnumMap} creation)
	 * @param capacity the initial capacity
	 * @return a new map instance
	 * @since 4.1.3
	 * @see java.util.LinkedHashMap
	 * @see java.util.TreeMap
	 * @see org.springframework.util.LinkedMultiValueMap
	 * @see java.util.EnumMap
	 * @throws IllegalArgumentException if the supplied {@code mapType} is
	 * {@code null}; or if the desired {@code mapType} is {@link EnumMap} and
	 * the supplied {@code keyType} is not a subtype of {@link Enum}
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <K, V> Map<K, V> createMap(Class<?> mapType, Class<?> keyType, int capacity) {
		Assert.notNull(mapType, "Map type must not be null");
		if (mapType.isInterface()) {
			if (Map.class == mapType) {
				return new LinkedHashMap<K, V>(capacity);
			}
			else if (SortedMap.class == mapType || NavigableMap.class == mapType) {
				return new TreeMap<K, V>();
			}
			else if (MultiValueMap.class == mapType) {
				return new LinkedMultiValueMap();
			}
			else {
				throw new IllegalArgumentException("Unsupported Map interface: " + mapType.getName());
			}
		}
		else if (EnumMap.class == mapType) {
			Assert.notNull(keyType, "Cannot create EnumMap for unknown key type");
			return new EnumMap(asEnumType(keyType));
		}
		else {
			if (!Map.class.isAssignableFrom(mapType)) {
				throw new IllegalArgumentException("Unsupported Map type: " + mapType.getName());
			}
			try {
				return (Map<K, V>) mapType.newInstance();
			}
			catch (Throwable ex) {
				throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName(), ex);
			}
		}
	}

	/**
	 * Cast the given type to a subtype of {@link Enum}.
	 * <p>
	 * 
	 * 
	 * @param enumType the enum type, never {@code null}
	 * @return the given type as subtype of {@link Enum}
	 * @throws IllegalArgumentException if the given type is not a subtype of {@link Enum}
	 */
	@SuppressWarnings("rawtypes")
	private static Class<? extends Enum> asEnumType(Class<?> enumType) {
		Assert.notNull(enumType, "Enum type must not be null");
		if (!Enum.class.isAssignableFrom(enumType)) {
			throw new IllegalArgumentException("Supplied type is not an enum: " + enumType.getName());
		}
		return enumType.asSubclass(Enum.class);
	}

}
