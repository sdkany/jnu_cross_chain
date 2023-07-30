package jnu.cross;

/**
 * @author SDKany
 * @ClassName Response
 * @Date 2023/7/30 15:27
 * @Version V1.0
 * @Description
 */
public class Response {
    private String message;
    private int code;
    private String data;

    public Response(String message, int code, String data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
