package platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import platform.model.Code;
import platform.repository.CodeRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class Controller {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private static final String HLJS_HEAD =
        "<link rel=\"stylesheet\" href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">" +
        "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>" +
        "<script>hljs.initHighlightingOnLoad();</script>";

    @Autowired
    private CodeRepository codeRepository;

    @PostMapping(path = "/api/code/new", produces = "application/json;charset=UTF-8")
    public Map<String, String> postApiCodeNew(@RequestBody Code body) {
        Code saved = codeRepository.save(new Code(body.getCode(), LocalDateTime.now().format(FMT)));
        Map<String, String> result = new HashMap<>();
        result.put("id", String.valueOf(saved.getId()));
        return result;
    }

    @GetMapping(path = "/api/code/latest", produces = "application/json;charset=UTF-8")
    public List<Code> getApiLatest() {
        return codeRepository.findTop10ByOrderByIdDesc();
    }

    @GetMapping(path = "/api/code/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Code> getApiCode(@PathVariable long id) {
        Optional<Code> code = codeRepository.findById(id);
        return code.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

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

    @GetMapping(path = "/code/latest", produces = "text/html")
    public ResponseEntity<String> getHtmlLatest() {
        List<Code> latest = codeRepository.findTop10ByOrderByIdDesc();
        StringBuilder sb = new StringBuilder("<html><head><title>Latest</title>")
            .append(HLJS_HEAD)
            .append("</head><body>");
        for (Code c : latest) {
            sb.append("<pre><code>").append(c.getCode()).append("</code></pre>");
            sb.append("<span>").append(c.getDate()).append("</span>");
        }
        sb.append("</body></html>");
        return ResponseEntity.ok(sb.toString());
    }

    @GetMapping(path = "/code/{id}", produces = "text/html")
    public ResponseEntity<String> getHtmlCode(@PathVariable long id) {
        Optional<Code> code = codeRepository.findById(id);
        if (code.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Code c = code.get();
        return ResponseEntity.ok(
            "<html><head><title>Code</title>" + HLJS_HEAD + "</head><body>" +
            "<pre id=\"code_snippet\"><code>" + c.getCode() + "</code></pre>" +
            "<span id=\"load_date\">" + c.getDate() + "</span>" +
            "</body></html>"
        );
    }
}
