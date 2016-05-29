package Gson;

/**
 * Created by Administrator on 2016/5/19 0019.
 */
public class First {

    /**
     * reason : 成功的返回
     * result : {"code":100000,"text":"您好，我是图灵机器人，有什么可以帮您的吗？"}
     * error_code : 0
     */

    private String reason;
    /**
     * code : 100000
     * text : 您好，我是图灵机器人，有什么可以帮您的吗？
     */

    private ResultBean result;
    private int error_code;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class ResultBean {
        private int code;
        private String text;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
