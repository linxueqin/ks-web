/**
 * 
 */
package ks.web.internal;

import java.util.Arrays;

import ks.bean.Field;

/**
 * @author xueqin.lin
 *
 */
public class MethodDescImpl implements MethodDesc {

    private String name;
    
    private String desc;
    
    private String targetType;
    
    private String returnType;
    
    private String returnTypeSignature;
    
    private Field[] parameterTypes;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the targetType
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * @param targetType the targetType to set
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * @return the returnType
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * @param returnType the returnType to set
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * @return the returnTypeSignature
     */
    public String getReturnTypeSignature() {
        return returnTypeSignature;
    }

    /**
     * @param returnTypeSignature the returnTypeSignature to set
     */
    public void setReturnTypeSignature(String returnTypeSignature) {
        this.returnTypeSignature = returnTypeSignature;
    }

    /**
     * @return the parameterTypes
     */
    public Field[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @param parameterTypes the parameterTypes to set
     */
    public void setParameterTypes(Field[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MethodDescImpl [name=" + name + ", desc=" + desc + ", targetType=" + targetType + ", returnType="
                + returnType + ", returnTypeSignature=" + returnTypeSignature + ", parameterTypes="
                + Arrays.toString(parameterTypes) + "]";
    }

}
