package platform.model;

/** Stores one code snippet and the formatted timestamp when it was last saved. */
public class Code {

    private String code;
    private String date;

    public Code() {
    }

    /** Creates a snapshot binding the snippet text to its upload timestamp. */
    public Code(String code, String date) {
        this.code = code;
        this.date = date;
    }

    /** Returns the raw code text shared by both the API and HTML endpoints. */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /** Returns the upload timestamp string included in both API and HTML responses. */
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
