/***** Lobxxx Translate Finished ******/
/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2013 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.springframework.asm;

/**
 * A reference to a type appearing in a class, field or method declaration, or
 * on an instruction. Such a reference designates the part of the class where
 * the referenced type is appearing (e.g. an 'extends', 'implements' or 'throws'
 * clause, a 'new' instruction, a 'catch' clause, a type cast, a local variable
 * declaration, etc).
 * 
 * <p>
 * 对类,字段或方法声明或指令中出现的类型的引用此类引用指定引用类型出现的类的部分(例如"extends","implements"或"throws"子句,一个'新'指令,'catch'子句,类型转换,局部
 * 变量声明等)。
 * 
 * 
 * @author Eric Bruneton
 */
public class TypeReference {

    /**
     * The sort of type references that target a type parameter of a generic
     * class. See {@link #getSort getSort}.
     * <p>
     *  定义通用类的类型参数的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int CLASS_TYPE_PARAMETER = 0x00;

    /**
     * The sort of type references that target a type parameter of a generic
     * method. See {@link #getSort getSort}.
     * <p>
     *  类型引用的类型引用,定义通用方法的类型参数请参阅{@link #getSort getSort}
     * 
     */
    public final static int METHOD_TYPE_PARAMETER = 0x01;

    /**
     * The sort of type references that target the super class of a class or one
     * of the interfaces it implements. See {@link #getSort getSort}.
     * <p>
     *  目标类的超类的类型引用类型或其实现的接口之一参见{@link #getSort getSort}
     * 
     */
    public final static int CLASS_EXTENDS = 0x10;

    /**
     * The sort of type references that target a bound of a type parameter of a
     * generic class. See {@link #getSort getSort}.
     * <p>
     *  类型引用的类型引用,定义通用类的类型参数的界限参见{@link #getSort getSort}
     * 
     */
    public final static int CLASS_TYPE_PARAMETER_BOUND = 0x11;

    /**
     * The sort of type references that target a bound of a type parameter of a
     * generic method. See {@link #getSort getSort}.
     * <p>
     * 目标类型参数的类型引用的类型参见{@link #getSort getSort}
     * 
     */
    public final static int METHOD_TYPE_PARAMETER_BOUND = 0x12;

    /**
     * The sort of type references that target the type of a field. See
     * {@link #getSort getSort}.
     * <p>
     *  指向字段类型的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int FIELD = 0x13;

    /**
     * The sort of type references that target the return type of a method. See
     * {@link #getSort getSort}.
     * <p>
     *  指向方法的返回类型的类型引用类型请参阅{@link #getSort getSort}
     * 
     */
    public final static int METHOD_RETURN = 0x14;

    /**
     * The sort of type references that target the receiver type of a method.
     * See {@link #getSort getSort}.
     * <p>
     *  针对方法的接收器类型的类型引用类型请参阅{@link #getSort getSort}
     * 
     */
    public final static int METHOD_RECEIVER = 0x15;

    /**
     * The sort of type references that target the type of a formal parameter of
     * a method. See {@link #getSort getSort}.
     * <p>
     *  类型引用的类型引用,定义方法的形式参数的类型请参阅{@link #getSort getSort}
     * 
     */
    public final static int METHOD_FORMAL_PARAMETER = 0x16;

    /**
     * The sort of type references that target the type of an exception declared
     * in the throws clause of a method. See {@link #getSort getSort}.
     * <p>
     *  类型引用,定义在方法的throws子句中声明的异常类型请参见{@link #getSort getSort}
     * 
     */
    public final static int THROWS = 0x17;

    /**
     * The sort of type references that target the type of a local variable in a
     * method. See {@link #getSort getSort}.
     * <p>
     *  在方法中定位局部变量类型的类型引用类型请参阅{@link #getSort getSort}
     * 
     */
    public final static int LOCAL_VARIABLE = 0x40;

    /**
     * The sort of type references that target the type of a resource variable
     * in a method. See {@link #getSort getSort}.
     * <p>
     * 在方法中定位资源变量类型的类型引用类型请参阅{@link #getSort getSort}
     * 
     */
    public final static int RESOURCE_VARIABLE = 0x41;

    /**
     * The sort of type references that target the type of the exception of a
     * 'catch' clause in a method. See {@link #getSort getSort}.
     * <p>
     *  在方法中定位"catch"子句异常类型的类型引用类型请参阅{@link #getSort getSort}
     * 
     */
    public final static int EXCEPTION_PARAMETER = 0x42;

    /**
     * The sort of type references that target the type declared in an
     * 'instanceof' instruction. See {@link #getSort getSort}.
     * <p>
     *  指向"instanceof"指令中声明的类型的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int INSTANCEOF = 0x43;

    /**
     * The sort of type references that target the type of the object created by
     * a 'new' instruction. See {@link #getSort getSort}.
     * <p>
     *  以"new"指令创建的对象类型的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int NEW = 0x44;

    /**
     * The sort of type references that target the receiver type of a
     * constructor reference. See {@link #getSort getSort}.
     * <p>
     *  针对构造函数引用的接收器类型的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int CONSTRUCTOR_REFERENCE = 0x45;

    /**
     * The sort of type references that target the receiver type of a method
     * reference. See {@link #getSort getSort}.
     * <p>
     *  定位方法引用类型的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int METHOD_REFERENCE = 0x46;

    /**
     * The sort of type references that target the type declared in an explicit
     * or implicit cast instruction. See {@link #getSort getSort}.
     * <p>
     * 类型引用的目标类型在显式或隐式转换指令中声明请参见{@link #getSort getSort}
     * 
     */
    public final static int CAST = 0x47;

    /**
     * The sort of type references that target a type parameter of a generic
     * constructor in a constructor call. See {@link #getSort getSort}.
     * <p>
     *  在构造函数调用中定义通用构造函数的类型参数的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 0x48;

    /**
     * The sort of type references that target a type parameter of a generic
     * method in a method call. See {@link #getSort getSort}.
     * <p>
     *  在方法调用中定位通用方法的类型参数的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int METHOD_INVOCATION_TYPE_ARGUMENT = 0x49;

    /**
     * The sort of type references that target a type parameter of a generic
     * constructor in a constructor reference. See {@link #getSort getSort}.
     * <p>
     *  在构造函数引用中定义通用构造函数的类型参数的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 0x4A;

    /**
     * The sort of type references that target a type parameter of a generic
     * method in a method reference. See {@link #getSort getSort}.
     * <p>
     *  在方法引用中定义通用方法的类型参数的类型引用类型参见{@link #getSort getSort}
     * 
     */
    public final static int METHOD_REFERENCE_TYPE_ARGUMENT = 0x4B;

    /**
     * The type reference value in Java class file format.
     * <p>
     *  Java类文件格式的类型参考值
     * 
     */
    private int value;

    /**
     * Creates a new TypeReference.
     * 
     * <p>
     *  创建一个新的TypeReference
     * 
     * 
     * @param typeRef
     *            the int encoded value of the type reference, as received in a
     *            visit method related to type annotations, like
     *            visitTypeAnnotation.
     */
    public TypeReference(int typeRef) {
        this.value = typeRef;
    }

    /**
     * Returns a type reference of the given sort.
     * 
     * <p>
     *  返回给定排序的类型引用
     * 
     * 
     * @param sort
     *            {@link #FIELD FIELD}, {@link #METHOD_RETURN METHOD_RETURN},
     *            {@link #METHOD_RECEIVER METHOD_RECEIVER},
     *            {@link #LOCAL_VARIABLE LOCAL_VARIABLE},
     *            {@link #RESOURCE_VARIABLE RESOURCE_VARIABLE},
     *            {@link #INSTANCEOF INSTANCEOF}, {@link #NEW NEW},
     *            {@link #CONSTRUCTOR_REFERENCE CONSTRUCTOR_REFERENCE}, or
     *            {@link #METHOD_REFERENCE METHOD_REFERENCE}.
     * @return a type reference of the given sort.
     */
    public static TypeReference newTypeReference(int sort) {
        return new TypeReference(sort << 24);
    }

    /**
     * Returns a reference to a type parameter of a generic class or method.
     * 
     * <p>
     * 返回对泛型类或方法的类型参数的引用
     * 
     * 
     * @param sort
     *            {@link #CLASS_TYPE_PARAMETER CLASS_TYPE_PARAMETER} or
     *            {@link #METHOD_TYPE_PARAMETER METHOD_TYPE_PARAMETER}.
     * @param paramIndex
     *            the type parameter index.
     * @return a reference to the given generic class or method type parameter.
     */
    public static TypeReference newTypeParameterReference(int sort,
            int paramIndex) {
        return new TypeReference((sort << 24) | (paramIndex << 16));
    }

    /**
     * Returns a reference to a type parameter bound of a generic class or
     * method.
     * 
     * <p>
     *  返回对通用类或方法绑定的类型参数的引用
     * 
     * 
     * @param sort
     *            {@link #CLASS_TYPE_PARAMETER CLASS_TYPE_PARAMETER} or
     *            {@link #METHOD_TYPE_PARAMETER METHOD_TYPE_PARAMETER}.
     * @param paramIndex
     *            the type parameter index.
     * @param boundIndex
     *            the type bound index within the above type parameters.
     * @return a reference to the given generic class or method type parameter
     *         bound.
     */
    public static TypeReference newTypeParameterBoundReference(int sort,
            int paramIndex, int boundIndex) {
        return new TypeReference((sort << 24) | (paramIndex << 16)
                | (boundIndex << 8));
    }

    /**
     * Returns a reference to the super class or to an interface of the
     * 'implements' clause of a class.
     * 
     * <p>
     *  返回对类的"implements"子句的超类或接口的引用
     * 
     * 
     * @param itfIndex
     *            the index of an interface in the 'implements' clause of a
     *            class, or -1 to reference the super class of the class.
     * @return a reference to the given super type of a class.
     */
    public static TypeReference newSuperTypeReference(int itfIndex) {
        itfIndex &= 0xFFFF;
        return new TypeReference((CLASS_EXTENDS << 24) | (itfIndex << 8));
    }

    /**
     * Returns a reference to the type of a formal parameter of a method.
     * 
     * <p>
     *  返回对方法的形式参数类型的引用
     * 
     * 
     * @param paramIndex
     *            the formal parameter index.
     * 
     * @return a reference to the type of the given method formal parameter.
     */
    public static TypeReference newFormalParameterReference(int paramIndex) {
        return new TypeReference((METHOD_FORMAL_PARAMETER << 24)
                | (paramIndex << 16));
    }

    /**
     * Returns a reference to the type of an exception, in a 'throws' clause of
     * a method.
     * 
     * <p>
     *  在方法的'throws'子句中返回对异常类型的引用
     * 
     * 
     * @param exceptionIndex
     *            the index of an exception in a 'throws' clause of a method.
     * 
     * @return a reference to the type of the given exception.
     */
    public static TypeReference newExceptionReference(int exceptionIndex) {
        return new TypeReference((THROWS << 24) | (exceptionIndex << 8));
    }

    /**
     * Returns a reference to the type of the exception declared in a 'catch'
     * clause of a method.
     * 
     * <p>
     *  返回对方法的'catch'子句中声明的异常类型的引用
     * 
     * 
     * @param tryCatchBlockIndex
     *            the index of a try catch block (using the order in which they
     *            are visited with visitTryCatchBlock).
     * 
     * @return a reference to the type of the given exception.
     */
    public static TypeReference newTryCatchReference(int tryCatchBlockIndex) {
        return new TypeReference((EXCEPTION_PARAMETER << 24)
                | (tryCatchBlockIndex << 8));
    }

    /**
     * Returns a reference to the type of a type argument in a constructor or
     * method call or reference.
     * 
     * <p>
     *  返回对构造函数或方法调用或引用中类型参数类型的引用
     * 
     * 
     * @param sort
     *            {@link #CAST CAST},
     *            {@link #CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT
     *            CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT},
     *            {@link #METHOD_INVOCATION_TYPE_ARGUMENT
     *            METHOD_INVOCATION_TYPE_ARGUMENT},
     *            {@link #CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT
     *            CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or
     *            {@link #METHOD_REFERENCE_TYPE_ARGUMENT
     *            METHOD_REFERENCE_TYPE_ARGUMENT}.
     * @param argIndex
     *            the type argument index.
     * 
     * @return a reference to the type of the given type argument.
     */
    public static TypeReference newTypeArgumentReference(int sort, int argIndex) {
        return new TypeReference((sort << 24) | argIndex);
    }

    /**
     * Returns the sort of this type reference.
     * 
     * <p>
     *  返回此类型引用的类型
     * 
     * 
     * @return {@link #CLASS_TYPE_PARAMETER CLASS_TYPE_PARAMETER},
     *         {@link #METHOD_TYPE_PARAMETER METHOD_TYPE_PARAMETER},
     *         {@link #CLASS_EXTENDS CLASS_EXTENDS},
     *         {@link #CLASS_TYPE_PARAMETER_BOUND CLASS_TYPE_PARAMETER_BOUND},
     *         {@link #METHOD_TYPE_PARAMETER_BOUND METHOD_TYPE_PARAMETER_BOUND},
     *         {@link #FIELD FIELD}, {@link #METHOD_RETURN METHOD_RETURN},
     *         {@link #METHOD_RECEIVER METHOD_RECEIVER},
     *         {@link #METHOD_FORMAL_PARAMETER METHOD_FORMAL_PARAMETER},
     *         {@link #THROWS THROWS}, {@link #LOCAL_VARIABLE LOCAL_VARIABLE},
     *         {@link #RESOURCE_VARIABLE RESOURCE_VARIABLE},
     *         {@link #EXCEPTION_PARAMETER EXCEPTION_PARAMETER},
     *         {@link #INSTANCEOF INSTANCEOF}, {@link #NEW NEW},
     *         {@link #CONSTRUCTOR_REFERENCE CONSTRUCTOR_REFERENCE},
     *         {@link #METHOD_REFERENCE METHOD_REFERENCE}, {@link #CAST CAST},
     *         {@link #CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT
     *         CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT},
     *         {@link #METHOD_INVOCATION_TYPE_ARGUMENT
     *         METHOD_INVOCATION_TYPE_ARGUMENT},
     *         {@link #CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT
     *         CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or
     *         {@link #METHOD_REFERENCE_TYPE_ARGUMENT
     *         METHOD_REFERENCE_TYPE_ARGUMENT}.
     */
    public int getSort() {
        return value >>> 24;
    }

    /**
     * Returns the index of the type parameter referenced by this type
     * reference. This method must only be used for type references whose sort
     * is {@link #CLASS_TYPE_PARAMETER CLASS_TYPE_PARAMETER},
     * {@link #METHOD_TYPE_PARAMETER METHOD_TYPE_PARAMETER},
     * {@link #CLASS_TYPE_PARAMETER_BOUND CLASS_TYPE_PARAMETER_BOUND} or
     * {@link #METHOD_TYPE_PARAMETER_BOUND METHOD_TYPE_PARAMETER_BOUND}.
     * 
     * <p>
     * 返回此类型引用引用的类型参数的索引此方法只能用于类型为{@link #CLASS_TYPE_PARAMETER CLASS_TYPE_PARAMETER},{@link #METHOD_TYPE_PARAMETER METHOD_TYPE_PARAMETER}
     * ,{@link #CLASS_TYPE_PARAMETER_BOUND CLASS_TYPE_PARAMETER_BOUND}或{@链接#METHOD_TYPE_PARAMETER_BOUND METHOD_TYPE_PARAMETER_BOUND}
     * 。
     * 
     * 
     * @return a type parameter index.
     */
    public int getTypeParameterIndex() {
        return (value & 0x00FF0000) >> 16;
    }

    /**
     * Returns the index of the type parameter bound, within the type parameter
     * {@link #getTypeParameterIndex}, referenced by this type reference. This
     * method must only be used for type references whose sort is
     * {@link #CLASS_TYPE_PARAMETER_BOUND CLASS_TYPE_PARAMETER_BOUND} or
     * {@link #METHOD_TYPE_PARAMETER_BOUND METHOD_TYPE_PARAMETER_BOUND}.
     * 
     * <p>
     *  返回类型参数绑定的索引,在类型参数{@link #getTypeParameterIndex}中引用此类型引用此方法只能用于类型为{@link #CLASS_TYPE_PARAMETER_BOUND CLASS_TYPE_PARAMETER_BOUND}
     * 或{@link #METHOD_TYPE_PARAMETER_BOUND METHOD_TYPE_PARAMETER_BOUND}。
     * 
     * 
     * @return a type parameter bound index.
     */
    public int getTypeParameterBoundIndex() {
        return (value & 0x0000FF00) >> 8;
    }

    /**
     * Returns the index of the "super type" of a class that is referenced by
     * this type reference. This method must only be used for type references
     * whose sort is {@link #CLASS_EXTENDS CLASS_EXTENDS}.
     * 
     * <p>
     * 返回此类型引用的类的"超类型"的索引此方法只能用于类型为{@link #CLASS_EXTENDS CLASS_EXTENDS}的类型引用
     * 
     * 
     * @return the index of an interface in the 'implements' clause of a class,
     *         or -1 if this type reference references the type of the super
     *         class.
     */
    public int getSuperTypeIndex() {
        return (short) ((value & 0x00FFFF00) >> 8);
    }

    /**
     * Returns the index of the formal parameter whose type is referenced by
     * this type reference. This method must only be used for type references
     * whose sort is {@link #METHOD_FORMAL_PARAMETER METHOD_FORMAL_PARAMETER}.
     * 
     * <p>
     *  返回此类型引用类型的形式参数的索引此方法只能用于类型为{@link #METHOD_FORMAL_PARAMETER METHOD_FORMAL_PARAMETER}的类型引用
     * 
     * 
     * @return a formal parameter index.
     */
    public int getFormalParameterIndex() {
        return (value & 0x00FF0000) >> 16;
    }

    /**
     * Returns the index of the exception, in a 'throws' clause of a method,
     * whose type is referenced by this type reference. This method must only be
     * used for type references whose sort is {@link #THROWS THROWS}.
     * 
     * <p>
     *  在方法的"throws"子句中返回异常的索引,该类型引用此类型引用此方法只能用于类型为{@link #THROWS THROWS}的类型引用
     * 
     * 
     * @return the index of an exception in the 'throws' clause of a method.
     */
    public int getExceptionIndex() {
        return (value & 0x00FFFF00) >> 8;
    }

    /**
     * Returns the index of the try catch block (using the order in which they
     * are visited with visitTryCatchBlock), whose 'catch' type is referenced by
     * this type reference. This method must only be used for type references
     * whose sort is {@link #EXCEPTION_PARAMETER EXCEPTION_PARAMETER} .
     * 
     * <p>
     * 返回try catch块的索引(使用visitTryCatchBlock访问的顺序),其"catch"类型由此类型引用引用此方法只能用于类型为{@link #EXCEPTION_PARAMETER EXCEPTION_PARAMETER }
     * 。
     * 
     * 
     * @return the index of an exception in the 'throws' clause of a method.
     */
    public int getTryCatchBlockIndex() {
        return (value & 0x00FFFF00) >> 8;
    }

    /**
     * Returns the index of the type argument referenced by this type reference.
     * This method must only be used for type references whose sort is
     * {@link #CAST CAST}, {@link #CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT
     * CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT},
     * {@link #METHOD_INVOCATION_TYPE_ARGUMENT METHOD_INVOCATION_TYPE_ARGUMENT},
     * {@link #CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT
     * CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or
     * {@link #METHOD_REFERENCE_TYPE_ARGUMENT METHOD_REFERENCE_TYPE_ARGUMENT}.
     * 
     * <p>
     *  返回此类型引用引用的类型参数的索引此方法只能用于类型为{@link #CAST CAST},{@link #CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT}
     * ,{@link #METHOD_INVOCATION_TYPE_ARGUMENT METHOD_INVOCATION_TYPE_ARGUMENT},{@链接#CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}
     * 或{@link #METHOD_REFERENCE_TYPE_ARGUMENT METHOD_REFERENCE_TYPE_ARGUMENT}。
     * 
     * 
     * @return a type parameter index.
     */
    public int getTypeArgumentIndex() {
        return value & 0xFF;
    }

    /**
     * Returns the int encoded value of this type reference, suitable for use in
     * visit methods related to type annotations, like visitTypeAnnotation.
     * 
     * <p>
     * 
     * @return the int encoded value of this type reference.
     */
    public int getValue() {
        return value;
    }
}
