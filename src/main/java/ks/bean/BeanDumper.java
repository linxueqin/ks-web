package ks.bean;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import ks.util.TextUtil;

public class BeanDumper implements Opcodes {
    
    public static byte[] dump(String typeName, Field[] fields) throws Exception {
        return dump(typeName, fields, "java/lang/Object", null);
    }
    
    public static byte[] dump(String typeName, Field[] fields, String parentType, String[] interfaces) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;
        
        parentType = (parentType == null) ? "java.lang.Object" : parentType;
        
        parentType = parentType.replace(".", "/");
        
        typeName = typeName.replace(".", "/");
        
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, typeName, null, parentType, interfaces);

        for (Field field : fields) {
            cw.visitField(ACC_PRIVATE, field.getName(), field.getDesc(), field.getSignature(), null).visitEnd();
        }
        
        
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, parentType, "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        
        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldTypeDesc = field.getDesc();
            
            // GET
            mv = cw.visitMethod(ACC_PUBLIC, "get" + upperFirst(fieldName), "()" + fieldTypeDesc, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, typeName, fieldName, fieldTypeDesc);
            if ("I".equals(fieldTypeDesc) || "B".equals(fieldTypeDesc) || "C".equals(fieldTypeDesc) || "S".equals(fieldTypeDesc) || "Z".equals(fieldTypeDesc)) {
                mv.visitInsn(IRETURN);
            } else if ("J".equals(fieldTypeDesc)) {
                mv.visitInsn(LRETURN);
            } else if ("F".equals(fieldTypeDesc)) {
                mv.visitInsn(FRETURN);
            } else if ("D".equals(fieldTypeDesc)) {
                mv.visitInsn(DRETURN);
            } else {
                mv.visitInsn(ARETURN);
            }
            
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            
            // SET
            mv = cw.visitMethod(ACC_PUBLIC, "set" + upperFirst(fieldName), "(" + fieldTypeDesc + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            
            if ("I".equals(fieldTypeDesc) || "B".equals(fieldTypeDesc) || "C".equals(fieldTypeDesc) || "S".equals(fieldTypeDesc) || "Z".equals(fieldTypeDesc)) {
                mv.visitVarInsn(ILOAD, 1);
            } else if ("J".equals(fieldTypeDesc)) {
                mv.visitVarInsn(LLOAD, 1);
            } else if ("F".equals(fieldTypeDesc)) {
                mv.visitVarInsn(FLOAD, 1);
            } else if ("D".equals(fieldTypeDesc)) {
                mv.visitVarInsn(DLOAD, 1);
            } else {
                mv.visitVarInsn(ALOAD, 1);
            }
            
            mv.visitFieldInsn(PUTFIELD, typeName, fieldName, fieldTypeDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        
        cw.visitEnd();
        
        return cw.toByteArray();
    }

    private static String upperFirst(String text) {
        return TextUtil.upperFirst(text);
    }
}
