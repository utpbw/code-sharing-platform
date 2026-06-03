package platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import platform.model.Code;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

/** Serves the single mutable code snippet over both a JSON API and an HTML web interface. */
@RestController
public class Controller {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private volatile Code current = new Code(
        "public static void main(String[] args) {\n    SpringApplication.run(CodeSharingPlatform.class, args);\n}",
        LocalDateTime.now().format(FMT)
    );

    /** Returns the current snippet and its upload date for programmatic consumers. */
    @GetMapping(path = "/api/code", produces = "application/json;charset=UTF-8")
    public Code getApiCode() {
        return current;
    }

    /** Replaces the stored snippet, resets the timestamp, and returns an empty JSON acknowledgement. */
    @PostMapping(path = "/api/code/new", produces = "application/json;charset=UTF-8")
    public Map<String, Object> postApiCodeNew(@RequestBody Code body) {
        current = new Code(body.getCode(), LocalDateTime.now().format(FMT));
        return Collections.emptyMap();
    }

    /** Returns the current snippet and date wrapped in HTML so browsers can display them. */
    @GetMapping(path = "/code", produces = "text/html")
    public ResponseEntity<String> getHtmlCode() {
        Code c = current;
        return ResponseEntity.ok(
            "<html><head><title>Code</title></head><body>" +
            "<pre id=\"code_snippet\">" + c.getCode() + "</pre>" +
            "<span id=\"load_date\">" + c.getDate() + "</span>" +
            "</body></html>"
        );
    }

    /** Returns an HTML form for submitting a new snippet; JS sends it as JSON to avoid form encoding. */
    @GetMapping(path = "/code/new", produces = "text/html")
    public ResponseEntity<String> getHtmlCodeNew() {
        return ResponseEntity.ok(
            "<html><head><title>Create</title></head><body>" +
            "<textarea id=\"code_snippet\"></textarea>" +
            "<button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>" +
            "<script>function send(){" +
            "let o={\"code\":document.getElementById(\"code_snippet\").value};" +
            "let x=new XMLHttpRequest();" +
            "x.open(\"POST\",\"/api/code/new\",false);" +
            "x.setRequestHeader(\"Content-type\",\"application/json; charset=utf-8\");" +
            "x.send(JSON.stringify(o));" +
            "if(x.status==200){alert(\"Success!\");}" +
            "}</script>" +
            "</body></html>"
        );
    }
}
