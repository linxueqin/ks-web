/**
 * 
 */
package ks.web;

/**
 * @author xueqin.lin
 *
 */
public class HandlerMapping {

    private String url;
    private Handler handler;
    private String permission;
    private String description;
    private boolean pattern;
    
    /**
     * 
     */
    public HandlerMapping() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param url
     * @param handler
     * @param permission
     * @param description
     */
    public HandlerMapping(String url, Handler handler, String permission, String description) {
        super();
        this.url = url;
        this.handler = handler;
        this.permission = permission;
        this.description = description;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return the handler
     */
    public Handler getHandler() {
        return handler;
    }
    /**
     * @param handler the handler to set
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    /**
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }
    /**
     * @param permission the permission to set
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPattern() {
		return pattern;
	}

	public void setPattern(boolean pattern) {
		this.pattern = pattern;
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HandlerPacking [url=" + url + ", handler=" + handler + ", permission=" + permission + ", description="
                + description + "]";
    }
    
    
}
