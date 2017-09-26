package ks.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xueqin.lin
 *
 *         启动过程初始化, 不考虑并发
 */
public class ExceptionMappings {

    private static final Map<Class<? extends Throwable>, CodeMsg> CODE_MSG_MAP = new HashMap<>();

    private static final List<Class<? extends Throwable>> ERR_LIST = new ArrayList<Class<? extends Throwable>>() {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public boolean add(java.lang.Class<? extends Throwable> e) {
            int pos = -1;
            for (int i = 0; i < size(); i++) {
                if (get(i) == e) {
                    return false;
                }
                if (get(i).isAssignableFrom(e)) {
                    pos = i;
                    break;
                }
            }
            if (pos > -1) {
                add(pos, e);
                return true;
            }

            return super.add(e);
        };

    };

    public static void regist(Class<? extends Throwable> type, int code, String defaultMsg) {
        if (code == 0) {
            throw new IllegalArgumentException("code 0 is not allow");
        }

        if (type == null) {
            throw new IllegalArgumentException("exception type is null");
        }

        CODE_MSG_MAP.put(type, new CodeMsg(code, defaultMsg));
    }

    /**
     * @param type
     * @return
     */
    public static CodeMsg findCodeMsg(Throwable tb) {
        Class<? extends Throwable> errType = tb.getClass();
        CodeMsg codeMsg = CODE_MSG_MAP.get(errType);
        if (codeMsg == null) {
            Class<? extends Throwable> parentErrType = null;
            for (int i = 0; i < ERR_LIST.size(); i++) {
                Class<? extends Throwable> item = ERR_LIST.get(i);
                if (item.isAssignableFrom(errType)) {
                    parentErrType = item;
                    break;
                }
            }

            if (parentErrType != null) {
                return CODE_MSG_MAP.get(errType);
            } else {
                codeMsg = new CodeMsg();
                codeMsg.setCode(1);
            }

        }

        return codeMsg;
    }

}
