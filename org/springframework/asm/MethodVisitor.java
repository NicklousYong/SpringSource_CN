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
 * A visitor to visit a Java method. The methods of this class must be called in
 * the following order: ( <tt>visitParameter</tt> )* [
 * <tt>visitAnnotationDefault</tt> ] ( <tt>visitAnnotation</tt> |
 * <tt>visitTypeAnnotation</tt> | <tt>visitAttribute</tt> )* [
 * <tt>visitCode</tt> ( <tt>visitFrame</tt> | <tt>visit<i>X</i>Insn</tt> |
 * <tt>visitLabel</tt> | <tt>visitInsnAnnotation</tt> |
 * <tt>visitTryCatchBlock</tt> | <tt>visitTryCatchBlockAnnotation</tt> |
 * <tt>visitLocalVariable</tt> | <tt>visitLocalVariableAnnotation</tt> |
 * <tt>visitLineNumber</tt> )* <tt>visitMaxs</tt> ] <tt>visitEnd</tt>. In
 * addition, the <tt>visit<i>X</i>Insn</tt> and <tt>visitLabel</tt> methods must
 * be called in the sequential order of the bytecode instructions of the visited
 * code, <tt>visitInsnAnnotation</tt> must be called <i>after</i> the annotated
 * instruction, <tt>visitTryCatchBlock</tt> must be called <i>before</i> the
 * labels passed as arguments have been visited,
 * <tt>visitTryCatchBlockAnnotation</tt> must be called <i>after</i> the
 * corresponding try catch block has been visited, and the
 * <tt>visitLocalVariable</tt>, <tt>visitLocalVariableAnnotation</tt> and
 * <tt>visitLineNumber</tt> methods must be called <i>after</i> the labels
 * passed as arguments have been visited.
 * 
 * <p>
 * 访问Java方法的访问者必须按以下顺序调用此类的方法：(<tt> visitParameter </tt>)* [<tt> visitAnnotationDefault </tt>](<tt> visi
 * tAnnotation </tt> | <tt> visitTypeAnnotation </tt> | <tt> visitAttribute </tt>)* [<tt> visitCode </tt>
 * (<tt> visitFrame </tt> | <tt>访问<i> X </i > Insn </tt> | <tt> visitLabel </tt> | <tt> visitInsnAnnotat
 * ion </tt> | <tt> visitTryCatchBlock </tt> | <tt> visitTryCatchBlockAnnotation </tt> | <tt> visitLocal
 * Variable </tt > | <tt> visitLocalVariableAnnotation </tt> | <tt> visitLineNumber </tt>)* <tt> visitMa
 * xs </tt>] <tt> visitEnd </tt>另外,<tt>访问</i> Ins </tt>和<tt> visitLabel </tt>方法必须按照访问代码的字节码指令的顺序调用<tt> v
 * isitInsnAnnotation </tt>必须在</i>注释指令之后调用<i>,<tt> visitTryCatchBlock </tt>必须在</i>之前调用<i>作为参数传递的标签,<tt >
 *  visitTryCatchBlockAnnotation </tt>必须在</i>之后调用相应的try catch块,<tt>访问LocalVariable </tt>,<tt>访问LocalVari
 * ableAnnotation </tt>和<tt> visitLineNumber </i>之后,</i>在传入参数的标签被访问之后,必须调用</i>方法。
 * 
 * 
 * @author Eric Bruneton
 */
public abstract class MethodVisitor {

    /**
     * The ASM API version implemented by this visitor. The value of this field
     * must be one of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * <p>
     * 此访问者实现的ASM API版本此字段的值必须是{@link操作码#ASM4}或{@link操作码#ASM5}之一
     * 
     */
    protected final int api;

    /**
     * The method visitor to which this visitor must delegate method calls. May
     * be null.
     * <p>
     *  访问者必须委派方法调用的方法访问者可以为null
     * 
     */
    protected MethodVisitor mv;

    /**
     * Constructs a new {@link MethodVisitor}.
     * 
     * <p>
     *  构造一个新的{@link MethodVisitor}
     * 
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     */
    public MethodVisitor(final int api) {
        this(api, null);
    }

    /**
     * Constructs a new {@link MethodVisitor}.
     * 
     * <p>
     *  构造一个新的{@link MethodVisitor}
     * 
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param mv
     *            the method visitor to which this visitor must delegate method
     *            calls. May be null.
     */
    public MethodVisitor(final int api, final MethodVisitor mv) {
        if (api != Opcodes.ASM4 && api != Opcodes.ASM5) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.mv = mv;
    }

    // -------------------------------------------------------------------------
    // Parameters, annotations and non standard attributes
    // -------------------------------------------------------------------------

    /**
     * Visits a parameter of this method.
     * 
     * <p>
     *  访问此方法的参数
     * 
     * 
     * @param name
     *            parameter name or null if none is provided.
     * @param access
     *            the parameter's access flags, only <tt>ACC_FINAL</tt>,
     *            <tt>ACC_SYNTHETIC</tt> or/and <tt>ACC_MANDATED</tt> are
     *            allowed (see {@link Opcodes}).
     */
    public void visitParameter(String name, int access) {
		/* SPRING PATCH: REMOVED FOR COMPATIBILITY WITH CGLIB 3.1
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
		/* <p>
		/*  if(api <OpcodesASM5){throw new RuntimeException(); }
		/* 
        */
        if (mv != null) {
            mv.visitParameter(name, access);
        }
    }

    /**
     * Visits the default value of this annotation interface method.
     * 
     * <p>
     *  访问此注释接口方法的默认值
     * 
     * 
     * @return a visitor to the visit the actual default value of this
     *         annotation interface method, or <tt>null</tt> if this visitor is
     *         not interested in visiting this default value. The 'name'
     *         parameters passed to the methods of this annotation visitor are
     *         ignored. Moreover, exacly one visit method must be called on this
     *         annotation visitor, followed by visitEnd.
     */
    public AnnotationVisitor visitAnnotationDefault() {
        if (mv != null) {
            return mv.visitAnnotationDefault();
        }
        return null;
    }

    /**
     * Visits an annotation of this method.
     * 
     * <p>
     *  访问此方法的注释
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
        if (mv != null) {
            return mv.visitAnnotation(desc, visible);
        }
        return null;
    }

    /**
     * Visits an annotation on a type in the method signature.
     * 
     * <p>
     *  访问方法签名中类型的注释
     * 
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#METHOD_TYPE_PARAMETER
     *            METHOD_TYPE_PARAMETER},
     *            {@link TypeReference#METHOD_TYPE_PARAMETER_BOUND
     *            METHOD_TYPE_PARAMETER_BOUND},
     *            {@link TypeReference#METHOD_RETURN METHOD_RETURN},
     *            {@link TypeReference#METHOD_RECEIVER METHOD_RECEIVER},
     *            {@link TypeReference#METHOD_FORMAL_PARAMETER
     *            METHOD_FORMAL_PARAMETER} or {@link TypeReference#THROWS
     *            THROWS}. See {@link TypeReference}.
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
        if (mv != null) {
            return mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    /**
     * Visits an annotation of a parameter this method.
     * 
     * <p>
     *  访问此方法的参数注释
     * 
     * 
     * @param parameter
     *            the parameter index.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitParameterAnnotation(int parameter,
            String desc, boolean visible) {
        if (mv != null) {
            return mv.visitParameterAnnotation(parameter, desc, visible);
        }
        return null;
    }

    /**
     * Visits a non standard attribute of this method.
     * 
     * <p>
     *  访问此方法的非标准属性
     * 
     * 
     * @param attr
     *            an attribute.
     */
    public void visitAttribute(Attribute attr) {
        if (mv != null) {
            mv.visitAttribute(attr);
        }
    }

    /**
     * Starts the visit of the method's code, if any (i.e. non abstract method).
     * <p>
     *  开始访问方法的代码,如果有的话(即非抽象方法)
     * 
     */
    public void visitCode() {
        if (mv != null) {
            mv.visitCode();
        }
    }

    /**
     * Visits the current state of the local variables and operand stack
     * elements. This method must(*) be called <i>just before</i> any
     * instruction <b>i</b> that follows an unconditional branch instruction
     * such as GOTO or THROW, that is the target of a jump instruction, or that
     * starts an exception handler block. The visited types must describe the
     * values of the local variables and of the operand stack elements <i>just
     * before</i> <b>i</b> is executed.<br>
     * <br>
     * (*) this is mandatory only for classes whose version is greater than or
     * equal to {@link Opcodes#V1_6 V1_6}. <br>
     * <br>
     * The frames of a method must be given either in expanded form, or in
     * compressed form (all frames must use the same format, i.e. you must not
     * mix expanded and compressed frames within a single method):
     * <ul>
     * <li>In expanded form, all frames must have the F_NEW type.</li>
     * <li>In compressed form, frames are basically "deltas" from the state of
     * the previous frame:
     * <ul>
     * <li>{@link Opcodes#F_SAME} representing frame with exactly the same
     * locals as the previous frame and with the empty stack.</li>
     * <li>{@link Opcodes#F_SAME1} representing frame with exactly the same
     * locals as the previous frame and with single value on the stack (
     * <code>nStack</code> is 1 and <code>stack[0]</code> contains value for the
     * type of the stack item).</li>
     * <li>{@link Opcodes#F_APPEND} representing frame with current locals are
     * the same as the locals in the previous frame, except that additional
     * locals are defined (<code>nLocal</code> is 1, 2 or 3 and
     * <code>local</code> elements contains values representing added types).</li>
     * <li>{@link Opcodes#F_CHOP} representing frame with current locals are the
     * same as the locals in the previous frame, except that the last 1-3 locals
     * are absent and with the empty stack (<code>nLocals</code> is 1, 2 or 3).</li>
     * <li>{@link Opcodes#F_FULL} representing complete frame data.</li>
     * </ul>
     * </li>
     * </ul>
     * <br>
     * In both cases the first frame, corresponding to the method's parameters
     * and access flags, is implicit and must not be visited. Also, it is
     * illegal to visit two or more frames for the same code location (i.e., at
     * least one instruction must be visited between two calls to visitFrame).
     * 
     * <p>
     * 访问局部变量和操作数堆栈元素的当前状态此方法必须(*)在</i>之前的任何指令<b> i </b>之后调用</i>,无条件转移指令如GOTO或THROW ,即跳转指令的目标,或启动异常处理程序块访问类型
     * 必须描述局部变量和操作数堆栈元素<i>之前</i> <b> i </b >被执行<br>。
     * <br>
     *  (*)对于版本大于或等于{@link Opcodes#V1_6 V1_6}的类,这是强制性的<br>
     * <br>
     *  必须以扩展形式或压缩格式给出方法的帧(所有帧必须使用相同的格式,即不能在单个方法中混合扩展和压缩帧)：
     * <ul>
     * <li>在展开形式中,所有帧必须具有F_NEW类型</li> <li>在压缩格式中,帧基本上是从前一帧的状态"deltas"：
     * <ul>
     * 表示具有与前一帧完全相同的本地的帧,以及空白堆栈</li> <li> {@链接操作码#F_SAME1}的帧的代码帧#F_SAME1代表具有与前一帧完全相同的本地的帧(<code> nStack </code>
     * 为1,<code> stack [0] </code>包含堆栈项的类型的值)</li> <li> {@表示与当前本地的帧的链接操作码#F_APPEND}与前一帧中的本地人相同,但定义了其他本地人(<code>
     *  nLocal </code>是1,2或3和<code> local </code >元素包含表示添加类型的值)表示具有当前本地位置的帧的</li> <li> {@ link操作码#F_CHOP}与前一
     * 帧中的本地人员相同,但最后1-3个本地不存在并且具有空堆栈(<code> nLocals </code>是1,2或3)表示完整帧数据的</li> <li> {@ link操作码#F_FULL} </li>
     * 。
     * </ul>
     * </li>
     * </ul>
     * <br>
     * 在这两种情况下,与方法的参数和访问标志相对应的第一帧是隐式的,不能被访问。另外,访问相同代码位置的两个或多个帧是非法的(即,必须在两个或更多个帧之间访问至少一个指令两次来电参观)
     * 
     * 
     * @param type
     *            the type of this stack map frame. Must be
     *            {@link Opcodes#F_NEW} for expanded frames, or
     *            {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND},
     *            {@link Opcodes#F_CHOP}, {@link Opcodes#F_SAME} or
     *            {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for
     *            compressed frames.
     * @param nLocal
     *            the number of local variables in the visited frame.
     * @param local
     *            the local variable types in this frame. This array must not be
     *            modified. Primitive types are represented by
     *            {@link Opcodes#TOP}, {@link Opcodes#INTEGER},
     *            {@link Opcodes#FLOAT}, {@link Opcodes#LONG},
     *            {@link Opcodes#DOUBLE},{@link Opcodes#NULL} or
     *            {@link Opcodes#UNINITIALIZED_THIS} (long and double are
     *            represented by a single element). Reference types are
     *            represented by String objects (representing internal names),
     *            and uninitialized types by Label objects (this label
     *            designates the NEW instruction that created this uninitialized
     *            value).
     * @param nStack
     *            the number of operand stack elements in the visited frame.
     * @param stack
     *            the operand stack types in this frame. This array must not be
     *            modified. Its content has the same format as the "local"
     *            array.
     * @throws IllegalStateException
     *             if a frame is visited just after another one, without any
     *             instruction between the two (unless this frame is a
     *             Opcodes#F_SAME frame, in which case it is silently ignored).
     */
    public void visitFrame(int type, int nLocal, Object[] local, int nStack,
            Object[] stack) {
        if (mv != null) {
            mv.visitFrame(type, nLocal, local, nStack, stack);
        }
    }

    // -------------------------------------------------------------------------
    // Normal instructions
    // -------------------------------------------------------------------------

    /**
     * Visits a zero operand instruction.
     * 
     * <p>
     *  访问零操作数指令
     * 
     * 
     * @param opcode
     *            the opcode of the instruction to be visited. This opcode is
     *            either NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1,
     *            ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1,
     *            FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD,
     *            LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
     *            IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE,
     *            SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1,
     *            DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
     *            IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
     *            FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
     *            IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D,
     *            L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
     *            LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
     *            DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER,
     *            or MONITOREXIT.
     */
    public void visitInsn(int opcode) {
        if (mv != null) {
            mv.visitInsn(opcode);
        }
    }

    /**
     * Visits an instruction with a single int operand.
     * 
     * <p>
     *  使用单个int操作数访问指令
     * 
     * 
     * @param opcode
     *            the opcode of the instruction to be visited. This opcode is
     *            either BIPUSH, SIPUSH or NEWARRAY.
     * @param operand
     *            the operand of the instruction to be visited.<br>
     *            When opcode is BIPUSH, operand value should be between
     *            Byte.MIN_VALUE and Byte.MAX_VALUE.<br>
     *            When opcode is SIPUSH, operand value should be between
     *            Short.MIN_VALUE and Short.MAX_VALUE.<br>
     *            When opcode is NEWARRAY, operand value should be one of
     *            {@link Opcodes#T_BOOLEAN}, {@link Opcodes#T_CHAR},
     *            {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE},
     *            {@link Opcodes#T_BYTE}, {@link Opcodes#T_SHORT},
     *            {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
     */
    public void visitIntInsn(int opcode, int operand) {
        if (mv != null) {
            mv.visitIntInsn(opcode, operand);
        }
    }

    /**
     * Visits a local variable instruction. A local variable instruction is an
     * instruction that loads or stores the value of a local variable.
     * 
     * <p>
     *  访问局部变量指令本地变量指令是加载或存储局部变量值的指令
     * 
     * 
     * @param opcode
     *            the opcode of the local variable instruction to be visited.
     *            This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD,
     *            ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param var
     *            the operand of the instruction to be visited. This operand is
     *            the index of a local variable.
     */
    public void visitVarInsn(int opcode, int var) {
        if (mv != null) {
            mv.visitVarInsn(opcode, var);
        }
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that
     * takes the internal name of a class as parameter.
     * 
     * <p>
     *  访问类型指令类型指令是将类的内部名称作为参数的指令
     * 
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param type
     *            the operand of the instruction to be visited. This operand
     *            must be the internal name of an object or array class (see
     *            {@link Type#getInternalName() getInternalName}).
     */
    public void visitTypeInsn(int opcode, String type) {
        if (mv != null) {
            mv.visitTypeInsn(opcode, type);
        }
    }

    /**
     * Visits a field instruction. A field instruction is an instruction that
     * loads or stores the value of a field of an object.
     * 
     * <p>
     *  访问字段指令字段指令是加载或存储对象的字段值的指令
     * 
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner
     *            the internal name of the field's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor (see {@link Type Type}).
     */
    public void visitFieldInsn(int opcode, String owner, String name,
            String desc) {
        if (mv != null) {
            mv.visitFieldInsn(opcode, owner, name, desc);
        }
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that
     * invokes a method.
     * 
     * <p>
     * 访问方法指令方法指令是调用方法的指令
     * 
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     */
    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc) {
        if (api >= Opcodes.ASM5) {
            boolean itf = opcode == Opcodes.INVOKEINTERFACE;
            visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that
     * invokes a method.
     * 
     * <p>
     *  访问方法指令方法指令是调用方法的指令
     * 
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param itf
     *            if the method's owner class is an interface.
     */
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc, boolean itf) {
        if (api < Opcodes.ASM5) {
            if (itf != (opcode == Opcodes.INVOKEINTERFACE)) {
                throw new IllegalArgumentException(
                        "INVOKESPECIAL/STATIC on interfaces require ASM 5");
            }
            visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    /**
     * Visits an invokedynamic instruction.
     * 
     * <p>
     *  访问一个invokedynamic指令
     * 
     * 
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param bsm
     *            the bootstrap method.
     * @param bsmArgs
     *            the bootstrap method constant arguments. Each argument must be
     *            an {@link Integer}, {@link Float}, {@link Long},
     *            {@link Double}, {@link String}, {@link Type} or {@link Handle}
     *            value. This method is allowed to modify the content of the
     *            array so a caller should expect that this array may change.
     */
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
            Object... bsmArgs) {
        if (mv != null) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        }
    }

    /**
     * Visits a jump instruction. A jump instruction is an instruction that may
     * jump to another instruction.
     * 
     * <p>
     *  访问跳转指令跳转指令是可能跳转到另一条指令的指令
     * 
     * 
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
     *            IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
     *            IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
     * @param label
     *            the operand of the instruction to be visited. This operand is
     *            a label that designates the instruction to which the jump
     *            instruction may jump.
     */
    public void visitJumpInsn(int opcode, Label label) {
        if (mv != null) {
            mv.visitJumpInsn(opcode, label);
        }
    }

    /**
     * Visits a label. A label designates the instruction that will be visited
     * just after it.
     * 
     * <p>
     *  访问标签标签指定将在其后访问的指令
     * 
     * 
     * @param label
     *            a {@link Label Label} object.
     */
    public void visitLabel(Label label) {
        if (mv != null) {
            mv.visitLabel(label);
        }
    }

    // -------------------------------------------------------------------------
    // Special instructions
    // -------------------------------------------------------------------------

    /**
     * Visits a LDC instruction. Note that new constant types may be added in
     * future versions of the Java Virtual Machine. To easily detect new
     * constant types, implementations of this method should check for
     * unexpected constant types, like this:
     * 
     * <pre>
     * if (cst instanceof Integer) {
     *     // ...
     * } else if (cst instanceof Float) {
     *     // ...
     * } else if (cst instanceof Long) {
     *     // ...
     * } else if (cst instanceof Double) {
     *     // ...
     * } else if (cst instanceof String) {
     *     // ...
     * } else if (cst instanceof Type) {
     *     int sort = ((Type) cst).getSort();
     *     if (sort == Type.OBJECT) {
     *         // ...
     *     } else if (sort == Type.ARRAY) {
     *         // ...
     *     } else if (sort == Type.METHOD) {
     *         // ...
     *     } else {
     *         // throw an exception
     *     }
     * } else if (cst instanceof Handle) {
     *     // ...
     * } else {
     *     // throw an exception
     * }
     * </pre>
     * 
     * <p>
     *  访问LDC指令请注意,Java虚拟机的将来版本中可能会添加新的常量类型为了轻松检测新的常量类型,此方法的实现应检查意外的常量类型,如下所示：
     * 
     * <pre>
     * if(cst instanceof Integer){//} else if(cst instanceof Float){//} else if(cst instanceof Long){//} els
     * e if(cst instanceof Double){//} else if(cst instanceof String) {//} else if(cst instanceof Type){int sort =((Type)cst)getSort(); if(sort == TypeOBJECT){//}
     *  else if(sort == TypeARRAY){//} else if(sort == TypeMETHOD){//} else {//抛出异常} else else(cst instanceo
     * f Handle ){//} else {//抛出异常}。
     * </pre>
     * 
     * 
     * @param cst
     *            the constant to be loaded on the stack. This parameter must be
     *            a non null {@link Integer}, a {@link Float}, a {@link Long}, a
     *            {@link Double}, a {@link String}, a {@link Type} of OBJECT or
     *            ARRAY sort for <tt>.class</tt> constants, for classes whose
     *            version is 49.0, a {@link Type} of METHOD sort or a
     *            {@link Handle} for MethodType and MethodHandle constants, for
     *            classes whose version is 51.0.
     */
    public void visitLdcInsn(Object cst) {
        if (mv != null) {
            mv.visitLdcInsn(cst);
        }
    }

    /**
     * Visits an IINC instruction.
     * 
     * <p>
     *  访问IINC指令
     * 
     * 
     * @param var
     *            index of the local variable to be incremented.
     * @param increment
     *            amount to increment the local variable by.
     */
    public void visitIincInsn(int var, int increment) {
        if (mv != null) {
            mv.visitIincInsn(var, increment);
        }
    }

    /**
     * Visits a TABLESWITCH instruction.
     * 
     * <p>
     *  访问TABLESWITCH指令
     * 
     * 
     * @param min
     *            the minimum key value.
     * @param max
     *            the maximum key value.
     * @param dflt
     *            beginning of the default handler block.
     * @param labels
     *            beginnings of the handler blocks. <tt>labels[i]</tt> is the
     *            beginning of the handler block for the <tt>min + i</tt> key.
     */
    public void visitTableSwitchInsn(int min, int max, Label dflt,
            Label... labels) {
        if (mv != null) {
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    /**
     * Visits a LOOKUPSWITCH instruction.
     * 
     * <p>
     *  访问LOOKUPSWITCH指令
     * 
     * 
     * @param dflt
     *            beginning of the default handler block.
     * @param keys
     *            the values of the keys.
     * @param labels
     *            beginnings of the handler blocks. <tt>labels[i]</tt> is the
     *            beginning of the handler block for the <tt>keys[i]</tt> key.
     */
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (mv != null) {
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    /**
     * Visits a MULTIANEWARRAY instruction.
     * 
     * <p>
     *  访问MULTIANEWARRAY指令
     * 
     * 
     * @param desc
     *            an array type descriptor (see {@link Type Type}).
     * @param dims
     *            number of dimensions of the array to allocate.
     */
    public void visitMultiANewArrayInsn(String desc, int dims) {
        if (mv != null) {
            mv.visitMultiANewArrayInsn(desc, dims);
        }
    }

    /**
     * Visits an annotation on an instruction. This method must be called just
     * <i>after</i> the annotated instruction. It can be called several times
     * for the same instruction.
     * 
     * <p>
     * 访问指令上的注释此方法必须在</i>注释指令后调用<i>可以为同一指令调用多次
     * 
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#INSTANCEOF INSTANCEOF},
     *            {@link TypeReference#NEW NEW},
     *            {@link TypeReference#CONSTRUCTOR_REFERENCE
     *            CONSTRUCTOR_REFERENCE}, {@link TypeReference#METHOD_REFERENCE
     *            METHOD_REFERENCE}, {@link TypeReference#CAST CAST},
     *            {@link TypeReference#CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT
     *            CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT},
     *            {@link TypeReference#METHOD_INVOCATION_TYPE_ARGUMENT
     *            METHOD_INVOCATION_TYPE_ARGUMENT},
     *            {@link TypeReference#CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT
     *            CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or
     *            {@link TypeReference#METHOD_REFERENCE_TYPE_ARGUMENT
     *            METHOD_REFERENCE_TYPE_ARGUMENT}. See {@link TypeReference}.
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
    public AnnotationVisitor visitInsnAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
		/* SPRING PATCH: REMOVED FOR COMPATIBILITY WITH CGLIB 3.1
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
		/* <p>
		/*  if(api <OpcodesASM5){throw new RuntimeException(); }
		/* 
        */
        if (mv != null) {
            return mv.visitInsnAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Exceptions table entries, debug information, max stack and max locals
    // -------------------------------------------------------------------------

    /**
     * Visits a try catch block.
     * 
     * <p>
     *  访问一个try catch块
     * 
     * 
     * @param start
     *            beginning of the exception handler's scope (inclusive).
     * @param end
     *            end of the exception handler's scope (exclusive).
     * @param handler
     *            beginning of the exception handler's code.
     * @param type
     *            internal name of the type of exceptions handled by the
     *            handler, or <tt>null</tt> to catch any exceptions (for
     *            "finally" blocks).
     * @throws IllegalArgumentException
     *             if one of the labels has already been visited by this visitor
     *             (by the {@link #visitLabel visitLabel} method).
     */
    public void visitTryCatchBlock(Label start, Label end, Label handler,
            String type) {
        if (mv != null) {
            mv.visitTryCatchBlock(start, end, handler, type);
        }
    }

    /**
     * Visits an annotation on an exception handler type. This method must be
     * called <i>after</i> the {@link #visitTryCatchBlock} for the annotated
     * exception handler. It can be called several times for the same exception
     * handler.
     * 
     * <p>
     *  访问异常处理程序类型的注释在</i>注释异常处理程序的{@link #visitTryCatchBlock}之后,必须调用此方法<i>可以为同一个异常处理程序调用多次
     * 
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#EXCEPTION_PARAMETER
     *            EXCEPTION_PARAMETER}. See {@link TypeReference}.
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
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
		/* SPRING PATCH: REMOVED FOR COMPATIBILITY WITH CGLIB 3.1
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
		/* <p>
		/*  if(api <OpcodesASM5){throw new RuntimeException(); }
		/* 
        */
        if (mv != null) {
            return mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    /**
     * Visits a local variable declaration.
     * 
     * <p>
     *  访问局部变量声明
     * 
     * 
     * @param name
     *            the name of a local variable.
     * @param desc
     *            the type descriptor of this local variable.
     * @param signature
     *            the type signature of this local variable. May be
     *            <tt>null</tt> if the local variable type does not use generic
     *            types.
     * @param start
     *            the first instruction corresponding to the scope of this local
     *            variable (inclusive).
     * @param end
     *            the last instruction corresponding to the scope of this local
     *            variable (exclusive).
     * @param index
     *            the local variable's index.
     * @throws IllegalArgumentException
     *             if one of the labels has not already been visited by this
     *             visitor (by the {@link #visitLabel visitLabel} method).
     */
    public void visitLocalVariable(String name, String desc, String signature,
            Label start, Label end, int index) {
        if (mv != null) {
            mv.visitLocalVariable(name, desc, signature, start, end, index);
        }
    }

    /**
     * Visits an annotation on a local variable type.
     * 
     * <p>
     *  访问局部变量类型的注释
     * 
     * 
     * @param typeRef
     *            a reference to the annotated type. The sort of this type
     *            reference must be {@link TypeReference#LOCAL_VARIABLE
     *            LOCAL_VARIABLE} or {@link TypeReference#RESOURCE_VARIABLE
     *            RESOURCE_VARIABLE}. See {@link TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param start
     *            the fist instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (inclusive).
     * @param end
     *            the last instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (exclusive). This
     *            array must have the same size as the 'start' array.
     * @param index
     *            the local variable's index in each range. This array must have
     *            the same size as the 'start' array.
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
            TypePath typePath, Label[] start, Label[] end, int[] index,
            String desc, boolean visible) {
		/* SPRING PATCH: REMOVED FOR COMPATIBILITY WITH CGLIB 3.1
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
		/* <p>
		/*  if(api <OpcodesASM5){throw new RuntimeException(); }
		/* 
        */
        if (mv != null) {
            return mv.visitLocalVariableAnnotation(typeRef, typePath, start,
                    end, index, desc, visible);
        }
        return null;
    }

    /**
     * Visits a line number declaration.
     * 
     * <p>
     *  访问行号声明
     * 
     * 
     * @param line
     *            a line number. This number refers to the source file from
     *            which the class was compiled.
     * @param start
     *            the first instruction corresponding to this line number.
     * @throws IllegalArgumentException
     *             if <tt>start</tt> has not already been visited by this
     *             visitor (by the {@link #visitLabel visitLabel} method).
     */
    public void visitLineNumber(int line, Label start) {
        if (mv != null) {
            mv.visitLineNumber(line, start);
        }
    }

    /**
     * Visits the maximum stack size and the maximum number of local variables
     * of the method.
     * 
     * <p>
     * 访问最大堆栈大小和方法的局部变量的最大数量
     * 
     * 
     * @param maxStack
     *            maximum stack size of the method.
     * @param maxLocals
     *            maximum number of local variables for the method.
     */
    public void visitMaxs(int maxStack, int maxLocals) {
        if (mv != null) {
            mv.visitMaxs(maxStack, maxLocals);
        }
    }

    /**
     * Visits the end of the method. This method, which is the last one to be
     * called, is used to inform the visitor that all the annotations and
     * attributes of the method have been visited.
     * <p>
     *  访问方法的结尾该方法是最后一个被调用的方法,用于通知访问者方法的所有注释和属性已被访问
     */
    public void visitEnd() {
        if (mv != null) {
            mv.visitEnd();
        }
    }
}
