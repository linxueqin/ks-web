package ks.web.internal;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ks.bean.Field;
import ks.util.TextUtil;
import ks.web.InvokeHandler;
import ks.web.ModelAndView;
import ks.web.PageHandler;

/**
 * @author xueqin.lin
 * 处理类型导出
 * 需要自动包装的类型不能是原始数据类型
 * 当返回值是ModleAndView类型时, Handler类型将实现PageHandler接口
 */
public class HandlerDumper implements Opcodes {

	private static String INVOKE_HANDLER_NAME = Type.getInternalName(InvokeHandler.class);
	
	private static List<String> WRAP_TYPES = new ArrayList<String>();
	
	static {
		WRAP_TYPES.add(Type.getDescriptor(HttpServletRequest.class));
		WRAP_TYPES.add(Type.getDescriptor(HttpServletResponse.class));
		WRAP_TYPES.add(Type.getDescriptor(HttpSession.class));
	}
	
	@SuppressWarnings("rawtypes")
    public static void setInvokeHandlerType(Class<? extends InvokeHandler> parentHandlerType) {
		INVOKE_HANDLER_NAME = Type.getInternalName(parentHandlerType);
	}
	
	public static void addWrapType(Class<?> type) {
		WRAP_TYPES.add(Type.getDescriptor(type));
	}
	
	public static boolean isWrapType(Class<?> type) {
		return WRAP_TYPES.contains(Type.getDescriptor(type));
	}
	
    public static byte[] dump(MethodDesc methodDesc, String pkg) throws Exception {
    	ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;

        String[] ifs = null;
        
        // 如果方法返回值类型是ModelAndView,则生成的类型将实现PageHandler接口
        if (Type.getDescriptor(ModelAndView.class).equals(methodDesc.getReturnType())) {
            ifs = new String[1];
            ifs[0] = Type.getInternalName(PageHandler.class);
        }
        
        pkg = pkg.replace(".", "/");

        String typeName = pkg + "/Handler";

        String targetType = methodDesc.getTargetType().replace(".", "/");
        String targetTypeDesc = "L" + targetType + ";";

        String requestType = pkg + "/Request";
        String requestTypeDesc = "L" + requestType + ";";

        String responseType = pkg + "/Response";
        String responseTypeDesc = "L" + responseType + ";";

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, typeName, "L" + INVOKE_HANDLER_NAME + "<" + responseTypeDesc + ">;", INVOKE_HANDLER_NAME, ifs);

        {
             fv = cw.visitField(ACC_PRIVATE, "target", targetTypeDesc, null, null);
             fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, INVOKE_HANDLER_NAME, "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke",
                    "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)" + responseTypeDesc, null,
                    new String[] { "java/lang/Exception" });
            mv.visitCode();

            mv.visitTypeInsn(NEW, responseType);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, responseType, "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 3);

            int markPosition = 4;

            Field[] parameters = methodDesc.getParameterTypes();
            if (parameters.length > 0) {

                markPosition = 5;

                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitLdcInsn(Type.getType(requestTypeDesc));
                mv.visitMethodInsn(INVOKEVIRTUAL, 
                		typeName, 
                		"read",
                        "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/Class;)Ljava/lang/Object;",
                        false);
                mv.visitTypeInsn(CHECKCAST, requestType);
                mv.visitVarInsn(ASTORE, 4);
                
                
                for (Field field : parameters) {
            		if (WRAP_TYPES != null && WRAP_TYPES.contains(field.getDesc())) {
            			mv.visitVarInsn(ALOAD, 4);
            			mv.visitVarInsn(ALOAD, 0);
            			mv.visitVarInsn(ALOAD, 1);
            			mv.visitVarInsn(ALOAD, 2);
            			mv.visitLdcInsn(Type.getType(field.getDesc()));
            			mv.visitMethodInsn(INVOKEVIRTUAL, 
                        		typeName, 
                        		"fetch",
                                "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/Class;)Ljava/lang/Object;",
                                false);
            			mv.visitTypeInsn(CHECKCAST, Type.getType(field.getDesc()).getInternalName());
            			mv.visitMethodInsn(INVOKEVIRTUAL, requestType, "set" + upperFirst(field.getName()), "(" + field.getDesc() + ")V", false);
            		}
				}
                
            }

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, typeName, "target", targetTypeDesc);

            if (parameters.length > 0) {
                for (Field field : parameters) {
                	mv.visitVarInsn(ALOAD, 4);
                    mv.visitMethodInsn(INVOKEVIRTUAL, requestType, "get" + upperFirst(field.getName()), "()" + field.getDesc(), false);
                }
            }

            mv.visitMethodInsn(INVOKEVIRTUAL, targetType, methodDesc.getName(), methodDesc.getDesc(), false);

            String returnType = methodDesc.getReturnType();

            if (!"V".equals(returnType)) {
                if ("I".equals(returnType)) {
                    mv.visitVarInsn(ISTORE, markPosition);
                } else if ("J".equals(returnType)) {
                    mv.visitVarInsn(LSTORE, markPosition);
                } else if ("F".equals(returnType)) {
                    mv.visitVarInsn(FSTORE, markPosition);
                } else if ("D".equals(returnType)) {
                    mv.visitVarInsn(DSTORE, markPosition);
                } else {
                    mv.visitVarInsn(ASTORE, markPosition);
                }
                
                mv.visitVarInsn(ALOAD, 3);
                
                if ("I".equals(returnType)) {
                    mv.visitVarInsn(ILOAD, markPosition);
                } else if ("J".equals(returnType)) {
                    mv.visitVarInsn(LLOAD, markPosition);
                } else if ("F".equals(returnType)) {
                    mv.visitVarInsn(FLOAD, markPosition);
                } else if ("D".equals(returnType)) {
                    mv.visitVarInsn(DLOAD, markPosition);
                } else {
                    mv.visitVarInsn(ALOAD, markPosition);
                }
                
                mv.visitMethodInsn(INVOKEVIRTUAL, responseType, "setData", "(" + returnType + ")V", false);
            }
            
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
            
            mv = cw.visitMethod(ACC_PUBLIC, "setTarget", "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, targetType);
            mv.visitFieldInsn(PUTFIELD, typeName, "target", targetTypeDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "invoke", "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)Lks/web/Response;", null, new String[] { "java/lang/Exception" });
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, typeName, "invoke", "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)" + responseTypeDesc, false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    
    private static String upperFirst(String text) {
        return TextUtil.upperFirst(text);
    }
}
