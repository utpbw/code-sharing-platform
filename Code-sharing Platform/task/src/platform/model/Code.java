package platform.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Holds a code snippet and its display title; title is excluded from JSON output to keep the API response minimal. */
public class Code {

    String code;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String title;


    public Code() {
    }

    /** Creates a snippet with a title for the web view and the raw code for both views. */
    public Code(String title, String code) {
        this.title = title;
        this.code = code;
    }

    /** Returns the code snippet shared by both the API and web endpoints. */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /** Returns the page title used only by the HTML endpoint. */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
