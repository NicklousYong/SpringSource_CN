/***** Lobxxx Translate Finished ******/
/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
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
 * A visitor to visit a Java field. The methods of this class must be called in
 * the following order: ( <tt>visitAnnotation</tt> |
 * <tt>visitTypeAnnotation</tt> | <tt>visitAttribute</tt> )* <tt>visitEnd</tt>.
 * 
 * <p>
 * 访问Java字段此类的方法必须按以下顺序调用：(<tt> visitAnnotation </tt> | <tt> visitTypeAnnotation </tt> | <tt> visitAttri
 * bute </tt>)* < tt>的visitEnd </tt>的。
 * 
 * 
 * @author Eric Bruneton
 */
public abstract class FieldVisitor {

    /**
     * The ASM API version implemented by this visitor. The value of this field
     * must be one of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * <p>
     *  此访问者实现的ASM API版本此字段的值必须是{@link操作码#ASM4}或{@link操作码#ASM5}之一
     * 
     */
    protected final int api;

    /**
     * The field visitor to which this visitor must delegate method calls. May
     * be null.
     * <p>
     *  访问者必须委派方法调用的字段访问者可以为null
     * 
     */
    protected FieldVisitor fv;

    /**
     * Constructs a new {@link FieldVisitor}.
     * 
     * <p>
     *  构造一个新的{@link FieldVisitor}
     * 
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     */
    public FieldVisitor(final int api) {
        this(api, null);
    }

    /**
     * Constructs a new {@link FieldVisitor}.
     * 
     * <p>
     *  构造一个新的{@link FieldVisitor}
     * 
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param fv
     *            the field visitor to which this visitor must delegate method
     *            calls. May be null.
     */
    public FieldVisitor(final int api, final FieldVisitor fv) {
        if (api != Opcodes.ASM4 && api != Opcodes.ASM5) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.fv = fv;
    }

    /**
     * Visits an annotation of the field.
     * 
     * <p>
     *  访问该字段的注释
     * 
     * 
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (fv != null) {
            return fv.visitAnnotation(desc, visible);
        }
        return null;
    }

    /**
     * Visits an annotation on the type of the field.
     * 
     * <p>
     *  访问字段类型的注释
     * 
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#FIELD FIELD}. See
     *            {@link TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
		/* SPRING PATCH: REMOVED FOR COMPATIBILITY WITH CGLIB 3.1
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
		/* <p>
		/*  if(api <OpcodesASM5){throw new RuntimeException(); }
		/* 
        */
        if (fv != null) {
            return fv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    /**
     * Visits a non standard attribute of the field.
     * 
     * <p>
     *  访问该字段的非标准属性
     * 
     * 
     * @param attr
     *            an attribute.
     */
    public void visitAttribute(Attribute attr) {
        if (fv != null) {
            fv.visitAttribute(attr);
        }
    }

    /**
     * Visits the end of the field. This method, which is the last one to be
     * called, is used to inform the visitor that all the annotations and
     * attributes of the field have been visited.
     * <p>
     * 访问字段的末尾该方法(最后一个被调用)用于通知访问者已经访问了该字段的所有注释和属性
     */
    public void visitEnd() {
        if (fv != null) {
            fv.visitEnd();
        }
    }
}
