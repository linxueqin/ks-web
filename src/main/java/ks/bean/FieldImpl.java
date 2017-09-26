package ks.bean;

public class FieldImpl implements Field {

    private String name;

    private String desc;

    private String signature;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
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
     * @param desc
     *            the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature
     *            the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FieldImpl [name=" + name + ", desc=" + desc + ", signature=" + signature + "]";
    }

}
