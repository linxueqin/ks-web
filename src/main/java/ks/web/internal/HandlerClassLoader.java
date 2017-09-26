package ks.web.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;

import ks.bean.BeanDumper;
import ks.bean.Field;
import ks.bean.FieldImpl;
import ks.web.ModelAndView;
import ks.web.Page;
import ks.web.Response;

/**
 * @author xueqin.lin
 *
 */
public class HandlerClassLoader extends ClassLoader {

    private Map<String, MethodDesc> methodDescMapper = new HashMap<String, MethodDesc>();
    

    public void registProxy(String pkg, MethodDesc methodDesc) throws Exception {
        if (methodDescMapper.containsKey(pkg)) {
            return;
        }

        methodDescMapper.put(pkg, methodDesc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        int pos = name.lastIndexOf(".");
        String pkg = name.substring(0, pos);
        String type = name.substring(pos + 1);
        if (!methodDescMapper.containsKey(pkg)) {
            throw new ClassNotFoundException(name);
        }

        MethodDesc methodDesc = methodDescMapper.get(pkg);

        try {
            if ("Response".equals(type)) {
                List<Field> returnFields = new ArrayList<Field>();
                
                if (!"V".equals(methodDesc.getReturnType())) {
                    FieldImpl dataField = new FieldImpl();
                    dataField.setDesc(methodDesc.getReturnType());
                    dataField.setName("data");
                    dataField.setSignature(methodDesc.getReturnTypeSignature());
                    returnFields.add(dataField);
                }
                
                String[] ifs = null;
                if (Type.getDescriptor(ModelAndView.class).equals(methodDesc.getReturnType())) {
                	ifs = new String[1];
                	ifs[0] = Type.getInternalName(Page.class);
                }
                
                byte[] dump = BeanDumper.dump(name, returnFields.toArray(new Field[0]), Type.getInternalName(Response.class), ifs);
                return defineClass(name, dump, 0, dump.length);
            } else if ("Handler".equals(type)) {
                byte[] dump = HandlerDumper.dump(methodDesc, pkg);
                return defineClass(name, dump, 0, dump.length);
            } else if ("Request".equals(type)) {
                Field[] parameterTypes = methodDesc.getParameterTypes();
                if (parameterTypes != null && parameterTypes.length > 0) {
                    byte[] dump = BeanDumper.dump(name, parameterTypes);
                    return defineClass(name, dump, 0, dump.length);
                } else {
                    throw new ClassNotFoundException("no request");
                }
            }
        } catch (Exception e) {
            throw new ClassNotFoundException("error in build type", e);
        }

        return super.findClass(name);
    }

}
