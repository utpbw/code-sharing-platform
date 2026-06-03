package platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.model.Code;

/** Exposes the shared code snippet over both a JSON API and an HTML web interface. */
@RestController
public class Controller {

    private final String titleData = "Code";
    private final String codeData = "public static void main(String[] args) {\n    SpringApplication.run(CodeSharingPlatform.class, args);\n}";

    private final Code code = new Code(titleData, codeData);

    public Controller() {
    }

    /** Returns the code snippet as JSON so clients can consume it programmatically. */
    @GetMapping(path = "/api/code", produces = "application/json;charset=UTF-8")
    public Code getApiCode() {
        return code;
    }

    /** Returns the code snippet wrapped in HTML so browsers can display it with a readable title. */
    @GetMapping(path = "/code", produces = "text/html")
    public ResponseEntity<String> getHtmlCode() {
        return ResponseEntity.ok()
                .body("<title>" + code.getTitle() + "</title>"
                        + "<pre>" + code.getCode() + "</pre>");
    }

}


