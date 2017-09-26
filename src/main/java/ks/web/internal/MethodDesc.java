/**
 * 
 */
package ks.web.internal;

import ks.bean.Field;

/**
 * @author xueqin.lin
 *
 */
public interface MethodDesc {

    String getName();
    
    String getDesc();
    
    String getTargetType();
    
    String getReturnType();
    
    String getReturnTypeSignature();
    
    Field[] getParameterTypes();
    
}
