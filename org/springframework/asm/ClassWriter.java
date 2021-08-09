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
 * A {@link ClassVisitor} that generates classes in bytecode form. More
 * precisely this visitor generates a byte array conforming to the Java class
 * file format. It can be used alone, to generate a Java class "from scratch",
 * or with one or more {@link ClassReader ClassReader} and adapter class visitor
 * to generate a modified class from one or more existing Java classes.
 * 
 * <p>
 * 使用字节码形式生成类的{@link ClassVisitor}更准确地说,此访问者生成符合Java类文件格式的字节数组可以单独使用,以从头开始生成Java类,或者使用一个或多个{@链接ClassReader ClassReader}
 * 和适配器类访问器,以从一个或多个现有Java类生成修改的类。
 * 
 * 
 * @author Eric Bruneton
 */
public class ClassWriter extends ClassVisitor {

    /**
     * Flag to automatically compute the maximum stack size and the maximum
     * number of local variables of methods. If this flag is set, then the
     * arguments of the {@link MethodVisitor#visitMaxs visitMaxs} method of the
     * {@link MethodVisitor} returned by the {@link #visitMethod visitMethod}
     * method will be ignored, and computed automatically from the signature and
     * the bytecode of each method.
     * 
     * <p>
     *  标志自动计算最大堆栈大小和方法的局部变量的最大数量如果设置了此标志,则{@link返回的{@link MethodVisitor}的{@link MethodVisitor#visitMaxs visitMaxs}
     * 方法的参数#visitMethod visitMethod}方法将被忽略,并从每个方法的签名和字节码自动计算。
     * 
     * 
     * @see #ClassWriter(int)
     */
    public static final int COMPUTE_MAXS = 1;

    /**
     * Flag to automatically compute the stack map frames of methods from
     * scratch. If this flag is set, then the calls to the
     * {@link MethodVisitor#visitFrame} method are ignored, and the stack map
     * frames are recomputed from the methods bytecode. The arguments of the
     * {@link MethodVisitor#visitMaxs visitMaxs} method are also ignored and
     * recomputed from the bytecode. In other words, computeFrames implies
     * computeMaxs.
     * 
     * <p>
     * 标志从头开始自动计算方法的堆栈映射帧如果设置了此标志,则将忽略对{@link MethodVisitor#visitFrame}方法的调用,并从方法bytecode重新计算堆栈映射帧。
     * 参数{ @link MethodVisitor#visitMaxs visitMaxs}方法也被忽略,并从字节码重新计算换句话说,computeFrames意味着computeMaxs。
     * 
     * 
     * @see #ClassWriter(int)
     */
    public static final int COMPUTE_FRAMES = 2;

    /**
     * Pseudo access flag to distinguish between the synthetic attribute and the
     * synthetic access flag.
     * <p>
     *  伪访问标志来区分合成属性和合成访问标志
     * 
     */
    static final int ACC_SYNTHETIC_ATTRIBUTE = 0x40000;

    /**
     * Factor to convert from ACC_SYNTHETIC_ATTRIBUTE to Opcode.ACC_SYNTHETIC.
     * <p>
     *  要从ACC_SYNTHETIC_ATTRIBUTE转换为OpcodeACC_SYNTHETIC的因子
     * 
     */
    static final int TO_ACC_SYNTHETIC = ACC_SYNTHETIC_ATTRIBUTE
            / Opcodes.ACC_SYNTHETIC;

    /**
     * The type of instructions without any argument.
     * <p>
     *  指令的类型没有任何参数
     * 
     */
    static final int NOARG_INSN = 0;

    /**
     * The type of instructions with an signed byte argument.
     * <p>
     *  带有符号字节参数的指令类型
     * 
     */
    static final int SBYTE_INSN = 1;

    /**
     * The type of instructions with an signed short argument.
     * <p>
     *  具有签名短参数的指令类型
     * 
     */
    static final int SHORT_INSN = 2;

    /**
     * The type of instructions with a local variable index argument.
     * <p>
     * 具有局部变量索引参数的指令类型
     * 
     */
    static final int VAR_INSN = 3;

    /**
     * The type of instructions with an implicit local variable index argument.
     * <p>
     *  具有隐式局部变量索引参数的指令类型
     * 
     */
    static final int IMPLVAR_INSN = 4;

    /**
     * The type of instructions with a type descriptor argument.
     * <p>
     *  具有类型描述符参数的指令类型
     * 
     */
    static final int TYPE_INSN = 5;

    /**
     * The type of field and method invocations instructions.
     * <p>
     *  字段和方法的类型调用指令
     * 
     */
    static final int FIELDORMETH_INSN = 6;

    /**
     * The type of the INVOKEINTERFACE/INVOKEDYNAMIC instruction.
     * <p>
     *  INVOKEINTERFACE / INVOKEDYNAMIC指令的类型
     * 
     */
    static final int ITFMETH_INSN = 7;

    /**
     * The type of the INVOKEDYNAMIC instruction.
     * <p>
     *  INVOKEDYNAMIC指令的类型
     * 
     */
    static final int INDYMETH_INSN = 8;

    /**
     * The type of instructions with a 2 bytes bytecode offset label.
     * <p>
     *  具有2字节字节代码偏移标签的指令类型
     * 
     */
    static final int LABEL_INSN = 9;

    /**
     * The type of instructions with a 4 bytes bytecode offset label.
     * <p>
     *  具有4字节字节代码偏移标签的指令类型
     * 
     */
    static final int LABELW_INSN = 10;

    /**
     * The type of the LDC instruction.
     * <p>
     *  LDC指令的类型
     * 
     */
    static final int LDC_INSN = 11;

    /**
     * The type of the LDC_W and LDC2_W instructions.
     * <p>
     *  LDC_W和LDC2_W指令的类型
     * 
     */
    static final int LDCW_INSN = 12;

    /**
     * The type of the IINC instruction.
     * <p>
     *  IINC指令的类型
     * 
     */
    static final int IINC_INSN = 13;

    /**
     * The type of the TABLESWITCH instruction.
     * <p>
     *  TABLESWITCH指令的类型
     * 
     */
    static final int TABL_INSN = 14;

    /**
     * The type of the LOOKUPSWITCH instruction.
     * <p>
     *  LOOKUPSWITCH指令的类型
     * 
     */
    static final int LOOK_INSN = 15;

    /**
     * The type of the MULTIANEWARRAY instruction.
     * <p>
     *  MULTIANEWARRAY指令的类型
     * 
     */
    static final int MANA_INSN = 16;

    /**
     * The type of the WIDE instruction.
     * <p>
     *  WIDE指令的类型
     * 
     */
    static final int WIDE_INSN = 17;

    /**
     * The instruction types of all JVM opcodes.
     * <p>
     * 所有JVM操作码的指令类型
     * 
     */
    static final byte[] TYPE;

    /**
     * The type of CONSTANT_Class constant pool items.
     * <p>
     *  CONSTANT_Class常量池项的类型
     * 
     */
    static final int CLASS = 7;

    /**
     * The type of CONSTANT_Fieldref constant pool items.
     * <p>
     *  CONSTANT_Fieldref常量池项的类型
     * 
     */
    static final int FIELD = 9;

    /**
     * The type of CONSTANT_Methodref constant pool items.
     * <p>
     *  CONSTANT_Methodref常量池项目的类型
     * 
     */
    static final int METH = 10;

    /**
     * The type of CONSTANT_InterfaceMethodref constant pool items.
     * <p>
     *  CONSTANT_InterfaceMethodref常量池项目的类型
     * 
     */
    static final int IMETH = 11;

    /**
     * The type of CONSTANT_String constant pool items.
     * <p>
     *  CONSTANT_String常量池项的类型
     * 
     */
    static final int STR = 8;

    /**
     * The type of CONSTANT_Integer constant pool items.
     * <p>
     *  CONSTANT_Integer常量池项目的类型
     * 
     */
    static final int INT = 3;

    /**
     * The type of CONSTANT_Float constant pool items.
     * <p>
     *  CONSTANT_Float常量池项目的类型
     * 
     */
    static final int FLOAT = 4;

    /**
     * The type of CONSTANT_Long constant pool items.
     * <p>
     *  CONSTANT_Long常量池项目的类型
     * 
     */
    static final int LONG = 5;

    /**
     * The type of CONSTANT_Double constant pool items.
     * <p>
     *  CONSTANT_Double常量池项的类型
     * 
     */
    static final int DOUBLE = 6;

    /**
     * The type of CONSTANT_NameAndType constant pool items.
     * <p>
     *  CONSTANT_NameAndType常量池项目的类型
     * 
     */
    static final int NAME_TYPE = 12;

    /**
     * The type of CONSTANT_Utf8 constant pool items.
     * <p>
     *  CONSTANT_Utf8常量池项目的类型
     * 
     */
    static final int UTF8 = 1;

    /**
     * The type of CONSTANT_MethodType constant pool items.
     * <p>
     *  CONSTANT_MethodType常量池项目的类型
     * 
     */
    static final int MTYPE = 16;

    /**
     * The type of CONSTANT_MethodHandle constant pool items.
     * <p>
     *  CONSTANT_MethodHandle常量池项目的类型
     * 
     */
    static final int HANDLE = 15;

    /**
     * The type of CONSTANT_InvokeDynamic constant pool items.
     * <p>
     *  CONSTANT_InvokeDynamic常量池项目的类型
     * 
     */
    static final int INDY = 18;

    /**
     * The base value for all CONSTANT_MethodHandle constant pool items.
     * Internally, ASM store the 9 variations of CONSTANT_MethodHandle into 9
     * different items.
     * <p>
     * 所有CONSTANT_MethodHandle常量池项目的基值在内部,ASM将9个变量的CONSTANT_MethodHandle存储到9个不同的项目
     * 
     */
    static final int HANDLE_BASE = 20;

    /**
     * Normal type Item stored in the ClassWriter {@link ClassWriter#typeTable},
     * instead of the constant pool, in order to avoid clashes with normal
     * constant pool items in the ClassWriter constant pool's hash table.
     * <p>
     *  正常类型存储在ClassWriter {@link ClassWriter#typeTable}中的项目,而不是常量池,以避免与ClassWriter常量池的散列表中的常规常量池项目冲突
     * 
     */
    static final int TYPE_NORMAL = 30;

    /**
     * Uninitialized type Item stored in the ClassWriter
     * {@link ClassWriter#typeTable}, instead of the constant pool, in order to
     * avoid clashes with normal constant pool items in the ClassWriter constant
     * pool's hash table.
     * <p>
     *  未初始化类型存储在ClassWriter {@link ClassWriter#typeTable}中的项目,而不是常量池,以避免与ClassWriter常量池的散列表中的常规常量池项目冲突
     * 
     */
    static final int TYPE_UNINIT = 31;

    /**
     * Merged type Item stored in the ClassWriter {@link ClassWriter#typeTable},
     * instead of the constant pool, in order to avoid clashes with normal
     * constant pool items in the ClassWriter constant pool's hash table.
     * <p>
     *  合并类型项目存储在ClassWriter {@link ClassWriter#typeTable}中,而不是常量池,以避免与ClassWriter常量池的散列表中常规常量池项目冲突
     * 
     */
    static final int TYPE_MERGED = 32;

    /**
     * The type of BootstrapMethods items. These items are stored in a special
     * class attribute named BootstrapMethods and not in the constant pool.
     * <p>
     * BootstrapMethods项的类型这些项存储在名为BootstrapMethods的特殊类属性中,而不是在常量池中
     * 
     */
    static final int BSM = 33;

    /**
     * The class reader from which this class writer was constructed, if any.
     * <p>
     *  这个类作家的类读者,如果有的话
     * 
     */
    ClassReader cr;

    /**
     * Minor and major version numbers of the class to be generated.
     * <p>
     *  要生成的类的次要和主要版本号
     * 
     */
    int version;

    /**
     * Index of the next item to be added in the constant pool.
     * <p>
     *  要添加到常量池中的下一个项目的索引
     * 
     */
    int index;

    /**
     * The constant pool of this class.
     * <p>
     *  这个类的常量池
     * 
     */
    final ByteVector pool;

    /**
     * The constant pool's hash table data.
     * <p>
     *  常量池的哈希表数据
     * 
     */
    Item[] items;

    /**
     * The threshold of the constant pool's hash table.
     * <p>
     *  常量池的哈希表的阈值
     * 
     */
    int threshold;

    /**
     * A reusable key used to look for items in the {@link #items} hash table.
     * <p>
     *  用于查找{@link #items}哈希表中的项目的可重用密钥
     * 
     */
    final Item key;

    /**
     * A reusable key used to look for items in the {@link #items} hash table.
     * <p>
     *  用于查找{@link #items}哈希表中的项目的可重用密钥
     * 
     */
    final Item key2;

    /**
     * A reusable key used to look for items in the {@link #items} hash table.
     * <p>
     *  用于查找{@link #items}哈希表中的项目的可重用密钥
     * 
     */
    final Item key3;

    /**
     * A reusable key used to look for items in the {@link #items} hash table.
     * <p>
     *  用于查找{@link #items}哈希表中的项目的可重用密钥
     * 
     */
    final Item key4;

    /**
     * A type table used to temporarily store internal names that will not
     * necessarily be stored in the constant pool. This type table is used by
     * the control flow and data flow analysis algorithm used to compute stack
     * map frames from scratch. This array associates to each index <tt>i</tt>
     * the Item whose index is <tt>i</tt>. All Item objects stored in this array
     * are also stored in the {@link #items} hash table. These two arrays allow
     * to retrieve an Item from its index or, conversely, to get the index of an
     * Item from its value. Each Item stores an internal name in its
     * {@link Item#strVal1} field.
     * <p>
     * 用于临时存储不一定存储在常量池中的内部名称的类型表此类型表由用于从头计算堆栈映射帧的控制流和数据流分析算法使用该数组与每个索引相关联<tt> i </tt>其索引为<tt> i的项目</tt>存储在此数
     * 组中的所有Item对象也存储在{@link #items}哈希表中这两个数组允许从其索引或相反,要从其值获取项目的索引每个项目在其{@link Item#strVal1}字段中存储一个内部名称。
     * 
     */
    Item[] typeTable;

    /**
     * Number of elements in the {@link #typeTable} array.
     * <p>
     *  {@link #typeTable}数组中的元素数
     * 
     */
    private short typeCount;

    /**
     * The access flags of this class.
     * <p>
     *  这个类的访问标志
     * 
     */
    private int access;

    /**
     * The constant pool item that contains the internal name of this class.
     * <p>
     *  包含此类内部名称的常量池项目
     * 
     */
    private int name;

    /**
     * The internal name of this class.
     * <p>
     *  这个类的内部名称
     * 
     */
    String thisName;

    /**
     * The constant pool item that contains the signature of this class.
     * <p>
     * 包含此类签名的常量池项
     * 
     */
    private int signature;

    /**
     * The constant pool item that contains the internal name of the super class
     * of this class.
     * <p>
     *  包含此类的超级类的内部名称的常量池项
     * 
     */
    private int superName;

    /**
     * Number of interfaces implemented or extended by this class or interface.
     * <p>
     *  此类或接口实现或扩展的接口数
     * 
     */
    private int interfaceCount;

    /**
     * The interfaces implemented or extended by this class or interface. More
     * precisely, this array contains the indexes of the constant pool items
     * that contain the internal names of these interfaces.
     * <p>
     *  此类或接口实现或扩展的接口更准确地说,此数组包含包含这些接口的内部名称的常量池项目的索引
     * 
     */
    private int[] interfaces;

    /**
     * The index of the constant pool item that contains the name of the source
     * file from which this class was compiled.
     * <p>
     *  常量池项的索引,其中包含编译此类的源文件的名称
     * 
     */
    private int sourceFile;

    /**
     * The SourceDebug attribute of this class.
     * <p>
     *  该类的SourceDebug属性
     * 
     */
    private ByteVector sourceDebug;

    /**
     * The constant pool item that contains the name of the enclosing class of
     * this class.
     * <p>
     *  包含此类的封闭类名称的常量池项
     * 
     */
    private int enclosingMethodOwner;

    /**
     * The constant pool item that contains the name and descriptor of the
     * enclosing method of this class.
     * <p>
     *  常量池项,其中包含此类的封闭方法的名称和描述符
     * 
     */
    private int enclosingMethod;

    /**
     * The runtime visible annotations of this class.
     * <p>
     * 该类的运行时可见注释
     * 
     */
    private AnnotationWriter anns;

    /**
     * The runtime invisible annotations of this class.
     * <p>
     *  该类的运行时不可见注释
     * 
     */
    private AnnotationWriter ianns;

    /**
     * The runtime visible type annotations of this class.
     * <p>
     *  该类的运行时可见类型注释
     * 
     */
    private AnnotationWriter tanns;

    /**
     * The runtime invisible type annotations of this class.
     * <p>
     *  此类的运行时不可见类型注释
     * 
     */
    private AnnotationWriter itanns;

    /**
     * The non standard attributes of this class.
     * <p>
     *  这个类的非标准属性
     * 
     */
    private Attribute attrs;

    /**
     * The number of entries in the InnerClasses attribute.
     * <p>
     *  InnerClasses属性中的条目数
     * 
     */
    private int innerClassesCount;

    /**
     * The InnerClasses attribute.
     * <p>
     *  InnerClasses属性
     * 
     */
    private ByteVector innerClasses;

    /**
     * The number of entries in the BootstrapMethods attribute.
     * <p>
     *  BootstrapMethods属性中的条目数
     * 
     */
    int bootstrapMethodsCount;

    /**
     * The BootstrapMethods attribute.
     * <p>
     *  BootstrapMethods属性
     * 
     */
    ByteVector bootstrapMethods;

    /**
     * The fields of this class. These fields are stored in a linked list of
     * {@link FieldWriter} objects, linked to each other by their
     * {@link FieldWriter#fv} field. This field stores the first element of this
     * list.
     * <p>
     *  此类的字段这些字段存储在{@link FieldWriter}对象的链接列表中,通过其{@link FieldWriter#fv}字段彼此链接此字段存储此列表的第一个元素
     * 
     */
    FieldWriter firstField;

    /**
     * The fields of this class. These fields are stored in a linked list of
     * {@link FieldWriter} objects, linked to each other by their
     * {@link FieldWriter#fv} field. This field stores the last element of this
     * list.
     * <p>
     * 此类的字段这些字段存储在{@link FieldWriter}对象的链接列表中,通过其{@link FieldWriter#fv}字段彼此链接此字段存储此列表的最后一个元素
     * 
     */
    FieldWriter lastField;

    /**
     * The methods of this class. These methods are stored in a linked list of
     * {@link MethodWriter} objects, linked to each other by their
     * {@link MethodWriter#mv} field. This field stores the first element of
     * this list.
     * <p>
     *  此类的方法这些方法存储在{@link MethodWriter}对象的链接列表中,它们通过其{@link MethodWriter#mv}字段彼此链接。此字段存储此列表的第一个元素
     * 
     */
    MethodWriter firstMethod;

    /**
     * The methods of this class. These methods are stored in a linked list of
     * {@link MethodWriter} objects, linked to each other by their
     * {@link MethodWriter#mv} field. This field stores the last element of this
     * list.
     * <p>
     *  此类的方法这些方法存储在{@link MethodWriter}对象的链接列表中,通过其{@link MethodWriter#mv}字段彼此链接此字段存储此列表的最后一个元素
     * 
     */
    MethodWriter lastMethod;

    /**
     * <tt>true</tt> if the maximum stack size and number of local variables
     * must be automatically computed.
     * <p>
     *  <tt> true </tt>如果最大堆栈大小和局部变量的数量必须自动计算
     * 
     */
    private boolean computeMaxs;

    /**
     * <tt>true</tt> if the stack map frames must be recomputed from scratch.
     * <p>
     * <tt> true </tt>如果堆栈映射帧必须从头开始重新计算
     * 
     */
    private boolean computeFrames;

    /**
     * <tt>true</tt> if the stack map tables of this class are invalid. The
     * {@link MethodWriter#resizeInstructions} method cannot transform existing
     * stack map tables, and so produces potentially invalid classes when it is
     * executed. In this case the class is reread and rewritten with the
     * {@link #COMPUTE_FRAMES} option (the resizeInstructions method can resize
     * stack map tables when this option is used).
     * <p>
     *  <tt> true </tt>如果此类的堆栈映射表无效{@link MethodWriter#resizeInstructions}方法不能转换现有的堆栈映射表,因此在执行时会生成潜在的无效类在这种情
     * 况下,该类是使用{@link #COMPUTE_FRAMES}选项重新读取并重新写入(当使用此选项时,resizeInstructions方法可以调整堆栈映射表的大小)。
     * 
     */
    boolean invalidFrames;

    // ------------------------------------------------------------------------
    // Static initializer
    // ------------------------------------------------------------------------

    /**
     * Computes the instruction types of JVM opcodes.
     * <p>
     *  计算JVM操作码的指令类型
     * 
     */
    static {
        int i;
        byte[] b = new byte[220];
        String s = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADD"
                + "DDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAA"
                + "AAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
        for (i = 0; i < b.length; ++i) {
            b[i] = (byte) (s.charAt(i) - 'A');
        }
        TYPE = b;

        // code to generate the above string
        //
        // // SBYTE_INSN instructions
        // b[Constants.NEWARRAY] = SBYTE_INSN;
        // b[Constants.BIPUSH] = SBYTE_INSN;
        //
        // // SHORT_INSN instructions
        // b[Constants.SIPUSH] = SHORT_INSN;
        //
        // // (IMPL)VAR_INSN instructions
        // b[Constants.RET] = VAR_INSN;
        // for (i = Constants.ILOAD; i <= Constants.ALOAD; ++i) {
        // b[i] = VAR_INSN;
        // }
        // for (i = Constants.ISTORE; i <= Constants.ASTORE; ++i) {
        // b[i] = VAR_INSN;
        // }
        // for (i = 26; i <= 45; ++i) { // ILOAD_0 to ALOAD_3
        // b[i] = IMPLVAR_INSN;
        // }
        // for (i = 59; i <= 78; ++i) { // ISTORE_0 to ASTORE_3
        // b[i] = IMPLVAR_INSN;
        // }
        //
        // // TYPE_INSN instructions
        // b[Constants.NEW] = TYPE_INSN;
        // b[Constants.ANEWARRAY] = TYPE_INSN;
        // b[Constants.CHECKCAST] = TYPE_INSN;
        // b[Constants.INSTANCEOF] = TYPE_INSN;
        //
        // // (Set)FIELDORMETH_INSN instructions
        // for (i = Constants.GETSTATIC; i <= Constants.INVOKESTATIC; ++i) {
        // b[i] = FIELDORMETH_INSN;
        // }
        // b[Constants.INVOKEINTERFACE] = ITFMETH_INSN;
        // b[Constants.INVOKEDYNAMIC] = INDYMETH_INSN;
        //
        // // LABEL(W)_INSN instructions
        // for (i = Constants.IFEQ; i <= Constants.JSR; ++i) {
        // b[i] = LABEL_INSN;
        // }
        // b[Constants.IFNULL] = LABEL_INSN;
        // b[Constants.IFNONNULL] = LABEL_INSN;
        // b[200] = LABELW_INSN; // GOTO_W
        // b[201] = LABELW_INSN; // JSR_W
        // // temporary opcodes used internally by ASM - see Label and
        // MethodWriter
        // for (i = 202; i < 220; ++i) {
        // b[i] = LABEL_INSN;
        // }
        //
        // // LDC(_W) instructions
        // b[Constants.LDC] = LDC_INSN;
        // b[19] = LDCW_INSN; // LDC_W
        // b[20] = LDCW_INSN; // LDC2_W
        //
        // // special instructions
        // b[Constants.IINC] = IINC_INSN;
        // b[Constants.TABLESWITCH] = TABL_INSN;
        // b[Constants.LOOKUPSWITCH] = LOOK_INSN;
        // b[Constants.MULTIANEWARRAY] = MANA_INSN;
        // b[196] = WIDE_INSN; // WIDE
        //
        // for (i = 0; i < b.length; ++i) {
        // System.err.print((char)('A' + b[i]));
        // }
        // System.err.println();
    }

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new {@link ClassWriter} object.
     * 
     * <p>
     *  构造一个新的{@link ClassWriter}对象
     * 
     * 
     * @param flags
     *            option flags that can be used to modify the default behavior
     *            of this class. See {@link #COMPUTE_MAXS},
     *            {@link #COMPUTE_FRAMES}.
     */
    public ClassWriter(final int flags) {
        super(Opcodes.ASM5);
        index = 1;
        pool = new ByteVector();
        items = new Item[256];
        threshold = (int) (0.75d * items.length);
        key = new Item();
        key2 = new Item();
        key3 = new Item();
        key4 = new Item();
        this.computeMaxs = (flags & COMPUTE_MAXS) != 0;
        this.computeFrames = (flags & COMPUTE_FRAMES) != 0;
    }

    /**
     * Constructs a new {@link ClassWriter} object and enables optimizations for
     * "mostly add" bytecode transformations. These optimizations are the
     * following:
     * 
     * <ul>
     * <li>The constant pool from the original class is copied as is in the new
     * class, which saves time. New constant pool entries will be added at the
     * end if necessary, but unused constant pool entries <i>won't be
     * removed</i>.</li>
     * <li>Methods that are not transformed are copied as is in the new class,
     * directly from the original class bytecode (i.e. without emitting visit
     * events for all the method instructions), which saves a <i>lot</i> of
     * time. Untransformed methods are detected by the fact that the
     * {@link ClassReader} receives {@link MethodVisitor} objects that come from
     * a {@link ClassWriter} (and not from any other {@link ClassVisitor}
     * instance).</li>
     * </ul>
     * 
     * <p>
     *  构造一个新的{@link ClassWriter}对象,并启用"大部分添加"字节码转换的优化这些优化如下：
     * 
     * <ul>
     * <li>原始类中的常量池按照新的类复制,从而节省时间如果需要,将在最后添加新常量池条目,但不会删除未使用的常量池条目<i> </i> </li> <li>未转换的方法将按照新类中直接从原始类字节码(即不
     * 发布所有方法指令的访问事件)进行复制,从而节省了<i>批次< / i>时间未转换的方法通过{@link ClassReader}接收来自{@link ClassWriter}(而不是来自任何其他{@link ClassVisitor}
     * 实例)的{@link MethodVisitor}对象的事实来检测, LI>。
     * </ul>
     * 
     * 
     * @param classReader
     *            the {@link ClassReader} used to read the original class. It
     *            will be used to copy the entire constant pool from the
     *            original class and also to copy other fragments of original
     *            bytecode where applicable.
     * @param flags
     *            option flags that can be used to modify the default behavior
     *            of this class. <i>These option flags do not affect methods
     *            that are copied as is in the new class. This means that the
     *            maximum stack size nor the stack frames will be computed for
     *            these methods</i>. See {@link #COMPUTE_MAXS},
     *            {@link #COMPUTE_FRAMES}.
     */
    public ClassWriter(final ClassReader classReader, final int flags) {
        this(flags);
        classReader.copyPool(this);
        this.cr = classReader;
    }

    // ------------------------------------------------------------------------
    // Implementation of the ClassVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public final void visit(final int version, final int access,
            final String name, final String signature, final String superName,
            final String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = newClass(name);
        thisName = name;
        if (ClassReader.SIGNATURES && signature != null) {
            this.signature = newUTF8(signature);
        }
        this.superName = superName == null ? 0 : newClass(superName);
        if (interfaces != null && interfaces.length > 0) {
            interfaceCount = interfaces.length;
            this.interfaces = new int[interfaceCount];
            for (int i = 0; i < interfaceCount; ++i) {
                this.interfaces[i] = newClass(interfaces[i]);
            }
        }
    }

    @Override
    public final void visitSource(final String file, final String debug) {
        if (file != null) {
            sourceFile = newUTF8(file);
        }
        if (debug != null) {
            sourceDebug = new ByteVector().encodeUTF8(debug, 0,
                    Integer.MAX_VALUE);
        }
    }

    @Override
    public final void visitOuterClass(final String owner, final String name,
            final String desc) {
        enclosingMethodOwner = newClass(owner);
        if (name != null && desc != null) {
            enclosingMethod = newNameType(name, desc);
        }
    }

    @Override
    public final AnnotationVisitor visitAnnotation(final String desc,
            final boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write type, and reserve space for values count
        bv.putShort(newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this, true, bv, bv, 2);
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
    public final AnnotationVisitor visitTypeAnnotation(int typeRef,
            TypePath typePath, final String desc, final boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write target_type and target_info
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        // write type, and reserve space for values count
        bv.putShort(newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this, true, bv, bv,
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
    public final void visitAttribute(final Attribute attr) {
        attr.next = attrs;
        attrs = attr;
    }

    @Override
    public final void visitInnerClass(final String name,
            final String outerName, final String innerName, final int access) {
        if (innerClasses == null) {
            innerClasses = new ByteVector();
        }
        // Sec. 4.7.6 of the JVMS states "Every CONSTANT_Class_info entry in the
        // constant_pool table which represents a class or interface C that is
        // not a package member must have exactly one corresponding entry in the
        // classes array". To avoid duplicates we keep track in the intVal field
        // of the Item of each CONSTANT_Class_info entry C whether an inner
        // class entry has already been added for C (this field is unused for
        // class entries, and changing its value does not change the hashcode
        // and equality tests). If so we store the index of this inner class
        // entry (plus one) in intVal. This hack allows duplicate detection in
        // O(1) time.
        Item nameItem = newClassItem(name);
        if (nameItem.intVal == 0) {
            ++innerClassesCount;
            innerClasses.putShort(nameItem.index);
            innerClasses.putShort(outerName == null ? 0 : newClass(outerName));
            innerClasses.putShort(innerName == null ? 0 : newUTF8(innerName));
            innerClasses.putShort(access);
            nameItem.intVal = innerClassesCount;
        } else {
            // Compare the inner classes entry nameItem.intVal - 1 with the
            // arguments of this method and throw an exception if there is a
            // difference?
        }
    }

    @Override
    public final FieldVisitor visitField(final int access, final String name,
            final String desc, final String signature, final Object value) {
        return new FieldWriter(this, access, name, desc, signature, value);
    }

    @Override
    public final MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
        return new MethodWriter(this, access, name, desc, signature,
                exceptions, computeMaxs, computeFrames);
    }

    @Override
    public final void visitEnd() {
    }

    // ------------------------------------------------------------------------
    // Other public methods
    // ------------------------------------------------------------------------

    /**
     * Returns the bytecode of the class that was build with this class writer.
     * 
     * <p>
     *  返回使用此类作者构建的类的字节码
     * 
     * 
     * @return the bytecode of the class that was build with this class writer.
     */
    public byte[] toByteArray() {
        if (index > 0xFFFF) {
            throw new RuntimeException("Class file too large!");
        }
        // computes the real size of the bytecode of this class
        int size = 24 + 2 * interfaceCount;
        int nbFields = 0;
        FieldWriter fb = firstField;
        while (fb != null) {
            ++nbFields;
            size += fb.getSize();
            fb = (FieldWriter) fb.fv;
        }
        int nbMethods = 0;
        MethodWriter mb = firstMethod;
        while (mb != null) {
            ++nbMethods;
            size += mb.getSize();
            mb = (MethodWriter) mb.mv;
        }
        int attributeCount = 0;
        if (bootstrapMethods != null) {
            // we put it as first attribute in order to improve a bit
            // ClassReader.copyBootstrapMethods
            ++attributeCount;
            size += 8 + bootstrapMethods.length;
            newUTF8("BootstrapMethods");
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            ++attributeCount;
            size += 8;
            newUTF8("Signature");
        }
        if (sourceFile != 0) {
            ++attributeCount;
            size += 8;
            newUTF8("SourceFile");
        }
        if (sourceDebug != null) {
            ++attributeCount;
            size += sourceDebug.length + 6;
            newUTF8("SourceDebugExtension");
        }
        if (enclosingMethodOwner != 0) {
            ++attributeCount;
            size += 10;
            newUTF8("EnclosingMethod");
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            ++attributeCount;
            size += 6;
            newUTF8("Deprecated");
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((version & 0xFFFF) < Opcodes.V1_5
                    || (access & ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                ++attributeCount;
                size += 6;
                newUTF8("Synthetic");
            }
        }
        if (innerClasses != null) {
            ++attributeCount;
            size += 8 + innerClasses.length;
            newUTF8("InnerClasses");
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            ++attributeCount;
            size += 8 + anns.getSize();
            newUTF8("RuntimeVisibleAnnotations");
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            ++attributeCount;
            size += 8 + ianns.getSize();
            newUTF8("RuntimeInvisibleAnnotations");
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            ++attributeCount;
            size += 8 + tanns.getSize();
            newUTF8("RuntimeVisibleTypeAnnotations");
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            ++attributeCount;
            size += 8 + itanns.getSize();
            newUTF8("RuntimeInvisibleTypeAnnotations");
        }
        if (attrs != null) {
            attributeCount += attrs.getCount();
            size += attrs.getSize(this, null, 0, -1, -1);
        }
        size += pool.length;
        // allocates a byte vector of this size, in order to avoid unnecessary
        // arraycopy operations in the ByteVector.enlarge() method
        ByteVector out = new ByteVector(size);
        out.putInt(0xCAFEBABE).putInt(version);
        out.putShort(index).putByteArray(pool.data, 0, pool.length);
        int mask = Opcodes.ACC_DEPRECATED | ACC_SYNTHETIC_ATTRIBUTE
                | ((access & ACC_SYNTHETIC_ATTRIBUTE) / TO_ACC_SYNTHETIC);
        out.putShort(access & ~mask).putShort(name).putShort(superName);
        out.putShort(interfaceCount);
        for (int i = 0; i < interfaceCount; ++i) {
            out.putShort(interfaces[i]);
        }
        out.putShort(nbFields);
        fb = firstField;
        while (fb != null) {
            fb.put(out);
            fb = (FieldWriter) fb.fv;
        }
        out.putShort(nbMethods);
        mb = firstMethod;
        while (mb != null) {
            mb.put(out);
            mb = (MethodWriter) mb.mv;
        }
        out.putShort(attributeCount);
        if (bootstrapMethods != null) {
            out.putShort(newUTF8("BootstrapMethods"));
            out.putInt(bootstrapMethods.length + 2).putShort(
                    bootstrapMethodsCount);
            out.putByteArray(bootstrapMethods.data, 0, bootstrapMethods.length);
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            out.putShort(newUTF8("Signature")).putInt(2).putShort(signature);
        }
        if (sourceFile != 0) {
            out.putShort(newUTF8("SourceFile")).putInt(2).putShort(sourceFile);
        }
        if (sourceDebug != null) {
            int len = sourceDebug.length;
            out.putShort(newUTF8("SourceDebugExtension")).putInt(len);
            out.putByteArray(sourceDebug.data, 0, len);
        }
        if (enclosingMethodOwner != 0) {
            out.putShort(newUTF8("EnclosingMethod")).putInt(4);
            out.putShort(enclosingMethodOwner).putShort(enclosingMethod);
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            out.putShort(newUTF8("Deprecated")).putInt(0);
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((version & 0xFFFF) < Opcodes.V1_5
                    || (access & ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                out.putShort(newUTF8("Synthetic")).putInt(0);
            }
        }
        if (innerClasses != null) {
            out.putShort(newUTF8("InnerClasses"));
            out.putInt(innerClasses.length + 2).putShort(innerClassesCount);
            out.putByteArray(innerClasses.data, 0, innerClasses.length);
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            out.putShort(newUTF8("RuntimeVisibleAnnotations"));
            anns.put(out);
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            out.putShort(newUTF8("RuntimeInvisibleAnnotations"));
            ianns.put(out);
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            out.putShort(newUTF8("RuntimeVisibleTypeAnnotations"));
            tanns.put(out);
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            out.putShort(newUTF8("RuntimeInvisibleTypeAnnotations"));
            itanns.put(out);
        }
        if (attrs != null) {
            attrs.put(this, null, 0, -1, -1, out);
        }
        if (invalidFrames) {
            anns = null;
            ianns = null;
            attrs = null;
            innerClassesCount = 0;
            innerClasses = null;
            bootstrapMethodsCount = 0;
            bootstrapMethods = null;
            firstField = null;
            lastField = null;
            firstMethod = null;
            lastMethod = null;
            computeMaxs = false;
            computeFrames = true;
            invalidFrames = false;
            new ClassReader(out.data).accept(this, ClassReader.SKIP_FRAMES);
            return toByteArray();
        }
        return out.data;
    }

    // ------------------------------------------------------------------------
    // Utility methods: constant pool management
    // ------------------------------------------------------------------------

    /**
     * Adds a number or string constant to the constant pool of the class being
     * build. Does nothing if the constant pool already contains a similar item.
     * 
     * <p>
     * 将一个数字或字符串常量添加到正在构建的类的常量池如果常量池已经包含类似的项目,则不会执行任何操作
     * 
     * 
     * @param cst
     *            the value of the constant to be added to the constant pool.
     *            This parameter must be an {@link Integer}, a {@link Float}, a
     *            {@link Long}, a {@link Double}, a {@link String} or a
     *            {@link Type}.
     * @return a new or already existing constant item with the given value.
     */
    Item newConstItem(final Object cst) {
        if (cst instanceof Integer) {
            int val = ((Integer) cst).intValue();
            return newInteger(val);
        } else if (cst instanceof Byte) {
            int val = ((Byte) cst).intValue();
            return newInteger(val);
        } else if (cst instanceof Character) {
            int val = ((Character) cst).charValue();
            return newInteger(val);
        } else if (cst instanceof Short) {
            int val = ((Short) cst).intValue();
            return newInteger(val);
        } else if (cst instanceof Boolean) {
            int val = ((Boolean) cst).booleanValue() ? 1 : 0;
            return newInteger(val);
        } else if (cst instanceof Float) {
            float val = ((Float) cst).floatValue();
            return newFloat(val);
        } else if (cst instanceof Long) {
            long val = ((Long) cst).longValue();
            return newLong(val);
        } else if (cst instanceof Double) {
            double val = ((Double) cst).doubleValue();
            return newDouble(val);
        } else if (cst instanceof String) {
            return newString((String) cst);
        } else if (cst instanceof Type) {
            Type t = (Type) cst;
            int s = t.getSort();
            if (s == Type.OBJECT) {
                return newClassItem(t.getInternalName());
            } else if (s == Type.METHOD) {
                return newMethodTypeItem(t.getDescriptor());
            } else { // s == primitive type or array
                return newClassItem(t.getDescriptor());
            }
        } else if (cst instanceof Handle) {
            Handle h = (Handle) cst;
            return newHandleItem(h.tag, h.owner, h.name, h.desc, h.itf);
        } else {
            throw new IllegalArgumentException("value " + cst);
        }
    }

    /**
     * Adds a number or string constant to the constant pool of the class being
     * build. Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     *  向正在构建的类的常量池添加数字或字符串常量如果常量池已经包含类似的项目,则不会执行任何操作。此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>
     * 
     * 
     * @param cst
     *            the value of the constant to be added to the constant pool.
     *            This parameter must be an {@link Integer}, a {@link Float}, a
     *            {@link Long}, a {@link Double} or a {@link String}.
     * @return the index of a new or already existing constant item with the
     *         given value.
     */
    public int newConst(final Object cst) {
        return newConstItem(cst).index;
    }

    /**
     * Adds an UTF8 string to the constant pool of the class being build. Does
     * nothing if the constant pool already contains a similar item. <i>This
     * method is intended for {@link Attribute} sub classes, and is normally not
     * needed by class generators or adapters.</i>
     * 
     * <p>
     *  将UTF8字符串添加到正在构建的类的常量池如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param value
     *            the String value.
     * @return the index of a new or already existing UTF8 item.
     */
    public int newUTF8(final String value) {
        key.set(UTF8, value, null, null);
        Item result = get(key);
        if (result == null) {
            pool.putByte(UTF8).putUTF8(value);
            result = new Item(index++, key);
            put(result);
        }
        return result.index;
    }

    /**
     * Adds a class reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     * 为正在构建的类的常量池添加类引用如果常量池已经包含类似的项目,则不会执行任何操作<i>此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param value
     *            the internal name of the class.
     * @return a new or already existing class reference item.
     */
    Item newClassItem(final String value) {
        key2.set(CLASS, value, null, null);
        Item result = get(key2);
        if (result == null) {
            pool.put12(CLASS, newUTF8(value));
            result = new Item(index++, key2);
            put(result);
        }
        return result;
    }

    /**
     * Adds a class reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     *  为正在构建的类的常量池添加类引用如果常量池已经包含类似的项目,则不会执行任何操作<i>此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param value
     *            the internal name of the class.
     * @return the index of a new or already existing class reference item.
     */
    public int newClass(final String value) {
        return newClassItem(value).index;
    }

    /**
     * Adds a method type reference to the constant pool of the class being
     * build. Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     *  添加对正在构建的类的常量池的方法类型引用如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param methodDesc
     *            method descriptor of the method type.
     * @return a new or already existing method type reference item.
     */
    Item newMethodTypeItem(final String methodDesc) {
        key2.set(MTYPE, methodDesc, null, null);
        Item result = get(key2);
        if (result == null) {
            pool.put12(MTYPE, newUTF8(methodDesc));
            result = new Item(index++, key2);
            put(result);
        }
        return result;
    }

    /**
     * Adds a method type reference to the constant pool of the class being
     * build. Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     * 添加对正在构建的类的常量池的方法类型引用如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param methodDesc
     *            method descriptor of the method type.
     * @return the index of a new or already existing method type reference
     *         item.
     */
    public int newMethodType(final String methodDesc) {
        return newMethodTypeItem(methodDesc).index;
    }

    /**
     * Adds a handle to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by
     * class generators or adapters.</i>
     * 
     * <p>
     *  为正在构建的类的常量池添加一个句柄如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器< / i>的
     * 
     * 
     * @param tag
     *            the kind of this handle. Must be {@link Opcodes#H_GETFIELD},
     *            {@link Opcodes#H_GETSTATIC}, {@link Opcodes#H_PUTFIELD},
     *            {@link Opcodes#H_PUTSTATIC}, {@link Opcodes#H_INVOKEVIRTUAL},
     *            {@link Opcodes#H_INVOKESTATIC},
     *            {@link Opcodes#H_INVOKESPECIAL},
     *            {@link Opcodes#H_NEWINVOKESPECIAL} or
     *            {@link Opcodes#H_INVOKEINTERFACE}.
     * @param owner
     *            the internal name of the field or method owner class.
     * @param name
     *            the name of the field or method.
     * @param desc
     *            the descriptor of the field or method.
     * @param itf
     *            true if the owner is an interface.
     * @return a new or an already existing method type reference item.
     */
    Item newHandleItem(final int tag, final String owner, final String name,
            final String desc, final boolean itf) {
        key4.set(HANDLE_BASE + tag, owner, name, desc);
        Item result = get(key4);
        if (result == null) {
            if (tag <= Opcodes.H_PUTSTATIC) {
                put112(HANDLE, tag, newField(owner, name, desc));
            } else {
                put112(HANDLE,
                        tag,
                        newMethod(owner, name, desc, itf));
            }
            result = new Item(index++, key4);
            put(result);
        }
        return result;
    }

    /**
     * Adds a handle to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by
     * class generators or adapters.</i>
     * 
     * <p>
     *  为正在构建的类的常量池添加一个句柄如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器< / i>的
     * 
     * 
     * @param tag
     *            the kind of this handle. Must be {@link Opcodes#H_GETFIELD},
     *            {@link Opcodes#H_GETSTATIC}, {@link Opcodes#H_PUTFIELD},
     *            {@link Opcodes#H_PUTSTATIC}, {@link Opcodes#H_INVOKEVIRTUAL},
     *            {@link Opcodes#H_INVOKESTATIC},
     *            {@link Opcodes#H_INVOKESPECIAL},
     *            {@link Opcodes#H_NEWINVOKESPECIAL} or
     *            {@link Opcodes#H_INVOKEINTERFACE}.
     * @param owner
     *            the internal name of the field or method owner class.
     * @param name
     *            the name of the field or method.
     * @param desc
     *            the descriptor of the field or method.
     * @return the index of a new or already existing method type reference
     *         item.
     *
     * @deprecated this method is superseded by
     *             {@link #newHandle(int, String, String, String, boolean)}.
     */
    @Deprecated
    public int newHandle(final int tag, final String owner, final String name,
            final String desc) {
        return newHandle(tag, owner, name, desc, tag == Opcodes.H_INVOKEINTERFACE);
    }

    /**
     * Adds a handle to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by
     * class generators or adapters.</i>
     *
     * <p>
     * 为正在构建的类的常量池添加一个句柄如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器< / i>的
     * 
     * 
     * @param tag
     *            the kind of this handle. Must be {@link Opcodes#H_GETFIELD},
     *            {@link Opcodes#H_GETSTATIC}, {@link Opcodes#H_PUTFIELD},
     *            {@link Opcodes#H_PUTSTATIC}, {@link Opcodes#H_INVOKEVIRTUAL},
     *            {@link Opcodes#H_INVOKESTATIC},
     *            {@link Opcodes#H_INVOKESPECIAL},
     *            {@link Opcodes#H_NEWINVOKESPECIAL} or
     *            {@link Opcodes#H_INVOKEINTERFACE}.
     * @param owner
     *            the internal name of the field or method owner class.
     * @param name
     *            the name of the field or method.
     * @param desc
     *            the descriptor of the field or method.
     * @param itf
     *            true if the owner is an interface.
     * @return the index of a new or already existing method type reference
     *         item.
     */
    public int newHandle(final int tag, final String owner, final String name,
            final String desc, final boolean itf) {
        return newHandleItem(tag, owner, name, desc, itf).index;
    }

    /**
     * Adds an invokedynamic reference to the constant pool of the class being
     * build. Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     *  添加对正在构建的类的常量池的invokedynamic引用如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param name
     *            name of the invoked method.
     * @param desc
     *            descriptor of the invoke method.
     * @param bsm
     *            the bootstrap method.
     * @param bsmArgs
     *            the bootstrap method constant arguments.
     * 
     * @return a new or an already existing invokedynamic type reference item.
     */
    Item newInvokeDynamicItem(final String name, final String desc,
            final Handle bsm, final Object... bsmArgs) {
        // cache for performance
        ByteVector bootstrapMethods = this.bootstrapMethods;
        if (bootstrapMethods == null) {
            bootstrapMethods = this.bootstrapMethods = new ByteVector();
        }

        int position = bootstrapMethods.length; // record current position

        int hashCode = bsm.hashCode();
        bootstrapMethods.putShort(newHandle(bsm.tag, bsm.owner, bsm.name,
                bsm.desc, bsm.isInterface()));

        int argsLength = bsmArgs.length;
        bootstrapMethods.putShort(argsLength);

        for (int i = 0; i < argsLength; i++) {
            Object bsmArg = bsmArgs[i];
            hashCode ^= bsmArg.hashCode();
            bootstrapMethods.putShort(newConst(bsmArg));
        }

        byte[] data = bootstrapMethods.data;
        int length = (1 + 1 + argsLength) << 1; // (bsm + argCount + arguments)
        hashCode &= 0x7FFFFFFF;
        Item result = items[hashCode % items.length];
        loop: while (result != null) {
            if (result.type != BSM || result.hashCode != hashCode) {
                result = result.next;
                continue;
            }

            // because the data encode the size of the argument
            // we don't need to test if these size are equals
            int resultPosition = result.intVal;
            for (int p = 0; p < length; p++) {
                if (data[position + p] != data[resultPosition + p]) {
                    result = result.next;
                    continue loop;
                }
            }
            break;
        }

        int bootstrapMethodIndex;
        if (result != null) {
            bootstrapMethodIndex = result.index;
            bootstrapMethods.length = position; // revert to old position
        } else {
            bootstrapMethodIndex = bootstrapMethodsCount++;
            result = new Item(bootstrapMethodIndex);
            result.set(position, hashCode);
            put(result);
        }

        // now, create the InvokeDynamic constant
        key3.set(name, desc, bootstrapMethodIndex);
        result = get(key3);
        if (result == null) {
            put122(INDY, bootstrapMethodIndex, newNameType(name, desc));
            result = new Item(index++, key3);
            put(result);
        }
        return result;
    }

    /**
     * Adds an invokedynamic reference to the constant pool of the class being
     * build. Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     *  添加对正在构建的类的常量池的invokedynamic引用如果常量池已经包含类似的项目,则不会执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param name
     *            name of the invoked method.
     * @param desc
     *            descriptor of the invoke method.
     * @param bsm
     *            the bootstrap method.
     * @param bsmArgs
     *            the bootstrap method constant arguments.
     * 
     * @return the index of a new or already existing invokedynamic reference
     *         item.
     */
    public int newInvokeDynamic(final String name, final String desc,
            final Handle bsm, final Object... bsmArgs) {
        return newInvokeDynamicItem(name, desc, bsm, bsmArgs).index;
    }

    /**
     * Adds a field reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * 
     * <p>
     * 为正在构建的类的常量池添加字段引用如果常量池已经包含类似的项目,则不会执行任何操作
     * 
     * 
     * @param owner
     *            the internal name of the field's owner class.
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor.
     * @return a new or already existing field reference item.
     */
    Item newFieldItem(final String owner, final String name, final String desc) {
        key3.set(FIELD, owner, name, desc);
        Item result = get(key3);
        if (result == null) {
            put122(FIELD, newClass(owner), newNameType(name, desc));
            result = new Item(index++, key3);
            put(result);
        }
        return result;
    }

    /**
     * Adds a field reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     *  添加对正在构建的类的常量池的字段引用如果常量池已经包含类似的项目,则不会执行任何操作<i>此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param owner
     *            the internal name of the field's owner class.
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor.
     * @return the index of a new or already existing field reference item.
     */
    public int newField(final String owner, final String name, final String desc) {
        return newFieldItem(owner, name, desc).index;
    }

    /**
     * Adds a method reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * 
     * <p>
     *  添加对正在构建的类的常量池的方法引用如果常量池已经包含类似的项目,则不会执行任何操作
     * 
     * 
     * @param owner
     *            the internal name of the method's owner class.
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor.
     * @param itf
     *            <tt>true</tt> if <tt>owner</tt> is an interface.
     * @return a new or already existing method reference item.
     */
    Item newMethodItem(final String owner, final String name,
            final String desc, final boolean itf) {
        int type = itf ? IMETH : METH;
        key3.set(type, owner, name, desc);
        Item result = get(key3);
        if (result == null) {
            put122(type, newClass(owner), newNameType(name, desc));
            result = new Item(index++, key3);
            put(result);
        }
        return result;
    }

    /**
     * Adds a method reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     * 
     * <p>
     * 添加对正在构建的类的常量池的方法引用如果常量池已经包含相似的项目,则不执行任何操作此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param owner
     *            the internal name of the method's owner class.
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor.
     * @param itf
     *            <tt>true</tt> if <tt>owner</tt> is an interface.
     * @return the index of a new or already existing method reference item.
     */
    public int newMethod(final String owner, final String name,
            final String desc, final boolean itf) {
        return newMethodItem(owner, name, desc, itf).index;
    }

    /**
     * Adds an integer to the constant pool of the class being build. Does
     * nothing if the constant pool already contains a similar item.
     * 
     * <p>
     *  在正在构建的类的常量池中添加一个整数如果常量池已经包含类似的项目,则不会有任何异常
     * 
     * 
     * @param value
     *            the int value.
     * @return a new or already existing int item.
     */
    Item newInteger(final int value) {
        key.set(value);
        Item result = get(key);
        if (result == null) {
            pool.putByte(INT).putInt(value);
            result = new Item(index++, key);
            put(result);
        }
        return result;
    }

    /**
     * Adds a float to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     * 
     * <p>
     *  在正在构建的类的常量池中添加一个float如果常量池已经包含类似的项目,则不会有任何异常
     * 
     * 
     * @param value
     *            the float value.
     * @return a new or already existing float item.
     */
    Item newFloat(final float value) {
        key.set(value);
        Item result = get(key);
        if (result == null) {
            pool.putByte(FLOAT).putInt(key.intVal);
            result = new Item(index++, key);
            put(result);
        }
        return result;
    }

    /**
     * Adds a long to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     * 
     * <p>
     *  对正在构建的类的常量池添加很长时间如果常量池已经包含类似的项目,则不会执行任何操作
     * 
     * 
     * @param value
     *            the long value.
     * @return a new or already existing long item.
     */
    Item newLong(final long value) {
        key.set(value);
        Item result = get(key);
        if (result == null) {
            pool.putByte(LONG).putLong(value);
            result = new Item(index, key);
            index += 2;
            put(result);
        }
        return result;
    }

    /**
     * Adds a double to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     * 
     * <p>
     *  在正在构建的类的常量池中添加一个double如果常量池已经包含类似的项目,则不会有任何异常
     * 
     * 
     * @param value
     *            the double value.
     * @return a new or already existing double item.
     */
    Item newDouble(final double value) {
        key.set(value);
        Item result = get(key);
        if (result == null) {
            pool.putByte(DOUBLE).putLong(key.longVal);
            result = new Item(index, key);
            index += 2;
            put(result);
        }
        return result;
    }

    /**
     * Adds a string to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     * 
     * <p>
     * 将一个字符串添加到正在构建的类的常量池如果常量池已经包含类似的项目,则不会发生任何异常
     * 
     * 
     * @param value
     *            the String value.
     * @return a new or already existing string item.
     */
    private Item newString(final String value) {
        key2.set(STR, value, null, null);
        Item result = get(key2);
        if (result == null) {
            pool.put12(STR, newUTF8(value));
            result = new Item(index++, key2);
            put(result);
        }
        return result;
    }

    /**
     * Adds a name and type to the constant pool of the class being build. Does
     * nothing if the constant pool already contains a similar item. <i>This
     * method is intended for {@link Attribute} sub classes, and is normally not
     * needed by class generators or adapters.</i>
     * 
     * <p>
     *  将一个名称和类型添加到正在构建的类的常量池中如果常量池已经包含相似的项目,则不执行任何操作。此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>的
     * 
     * 
     * @param name
     *            a name.
     * @param desc
     *            a type descriptor.
     * @return the index of a new or already existing name and type item.
     */
    public int newNameType(final String name, final String desc) {
        return newNameTypeItem(name, desc).index;
    }

    /**
     * Adds a name and type to the constant pool of the class being build. Does
     * nothing if the constant pool already contains a similar item.
     * 
     * <p>
     *  将一个名称和类型添加到正在构建的类的常量池如果常量池已经包含类似的项目,则不会执行任何操作
     * 
     * 
     * @param name
     *            a name.
     * @param desc
     *            a type descriptor.
     * @return a new or already existing name and type item.
     */
    Item newNameTypeItem(final String name, final String desc) {
        key2.set(NAME_TYPE, name, desc, null);
        Item result = get(key2);
        if (result == null) {
            put122(NAME_TYPE, newUTF8(name), newUTF8(desc));
            result = new Item(index++, key2);
            put(result);
        }
        return result;
    }

    /**
     * Adds the given internal name to {@link #typeTable} and returns its index.
     * Does nothing if the type table already contains this internal name.
     * 
     * <p>
     *  将给定的内部名称添加到{@link #typeTable}并返回其索引如果类型表已包含此内部名称,则不会执行任何操作
     * 
     * 
     * @param type
     *            the internal name to be added to the type table.
     * @return the index of this internal name in the type table.
     */
    int addType(final String type) {
        key.set(TYPE_NORMAL, type, null, null);
        Item result = get(key);
        if (result == null) {
            result = addType(key);
        }
        return result.index;
    }

    /**
     * Adds the given "uninitialized" type to {@link #typeTable} and returns its
     * index. This method is used for UNINITIALIZED types, made of an internal
     * name and a bytecode offset.
     * 
     * <p>
     * 将给定的"未初始化"类型添加到{@link #typeTable}并返回其索引此方法用于UNINITIALIZED类型,由内部名称和字节码偏移量组成
     * 
     * 
     * @param type
     *            the internal name to be added to the type table.
     * @param offset
     *            the bytecode offset of the NEW instruction that created this
     *            UNINITIALIZED type value.
     * @return the index of this internal name in the type table.
     */
    int addUninitializedType(final String type, final int offset) {
        key.type = TYPE_UNINIT;
        key.intVal = offset;
        key.strVal1 = type;
        key.hashCode = 0x7FFFFFFF & (TYPE_UNINIT + type.hashCode() + offset);
        Item result = get(key);
        if (result == null) {
            result = addType(key);
        }
        return result.index;
    }

    /**
     * Adds the given Item to {@link #typeTable}.
     * 
     * <p>
     *  将给定的项目添加到{@link #typeTable}
     * 
     * 
     * @param item
     *            the value to be added to the type table.
     * @return the added Item, which a new Item instance with the same value as
     *         the given Item.
     */
    private Item addType(final Item item) {
        ++typeCount;
        Item result = new Item(typeCount, key);
        put(result);
        if (typeTable == null) {
            typeTable = new Item[16];
        }
        if (typeCount == typeTable.length) {
            Item[] newTable = new Item[2 * typeTable.length];
            System.arraycopy(typeTable, 0, newTable, 0, typeTable.length);
            typeTable = newTable;
        }
        typeTable[typeCount] = result;
        return result;
    }

    /**
     * Returns the index of the common super type of the two given types. This
     * method calls {@link #getCommonSuperClass} and caches the result in the
     * {@link #items} hash table to speedup future calls with the same
     * parameters.
     * 
     * <p>
     *  返回两个给定类型的公共超类型的索引此方法调用{@link #getCommonSuperClass}并将结果缓存在{@link #items}哈希表中,以加速未来具有相同参数的调用
     * 
     * 
     * @param type1
     *            index of an internal name in {@link #typeTable}.
     * @param type2
     *            index of an internal name in {@link #typeTable}.
     * @return the index of the common super type of the two given types.
     */
    int getMergedType(final int type1, final int type2) {
        key2.type = TYPE_MERGED;
        key2.longVal = type1 | (((long) type2) << 32);
        key2.hashCode = 0x7FFFFFFF & (TYPE_MERGED + type1 + type2);
        Item result = get(key2);
        if (result == null) {
            String t = typeTable[type1].strVal1;
            String u = typeTable[type2].strVal1;
            key2.intVal = addType(getCommonSuperClass(t, u));
            result = new Item((short) 0, key2);
            put(result);
        }
        return result.intVal;
    }

    /**
     * Returns the common super type of the two given types. The default
     * implementation of this method <i>loads</i> the two given classes and uses
     * the java.lang.Class methods to find the common super class. It can be
     * overridden to compute this common super type in other ways, in particular
     * without actually loading any class, or to take into account the class
     * that is currently being generated by this ClassWriter, which can of
     * course not be loaded since it is under construction.
     * 
     * <p>
     * 返回两个给定类型的常用超类型此方法的默认实现<i>加载两个给定的类,并使用javalangClass方法来查找公共超类可以覆盖以计算此常用超类型其他方式,特别是没有实际加载任何类,或者考虑到当前正在由此
     * ClassWriter生成的类,当然这个类可能由于正在构建中而被加载。
     * 
     * 
     * @param type1
     *            the internal name of a class.
     * @param type2
     *            the internal name of another class.
     * @return the internal name of the common super class of the two given
     *         classes.
     */
    protected String getCommonSuperClass(final String type1, final String type2) {
        Class<?> c, d;
        // SPRING PATCH: PREFER APPLICATION CLASSLOADER
        ClassLoader classLoader = getClassLoader();
        try {
            c = Class.forName(type1.replace('/', '.'), false, classLoader);
            d = Class.forName(type2.replace('/', '.'), false, classLoader);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }
    }

    // SPRING PATCH: PREFER THREAD CONTEXT CLASSLOADER FOR APPLICATION CLASSES
    protected ClassLoader getClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        return (classLoader != null ? classLoader : getClass().getClassLoader());
    }

    /**
     * Returns the constant pool's hash table item which is equal to the given
     * item.
     * 
     * <p>
     *  返回等于给定项的常量池的哈希表项
     * 
     * 
     * @param key
     *            a constant pool item.
     * @return the constant pool's hash table item which is equal to the given
     *         item, or <tt>null</tt> if there is no such item.
     */
    private Item get(final Item key) {
        Item i = items[key.hashCode % items.length];
        while (i != null && (i.type != key.type || !key.isEqualTo(i))) {
            i = i.next;
        }
        return i;
    }

    /**
     * Puts the given item in the constant pool's hash table. The hash table
     * <i>must</i> not already contains this item.
     * 
     * <p>
     *  将给定项目放在常量池的哈希表中哈希表<i>必须</i>不包含此项目
     * 
     * 
     * @param i
     *            the item to be added to the constant pool's hash table.
     */
    private void put(final Item i) {
        if (index + typeCount > threshold) {
            int ll = items.length;
            int nl = ll * 2 + 1;
            Item[] newItems = new Item[nl];
            for (int l = ll - 1; l >= 0; --l) {
                Item j = items[l];
                while (j != null) {
                    int index = j.hashCode % newItems.length;
                    Item k = j.next;
                    j.next = newItems[index];
                    newItems[index] = j;
                    j = k;
                }
            }
            items = newItems;
            threshold = (int) (nl * 0.75);
        }
        int index = i.hashCode % items.length;
        i.next = items[index];
        items[index] = i;
    }

    /**
     * Puts one byte and two shorts into the constant pool.
     * 
     * <p>
     *  将一个字节和两个短裤放入常量池中
     * 
     * 
     * @param b
     *            a byte.
     * @param s1
     *            a short.
     * @param s2
     *            another short.
     */
    private void put122(final int b, final int s1, final int s2) {
        pool.put12(b, s1).putShort(s2);
    }

    /**
     * Puts two bytes and one short into the constant pool.
     * 
     * <p>
     *  将两个字节和一个短整数放入常量池
     * 
     * @param b1
     *            a byte.
     * @param b2
     *            another byte.
     * @param s
     *            a short.
     */
    private void put112(final int b1, final int b2, final int s) {
        pool.put11(b1, b2).putShort(s);
    }
}
