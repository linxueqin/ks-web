package ks.bean;

public class DeclareField implements Field {

    private String name;
    
    private String type;
    
    private int dimension;
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String getDesc() {
        String typeDesc = getTypeDesc();
        for(int i = 0; i < dimension; i++) {
            typeDesc = "[" + typeDesc;
        }
        return typeDesc;
    }

    public String getSignature() {
        return null;
    }
    
    private String getTypeDesc() {
        if ("boolean".equals(type)) {
            return "Z";
        } else if ("byte".equals(type)) {
            return "B";
        } else if ("char".equals(type)) {
            return "C";
        } else if ("short".equals(type)) {
            return "S";
        } else if ("int".equals(type)) {
            return "I";
        } else if ("long".equals(type)) {
            return "J";
        } else if ("float".equals(type)) {
            return "F";
        } else if ("double".equals(type)) {
            return "D";
        } else {
            return "L" + type.replace(".", "/") + ";";
        }
    }

    public void setDesc(String desc) {
        
    }

    public void setSignature(String signature) {
        
    }

}
