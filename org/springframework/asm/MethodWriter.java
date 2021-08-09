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
 * A {@link MethodVisitor} that generates methods in bytecode form. Each visit
 * method of this class appends the bytecode corresponding to the visited
 * instruction to a byte vector, in the order these methods are called.
 * 
 * <p>
 * 生成字节码形式的方法的{@link MethodVisitor}此类的每个访问方法都将对应于被访问指令的字节码追加到字节向量,这些方法称为
 * 
 * 
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
class MethodWriter extends MethodVisitor {

    /**
     * Pseudo access flag used to denote constructors.
     * <p>
     *  用于表示构造函数的伪访问标志
     * 
     */
    static final int ACC_CONSTRUCTOR = 0x80000;

    /**
     * Frame has exactly the same locals as the previous stack map frame and
     * number of stack items is zero.
     * <p>
     *  帧具有与先前堆栈映射帧完全相同的本地,并且堆栈项的数量为零
     * 
     */
    static final int SAME_FRAME = 0; // to 63 (0-3f)

    /**
     * Frame has exactly the same locals as the previous stack map frame and
     * number of stack items is 1
     * <p>
     *  帧具有与先前堆栈映射帧完全相同的本地,并且堆栈项的数量为1
     * 
     */
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64; // to 127 (40-7f)

    /**
     * Reserved for future use
     * <p>
     *  留作将来使用
     * 
     */
    static final int RESERVED = 128;

    /**
     * Frame has exactly the same locals as the previous stack map frame and
     * number of stack items is 1. Offset is bigger then 63;
     * <p>
     *  帧具有与先前堆栈映射帧完全相同的本地,堆栈项的数量为1偏移大于63;
     * 
     */
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247; // f7

    /**
     * Frame where current locals are the same as the locals in the previous
     * frame, except that the k last locals are absent. The value of k is given
     * by the formula 251-frame_type.
     * <p>
     *  除了k个最后一个本地不存在之外,当前本地人员与前一个帧中的本地人员相同的帧。k的值由公式251-frame_type
     * 
     */
    static final int CHOP_FRAME = 248; // to 250 (f8-fA)

    /**
     * Frame has exactly the same locals as the previous stack map frame and
     * number of stack items is zero. Offset is bigger then 63;
     * <p>
     * 帧具有与先前堆栈映射帧完全相同的本地,堆栈项的数量为零偏移大于63;
     * 
     */
    static final int SAME_FRAME_EXTENDED = 251; // fb

    /**
     * Frame where current locals are the same as the locals in the previous
     * frame, except that k additional locals are defined. The value of k is
     * given by the formula frame_type-251.
     * <p>
     *  除了定义了k个额外的本地人之外,当前本地人物与前一帧中的本地人员相同的帧。k的值由公式frame_type-251
     * 
     */
    static final int APPEND_FRAME = 252; // to 254 // fc-fe

    /**
     * Full frame
     * <p>
     *  全画幅
     * 
     */
    static final int FULL_FRAME = 255; // ff

    /**
     * Indicates that the stack map frames must be recomputed from scratch. In
     * this case the maximum stack size and number of local variables is also
     * recomputed from scratch.
     * 
     * <p>
     *  表示堆栈映射帧必须从头重新计算在这种情况下,最大堆栈大小和局部变量的数量也从头重新计算
     * 
     * 
     * @see #compute
     */
    private static final int FRAMES = 0;

    /**
     * Indicates that the maximum stack size and number of local variables must
     * be automatically computed.
     * 
     * <p>
     *  表示必须自动计算最大堆栈大小和局部变量的数量
     * 
     * 
     * @see #compute
     */
    private static final int MAXS = 1;

    /**
     * Indicates that nothing must be automatically computed.
     * 
     * <p>
     *  表示不能自动计算
     * 
     * 
     * @see #compute
     */
    private static final int NOTHING = 2;

    /**
     * The class writer to which this method must be added.
     * <p>
     *  必须添加此方法的类编写器
     * 
     */
    final ClassWriter cw;

    /**
     * Access flags of this method.
     * <p>
     *  访问此方法的标志
     * 
     */
    private int access;

    /**
     * The index of the constant pool item that contains the name of this
     * method.
     * <p>
     * 包含此方法名称的常量池项目的索引
     * 
     */
    private final int name;

    /**
     * The index of the constant pool item that contains the descriptor of this
     * method.
     * <p>
     *  包含此方法描述符的常量池项的索引
     * 
     */
    private final int desc;

    /**
     * The descriptor of this method.
     * <p>
     *  这个方法的描述符
     * 
     */
    private final String descriptor;

    /**
     * The signature of this method.
     * <p>
     *  这种方法的签名
     * 
     */
    String signature;

    /**
     * If not zero, indicates that the code of this method must be copied from
     * the ClassReader associated to this writer in <code>cw.cr</code>. More
     * precisely, this field gives the index of the first byte to copied from
     * <code>cw.cr.b</code>.
     * <p>
     *  如果不为零,表示该方法的代码必须从<code> cwcr </code>中的与该写入器相关联的ClassReader复制。更确切地说,该字段给出了从<code> cwcrb < /代码>
     * 
     */
    int classReaderOffset;

    /**
     * If not zero, indicates that the code of this method must be copied from
     * the ClassReader associated to this writer in <code>cw.cr</code>. More
     * precisely, this field gives the number of bytes to copied from
     * <code>cw.cr.b</code>.
     * <p>
     *  如果不为零,则表示该方法的代码必须从<code> cwcr </code>中的与该写入程序相关联的ClassReader复制。
     * 更准确地说,该字段给出从<code> cwcrb </code复制的字节数>。
     * 
     */
    int classReaderLength;

    /**
     * Number of exceptions that can be thrown by this method.
     * <p>
     *  可以通过此方法抛出的异常数
     * 
     */
    int exceptionCount;

    /**
     * The exceptions that can be thrown by this method. More precisely, this
     * array contains the indexes of the constant pool items that contain the
     * internal names of these exception classes.
     * <p>
     * 可以通过此方法抛出的异常更准确地说,此数组包含常量池项目的索引,它们包含这些异常类的内部名称
     * 
     */
    int[] exceptions;

    /**
     * The annotation default attribute of this method. May be <tt>null</tt>.
     * <p>
     *  此方法的注释默认属性可以是<tt> null </tt>
     * 
     */
    private ByteVector annd;

    /**
     * The runtime visible annotations of this method. May be <tt>null</tt>.
     * <p>
     *  此方法的运行时可见注释可能为<tt> null </tt>
     * 
     */
    private AnnotationWriter anns;

    /**
     * The runtime invisible annotations of this method. May be <tt>null</tt>.
     * <p>
     *  此方法的运行时不可见注释可能为<tt> null </tt>
     * 
     */
    private AnnotationWriter ianns;

    /**
     * The runtime visible type annotations of this method. May be <tt>null</tt>
     * .
     * <p>
     *  此方法的运行时可见类型注释可能为<tt> null </tt>
     * 
     */
    private AnnotationWriter tanns;

    /**
     * The runtime invisible type annotations of this method. May be
     * <tt>null</tt>.
     * <p>
     *  此方法的运行时不可见类型注释可能为<tt> null </tt>
     * 
     */
    private AnnotationWriter itanns;

    /**
     * The runtime visible parameter annotations of this method. May be
     * <tt>null</tt>.
     * <p>
     *  此方法的运行时可见参数注释可能为<tt> null </tt>
     * 
     */
    private AnnotationWriter[] panns;

    /**
     * The runtime invisible parameter annotations of this method. May be
     * <tt>null</tt>.
     * <p>
     *  此方法的运行时不可见参数注释可能为<tt> null </tt>
     * 
     */
    private AnnotationWriter[] ipanns;

    /**
     * The number of synthetic parameters of this method.
     * <p>
     *  该方法的合成参数数
     * 
     */
    private int synthetics;

    /**
     * The non standard attributes of the method.
     * <p>
     * 该方法的非标准属性
     * 
     */
    private Attribute attrs;

    /**
     * The bytecode of this method.
     * <p>
     *  这个方法的字节码
     * 
     */
    private ByteVector code = new ByteVector();

    /**
     * Maximum stack size of this method.
     * <p>
     *  此方法的最大堆栈大小
     * 
     */
    private int maxStack;

    /**
     * Maximum number of local variables for this method.
     * <p>
     *  此方法的最大局部变量数
     * 
     */
    private int maxLocals;

    /**
     * Number of local variables in the current stack map frame.
     * <p>
     *  当前堆栈映射帧中的局部变量数
     * 
     */
    private int currentLocals;

    /**
     * Number of stack map frames in the StackMapTable attribute.
     * <p>
     *  StackMapTable属性中的堆栈映射帧数
     * 
     */
    private int frameCount;

    /**
     * The StackMapTable attribute.
     * <p>
     *  StackMapTable属性
     * 
     */
    private ByteVector stackMap;

    /**
     * The offset of the last frame that was written in the StackMapTable
     * attribute.
     * <p>
     *  写入StackMapTable属性的最后一帧的偏移量
     * 
     */
    private int previousFrameOffset;

    /**
     * The last frame that was written in the StackMapTable attribute.
     * 
     * <p>
     *  最后一帧写在StackMapTable属性中
     * 
     * 
     * @see #frame
     */
    private int[] previousFrame;

    /**
     * The current stack map frame. The first element contains the offset of the
     * instruction to which the frame corresponds, the second element is the
     * number of locals and the third one is the number of stack elements. The
     * local variables start at index 3 and are followed by the operand stack
     * values. In summary frame[0] = offset, frame[1] = nLocal, frame[2] =
     * nStack, frame[3] = nLocal. All types are encoded as integers, with the
     * same format as the one used in {@link Label}, but limited to BASE types.
     * <p>
     * 当前堆栈映射帧第一个元素包含帧对应的指令的偏移量,第二个元素是本地数量,第三个元素是堆栈元素的数量局部变量从索引3开始,后面跟着操作数堆栈值总结帧[0] =偏移量,帧[1] = nLocal,frame
     *  [2] = nStack,frame [3] = nLocal所有类型都被编码为整数,格式与{@链接标签},但限于BASE类型。
     * 
     */
    private int[] frame;

    /**
     * Number of elements in the exception handler list.
     * <p>
     *  异常处理程序列表中的元素数
     * 
     */
    private int handlerCount;

    /**
     * The first element in the exception handler list.
     * <p>
     *  异常处理程序列表中的第一个元素
     * 
     */
    private Handler firstHandler;

    /**
     * The last element in the exception handler list.
     * <p>
     *  异常处理程序列表中的最后一个元素
     * 
     */
    private Handler lastHandler;

    /**
     * Number of entries in the MethodParameters attribute.
     * <p>
     *  MethodParameters属性中的条目数
     * 
     */
    private int methodParametersCount;

    /**
     * The MethodParameters attribute.
     * <p>
     *  MethodParameters属性
     * 
     */
    private ByteVector methodParameters;

    /**
     * Number of entries in the LocalVariableTable attribute.
     * <p>
     *  LocalVariableTable属性中的条目数
     * 
     */
    private int localVarCount;

    /**
     * The LocalVariableTable attribute.
     * <p>
     * LocalVariableTable属性
     * 
     */
    private ByteVector localVar;

    /**
     * Number of entries in the LocalVariableTypeTable attribute.
     * <p>
     *  LocalVariableTypeTable属性中的条目数
     * 
     */
    private int localVarTypeCount;

    /**
     * The LocalVariableTypeTable attribute.
     * <p>
     *  LocalVariableTypeTable属性
     * 
     */
    private ByteVector localVarType;

    /**
     * Number of entries in the LineNumberTable attribute.
     * <p>
     *  LineNumberTable属性中的条目数
     * 
     */
    private int lineNumberCount;

    /**
     * The LineNumberTable attribute.
     * <p>
     *  LineNumberTable属性
     * 
     */
    private ByteVector lineNumber;

    /**
     * The start offset of the last visited instruction.
     * <p>
     *  最后访问指令的起始偏移量
     * 
     */
    private int lastCodeOffset;

    /**
     * The runtime visible type annotations of the code. May be <tt>null</tt>.
     * <p>
     *  代码的运行时可见类型注释可以是<tt> null </tt>
     * 
     */
    private AnnotationWriter ctanns;

    /**
     * The runtime invisible type annotations of the code. May be <tt>null</tt>.
     * <p>
     *  代码的运行时不可见类型注释可以是<tt> null </tt>
     * 
     */
    private AnnotationWriter ictanns;

    /**
     * The non standard attributes of the method's code.
     * <p>
     *  方法代码的非标准属性
     * 
     */
    private Attribute cattrs;

    /**
     * Indicates if some jump instructions are too small and need to be resized.
     * <p>
     *  指示某些跳转指令是否过小,需要调整大小
     * 
     */
    private boolean resize;

    /**
     * The number of subroutines in this method.
     * <p>
     *  此方法中子程序的数量
     * 
     */
    private int subroutines;

    // ------------------------------------------------------------------------

    /*
     * Fields for the control flow graph analysis algorithm (used to compute the
     * maximum stack size). A control flow graph contains one node per "basic
     * block", and one edge per "jump" from one basic block to another. Each
     * node (i.e., each basic block) is represented by the Label object that
     * corresponds to the first instruction of this basic block. Each node also
     * stores the list of its successors in the graph, as a linked list of Edge
     * objects.
     * <p>
     * 控制流图分析算法的领域(用于计算最大堆栈大小)控制流程图每个"基本块"包含一个节点,每个"跳"从一个基本块到另一个基本块的每个节点(即每个基本块块)由对应于该基本块的第一条指令的Label对象表示。
     * 每个节点还将其后继列表存储在图中,作为Edge对象的链接列表。
     * 
     */

    /**
     * Indicates what must be automatically computed.
     * 
     * <p>
     *  表示必须自动计算什么
     * 
     * 
     * @see #FRAMES
     * @see #MAXS
     * @see #NOTHING
     */
    private final int compute;

    /**
     * A list of labels. This list is the list of basic blocks in the method,
     * i.e. a list of Label objects linked to each other by their
     * {@link Label#successor} field, in the order they are visited by
     * {@link MethodVisitor#visitLabel}, and starting with the first basic
     * block.
     * <p>
     *  标签列表此列表是方法中基本块的列表,即由{@link Label#successor}字段按照{@link MethodVisitor#visitLabel访问的顺序彼此链接的Label对象列表},并
     * 从第一个基本块开始。
     * 
     */
    private Label labels;

    /**
     * The previous basic block.
     * <p>
     *  以前的基本块
     * 
     */
    private Label previousBlock;

    /**
     * The current basic block.
     * <p>
     * 当前的基本块
     * 
     */
    private Label currentBlock;

    /**
     * The (relative) stack size after the last visited instruction. This size
     * is relative to the beginning of the current basic block, i.e., the true
     * stack size after the last visited instruction is equal to the
     * {@link Label#inputStackTop beginStackSize} of the current basic block
     * plus <tt>stackSize</tt>.
     * <p>
     *  最后访问指令之后的(相对)堆栈大小此大小相对于当前基本块的开始,即最后访问指令之后的真实堆栈大小等于当前的{@link Label#inputStackTop beginStackSize}基本块加<tt>
     *  stackSize </tt>。
     * 
     */
    private int stackSize;

    /**
     * The (relative) maximum stack size after the last visited instruction.
     * This size is relative to the beginning of the current basic block, i.e.,
     * the true maximum stack size after the last visited instruction is equal
     * to the {@link Label#inputStackTop beginStackSize} of the current basic
     * block plus <tt>stackSize</tt>.
     * <p>
     *  最后访问指令之后的(相对)最大堆栈大小此大小相对于当前基本块的开始,即最后访问指令之后的真实最大堆栈大小等于{@link Label#inputStackTop beginStackSize}当前基本
     * 块加<tt> stackSize </tt>。
     * 
     */
    private int maxStackSize;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new {@link MethodWriter}.
     * 
     * <p>
     *  构造一个新的{@link MethodWriter}
     * 
     * 
     * @param cw
     *            the class writer in which the method must be added.
     * @param access
     *            the method's access flags (see {@link Opcodes}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type}).
     * @param signature
     *            the method's signature. May be <tt>null</tt>.
     * @param exceptions
     *            the internal names of the method's exceptions. May be
     *            <tt>null</tt>.
     * @param computeMaxs
     *            <tt>true</tt> if the maximum stack size and number of local
     *            variables must be automatically computed.
     * @param computeFrames
     *            <tt>true</tt> if the stack map tables must be recomputed from
     *            scratch.
     */
    MethodWriter(final ClassWriter cw, final int access, final String name,
            final String desc, final String signature,
            final String[] exceptions, final boolean computeMaxs,
            final boolean computeFrames) {
        super(Opcodes.ASM5);
        if (cw.firstMethod == null) {
            cw.firstMethod = this;
        } else {
            cw.lastMethod.mv = this;
        }
        cw.lastMethod = this;
        this.cw = cw;
        this.access = access;
        if ("<init>".equals(name)) {
            this.access |= ACC_CONSTRUCTOR;
        }
        this.name = cw.newUTF8(name);
        this.desc = cw.newUTF8(desc);
        this.descriptor = desc;
        if (ClassReader.SIGNATURES) {
            this.signature = signature;
        }
        if (exceptions != null && exceptions.length > 0) {
            exceptionCount = exceptions.length;
            this.exceptions = new int[exceptionCount];
            for (int i = 0; i < exceptionCount; ++i) {
                this.exceptions[i] = cw.newClass(exceptions[i]);
            }
        }
        this.compute = computeFrames ? FRAMES : (computeMaxs ? MAXS : NOTHING);
        if (computeMaxs || computeFrames) {
            // updates maxLocals
            int size = Type.getArgumentsAndReturnSizes(descriptor) >> 2;
            if ((access & Opcodes.ACC_STATIC) != 0) {
                --size;
            }
            maxLocals = size;
            currentLocals = size;
            // creates and visits the label for the first basic block
            labels = new Label();
            labels.status |= Label.PUSHED;
            visitLabel(labels);
        }
    }

    // ------------------------------------------------------------------------
    // Implementation of the MethodVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public void visitParameter(String name, int access) {
        if (methodParameters == null) {
            methodParameters = new ByteVector();
        }
        ++methodParametersCount;
        methodParameters.putShort((name == null) ? 0 : cw.newUTF8(name))
                .putShort(access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        annd = new ByteVector();
        return new AnnotationWriter(cw, false, annd, null, 0);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
            final boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv, 2);
        if (visible) {
            aw.next = anns;
            anns = aw;
        } else {
            aw.next = ianns;
            ianns = aw;
        }
        return aw;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef,
            final TypePath typePath, final String desc, final boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write target_type and target_info
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2);
        if (visible) {
            aw.next = tanns;
            tanns = aw;
        } else {
            aw.next = itanns;
            itanns = aw;
        }
        return aw;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter,
            final String desc, final boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        if ("Ljava/lang/Synthetic;".equals(desc)) {
            // workaround for a bug in javac with synthetic parameters
            // see ClassReader.readParameterAnnotations
            synthetics = Math.max(synthetics, parameter + 1);
            return new AnnotationWriter(cw, false, bv, null, 0);
        }
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv, 2);
        if (visible) {
            if (panns == null) {
                panns = new AnnotationWriter[Type.getArgumentTypes(descriptor).length];
            }
            aw.next = panns[parameter];
            panns[parameter] = aw;
        } else {
            if (ipanns == null) {
                ipanns = new AnnotationWriter[Type.getArgumentTypes(descriptor).length];
            }
            aw.next = ipanns[parameter];
            ipanns[parameter] = aw;
        }
        return aw;
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        if (attr.isCodeAttribute()) {
            attr.next = cattrs;
            cattrs = attr;
        } else {
            attr.next = attrs;
            attrs = attr;
        }
    }

    @Override
    public void visitCode() {
    }

    @Override
    public void visitFrame(final int type, final int nLocal,
            final Object[] local, final int nStack, final Object[] stack) {
        if (!ClassReader.FRAMES || compute == FRAMES) {
            return;
        }

        if (type == Opcodes.F_NEW) {
            if (previousFrame == null) {
                visitImplicitFirstFrame();
            }
            currentLocals = nLocal;
            int frameIndex = startFrame(code.length, nLocal, nStack);
            for (int i = 0; i < nLocal; ++i) {
                if (local[i] instanceof String) {
                    frame[frameIndex++] = Frame.OBJECT
                            | cw.addType((String) local[i]);
                } else if (local[i] instanceof Integer) {
                    frame[frameIndex++] = ((Integer) local[i]).intValue();
                } else {
                    frame[frameIndex++] = Frame.UNINITIALIZED
                            | cw.addUninitializedType("",
                                    ((Label) local[i]).position);
                }
            }
            for (int i = 0; i < nStack; ++i) {
                if (stack[i] instanceof String) {
                    frame[frameIndex++] = Frame.OBJECT
                            | cw.addType((String) stack[i]);
                } else if (stack[i] instanceof Integer) {
                    frame[frameIndex++] = ((Integer) stack[i]).intValue();
                } else {
                    frame[frameIndex++] = Frame.UNINITIALIZED
                            | cw.addUninitializedType("",
                                    ((Label) stack[i]).position);
                }
            }
            endFrame();
        } else {
            int delta;
            if (stackMap == null) {
                stackMap = new ByteVector();
                delta = code.length;
            } else {
                delta = code.length - previousFrameOffset - 1;
                if (delta < 0) {
                    if (type == Opcodes.F_SAME) {
                        return;
                    } else {
                        throw new IllegalStateException();
                    }
                }
            }

            switch (type) {
            case Opcodes.F_FULL:
                currentLocals = nLocal;
                stackMap.putByte(FULL_FRAME).putShort(delta).putShort(nLocal);
                for (int i = 0; i < nLocal; ++i) {
                    writeFrameType(local[i]);
                }
                stackMap.putShort(nStack);
                for (int i = 0; i < nStack; ++i) {
                    writeFrameType(stack[i]);
                }
                break;
            case Opcodes.F_APPEND:
                currentLocals += nLocal;
                stackMap.putByte(SAME_FRAME_EXTENDED + nLocal).putShort(delta);
                for (int i = 0; i < nLocal; ++i) {
                    writeFrameType(local[i]);
                }
                break;
            case Opcodes.F_CHOP:
                currentLocals -= nLocal;
                stackMap.putByte(SAME_FRAME_EXTENDED - nLocal).putShort(delta);
                break;
            case Opcodes.F_SAME:
                if (delta < 64) {
                    stackMap.putByte(delta);
                } else {
                    stackMap.putByte(SAME_FRAME_EXTENDED).putShort(delta);
                }
                break;
            case Opcodes.F_SAME1:
                if (delta < 64) {
                    stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME + delta);
                } else {
                    stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED)
                            .putShort(delta);
                }
                writeFrameType(stack[0]);
                break;
            }

            previousFrameOffset = code.length;
            ++frameCount;
        }

        maxStack = Math.max(maxStack, nStack);
        maxLocals = Math.max(maxLocals, currentLocals);
    }

    @Override
    public void visitInsn(final int opcode) {
        lastCodeOffset = code.length;
        // adds the instruction to the bytecode of the method
        code.putByte(opcode);
        // update currentBlock
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, 0, null, null);
            } else {
                // updates current and max stack sizes
                int size = stackSize + Frame.SIZE[opcode];
                if (size > maxStackSize) {
                    maxStackSize = size;
                }
                stackSize = size;
            }
            // if opcode == ATHROW or xRETURN, ends current block (no successor)
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                    || opcode == Opcodes.ATHROW) {
                noSuccessor();
            }
        }
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        lastCodeOffset = code.length;
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, operand, null, null);
            } else if (opcode != Opcodes.NEWARRAY) {
                // updates current and max stack sizes only for NEWARRAY
                // (stack size variation = 0 for BIPUSH or SIPUSH)
                int size = stackSize + 1;
                if (size > maxStackSize) {
                    maxStackSize = size;
                }
                stackSize = size;
            }
        }
        // adds the instruction to the bytecode of the method
        if (opcode == Opcodes.SIPUSH) {
            code.put12(opcode, operand);
        } else { // BIPUSH or NEWARRAY
            code.put11(opcode, operand);
        }
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        lastCodeOffset = code.length;
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, var, null, null);
            } else {
                // updates current and max stack sizes
                if (opcode == Opcodes.RET) {
                    // no stack change, but end of current block (no successor)
                    currentBlock.status |= Label.RET;
                    // save 'stackSize' here for future use
                    // (see {@link #findSubroutineSuccessors})
                    currentBlock.inputStackTop = stackSize;
                    noSuccessor();
                } else { // xLOAD or xSTORE
                    int size = stackSize + Frame.SIZE[opcode];
                    if (size > maxStackSize) {
                        maxStackSize = size;
                    }
                    stackSize = size;
                }
            }
        }
        if (compute != NOTHING) {
            // updates max locals
            int n;
            if (opcode == Opcodes.LLOAD || opcode == Opcodes.DLOAD
                    || opcode == Opcodes.LSTORE || opcode == Opcodes.DSTORE) {
                n = var + 2;
            } else {
                n = var + 1;
            }
            if (n > maxLocals) {
                maxLocals = n;
            }
        }
        // adds the instruction to the bytecode of the method
        if (var < 4 && opcode != Opcodes.RET) {
            int opt;
            if (opcode < Opcodes.ISTORE) {
                /* ILOAD_0 */
                opt = 26 + ((opcode - Opcodes.ILOAD) << 2) + var;
            } else {
                /* ISTORE_0 */
                opt = 59 + ((opcode - Opcodes.ISTORE) << 2) + var;
            }
            code.putByte(opt);
        } else if (var >= 256) {
            code.putByte(196 /* WIDE */).put12(opcode, var);
        } else {
            code.put11(opcode, var);
        }
        if (opcode >= Opcodes.ISTORE && compute == FRAMES && handlerCount > 0) {
            visitLabel(new Label());
        }
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        lastCodeOffset = code.length;
        Item i = cw.newClassItem(type);
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, code.length, cw, i);
            } else if (opcode == Opcodes.NEW) {
                // updates current and max stack sizes only if opcode == NEW
                // (no stack change for ANEWARRAY, CHECKCAST, INSTANCEOF)
                int size = stackSize + 1;
                if (size > maxStackSize) {
                    maxStackSize = size;
                }
                stackSize = size;
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(opcode, i.index);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner,
            final String name, final String desc) {
        lastCodeOffset = code.length;
        Item i = cw.newFieldItem(owner, name, desc);
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, 0, cw, i);
            } else {
                int size;
                // computes the stack size variation
                char c = desc.charAt(0);
                switch (opcode) {
                case Opcodes.GETSTATIC:
                    size = stackSize + (c == 'D' || c == 'J' ? 2 : 1);
                    break;
                case Opcodes.PUTSTATIC:
                    size = stackSize + (c == 'D' || c == 'J' ? -2 : -1);
                    break;
                case Opcodes.GETFIELD:
                    size = stackSize + (c == 'D' || c == 'J' ? 1 : 0);
                    break;
                // case Constants.PUTFIELD:
                default:
                    size = stackSize + (c == 'D' || c == 'J' ? -3 : -2);
                    break;
                }
                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size;
                }
                stackSize = size;
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(opcode, i.index);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
            final String name, final String desc, final boolean itf) {
        lastCodeOffset = code.length;
        Item i = cw.newMethodItem(owner, name, desc, itf);
        int argSize = i.intVal;
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, 0, cw, i);
            } else {
                /*
                 * computes the stack size variation. In order not to recompute
                 * several times this variation for the same Item, we use the
                 * intVal field of this item to store this variation, once it
                 * has been computed. More precisely this intVal field stores
                 * the sizes of the arguments and of the return value
                 * corresponding to desc.
                 * <p>
                 * } else {codeput11(opcode,var); } if(opcode> = OpcodesISTORE && compute == FRAMES && handlerCount> 0){visitLabel(new Label()); }
                 * }。
                 * 
                 *  @Override public void visitTypeInsn(final int opcode,final String type){lastCodeOffset = codelength; Item i = cwnewClassItem(type); // Label currentBlock = thiscurrentBlock; if(currentBlock！= null){if(compute == FRAMES){currentBlockframeexecute(opcode,codelength,cw,i); }
                 *  else if(opcode == OpcodesNEW){//仅当opcode == NEW更新当前和最大堆栈大小//(ANEWARRAY,CHECKCAST,INSTANCEOF没有堆栈更改)int size = stackSize + 1; if(size> maxStackSize){maxStackSize = size; }
                 *  stackSize = size; }} //将指令添加到方法codeput12(opcode,iindex)的字节码中; }。
                 * 
                 * @Override public void visitFieldInsn(final int opcode,final String owner,final String name,final Stri
                 * ng desc){lastCodeOffset = codelength;项目i = cwnewFieldItem(owner,name,desc); // Label currentBlock = thiscurrentBlock; if(currentBlock！= null){if(compute == FRAMES){currentBlockframeexecute(opcode,0,cw,i); }
                 *  else {int size; //计算堆栈大小变化char c = desccharAt(0); switch(opcode){case OpcodesGETSTATIC：size = stackSize +(c =='D'|| c =='J'?2：1);打破; case OpcodesPUTSTATIC：size = stackSize +(c =='D'|| c =='J'?-2：-1);打破; case OpcodesGETFIELD：size = stackSize +(c =='D'|| c =='J'?1：0);打破; // case常量PUTFIELD：default：size = stackSize +(c =='D'|| c =='J'?-3：-2);打破; }
                 *  //更新当前和最大堆栈大小if(size> maxStackSize){maxStackSize = size; } stackSize = size; }} //将指令添加到方法codeput12(
                 * opcode,iindex)的字节码中; }。
                 * 
                 * @Override public void visitMethodInsn(final int opcode,final String owner,final String name,final Str
                 * ing desc,final boolean itf){lastCodeOffset = codelength; Item i = cwnewMethodItem(owner,name,desc,itf); int argSize = iintVal; // Label currentBlock = thiscurrentBlock; if(currentBlock！= null){if(compute == FRAMES){currentBlockframeexecute(opcode,0,cw,i); }
                 *  else {/ *计算堆栈大小变化为了不对同一个项目重新计算这个变体,我们使用这个项目的intVal字段来存储这个变体,一旦计算了更精确的这个intVal字段存储大小的参数和返回值对应于desc。
                 * 
                 */
                if (argSize == 0) {
                    // the above sizes have not been computed yet,
                    // so we compute them...
                    argSize = Type.getArgumentsAndReturnSizes(desc);
                    // ... and we save them in order
                    // not to recompute them in the future
                    i.intVal = argSize;
                }
                int size;
                if (opcode == Opcodes.INVOKESTATIC) {
                    size = stackSize - (argSize >> 2) + (argSize & 0x03) + 1;
                } else {
                    size = stackSize - (argSize >> 2) + (argSize & 0x03);
                }
                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size;
                }
                stackSize = size;
            }
        }
        // adds the instruction to the bytecode of the method
        if (opcode == Opcodes.INVOKEINTERFACE) {
            if (argSize == 0) {
                argSize = Type.getArgumentsAndReturnSizes(desc);
                i.intVal = argSize;
            }
            code.put12(Opcodes.INVOKEINTERFACE, i.index).put11(argSize >> 2, 0);
        } else {
            code.put12(opcode, i.index);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc,
            final Handle bsm, final Object... bsmArgs) {
        lastCodeOffset = code.length;
        Item i = cw.newInvokeDynamicItem(name, desc, bsm, bsmArgs);
        int argSize = i.intVal;
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.INVOKEDYNAMIC, 0, cw, i);
            } else {
                /*
                 * computes the stack size variation. In order not to recompute
                 * several times this variation for the same Item, we use the
                 * intVal field of this item to store this variation, once it
                 * has been computed. More precisely this intVal field stores
                 * the sizes of the arguments and of the return value
                 * corresponding to desc.
                 * <p>
                 * 计算堆栈大小变化为了不对同一项目重新计算这个变体,我们使用该项目的intVal字段来存储此变体,一旦计算出更精确的这个intVal字段存储参数的大小和返回值对应于desc
                 * 
                 */
                if (argSize == 0) {
                    // the above sizes have not been computed yet,
                    // so we compute them...
                    argSize = Type.getArgumentsAndReturnSizes(desc);
                    // ... and we save them in order
                    // not to recompute them in the future
                    i.intVal = argSize;
                }
                int size = stackSize - (argSize >> 2) + (argSize & 0x03) + 1;

                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size;
                }
                stackSize = size;
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(Opcodes.INVOKEDYNAMIC, i.index);
        code.putShort(0);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        lastCodeOffset = code.length;
        Label nextInsn = null;
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(opcode, 0, null, null);
                // 'label' is the target of a jump instruction
                label.getFirst().status |= Label.TARGET;
                // adds 'label' as a successor of this basic block
                addSuccessor(Edge.NORMAL, label);
                if (opcode != Opcodes.GOTO) {
                    // creates a Label for the next basic block
                    nextInsn = new Label();
                }
            } else {
                if (opcode == Opcodes.JSR) {
                    if ((label.status & Label.SUBROUTINE) == 0) {
                        label.status |= Label.SUBROUTINE;
                        ++subroutines;
                    }
                    currentBlock.status |= Label.JSR;
                    addSuccessor(stackSize + 1, label);
                    // creates a Label for the next basic block
                    nextInsn = new Label();
                    /*
                     * note that, by construction in this method, a JSR block
                     * has at least two successors in the control flow graph:
                     * the first one leads the next instruction after the JSR,
                     * while the second one leads to the JSR target.
                     * <p>
                     *  请注意,通过这种方法的构造,JSR块在控制流程图中至少有两个后继：第一个引导JSR之后的下一个指令,而第二个引导到JSR目标
                     * 
                     */
                } else {
                    // updates current stack size (max stack size unchanged
                    // because stack size variation always negative in this
                    // case)
                    stackSize += Frame.SIZE[opcode];
                    addSuccessor(stackSize, label);
                }
            }
        }
        // adds the instruction to the bytecode of the method
        if ((label.status & Label.RESOLVED) != 0
                && label.position - code.length < Short.MIN_VALUE) {
            /*
             * case of a backward jump with an offset < -32768. In this case we
             * automatically replace GOTO with GOTO_W, JSR with JSR_W and IFxxx
             * <l> with IFNOTxxx <l'> GOTO_W <l>, where IFNOTxxx is the
             * "opposite" opcode of IFxxx (i.e., IFNE for IFEQ) and where <l'>
             * designates the instruction just after the GOTO_W.
             * <p>
             * 在这种情况下,我们会自动将GOTO与GOTO_W,JSR与JSR_W和IFxxx <1>替换为IFNOTxxx <1> GOTO_W <l>,其中IFNOTxxx是IFxxx的"相反"操作码(即IFEQ
             * 的IFNE),其中<l'>表示GOTO_W之后的指令。
             * 
             */
            if (opcode == Opcodes.GOTO) {
                code.putByte(200); // GOTO_W
            } else if (opcode == Opcodes.JSR) {
                code.putByte(201); // JSR_W
            } else {
                // if the IF instruction is transformed into IFNOT GOTO_W the
                // next instruction becomes the target of the IFNOT instruction
                if (nextInsn != null) {
                    nextInsn.status |= Label.TARGET;
                }
                code.putByte(opcode <= 166 ? ((opcode + 1) ^ 1) - 1
                        : opcode ^ 1);
                code.putShort(8); // jump offset
                code.putByte(200); // GOTO_W
            }
            label.put(this, code, code.length - 1, true);
        } else {
            /*
             * case of a backward jump with an offset >= -32768, or of a forward
             * jump with, of course, an unknown offset. In these cases we store
             * the offset in 2 bytes (which will be increased in
             * resizeInstructions, if needed).
             * <p>
             *  具有偏移量> = -32768的反向跳转的情况,或当然具有未知偏移量的前向跳转在这些情况下,我们将偏移量存储在2个字节中(如果需要,将在resizeInstructions中增加)
             * 
             */
            code.putByte(opcode);
            label.put(this, code, code.length - 1, false);
        }
        if (currentBlock != null) {
            if (nextInsn != null) {
                // if the jump instruction is not a GOTO, the next instruction
                // is also a successor of this instruction. Calling visitLabel
                // adds the label of this next instruction as a successor of the
                // current block, and starts a new basic block
                visitLabel(nextInsn);
            }
            if (opcode == Opcodes.GOTO) {
                noSuccessor();
            }
        }
    }

    @Override
    public void visitLabel(final Label label) {
        // resolves previous forward references to label, if any
        resize |= label.resolve(this, code.length, code.data);
        // updates currentBlock
        if ((label.status & Label.DEBUG) != 0) {
            return;
        }
        if (compute == FRAMES) {
            if (currentBlock != null) {
                if (label.position == currentBlock.position) {
                    // successive labels, do not start a new basic block
                    currentBlock.status |= (label.status & Label.TARGET);
                    label.frame = currentBlock.frame;
                    return;
                }
                // ends current block (with one new successor)
                addSuccessor(Edge.NORMAL, label);
            }
            // begins a new current block
            currentBlock = label;
            if (label.frame == null) {
                label.frame = new Frame();
                label.frame.owner = label;
            }
            // updates the basic block list
            if (previousBlock != null) {
                if (label.position == previousBlock.position) {
                    previousBlock.status |= (label.status & Label.TARGET);
                    label.frame = previousBlock.frame;
                    currentBlock = previousBlock;
                    return;
                }
                previousBlock.successor = label;
            }
            previousBlock = label;
        } else if (compute == MAXS) {
            if (currentBlock != null) {
                // ends current block (with one new successor)
                currentBlock.outputStackMax = maxStackSize;
                addSuccessor(stackSize, label);
            }
            // begins a new current block
            currentBlock = label;
            // resets the relative current and max stack sizes
            stackSize = 0;
            maxStackSize = 0;
            // updates the basic block list
            if (previousBlock != null) {
                previousBlock.successor = label;
            }
            previousBlock = label;
        }
    }

    @Override
    public void visitLdcInsn(final Object cst) {
        lastCodeOffset = code.length;
        Item i = cw.newConstItem(cst);
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.LDC, 0, cw, i);
            } else {
                int size;
                // computes the stack size variation
                if (i.type == ClassWriter.LONG || i.type == ClassWriter.DOUBLE) {
                    size = stackSize + 2;
                } else {
                    size = stackSize + 1;
                }
                // updates current and max stack sizes
                if (size > maxStackSize) {
                    maxStackSize = size;
                }
                stackSize = size;
            }
        }
        // adds the instruction to the bytecode of the method
        int index = i.index;
        if (i.type == ClassWriter.LONG || i.type == ClassWriter.DOUBLE) {
            code.put12(20 /* LDC2_W */, index);
        } else if (index >= 256) {
            code.put12(20 /* <p>
            code.put12(20 /*  } else if(index> = 256){
            code.put12(20 /* 
            code.put12(20 /* 
            code.put12(19 /* LDC_W */, index);
        } else {
            code.put11(Opcodes.LDC, index);
        }
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
        lastCodeOffset = code.length;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.IINC, var, null, null);
            }
        }
        if (compute != NOTHING) {
            // updates max locals
            int n = var + 1;
            if (n > maxLocals) {
                maxLocals = n;
            }
        }
        // adds the instruction to the bytecode of the method
        if ((var > 255) || (increment > 127) || (increment < -128)) {
            code.put12(19 /* <p>
            code.put12(19 /*  } else {codeput11(OpcodesLDC,index); }}
            code.put12(19 /* 
            code.put12(19 /* @Override public void visitIincInsn(final int var,final int increment){lastCodeOffset = codelength; if(currentBlock！= null){if(compute == FRAMES){currentBlockframeexecute(OpcodesIINC,var,null,null); }
            code.put12(19 /* } if(compute！= NOTHING){//更新max locals int n = var + 1; if(n> maxLocals){maxLocals = n; }} //将指令添加到方法
            code.put12(19 /* 的字节码if((var> 255)||(increment> 127)||(increment <-128)){。
            code.put12(19 /* 
            code.put12(19 /* 
            code.putByte(196 /* WIDE */).put12(Opcodes.IINC, var)
                    .putShort(increment);
        } else {
            code.putByte(Opcodes.IINC).put11(var, increment);
        }
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max,
            final Label dflt, final Label... labels) {
        lastCodeOffset = code.length;
        // adds the instruction to the bytecode of the method
        int source = code.length;
        code.putByte(Opcodes.TABLESWITCH);
        code.putByteArray(null, 0, (4 - code.length % 4) % 4);
        dflt.put(this, code, source, true);
        code.putInt(min).putInt(max);
        for (int i = 0; i < labels.length; ++i) {
            labels[i].put(this, code, source, true);
        }
        // updates currentBlock
        visitSwitchInsn(dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
            final Label[] labels) {
        lastCodeOffset = code.length;
        // adds the instruction to the bytecode of the method
        int source = code.length;
        code.putByte(Opcodes.LOOKUPSWITCH);
        code.putByteArray(null, 0, (4 - code.length % 4) % 4);
        dflt.put(this, code, source, true);
        code.putInt(labels.length);
        for (int i = 0; i < labels.length; ++i) {
            code.putInt(keys[i]);
            labels[i].put(this, code, source, true);
        }
        // updates currentBlock
        visitSwitchInsn(dflt, labels);
    }

    private void visitSwitchInsn(final Label dflt, final Label[] labels) {
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.LOOKUPSWITCH, 0, null, null);
                // adds current block successors
                addSuccessor(Edge.NORMAL, dflt);
                dflt.getFirst().status |= Label.TARGET;
                for (int i = 0; i < labels.length; ++i) {
                    addSuccessor(Edge.NORMAL, labels[i]);
                    labels[i].getFirst().status |= Label.TARGET;
                }
            } else {
                // updates current stack size (max stack size unchanged)
                --stackSize;
                // adds current block successors
                addSuccessor(stackSize, dflt);
                for (int i = 0; i < labels.length; ++i) {
                    addSuccessor(stackSize, labels[i]);
                }
            }
            // ends current block
            noSuccessor();
        }
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        lastCodeOffset = code.length;
        Item i = cw.newClassItem(desc);
        // Label currentBlock = this.currentBlock;
        if (currentBlock != null) {
            if (compute == FRAMES) {
                currentBlock.frame.execute(Opcodes.MULTIANEWARRAY, dims, cw, i);
            } else {
                // updates current stack size (max stack size unchanged because
                // stack size variation always negative or null)
                stackSize += 1 - dims;
            }
        }
        // adds the instruction to the bytecode of the method
        code.put12(Opcodes.MULTIANEWARRAY, i.index).putByte(dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write target_type and target_info
        typeRef = (typeRef & 0xFF0000FF) | (lastCodeOffset << 8);
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2);
        if (visible) {
            aw.next = ctanns;
            ctanns = aw;
        } else {
            aw.next = ictanns;
            ictanns = aw;
        }
        return aw;
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end,
            final Label handler, final String type) {
        ++handlerCount;
        Handler h = new Handler();
        h.start = start;
        h.end = end;
        h.handler = handler;
        h.desc = type;
        h.type = type != null ? cw.newClass(type) : 0;
        if (lastHandler == null) {
            firstHandler = h;
        } else {
            lastHandler.next = h;
        }
        lastHandler = h;
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write target_type and target_info
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2);
        if (visible) {
            aw.next = ctanns;
            ctanns = aw;
        } else {
            aw.next = ictanns;
            ictanns = aw;
        }
        return aw;
    }

    @Override
    public void visitLocalVariable(final String name, final String desc,
            final String signature, final Label start, final Label end,
            final int index) {
        if (signature != null) {
            if (localVarType == null) {
                localVarType = new ByteVector();
            }
            ++localVarTypeCount;
            localVarType.putShort(start.position)
                    .putShort(end.position - start.position)
                    .putShort(cw.newUTF8(name)).putShort(cw.newUTF8(signature))
                    .putShort(index);
        }
        if (localVar == null) {
            localVar = new ByteVector();
        }
        ++localVarCount;
        localVar.putShort(start.position)
                .putShort(end.position - start.position)
                .putShort(cw.newUTF8(name)).putShort(cw.newUTF8(desc))
                .putShort(index);
        if (compute != NOTHING) {
            // updates max locals
            char c = desc.charAt(0);
            int n = index + (c == 'J' || c == 'D' ? 2 : 1);
            if (n > maxLocals) {
                maxLocals = n;
            }
        }
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
            TypePath typePath, Label[] start, Label[] end, int[] index,
            String desc, boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write target_type and target_info
        bv.putByte(typeRef >>> 24).putShort(start.length);
        for (int i = 0; i < start.length; ++i) {
            bv.putShort(start[i].position)
                    .putShort(end[i].position - start[i].position)
                    .putShort(index[i]);
        }
        if (typePath == null) {
            bv.putByte(0);
        } else {
            int length = typePath.b[typePath.offset] * 2 + 1;
            bv.putByteArray(typePath.b, typePath.offset, length);
        }
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2);
        if (visible) {
            aw.next = ctanns;
            ctanns = aw;
        } else {
            aw.next = ictanns;
            ictanns = aw;
        }
        return aw;
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        if (lineNumber == null) {
            lineNumber = new ByteVector();
        }
        ++lineNumberCount;
        lineNumber.putShort(start.position);
        lineNumber.putShort(line);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        if (resize) {
            // replaces the temporary jump opcodes introduced by Label.resolve.
            if (ClassReader.RESIZE) {
                resizeInstructions();
            } else {
                throw new RuntimeException("Method code too large!");
            }
        }
        if (ClassReader.FRAMES && compute == FRAMES) {
            // completes the control flow graph with exception handler blocks
            Handler handler = firstHandler;
            while (handler != null) {
                Label l = handler.start.getFirst();
                Label h = handler.handler.getFirst();
                Label e = handler.end.getFirst();
                // computes the kind of the edges to 'h'
                String t = handler.desc == null ? "java/lang/Throwable"
                        : handler.desc;
                int kind = Frame.OBJECT | cw.addType(t);
                // h is an exception handler
                h.status |= Label.TARGET;
                // adds 'h' as a successor of labels between 'start' and 'end'
                while (l != e) {
                    // creates an edge to 'h'
                    Edge b = new Edge();
                    b.info = kind;
                    b.successor = h;
                    // adds it to the successors of 'l'
                    b.next = l.successors;
                    l.successors = b;
                    // goes to the next label
                    l = l.successor;
                }
                handler = handler.next;
            }

            // creates and visits the first (implicit) frame
            Frame f = labels.frame;
            Type[] args = Type.getArgumentTypes(descriptor);
            f.initInputFrame(cw, access, args, this.maxLocals);
            visitFrame(f);

            /*
             * fix point algorithm: mark the first basic block as 'changed'
             * (i.e. put it in the 'changed' list) and, while there are changed
             * basic blocks, choose one, mark it as unchanged, and update its
             * successors (which can be changed in the process).
             * <p>
             *  putShort(增量); } else {codeputByte(OpcodesIINC)put11(var,increment); }}
             * 
             * @Override public void visitTableSwitchInsn(final int min,final int max,final Label dflt,final Label l
             * abels){lastCodeOffset = codelength; //将指令添加到方法的字节码int source = codelength; codeputByte(OpcodesTABLESWITCH); codeputByteArray(null,0,(4  -  codelength％4)％4); dfltput(this,code,source,true); codeputInt(分钟)putInt(最大); for(int i = 0; i <labelslength; ++ i){labels [i] put(this,code,source,true); } //更新currentBlock visitSwitchInsn(dflt,labels); }。
             * 
             * @Override public void visitLookupSwitchInsn(final Label dflt,final int [] keys,final Label [] labels)
             * {lastCodeOffset = codelength; //将指令添加到方法的字节码int source = codelength; codeputByte(OpcodesLOOKUPSWITCH); codeputByteArray(null,0,(4  -  codelength％4)％4); dfltput(this,code,source,true); codeputInt(labelslength); for(int i = 0; i <labelslength; ++ i){codeputInt(keys [i]);标签[i] put(this,code,source,true); } //更新currentBlock visitSwitchInsn(dflt,labels); }。
             * 
             * private void visitSwitchInsn(final Label dflt,final Label [] labels){// Label currentBlock = thiscurrentBlock; if(currentBlock！= null){if(compute == FRAMES){currentBlockframeexecute(OpcodesLOOKUPSWITCH,0,null,null); //添加当前块后继addSuccessor(EdgeNORMAL,dflt); dfltgetFirst()status | = LabelTARGET; for(int i = 0; i <labelslength; ++ i){addSuccessor(EdgeNORMAL,labels [i]); labels [i] getFirst()status | = LabelTARGET; }} else {//更新当前堆栈大小(最大堆栈大小不变)--stackSize; //添加当前块后继addSuccessor(stackSize,dflt); for(int i = 0; i <labelslength; ++ i){addSuccessor(stackSize,labels [i]); }} //结束当前块noSuccessor(); }}。
             * 
             * @Override public void visitMultiANewArrayInsn(final String desc,final int dims){lastCodeOffset = codelength; Item i = cwnewClassItem(desc); // Label currentBlock = thiscurrentBlock; if(currentBlock！= null){if(compute == FRAMES){currentBlockframeexecute(OpcodesMULTIANEWARRAY,dims,cw,i); }
             *  else {//更新当前堆栈大小(最大堆栈大小不变,因为//堆栈大小变化总是为负数或null)stackSize + = 1  -  dims; }} //将指令添加到方法codeput12(Opco
             * desMULTIANEWARRAY,iindex)putByte(dims)的字节码中; }。
             * 
             * @Override public AnnotationVisitor visitInsnAnnotation(int typeRef,TypePath typePath,String desc,bool
             * ean visible){if(！ClassReaderANNOTATIONS){return null; } ByteVector bv = new ByteVector(); //写入target_
             * type和target_info typeRef =(typeRef&0xFF0000FF)| (lastCodeOffset << 8); AnnotationWriterputTarget(typeRef,typePath,bv); //写入类型,并为值计数bvputShort(cwnewUTF8(desc))putShort(0); AnnotationWriter aw = new AnnotationWriter(cw,true,bv,bv,bvlength  -  2); if(visible){awnext = ctanns; ctanns = aw } else {awnext = ictanns; ictanns = aw } return aw; }。
             * 
             * @Override public void visitTryCatchBlock(final Label start,final Label end,final Label handler,final 
             * String type){++ handlerCount; Handler h = new Handler(); hstart =开始结束hhandler = handler; hdesc = type; htype = type！= null? cwnewClass(type)：0; if(lastHandler == null){firstHandler = h; }
             *  else {lastHandlernext = h; } lastHandler = h; }。
             * 
             * @Override public AnnotationVisitor visitTryCatchAnnotation(int typeRef,TypePath typePath,String desc,
             * boolean visible){if(！ClassReaderANNOTATIONS){return null; } ByteVector bv = new ByteVector(); //写入tar
             * get_type和target_info AnnotationWriterputTarget(typeRef,typePath,bv); //写入类型,并为值计数bvputShort(cwnewUTF8
             * (desc))putShort(0); AnnotationWriter aw = new AnnotationWriter(cw,true,bv,bv,bvlength  -  2); if(visi
             * ble){awnext = ctanns; ctanns = aw } else {awnext = ictanns; ictanns = aw } return aw; }。
             * 
             * @Override public void visitLocalVariable(final String name,final String desc,final String signature,f
             * inal Label start,final Label end,final int index){if(signature！= null){if(localVarType == null){localVarType = new ByteVector(); }
             *  ++ localVarTypeCount; localVarTypeputShort(startposition)putShort(endposition  -  startposition)putS
             * hort(cwnewUTF8(name))putShort(cwnewUTF8(signature))putShort(index); } if(localVar == null){localVar = new ByteVector(); }
             *  ++ localVarCount localVarputShort(startposition)putShort(endposition  -  startposition)putShort(cwne
             * wUTF8(name))putShort(cwnewUTF8(desc))putShort(index); if(compute！= NOTHING){//更新max locals char c = desccharAt(0); int n = index +(c =='J'|| c =='D'?2：1); if(n> maxLocals){maxLocals = n; }
             * }}。
             * 
             * @Override public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,TypePath typePath,Label [
             * ] start,Label [] end,int [] index,String desc,boolean visible){if(！ClassReaderANNOTATIONS){return null; }
             *  ByteVector bv = new ByteVector(); //写target_type和target_info bvputByte(typeRef >>> 24)putShort(start
             * length); for(int i = 0; i <startlength; ++ i){bvputShort(start [i] position)putShort(end [i] position  -  start [i] position)putShort(index [i]); } if(typePath == null){bvputByte(0); } else {int length = typePathb [typePathoffset] * 2 + 1; bvputByteArray(typePathb,typePathoffset,length); } //写入类型,并为值计算bvputShort(cwnewUTF8(desc))putShort(0); AnnotationWriter aw = new AnnotationWriter(cw,true,bv,bv,bv长度 -  2); IF(可见){= ctanns awnext; ctanns =瓦特; }否则{awnext = ictanns; ictanns =瓦特;返回W】; }。
             * 
             * @Override public void visitLineNumber(final int line,final Label start){if(lineNumber == null){lineNumber = new ByteVector(); }
             *  ++ lineNumberCount; lineNumberputShort(指定startPosition); lineNumberputShort(线); }。
             * 
             * @Override public void visitMaxs(final int maxStack,final int maxLocals){if(resize){//替换Labelresolve引入的临时跳转操作if(ClassReaderRESIZE){resizeInstructions(); }
             *  else {throw new RuntimeException("方法代码太大！"); }} if(ClassReaderFRAMES && compute == FRAMES){//用异常处理程序块完成控制流程图处理程序处理程序= firstHandler; while(handler！= null){Label l = handlerstartgetFirst();标签h = handlerhandlergetFirst();标签e = handlerendgetFirst(); //计算边缘的类型为'h'String t = handlerdesc == null? "java / lang / Throwable"：handlerdesc; int kind = FrameOBJECT | cwaddType(T); // h是异常处理程序hstatus | = Label目标; //将'h'添加为'start'和'end'之间的标签的继承者,而(l！= e){//创建一个边缘为'h'Edge b = new Edge(); binfo = kind; bsuccessor = h; //将其添加到'l'bnext = lsuccessors的后继中; lsuccessors = b; //转到下一个标签l = lsuccessor; }
             *  handler = handlernext; }。
             * 
             * //创建并访问第一个(隐式)帧Frame f = labelsframe; Type [] args = TypegetArgumentTypes(descriptor); finitInputFram
             * e(cw,access,args,thismaxLocals); visitFrame(F);。
             * 
             *  / *固定点算法：将第一个基本块标记为"已更改"(即将其置于"已更改"列表中),并且更改基本块时,选择一个,将其标记为未更改,并更新其后继在过程中改变)
             * 
             */
            int max = 0;
            Label changed = labels;
            while (changed != null) {
                // removes a basic block from the list of changed basic blocks
                Label l = changed;
                changed = changed.next;
                l.next = null;
                f = l.frame;
                // a reachable jump target must be stored in the stack map
                if ((l.status & Label.TARGET) != 0) {
                    l.status |= Label.STORE;
                }
                // all visited labels are reachable, by definition
                l.status |= Label.REACHABLE;
                // updates the (absolute) maximum stack size
                int blockMax = f.inputStack.length + l.outputStackMax;
                if (blockMax > max) {
                    max = blockMax;
                }
                // updates the successors of the current basic block
                Edge e = l.successors;
                while (e != null) {
                    Label n = e.successor.getFirst();
                    boolean change = f.merge(cw, n.frame, e.info);
                    if (change && n.next == null) {
                        // if n has changed and is not already in the 'changed'
                        // list, adds it to this list
                        n.next = changed;
                        changed = n;
                    }
                    e = e.next;
                }
            }

            // visits all the frames that must be stored in the stack map
            Label l = labels;
            while (l != null) {
                f = l.frame;
                if ((l.status & Label.STORE) != 0) {
                    visitFrame(f);
                }
                if ((l.status & Label.REACHABLE) == 0) {
                    // finds start and end of dead basic block
                    Label k = l.successor;
                    int start = l.position;
                    int end = (k == null ? code.length : k.position) - 1;
                    // if non empty basic block
                    if (end >= start) {
                        max = Math.max(max, 1);
                        // replaces instructions with NOP ... NOP ATHROW
                        for (int i = start; i < end; ++i) {
                            code.data[i] = Opcodes.NOP;
                        }
                        code.data[end] = (byte) Opcodes.ATHROW;
                        // emits a frame for this unreachable block
                        int frameIndex = startFrame(start, 0, 1);
                        frame[frameIndex] = Frame.OBJECT
                                | cw.addType("java/lang/Throwable");
                        endFrame();
                        // removes the start-end range from the exception
                        // handlers
                        firstHandler = Handler.remove(firstHandler, l, k);
                    }
                }
                l = l.successor;
            }

            handler = firstHandler;
            handlerCount = 0;
            while (handler != null) {
                handlerCount += 1;
                handler = handler.next;
            }

            this.maxStack = max;
        } else if (compute == MAXS) {
            // completes the control flow graph with exception handler blocks
            Handler handler = firstHandler;
            while (handler != null) {
                Label l = handler.start;
                Label h = handler.handler;
                Label e = handler.end;
                // adds 'h' as a successor of labels between 'start' and 'end'
                while (l != e) {
                    // creates an edge to 'h'
                    Edge b = new Edge();
                    b.info = Edge.EXCEPTION;
                    b.successor = h;
                    // adds it to the successors of 'l'
                    if ((l.status & Label.JSR) == 0) {
                        b.next = l.successors;
                        l.successors = b;
                    } else {
                        // if l is a JSR block, adds b after the first two edges
                        // to preserve the hypothesis about JSR block successors
                        // order (see {@link #visitJumpInsn})
                        b.next = l.successors.next.next;
                        l.successors.next.next = b;
                    }
                    // goes to the next label
                    l = l.successor;
                }
                handler = handler.next;
            }

            if (subroutines > 0) {
                // completes the control flow graph with the RET successors
                /*
                 * first step: finds the subroutines. This step determines, for
                 * each basic block, to which subroutine(s) it belongs.
                 * <p>
                 *  第一步：找到子程序此步骤为每个基本块确定其所属的子例程
                 * 
                 */
                // finds the basic blocks that belong to the "main" subroutine
                int id = 0;
                labels.visitSubroutine(null, 1, subroutines);
                // finds the basic blocks that belong to the real subroutines
                Label l = labels;
                while (l != null) {
                    if ((l.status & Label.JSR) != 0) {
                        // the subroutine is defined by l's TARGET, not by l
                        Label subroutine = l.successors.next.successor;
                        // if this subroutine has not been visited yet...
                        if ((subroutine.status & Label.VISITED) == 0) {
                            // ...assigns it a new id and finds its basic blocks
                            id += 1;
                            subroutine.visitSubroutine(null, (id / 32L) << 32
                                    | (1L << (id % 32)), subroutines);
                        }
                    }
                    l = l.successor;
                }
                // second step: finds the successors of RET blocks
                l = labels;
                while (l != null) {
                    if ((l.status & Label.JSR) != 0) {
                        Label L = labels;
                        while (L != null) {
                            L.status &= ~Label.VISITED2;
                            L = L.successor;
                        }
                        // the subroutine is defined by l's TARGET, not by l
                        Label subroutine = l.successors.next.successor;
                        subroutine.visitSubroutine(l, 0, subroutines);
                    }
                    l = l.successor;
                }
            }

            /*
             * control flow analysis algorithm: while the block stack is not
             * empty, pop a block from this stack, update the max stack size,
             * compute the true (non relative) begin stack size of the
             * successors of this block, and push these successors onto the
             * stack (unless they have already been pushed onto the stack).
             * Note: by hypothesis, the {@link Label#inputStackTop} of the
             * blocks in the block stack are the true (non relative) beginning
             * stack sizes of these blocks.
             * <p>
             * 控制流分析算法：当块堆栈不为空时,从该堆栈中弹出一个块,更新最大堆栈大小,计算该块的后继者的真(非相对)开始堆栈大小,并将这些后继推送到堆栈(除非它们已被推送到堆栈)注意：通过假设,块堆栈中的块的{@link Label#inputStackTop}
             * 是这些块的真(非相对)起始堆栈大小。
             * 
             */
            int max = 0;
            Label stack = labels;
            while (stack != null) {
                // pops a block from the stack
                Label l = stack;
                stack = stack.next;
                // computes the true (non relative) max stack size of this block
                int start = l.inputStackTop;
                int blockMax = start + l.outputStackMax;
                // updates the global max stack size
                if (blockMax > max) {
                    max = blockMax;
                }
                // analyzes the successors of the block
                Edge b = l.successors;
                if ((l.status & Label.JSR) != 0) {
                    // ignores the first edge of JSR blocks (virtual successor)
                    b = b.next;
                }
                while (b != null) {
                    l = b.successor;
                    // if this successor has not already been pushed...
                    if ((l.status & Label.PUSHED) == 0) {
                        // computes its true beginning stack size...
                        l.inputStackTop = b.info == Edge.EXCEPTION ? 1 : start
                                + b.info;
                        // ...and pushes it onto the stack
                        l.status |= Label.PUSHED;
                        l.next = stack;
                        stack = l;
                    }
                    b = b.next;
                }
            }
            this.maxStack = Math.max(maxStack, max);
        } else {
            this.maxStack = maxStack;
            this.maxLocals = maxLocals;
        }
    }

    @Override
    public void visitEnd() {
    }

    // ------------------------------------------------------------------------
    // Utility methods: control flow analysis algorithm
    // ------------------------------------------------------------------------

    /**
     * Adds a successor to the {@link #currentBlock currentBlock} block.
     * 
     * <p>
     *  在{@link #currentBlock currentBlock}块中添加一个后继
     * 
     * 
     * @param info
     *            information about the control flow edge to be added.
     * @param successor
     *            the successor block to be added to the current block.
     */
    private void addSuccessor(final int info, final Label successor) {
        // creates and initializes an Edge object...
        Edge b = new Edge();
        b.info = info;
        b.successor = successor;
        // ...and adds it to the successor list of the currentBlock block
        b.next = currentBlock.successors;
        currentBlock.successors = b;
    }

    /**
     * Ends the current basic block. This method must be used in the case where
     * the current basic block does not have any successor.
     * <p>
     *  结束当前基本块当前基本块没有任何后继时,必须使用该方法
     * 
     */
    private void noSuccessor() {
        if (compute == FRAMES) {
            Label l = new Label();
            l.frame = new Frame();
            l.frame.owner = l;
            l.resolve(this, code.length, code.data);
            previousBlock.successor = l;
            previousBlock = l;
        } else {
            currentBlock.outputStackMax = maxStackSize;
        }
        currentBlock = null;
    }

    // ------------------------------------------------------------------------
    // Utility methods: stack map frames
    // ------------------------------------------------------------------------

    /**
     * Visits a frame that has been computed from scratch.
     * 
     * <p>
     *  访问从头开始计算的帧
     * 
     * 
     * @param f
     *            the frame that must be visited.
     */
    private void visitFrame(final Frame f) {
        int i, t;
        int nTop = 0;
        int nLocal = 0;
        int nStack = 0;
        int[] locals = f.inputLocals;
        int[] stacks = f.inputStack;
        // computes the number of locals (ignores TOP types that are just after
        // a LONG or a DOUBLE, and all trailing TOP types)
        for (i = 0; i < locals.length; ++i) {
            t = locals[i];
            if (t == Frame.TOP) {
                ++nTop;
            } else {
                nLocal += nTop + 1;
                nTop = 0;
            }
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i;
            }
        }
        // computes the stack size (ignores TOP types that are just after
        // a LONG or a DOUBLE)
        for (i = 0; i < stacks.length; ++i) {
            t = stacks[i];
            ++nStack;
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i;
            }
        }
        // visits the frame and its content
        int frameIndex = startFrame(f.owner.position, nLocal, nStack);
        for (i = 0; nLocal > 0; ++i, --nLocal) {
            t = locals[i];
            frame[frameIndex++] = t;
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i;
            }
        }
        for (i = 0; i < stacks.length; ++i) {
            t = stacks[i];
            frame[frameIndex++] = t;
            if (t == Frame.LONG || t == Frame.DOUBLE) {
                ++i;
            }
        }
        endFrame();
    }

    /**
     * Visit the implicit first frame of this method.
     * <p>
     *  访问该方法的隐式第一帧
     * 
     */
    private void visitImplicitFirstFrame() {
        // There can be at most descriptor.length() + 1 locals
        int frameIndex = startFrame(0, descriptor.length() + 1, 0);
        if ((access & Opcodes.ACC_STATIC) == 0) {
            if ((access & ACC_CONSTRUCTOR) == 0) {
                frame[frameIndex++] = Frame.OBJECT | cw.addType(cw.thisName);
            } else {
                frame[frameIndex++] = 6; // Opcodes.UNINITIALIZED_THIS;
            }
        }
        int i = 1;
        loop: while (true) {
            int j = i;
            switch (descriptor.charAt(i++)) {
            case 'Z':
            case 'C':
            case 'B':
            case 'S':
            case 'I':
                frame[frameIndex++] = 1; // Opcodes.INTEGER;
                break;
            case 'F':
                frame[frameIndex++] = 2; // Opcodes.FLOAT;
                break;
            case 'J':
                frame[frameIndex++] = 4; // Opcodes.LONG;
                break;
            case 'D':
                frame[frameIndex++] = 3; // Opcodes.DOUBLE;
                break;
            case '[':
                while (descriptor.charAt(i) == '[') {
                    ++i;
                }
                if (descriptor.charAt(i) == 'L') {
                    ++i;
                    while (descriptor.charAt(i) != ';') {
                        ++i;
                    }
                }
                frame[frameIndex++] = Frame.OBJECT
                        | cw.addType(descriptor.substring(j, ++i));
                break;
            case 'L':
                while (descriptor.charAt(i) != ';') {
                    ++i;
                }
                frame[frameIndex++] = Frame.OBJECT
                        | cw.addType(descriptor.substring(j + 1, i++));
                break;
            default:
                break loop;
            }
        }
        frame[1] = frameIndex - 3;
        endFrame();
    }

    /**
     * Starts the visit of a stack map frame.
     * 
     * <p>
     *  开始堆栈地图框的访问
     * 
     * 
     * @param offset
     *            the offset of the instruction to which the frame corresponds.
     * @param nLocal
     *            the number of local variables in the frame.
     * @param nStack
     *            the number of stack elements in the frame.
     * @return the index of the next element to be written in this frame.
     */
    private int startFrame(final int offset, final int nLocal, final int nStack) {
        int n = 3 + nLocal + nStack;
        if (frame == null || frame.length < n) {
            frame = new int[n];
        }
        frame[0] = offset;
        frame[1] = nLocal;
        frame[2] = nStack;
        return 3;
    }

    /**
     * Checks if the visit of the current frame {@link #frame} is finished, and
     * if yes, write it in the StackMapTable attribute.
     * <p>
     * 检查当前帧{@link #frame}的访问是否完成,如果是,请将其写入StackMapTable属性
     * 
     */
    private void endFrame() {
        if (previousFrame != null) { // do not write the first frame
            if (stackMap == null) {
                stackMap = new ByteVector();
            }
            writeFrame();
            ++frameCount;
        }
        previousFrame = frame;
        frame = null;
    }

    /**
     * Compress and writes the current frame {@link #frame} in the StackMapTable
     * attribute.
     * <p>
     *  在StackMapTable属性中压缩并写入当前帧{@link #frame}
     * 
     */
    private void writeFrame() {
        int clocalsSize = frame[1];
        int cstackSize = frame[2];
        if ((cw.version & 0xFFFF) < Opcodes.V1_6) {
            stackMap.putShort(frame[0]).putShort(clocalsSize);
            writeFrameTypes(3, 3 + clocalsSize);
            stackMap.putShort(cstackSize);
            writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize);
            return;
        }
        int localsSize = previousFrame[1];
        int type = FULL_FRAME;
        int k = 0;
        int delta;
        if (frameCount == 0) {
            delta = frame[0];
        } else {
            delta = frame[0] - previousFrame[0] - 1;
        }
        if (cstackSize == 0) {
            k = clocalsSize - localsSize;
            switch (k) {
            case -3:
            case -2:
            case -1:
                type = CHOP_FRAME;
                localsSize = clocalsSize;
                break;
            case 0:
                type = delta < 64 ? SAME_FRAME : SAME_FRAME_EXTENDED;
                break;
            case 1:
            case 2:
            case 3:
                type = APPEND_FRAME;
                break;
            }
        } else if (clocalsSize == localsSize && cstackSize == 1) {
            type = delta < 63 ? SAME_LOCALS_1_STACK_ITEM_FRAME
                    : SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED;
        }
        if (type != FULL_FRAME) {
            // verify if locals are the same
            int l = 3;
            for (int j = 0; j < localsSize; j++) {
                if (frame[l] != previousFrame[l]) {
                    type = FULL_FRAME;
                    break;
                }
                l++;
            }
        }
        switch (type) {
        case SAME_FRAME:
            stackMap.putByte(delta);
            break;
        case SAME_LOCALS_1_STACK_ITEM_FRAME:
            stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME + delta);
            writeFrameTypes(3 + clocalsSize, 4 + clocalsSize);
            break;
        case SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED:
            stackMap.putByte(SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED).putShort(
                    delta);
            writeFrameTypes(3 + clocalsSize, 4 + clocalsSize);
            break;
        case SAME_FRAME_EXTENDED:
            stackMap.putByte(SAME_FRAME_EXTENDED).putShort(delta);
            break;
        case CHOP_FRAME:
            stackMap.putByte(SAME_FRAME_EXTENDED + k).putShort(delta);
            break;
        case APPEND_FRAME:
            stackMap.putByte(SAME_FRAME_EXTENDED + k).putShort(delta);
            writeFrameTypes(3 + localsSize, 3 + clocalsSize);
            break;
        // case FULL_FRAME:
        default:
            stackMap.putByte(FULL_FRAME).putShort(delta).putShort(clocalsSize);
            writeFrameTypes(3, 3 + clocalsSize);
            stackMap.putShort(cstackSize);
            writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize);
        }
    }

    /**
     * Writes some types of the current frame {@link #frame} into the
     * StackMapTableAttribute. This method converts types from the format used
     * in {@link Label} to the format used in StackMapTable attributes. In
     * particular, it converts type table indexes to constant pool indexes.
     * 
     * <p>
     *  将当前框架的一些类型{@link #frame}写入StackMapTableAttribute中此方法将类型从{@link Label}中使用的格式转换为StackMapTable属性中使用的格式特
     * 别地,它将类型表索引转换为常量池索引。
     * 
     * 
     * @param start
     *            index of the first type in {@link #frame} to write.
     * @param end
     *            index of last type in {@link #frame} to write (exclusive).
     */
    private void writeFrameTypes(final int start, final int end) {
        for (int i = start; i < end; ++i) {
            int t = frame[i];
            int d = t & Frame.DIM;
            if (d == 0) {
                int v = t & Frame.BASE_VALUE;
                switch (t & Frame.BASE_KIND) {
                case Frame.OBJECT:
                    stackMap.putByte(7).putShort(
                            cw.newClass(cw.typeTable[v].strVal1));
                    break;
                case Frame.UNINITIALIZED:
                    stackMap.putByte(8).putShort(cw.typeTable[v].intVal);
                    break;
                default:
                    stackMap.putByte(v);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                d >>= 28;
                while (d-- > 0) {
                    sb.append('[');
                }
                if ((t & Frame.BASE_KIND) == Frame.OBJECT) {
                    sb.append('L');
                    sb.append(cw.typeTable[t & Frame.BASE_VALUE].strVal1);
                    sb.append(';');
                } else {
                    switch (t & 0xF) {
                    case 1:
                        sb.append('I');
                        break;
                    case 2:
                        sb.append('F');
                        break;
                    case 3:
                        sb.append('D');
                        break;
                    case 9:
                        sb.append('Z');
                        break;
                    case 10:
                        sb.append('B');
                        break;
                    case 11:
                        sb.append('C');
                        break;
                    case 12:
                        sb.append('S');
                        break;
                    default:
                        sb.append('J');
                    }
                }
                stackMap.putByte(7).putShort(cw.newClass(sb.toString()));
            }
        }
    }

    private void writeFrameType(final Object type) {
        if (type instanceof String) {
            stackMap.putByte(7).putShort(cw.newClass((String) type));
        } else if (type instanceof Integer) {
            stackMap.putByte(((Integer) type).intValue());
        } else {
            stackMap.putByte(8).putShort(((Label) type).position);
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods: dump bytecode array
    // ------------------------------------------------------------------------

    /**
     * Returns the size of the bytecode of this method.
     * 
     * <p>
     *  返回此方法的字节码大小
     * 
     * 
     * @return the size of the bytecode of this method.
     */
    final int getSize() {
        if (classReaderOffset != 0) {
            return 6 + classReaderLength;
        }
        int size = 8;
        if (code.length > 0) {
            if (code.length > 65535) {
                throw new RuntimeException("Method code too large!");
            }
            cw.newUTF8("Code");
            size += 18 + code.length + 8 * handlerCount;
            if (localVar != null) {
                cw.newUTF8("LocalVariableTable");
                size += 8 + localVar.length;
            }
            if (localVarType != null) {
                cw.newUTF8("LocalVariableTypeTable");
                size += 8 + localVarType.length;
            }
            if (lineNumber != null) {
                cw.newUTF8("LineNumberTable");
                size += 8 + lineNumber.length;
            }
            if (stackMap != null) {
                boolean zip = (cw.version & 0xFFFF) >= Opcodes.V1_6;
                cw.newUTF8(zip ? "StackMapTable" : "StackMap");
                size += 8 + stackMap.length;
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                cw.newUTF8("RuntimeVisibleTypeAnnotations");
                size += 8 + ctanns.getSize();
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                cw.newUTF8("RuntimeInvisibleTypeAnnotations");
                size += 8 + ictanns.getSize();
            }
            if (cattrs != null) {
                size += cattrs.getSize(cw, code.data, code.length, maxStack,
                        maxLocals);
            }
        }
        if (exceptionCount > 0) {
            cw.newUTF8("Exceptions");
            size += 8 + 2 * exceptionCount;
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                cw.newUTF8("Synthetic");
                size += 6;
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            cw.newUTF8("Deprecated");
            size += 6;
        }
        if (ClassReader.SIGNATURES && signature != null) {
            cw.newUTF8("Signature");
            cw.newUTF8(signature);
            size += 8;
        }
        if (methodParameters != null) {
            cw.newUTF8("MethodParameters");
            size += 7 + methodParameters.length;
        }
        if (ClassReader.ANNOTATIONS && annd != null) {
            cw.newUTF8("AnnotationDefault");
            size += 6 + annd.length;
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            cw.newUTF8("RuntimeVisibleAnnotations");
            size += 8 + anns.getSize();
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            cw.newUTF8("RuntimeInvisibleAnnotations");
            size += 8 + ianns.getSize();
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            cw.newUTF8("RuntimeVisibleTypeAnnotations");
            size += 8 + tanns.getSize();
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            cw.newUTF8("RuntimeInvisibleTypeAnnotations");
            size += 8 + itanns.getSize();
        }
        if (ClassReader.ANNOTATIONS && panns != null) {
            cw.newUTF8("RuntimeVisibleParameterAnnotations");
            size += 7 + 2 * (panns.length - synthetics);
            for (int i = panns.length - 1; i >= synthetics; --i) {
                size += panns[i] == null ? 0 : panns[i].getSize();
            }
        }
        if (ClassReader.ANNOTATIONS && ipanns != null) {
            cw.newUTF8("RuntimeInvisibleParameterAnnotations");
            size += 7 + 2 * (ipanns.length - synthetics);
            for (int i = ipanns.length - 1; i >= synthetics; --i) {
                size += ipanns[i] == null ? 0 : ipanns[i].getSize();
            }
        }
        if (attrs != null) {
            size += attrs.getSize(cw, null, 0, -1, -1);
        }
        return size;
    }

    /**
     * Puts the bytecode of this method in the given byte vector.
     * 
     * <p>
     *  将该方法的字节码放在给定的字节向量中
     * 
     * 
     * @param out
     *            the byte vector into which the bytecode of this method must be
     *            copied.
     */
    final void put(final ByteVector out) {
        final int FACTOR = ClassWriter.TO_ACC_SYNTHETIC;
        int mask = ACC_CONSTRUCTOR | Opcodes.ACC_DEPRECATED
                | ClassWriter.ACC_SYNTHETIC_ATTRIBUTE
                | ((access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) / FACTOR);
        out.putShort(access & ~mask).putShort(name).putShort(desc);
        if (classReaderOffset != 0) {
            out.putByteArray(cw.cr.b, classReaderOffset, classReaderLength);
            return;
        }
        int attributeCount = 0;
        if (code.length > 0) {
            ++attributeCount;
        }
        if (exceptionCount > 0) {
            ++attributeCount;
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                ++attributeCount;
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            ++attributeCount;
        }
        if (ClassReader.SIGNATURES && signature != null) {
            ++attributeCount;
        }
        if (methodParameters != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && annd != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && panns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && ipanns != null) {
            ++attributeCount;
        }
        if (attrs != null) {
            attributeCount += attrs.getCount();
        }
        out.putShort(attributeCount);
        if (code.length > 0) {
            int size = 12 + code.length + 8 * handlerCount;
            if (localVar != null) {
                size += 8 + localVar.length;
            }
            if (localVarType != null) {
                size += 8 + localVarType.length;
            }
            if (lineNumber != null) {
                size += 8 + lineNumber.length;
            }
            if (stackMap != null) {
                size += 8 + stackMap.length;
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                size += 8 + ctanns.getSize();
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                size += 8 + ictanns.getSize();
            }
            if (cattrs != null) {
                size += cattrs.getSize(cw, code.data, code.length, maxStack,
                        maxLocals);
            }
            out.putShort(cw.newUTF8("Code")).putInt(size);
            out.putShort(maxStack).putShort(maxLocals);
            out.putInt(code.length).putByteArray(code.data, 0, code.length);
            out.putShort(handlerCount);
            if (handlerCount > 0) {
                Handler h = firstHandler;
                while (h != null) {
                    out.putShort(h.start.position).putShort(h.end.position)
                            .putShort(h.handler.position).putShort(h.type);
                    h = h.next;
                }
            }
            attributeCount = 0;
            if (localVar != null) {
                ++attributeCount;
            }
            if (localVarType != null) {
                ++attributeCount;
            }
            if (lineNumber != null) {
                ++attributeCount;
            }
            if (stackMap != null) {
                ++attributeCount;
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                ++attributeCount;
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                ++attributeCount;
            }
            if (cattrs != null) {
                attributeCount += cattrs.getCount();
            }
            out.putShort(attributeCount);
            if (localVar != null) {
                out.putShort(cw.newUTF8("LocalVariableTable"));
                out.putInt(localVar.length + 2).putShort(localVarCount);
                out.putByteArray(localVar.data, 0, localVar.length);
            }
            if (localVarType != null) {
                out.putShort(cw.newUTF8("LocalVariableTypeTable"));
                out.putInt(localVarType.length + 2).putShort(localVarTypeCount);
                out.putByteArray(localVarType.data, 0, localVarType.length);
            }
            if (lineNumber != null) {
                out.putShort(cw.newUTF8("LineNumberTable"));
                out.putInt(lineNumber.length + 2).putShort(lineNumberCount);
                out.putByteArray(lineNumber.data, 0, lineNumber.length);
            }
            if (stackMap != null) {
                boolean zip = (cw.version & 0xFFFF) >= Opcodes.V1_6;
                out.putShort(cw.newUTF8(zip ? "StackMapTable" : "StackMap"));
                out.putInt(stackMap.length + 2).putShort(frameCount);
                out.putByteArray(stackMap.data, 0, stackMap.length);
            }
            if (ClassReader.ANNOTATIONS && ctanns != null) {
                out.putShort(cw.newUTF8("RuntimeVisibleTypeAnnotations"));
                ctanns.put(out);
            }
            if (ClassReader.ANNOTATIONS && ictanns != null) {
                out.putShort(cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
                ictanns.put(out);
            }
            if (cattrs != null) {
                cattrs.put(cw, code.data, code.length, maxLocals, maxStack, out);
            }
        }
        if (exceptionCount > 0) {
            out.putShort(cw.newUTF8("Exceptions")).putInt(
                    2 * exceptionCount + 2);
            out.putShort(exceptionCount);
            for (int i = 0; i < exceptionCount; ++i) {
                out.putShort(exceptions[i]);
            }
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                out.putShort(cw.newUTF8("Synthetic")).putInt(0);
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            out.putShort(cw.newUTF8("Deprecated")).putInt(0);
        }
        if (ClassReader.SIGNATURES && signature != null) {
            out.putShort(cw.newUTF8("Signature")).putInt(2)
                    .putShort(cw.newUTF8(signature));
        }
        if (methodParameters != null) {
            out.putShort(cw.newUTF8("MethodParameters"));
            out.putInt(methodParameters.length + 1).putByte(
                    methodParametersCount);
            out.putByteArray(methodParameters.data, 0, methodParameters.length);
        }
        if (ClassReader.ANNOTATIONS && annd != null) {
            out.putShort(cw.newUTF8("AnnotationDefault"));
            out.putInt(annd.length);
            out.putByteArray(annd.data, 0, annd.length);
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            out.putShort(cw.newUTF8("RuntimeVisibleAnnotations"));
            anns.put(out);
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            out.putShort(cw.newUTF8("RuntimeInvisibleAnnotations"));
            ianns.put(out);
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            out.putShort(cw.newUTF8("RuntimeVisibleTypeAnnotations"));
            tanns.put(out);
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            out.putShort(cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
            itanns.put(out);
        }
        if (ClassReader.ANNOTATIONS && panns != null) {
            out.putShort(cw.newUTF8("RuntimeVisibleParameterAnnotations"));
            AnnotationWriter.put(panns, synthetics, out);
        }
        if (ClassReader.ANNOTATIONS && ipanns != null) {
            out.putShort(cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
            AnnotationWriter.put(ipanns, synthetics, out);
        }
        if (attrs != null) {
            attrs.put(cw, null, 0, -1, -1, out);
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods: instruction resizing (used to handle GOTO_W and JSR_W)
    // ------------------------------------------------------------------------

    /**
     * Resizes and replaces the temporary instructions inserted by
     * {@link Label#resolve} for wide forward jumps, while keeping jump offsets
     * and instruction addresses consistent. This may require to resize other
     * existing instructions, or even to introduce new instructions: for
     * example, increasing the size of an instruction by 2 at the middle of a
     * method can increases the offset of an IFEQ instruction from 32766 to
     * 32768, in which case IFEQ 32766 must be replaced with IFNEQ 8 GOTO_W
     * 32765. This, in turn, may require to increase the size of another jump
     * instruction, and so on... All these operations are handled automatically
     * by this method.
     * <p>
     * <i>This method must be called after all the method that is being built
     * has been visited</i>. In particular, the {@link Label Label} objects used
     * to construct the method are no longer valid after this method has been
     * called.
     * <p>
     * 调整并替换{@link Label#resolve}插入的临时指令以进行广泛的前向跳转,同时保持跳转偏移量和指令地址一致。
     * 这可能需要调整其他现有指令的大小,甚至可以引入新的指令：例如,增加大小在方法中间的指令2可以将IFEQ指令的偏移量从32766增加到32768,在这种情况下,IFEQ 32766必须用IFNEQ替代8 
     * GOTO_W 32765这又可能需要增加另一个的大小跳转指令等等所有这些操作都是通过这种方式自动处理的。
     * 调整并替换{@link Label#resolve}插入的临时指令以进行广泛的前向跳转,同时保持跳转偏移量和指令地址一致。
     * <p>
     * <i>在所有正在构建的方法被访问之后,必须调用此方法</i>特别地,用于构造该方法的{@link Label Label}对象在调用此方法后将不再有效
     * 
     */
    private void resizeInstructions() {
        byte[] b = code.data; // bytecode of the method
        int u, v, label; // indexes in b
        int i, j; // loop indexes
        /*
         * 1st step: As explained above, resizing an instruction may require to
         * resize another one, which may require to resize yet another one, and
         * so on. The first step of the algorithm consists in finding all the
         * instructions that need to be resized, without modifying the code.
         * This is done by the following "fix point" algorithm:
         * 
         * Parse the code to find the jump instructions whose offset will need
         * more than 2 bytes to be stored (the future offset is computed from
         * the current offset and from the number of bytes that will be inserted
         * or removed between the source and target instructions). For each such
         * instruction, adds an entry in (a copy of) the indexes and sizes
         * arrays (if this has not already been done in a previous iteration!).
         * 
         * If at least one entry has been added during the previous step, go
         * back to the beginning, otherwise stop.
         * 
         * In fact the real algorithm is complicated by the fact that the size
         * of TABLESWITCH and LOOKUPSWITCH instructions depends on their
         * position in the bytecode (because of padding). In order to ensure the
         * convergence of the algorithm, the number of bytes to be added or
         * removed from these instructions is over estimated during the previous
         * loop, and computed exactly only after the loop is finished (this
         * requires another pass to parse the bytecode of the method).
         * <p>
         *  第一步：如上所述,调整大小的指令可能需要调整另一个指令,这可能需要重新调整大小,等等。该算法的第一步是找到所有需要调整大小的指令,而无需修改代码这是通过以下"修复点"算法完成的：
         * 
         * 解析代码以找到跳转指令,其偏移量将需要多于2个字节进行存储(将来的偏移量是从当前偏移量计算的,以及从源和目标指令之间插入或移除的字节数)这样的指令,在索引和大小数组的(副本)中添加一个条目(如果在之前的
         * 迭代中还没有完成)。
         * 
         *  如果在上一步骤中至少添加了一个条目,则返回到开头,否则停止
         * 
         * 实际上,真正的算法是复杂的,因为TABLESWITCH和LOOKUPSWITCH指令的大小取决于它们在字节码中的位置(因为填充)为了确保算法的收敛,要添加或删除的字节数这些指令在上一个循环中被过度估计,
         * 并且仅在循环完成之后计算(这需要另一个通过来解析该方法的字节码)。
         * 
         */
        int[] allIndexes = new int[0]; // copy of indexes
        int[] allSizes = new int[0]; // copy of sizes
        boolean[] resize; // instructions to be resized
        int newOffset; // future offset of a jump instruction

        resize = new boolean[code.length];

        // 3 = loop again, 2 = loop ended, 1 = last pass, 0 = done
        int state = 3;
        do {
            if (state == 3) {
                state = 2;
            }
            u = 0;
            while (u < b.length) {
                int opcode = b[u] & 0xFF; // opcode of current instruction
                int insert = 0; // bytes to be added after this instruction

                switch (ClassWriter.TYPE[opcode]) {
                case ClassWriter.NOARG_INSN:
                case ClassWriter.IMPLVAR_INSN:
                    u += 1;
                    break;
                case ClassWriter.LABEL_INSN:
                    if (opcode > 201) {
                        // converts temporary opcodes 202 to 217, 218 and
                        // 219 to IFEQ ... JSR (inclusive), IFNULL and
                        // IFNONNULL
                        opcode = opcode < 218 ? opcode - 49 : opcode - 20;
                        label = u + readUnsignedShort(b, u + 1);
                    } else {
                        label = u + readShort(b, u + 1);
                    }
                    newOffset = getNewOffset(allIndexes, allSizes, u, label);
                    if (newOffset < Short.MIN_VALUE
                            || newOffset > Short.MAX_VALUE) {
                        if (!resize[u]) {
                            if (opcode == Opcodes.GOTO || opcode == Opcodes.JSR) {
                                // two additional bytes will be required to
                                // replace this GOTO or JSR instruction with
                                // a GOTO_W or a JSR_W
                                insert = 2;
                            } else {
                                // five additional bytes will be required to
                                // replace this IFxxx <l> instruction with
                                // IFNOTxxx <l'> GOTO_W <l>, where IFNOTxxx
                                // is the "opposite" opcode of IFxxx (i.e.,
                                // IFNE for IFEQ) and where <l'> designates
                                // the instruction just after the GOTO_W.
                                insert = 5;
                            }
                            resize[u] = true;
                        }
                    }
                    u += 3;
                    break;
                case ClassWriter.LABELW_INSN:
                    u += 5;
                    break;
                case ClassWriter.TABL_INSN:
                    if (state == 1) {
                        // true number of bytes to be added (or removed)
                        // from this instruction = (future number of padding
                        // bytes - current number of padding byte) -
                        // previously over estimated variation =
                        // = ((3 - newOffset%4) - (3 - u%4)) - u%4
                        // = (-newOffset%4 + u%4) - u%4
                        // = -(newOffset & 3)
                        newOffset = getNewOffset(allIndexes, allSizes, 0, u);
                        insert = -(newOffset & 3);
                    } else if (!resize[u]) {
                        // over estimation of the number of bytes to be
                        // added to this instruction = 3 - current number
                        // of padding bytes = 3 - (3 - u%4) = u%4 = u & 3
                        insert = u & 3;
                        resize[u] = true;
                    }
                    // skips instruction
                    u = u + 4 - (u & 3);
                    u += 4 * (readInt(b, u + 8) - readInt(b, u + 4) + 1) + 12;
                    break;
                case ClassWriter.LOOK_INSN:
                    if (state == 1) {
                        // like TABL_INSN
                        newOffset = getNewOffset(allIndexes, allSizes, 0, u);
                        insert = -(newOffset & 3);
                    } else if (!resize[u]) {
                        // like TABL_INSN
                        insert = u & 3;
                        resize[u] = true;
                    }
                    // skips instruction
                    u = u + 4 - (u & 3);
                    u += 8 * readInt(b, u + 4) + 8;
                    break;
                case ClassWriter.WIDE_INSN:
                    opcode = b[u + 1] & 0xFF;
                    if (opcode == Opcodes.IINC) {
                        u += 6;
                    } else {
                        u += 4;
                    }
                    break;
                case ClassWriter.VAR_INSN:
                case ClassWriter.SBYTE_INSN:
                case ClassWriter.LDC_INSN:
                    u += 2;
                    break;
                case ClassWriter.SHORT_INSN:
                case ClassWriter.LDCW_INSN:
                case ClassWriter.FIELDORMETH_INSN:
                case ClassWriter.TYPE_INSN:
                case ClassWriter.IINC_INSN:
                    u += 3;
                    break;
                case ClassWriter.ITFMETH_INSN:
                case ClassWriter.INDYMETH_INSN:
                    u += 5;
                    break;
                // case ClassWriter.MANA_INSN:
                default:
                    u += 4;
                    break;
                }
                if (insert != 0) {
                    // adds a new (u, insert) entry in the allIndexes and
                    // allSizes arrays
                    int[] newIndexes = new int[allIndexes.length + 1];
                    int[] newSizes = new int[allSizes.length + 1];
                    System.arraycopy(allIndexes, 0, newIndexes, 0,
                            allIndexes.length);
                    System.arraycopy(allSizes, 0, newSizes, 0, allSizes.length);
                    newIndexes[allIndexes.length] = u;
                    newSizes[allSizes.length] = insert;
                    allIndexes = newIndexes;
                    allSizes = newSizes;
                    if (insert > 0) {
                        state = 3;
                    }
                }
            }
            if (state < 3) {
                --state;
            }
        } while (state != 0);

        // 2nd step:
        // copies the bytecode of the method into a new bytevector, updates the
        // offsets, and inserts (or removes) bytes as requested.

        ByteVector newCode = new ByteVector(code.length);

        u = 0;
        while (u < code.length) {
            int opcode = b[u] & 0xFF;
            switch (ClassWriter.TYPE[opcode]) {
            case ClassWriter.NOARG_INSN:
            case ClassWriter.IMPLVAR_INSN:
                newCode.putByte(opcode);
                u += 1;
                break;
            case ClassWriter.LABEL_INSN:
                if (opcode > 201) {
                    // changes temporary opcodes 202 to 217 (inclusive), 218
                    // and 219 to IFEQ ... JSR (inclusive), IFNULL and
                    // IFNONNULL
                    opcode = opcode < 218 ? opcode - 49 : opcode - 20;
                    label = u + readUnsignedShort(b, u + 1);
                } else {
                    label = u + readShort(b, u + 1);
                }
                newOffset = getNewOffset(allIndexes, allSizes, u, label);
                if (resize[u]) {
                    // replaces GOTO with GOTO_W, JSR with JSR_W and IFxxx
                    // <l> with IFNOTxxx <l'> GOTO_W <l>, where IFNOTxxx is
                    // the "opposite" opcode of IFxxx (i.e., IFNE for IFEQ)
                    // and where <l'> designates the instruction just after
                    // the GOTO_W.
                    if (opcode == Opcodes.GOTO) {
                        newCode.putByte(200); // GOTO_W
                    } else if (opcode == Opcodes.JSR) {
                        newCode.putByte(201); // JSR_W
                    } else {
                        newCode.putByte(opcode <= 166 ? ((opcode + 1) ^ 1) - 1
                                : opcode ^ 1);
                        newCode.putShort(8); // jump offset
                        newCode.putByte(200); // GOTO_W
                        // newOffset now computed from start of GOTO_W
                        newOffset -= 3;
                    }
                    newCode.putInt(newOffset);
                } else {
                    newCode.putByte(opcode);
                    newCode.putShort(newOffset);
                }
                u += 3;
                break;
            case ClassWriter.LABELW_INSN:
                label = u + readInt(b, u + 1);
                newOffset = getNewOffset(allIndexes, allSizes, u, label);
                newCode.putByte(opcode);
                newCode.putInt(newOffset);
                u += 5;
                break;
            case ClassWriter.TABL_INSN:
                // skips 0 to 3 padding bytes
                v = u;
                u = u + 4 - (v & 3);
                // reads and copies instruction
                newCode.putByte(Opcodes.TABLESWITCH);
                newCode.putByteArray(null, 0, (4 - newCode.length % 4) % 4);
                label = v + readInt(b, u);
                u += 4;
                newOffset = getNewOffset(allIndexes, allSizes, v, label);
                newCode.putInt(newOffset);
                j = readInt(b, u);
                u += 4;
                newCode.putInt(j);
                j = readInt(b, u) - j + 1;
                u += 4;
                newCode.putInt(readInt(b, u - 4));
                for (; j > 0; --j) {
                    label = v + readInt(b, u);
                    u += 4;
                    newOffset = getNewOffset(allIndexes, allSizes, v, label);
                    newCode.putInt(newOffset);
                }
                break;
            case ClassWriter.LOOK_INSN:
                // skips 0 to 3 padding bytes
                v = u;
                u = u + 4 - (v & 3);
                // reads and copies instruction
                newCode.putByte(Opcodes.LOOKUPSWITCH);
                newCode.putByteArray(null, 0, (4 - newCode.length % 4) % 4);
                label = v + readInt(b, u);
                u += 4;
                newOffset = getNewOffset(allIndexes, allSizes, v, label);
                newCode.putInt(newOffset);
                j = readInt(b, u);
                u += 4;
                newCode.putInt(j);
                for (; j > 0; --j) {
                    newCode.putInt(readInt(b, u));
                    u += 4;
                    label = v + readInt(b, u);
                    u += 4;
                    newOffset = getNewOffset(allIndexes, allSizes, v, label);
                    newCode.putInt(newOffset);
                }
                break;
            case ClassWriter.WIDE_INSN:
                opcode = b[u + 1] & 0xFF;
                if (opcode == Opcodes.IINC) {
                    newCode.putByteArray(b, u, 6);
                    u += 6;
                } else {
                    newCode.putByteArray(b, u, 4);
                    u += 4;
                }
                break;
            case ClassWriter.VAR_INSN:
            case ClassWriter.SBYTE_INSN:
            case ClassWriter.LDC_INSN:
                newCode.putByteArray(b, u, 2);
                u += 2;
                break;
            case ClassWriter.SHORT_INSN:
            case ClassWriter.LDCW_INSN:
            case ClassWriter.FIELDORMETH_INSN:
            case ClassWriter.TYPE_INSN:
            case ClassWriter.IINC_INSN:
                newCode.putByteArray(b, u, 3);
                u += 3;
                break;
            case ClassWriter.ITFMETH_INSN:
            case ClassWriter.INDYMETH_INSN:
                newCode.putByteArray(b, u, 5);
                u += 5;
                break;
            // case MANA_INSN:
            default:
                newCode.putByteArray(b, u, 4);
                u += 4;
                break;
            }
        }

        // updates the stack map frame labels
        if (compute == FRAMES) {
            Label l = labels;
            while (l != null) {
                /*
                 * Detects the labels that are just after an IF instruction that
                 * has been resized with the IFNOT GOTO_W pattern. These labels
                 * are now the target of a jump instruction (the IFNOT
                 * instruction). Note that we need the original label position
                 * here. getNewOffset must therefore never have been called for
                 * this label.
                 * <p>
                 *  检测正在使用IFNOT GOTO_W模式调整大小的IF指令之后的标签这些标签现在是跳转指令(IFNOT指令)的目标。请注意,我们需要原始标签位置,因此必须从未调用getNewOffset为此标签
                 * 
                 */
                u = l.position - 3;
                if (u >= 0 && resize[u]) {
                    l.status |= Label.TARGET;
                }
                getNewOffset(allIndexes, allSizes, l);
                l = l.successor;
            }
            // Update the offsets in the uninitialized types
            if (cw.typeTable != null) {
                for (i = 0; i < cw.typeTable.length; ++i) {
                    Item item = cw.typeTable[i];
                    if (item != null && item.type == ClassWriter.TYPE_UNINIT) {
                        item.intVal = getNewOffset(allIndexes, allSizes, 0,
                                item.intVal);
                    }
                }
            }
            // The stack map frames are not serialized yet, so we don't need
            // to update them. They will be serialized in visitMaxs.
        } else if (frameCount > 0) {
            /*
             * Resizing an existing stack map frame table is really hard. Not
             * only the table must be parsed to update the offets, but new
             * frames may be needed for jump instructions that were inserted by
             * this method. And updating the offsets or inserting frames can
             * change the format of the following frames, in case of packed
             * frames. In practice the whole table must be recomputed. For this
             * the frames are marked as potentially invalid. This will cause the
             * whole class to be reread and rewritten with the COMPUTE_FRAMES
             * option (see the ClassWriter.toByteArray method). This is not very
             * efficient but is much easier and requires much less code than any
             * other method I can think of.
             * <p>
             * 调整现有的堆栈映射帧表的大小真的很难不仅必须解析表才能更新漏洞,而且可能需要新的帧用于通过此方法插入的跳转指令和更新偏移量或插入帧可以更改格式以下框架在打包框的情况下实际上必须重新计算整个表对于这个框架
             * 被标记为潜在的无效这将导致整个类被重新读取并用COMPUTE_FRAMES选项重写(参见ClassWritertoByteArray方法)这不是很高效但要容易得多,而且需要比我想象的任何其他方法少得多的
             * 代码。
             * 
             */
            cw.invalidFrames = true;
        }
        // updates the exception handler block labels
        Handler h = firstHandler;
        while (h != null) {
            getNewOffset(allIndexes, allSizes, h.start);
            getNewOffset(allIndexes, allSizes, h.end);
            getNewOffset(allIndexes, allSizes, h.handler);
            h = h.next;
        }
        // updates the instructions addresses in the
        // local var and line number tables
        for (i = 0; i < 2; ++i) {
            ByteVector bv = i == 0 ? localVar : localVarType;
            if (bv != null) {
                b = bv.data;
                u = 0;
                while (u < bv.length) {
                    label = readUnsignedShort(b, u);
                    newOffset = getNewOffset(allIndexes, allSizes, 0, label);
                    writeShort(b, u, newOffset);
                    label += readUnsignedShort(b, u + 2);
                    newOffset = getNewOffset(allIndexes, allSizes, 0, label)
                            - newOffset;
                    writeShort(b, u + 2, newOffset);
                    u += 10;
                }
            }
        }
        if (lineNumber != null) {
            b = lineNumber.data;
            u = 0;
            while (u < lineNumber.length) {
                writeShort(
                        b,
                        u,
                        getNewOffset(allIndexes, allSizes, 0,
                                readUnsignedShort(b, u)));
                u += 4;
            }
        }
        // updates the labels of the other attributes
        Attribute attr = cattrs;
        while (attr != null) {
            Label[] labels = attr.getLabels();
            if (labels != null) {
                for (i = labels.length - 1; i >= 0; --i) {
                    getNewOffset(allIndexes, allSizes, labels[i]);
                }
            }
            attr = attr.next;
        }

        // replaces old bytecodes with new ones
        code = newCode;
    }

    /**
     * Reads an unsigned short value in the given byte array.
     * 
     * <p>
     *  读取给定字节数组中的无符号短值
     * 
     * 
     * @param b
     *            a byte array.
     * @param index
     *            the start index of the value to be read.
     * @return the read value.
     */
    static int readUnsignedShort(final byte[] b, final int index) {
        return ((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF);
    }

    /**
     * Reads a signed short value in the given byte array.
     * 
     * <p>
     *  在给定的字节数组中读取已签名的短值
     * 
     * 
     * @param b
     *            a byte array.
     * @param index
     *            the start index of the value to be read.
     * @return the read value.
     */
    static short readShort(final byte[] b, final int index) {
        return (short) (((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF));
    }

    /**
     * Reads a signed int value in the given byte array.
     * 
     * <p>
     * 在给定的字节数组中读取一个带符号的int值
     * 
     * 
     * @param b
     *            a byte array.
     * @param index
     *            the start index of the value to be read.
     * @return the read value.
     */
    static int readInt(final byte[] b, final int index) {
        return ((b[index] & 0xFF) << 24) | ((b[index + 1] & 0xFF) << 16)
                | ((b[index + 2] & 0xFF) << 8) | (b[index + 3] & 0xFF);
    }

    /**
     * Writes a short value in the given byte array.
     * 
     * <p>
     *  在给定的字节数组中写入一个简短的值
     * 
     * 
     * @param b
     *            a byte array.
     * @param index
     *            where the first byte of the short value must be written.
     * @param s
     *            the value to be written in the given byte array.
     */
    static void writeShort(final byte[] b, final int index, final int s) {
        b[index] = (byte) (s >>> 8);
        b[index + 1] = (byte) s;
    }

    /**
     * Computes the future value of a bytecode offset.
     * <p>
     * Note: it is possible to have several entries for the same instruction in
     * the <tt>indexes</tt> and <tt>sizes</tt>: two entries (index=a,size=b) and
     * (index=a,size=b') are equivalent to a single entry (index=a,size=b+b').
     * 
     * <p>
     *  计算字节码偏移的未来值
     * <p>
     *  注意：对于<tt>索引</tt>和<tt> sizes </tt>中的相同指令,可以有两个条目：两个条目(index = a,size = b)和(index = a, size = b')等价于单个
     * 条目(index = a,size = b + b')。
     * 
     * @param indexes
     *            current positions of the instructions to be resized. Each
     *            instruction must be designated by the index of its <i>last</i>
     *            byte, plus one (or, in other words, by the index of the
     *            <i>first</i> byte of the <i>next</i> instruction).
     * @param sizes
     *            the number of bytes to be <i>added</i> to the above
     *            instructions. More precisely, for each i < <tt>len</tt>,
     *            <tt>sizes</tt>[i] bytes will be added at the end of the
     *            instruction designated by <tt>indexes</tt>[i] or, if
     *            <tt>sizes</tt>[i] is negative, the <i>last</i> |
     *            <tt>sizes[i]</tt>| bytes of the instruction will be removed
     *            (the instruction size <i>must not</i> become negative or
     *            null).
     * @param begin
     *            index of the first byte of the source instruction.
     * @param end
     *            index of the first byte of the target instruction.
     * @return the future value of the given bytecode offset.
     */
    static int getNewOffset(final int[] indexes, final int[] sizes,
            final int begin, final int end) {
        int offset = end - begin;
        for (int i = 0; i < indexes.length; ++i) {
            if (begin < indexes[i] && indexes[i] <= end) {
                // forward jump
                offset += sizes[i];
            } else if (end < indexes[i] && indexes[i] <= begin) {
                // backward jump
                offset -= sizes[i];
            }
        }
        return offset;
    }

    /**
     * Updates the offset of the given label.
     * 
     * <p>
     * 
     * 
     * @param indexes
     *            current positions of the instructions to be resized. Each
     *            instruction must be designated by the index of its <i>last</i>
     *            byte, plus one (or, in other words, by the index of the
     *            <i>first</i> byte of the <i>next</i> instruction).
     * @param sizes
     *            the number of bytes to be <i>added</i> to the above
     *            instructions. More precisely, for each i < <tt>len</tt>,
     *            <tt>sizes</tt>[i] bytes will be added at the end of the
     *            instruction designated by <tt>indexes</tt>[i] or, if
     *            <tt>sizes</tt>[i] is negative, the <i>last</i> |
     *            <tt>sizes[i]</tt>| bytes of the instruction will be removed
     *            (the instruction size <i>must not</i> become negative or
     *            null).
     * @param label
     *            the label whose offset must be updated.
     */
    static void getNewOffset(final int[] indexes, final int[] sizes,
            final Label label) {
        if ((label.status & Label.RESIZED) == 0) {
            label.position = getNewOffset(indexes, sizes, 0, label.position);
            label.status |= Label.RESIZED;
        }
    }
}
