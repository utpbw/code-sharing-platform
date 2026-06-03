package platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Map<String, String> postApiCodeNew(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        long timeLimit = body.containsKey("time") ? ((Number) body.get("time")).longValue() : 0L;
        int viewsLimit = body.containsKey("views") ? ((Number) body.get("views")).intValue() : 0;
        if (timeLimit < 0) timeLimit = 0;
        if (viewsLimit < 0) viewsLimit = 0;

        String id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        Code snippet = new Code(id, code, LocalDateTime.now().format(FMT), timeLimit, viewsLimit, now);
        codeRepository.save(snippet);

        Map<String, String> result = new HashMap<>();
        result.put("id", id);
        return result;
    }

    @Transactional
    @GetMapping(path = "/api/code/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getApiCode(@PathVariable String id) {
        Optional<Code> opt = codeRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Code c = opt.get();
        long elapsedSec = (System.currentTimeMillis() - c.getCreatedAt()) / 1000;

        if (c.getTimeLimit() > 0 && elapsedSec >= c.getTimeLimit()) {
            codeRepository.delete(c);
            return ResponseEntity.notFound().build();
        }

        long remainingTime = c.getTimeLimit() > 0 ? c.getTimeLimit() - elapsedSec : 0;
        int remainingViews = consumeView(c);

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", c.getCode());
        resp.put("date", c.getDate());
        resp.put("time", remainingTime);
        resp.put("views", remainingViews);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(path = "/api/code/latest", produces = "application/json;charset=UTF-8")
    public List<Map<String, Object>> getApiLatest() {
        return codeRepository.findTop10ByTimeLimitAndViewsLimitOrderByCreatedAtDesc(0, 0)
            .stream()
            .map(c -> {
                Map<String, Object> m = new HashMap<>();
                m.put("code", c.getCode());
                m.put("date", c.getDate());
                m.put("time", 0);
                m.put("views", 0);
                return m;
            })
            .collect(Collectors.toList());
    }

    @GetMapping(path = "/code/new", produces = "text/html")
    public ResponseEntity<String> getHtmlCodeNew() {
        return ResponseEntity.ok(
            "<html><head><title>Create</title></head><body>" +
            "<textarea id=\"code_snippet\"></textarea>" +
            "<input id=\"time_restriction\" type=\"text\"/>" +
            "<input id=\"views_restriction\" type=\"text\"/>" +
            "<button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>" +
            "<script>function send(){" +
            "let o={" +
            "\"code\":document.getElementById(\"code_snippet\").value," +
            "\"time\":parseInt(document.getElementById(\"time_restriction\").value)||0," +
            "\"views\":parseInt(document.getElementById(\"views_restriction\").value)||0" +
            "};" +
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
        List<Code> latest = codeRepository.findTop10ByTimeLimitAndViewsLimitOrderByCreatedAtDesc(0, 0);
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

    @Transactional
    @GetMapping(path = "/code/{id}", produces = "text/html")
    public ResponseEntity<String> getHtmlCode(@PathVariable String id) {
        Optional<Code> opt = codeRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Code c = opt.get();
        long elapsedSec = (System.currentTimeMillis() - c.getCreatedAt()) / 1000;

        if (c.getTimeLimit() > 0 && elapsedSec >= c.getTimeLimit()) {
            codeRepository.delete(c);
            return ResponseEntity.notFound().build();
        }

        long remainingTime = c.getTimeLimit() > 0 ? c.getTimeLimit() - elapsedSec : 0;
        int remainingViews = consumeView(c);

        StringBuilder sb = new StringBuilder("<html><head><title>Code</title>")
            .append(HLJS_HEAD)
            .append("</head><body>")
            .append("<pre id=\"code_snippet\"><code>").append(c.getCode()).append("</code></pre>")
            .append("<span id=\"load_date\">").append(c.getDate()).append("</span>");

        if (c.getTimeLimit() > 0) {
            sb.append("<span id=\"time_restriction\">").append(remainingTime).append("</span>");
        }
        if (c.getViewsLimit() > 0) {
            sb.append("<span id=\"views_restriction\">").append(remainingViews).append("</span>");
        }

        sb.append("</body></html>");
        return ResponseEntity.ok(sb.toString());
    }

    /** Decrements viewsLimit by 1 and persists; deletes the snippet if exhausted. Returns remaining views (0 if no limit). */
    private int consumeView(Code c) {
        if (c.getViewsLimit() == 0) {
            return 0;
        }
        int remaining = c.getViewsLimit() - 1;
        if (remaining == 0) {
            codeRepository.delete(c);
        } else {
            c.setViewsLimit(remaining);
            codeRepository.save(c);
        }
        return remaining;
    }
}
