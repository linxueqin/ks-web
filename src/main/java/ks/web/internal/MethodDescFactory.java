/**
 * 
 */
package ks.web.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ks.bean.Field;
import ks.bean.FieldImpl;

/**
 * @author xueqin.lin
 *
 */
public class MethodDescFactory {

    public static MethodDesc generate(Method method) throws Exception {
        MethodDescFactory factory = new MethodDescFactory(method);
        factory.parse();
        return factory.methodDesc;
    }
    
    public static List<MethodDesc> generate(Class<?> type) throws Exception {
        MethodDescFactory factory = new MethodDescFactory(type);
        factory.parse();
        return factory.methodDescs;
    };

    private List<MethodDesc> methodDescs = new ArrayList<MethodDesc>();

    private MethodDescImpl methodDesc;

    private Method method;

    private Class<?> type;

    /**
     * @param type
     */
    private MethodDescFactory(Class<?> type) {
        super();
        this.type = type;
    }

    /**
     * 
     */
    private MethodDescFactory(Method method) {
        this.method = method;
    }

    public void parse() throws Exception {
        Class<?> targetType = type;
        if (method != null) {
            targetType = method.getDeclaringClass();
        }
        ClassReader cr = new ClassReader(targetType.getName());
        Cv cv = new Cv(Opcodes.ASM5);
        cr.accept(cv, 0);
    }

    /**
     * 类访问器
     */
    class Cv extends ClassVisitor {

        /**
         * @param api
         */
        public Cv(int api) {
            super(api);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.objectweb.asm.ClassVisitor#visitMethod(int,
         * java.lang.String, java.lang.String, java.lang.String,
         * java.lang.String[])
         */
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (Modifier.isStatic(access) || "<init>".equals(name)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }

            if (method == null) {
                methodDesc = new MethodDescImpl();
                methodDesc.setName(name);
                methodDesc.setDesc(desc);
                methodDesc.setTargetType(type.getName());
                methodDesc.setReturnType(getReturnType(desc));
                methodDesc.setReturnTypeSignature(getReturnTypeSignature(signature));

                int parameterLength = org.objectweb.asm.Type.getArgumentTypes(desc).length;
                methodDesc.setParameterTypes(new Field[parameterLength]);
                
                return new Mv(api);
            } else if (name.equals(method.getName()) && desc.equals(Type.getMethodDescriptor(method))) {
                methodDesc = new MethodDescImpl();
                methodDesc.setName(name);
                methodDesc.setDesc(desc);
                methodDesc.setTargetType(method.getDeclaringClass().getName());
                methodDesc.setReturnType(getReturnType(desc));
                methodDesc.setReturnTypeSignature(getReturnTypeSignature(signature));

                int parameterLength = Type.getArgumentTypes(desc).length;
                methodDesc.setParameterTypes(new Field[parameterLength]);

                return new Mv(api);
            } else {
                return null;
            }

        }

        protected String getReturnType(String methodDesc) {
            return methodDesc.substring(methodDesc.indexOf(")") + 1);
        }

        protected String getReturnTypeSignature(String methodSignature) {
            if (methodSignature == null) {
                return null;
            }

            String result = methodSignature.substring(methodSignature.indexOf(")") + 1);

            return result.indexOf(">") > 0 ? result : null;
        }

        /**
         * 方法访问器
         */
        class Mv extends MethodVisitor {

            private int fillIndex;

            /**
             * @param api
             */
            public Mv(int api) {
                super(api);
            }

            /*
             * (non-Javadoc)
             * 
             * @see
             * org.objectweb.asm.MethodVisitor#visitLocalVariable(java.lang.
             * String, java.lang.String, java.lang.String,
             * org.objectweb.asm.Label, org.objectweb.asm.Label, int)
             */
            @Override
            public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
                    int index) {
                if (index > 0 && fillIndex < methodDesc.getParameterTypes().length) {
                    FieldImpl parameter = new FieldImpl();
                    parameter.setName(name);
                    parameter.setDesc(desc);
                    parameter.setSignature(signature);
                    methodDesc.getParameterTypes()[fillIndex] = parameter;
                    fillIndex++;
                }

                super.visitLocalVariable(name, desc, signature, start, end, index);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.objectweb.asm.MethodVisitor#visitEnd()
             */
            @Override
            public void visitEnd() {
                methodDescs.add(methodDesc);
                super.visitEnd();
            }

        }

    }
}
