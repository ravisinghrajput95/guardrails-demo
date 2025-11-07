package com.example.vulndemo.controller;

import com.example.vulndemo.model.User;
import com.example.vulndemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * DemoController - intentionally contains insecure examples for DevSecOps demonstrations:
 *  - SQL injection via /user?name=...
 *  - Unsafe file upload via /upload
 *  - Unsafe command execution via /exec
 *  - Unsafe file read via /read
 *
 * For real apps: do NOT use these patterns in production.
 */
@RestController
public class DemoController {

    @Autowired
    private UserService userService;

    // Simple health check
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    // Home page to avoid Whitelabel 404 and show useful links
    @GetMapping("/")
    public String home() {
        return "<html><body>"
             + "<h1>VulnDemo Java App is Running</h1>"
             + "<p>Useful endpoints (demo only):</p>"
             + "<ul>"
             + "<li><a href=\"/health\">/health</a></li>"
             + "<li><a href=\"/user?name=Ravi\">/user?name=Ravi</a></li>"
             + "<li><a href=\"/users\">/users</a> (simple demo page)</li>"
             + "<li><code>/upload</code> (POST file)</li>"
             + "<li><code>/exec?cmd=echo hello</code> (RCE demo — do not expose)</li>"
             + "<li><code>/read?file=filename</code> (unsafe file read)</li>"
             + "</ul>"
             + "<p><strong>Warning:</strong> This application contains deliberate vulnerabilities for demo purposes. Do not expose publicly.</p>"
             + "</body></html>";
    }

    // Simple HTML page showing a couple of users (uses userService.findByName for demo)
    // This is not optimized and is intentionally simple for demo screenshots.
    @GetMapping("/users")
    public String users() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><h2>Users (demo)</h2><ul>");
        User u1 = userService.findByName("Ravi");
        if (u1 != null) {
            sb.append("<li>").append(u1.getName()).append(" — ").append(u1.getBio()).append("</li>");
        } else {
            sb.append("<li>Ravi — (not found)</li>");
        }
        User u2 = userService.findByName("Prashant");
        if (u2 != null) {
            sb.append("<li>").append(u2.getName()).append(" — ").append(u2.getBio()).append("</li>");
        } else {
            sb.append("<li>Prashant — (not found)</li>");
        }
        sb.append("</ul></body></html>");
        return sb.toString();
    }

    // 1) SQL injection example via query param
    @GetMapping("/user")
    public String getUser(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                          HttpServletResponse resp) {
        User u = userService.findByName(name);
        if (u == null) {
            resp.setStatus(404);
            return "User not found";
        }
        // reflected output without escaping (XSS risk if used in a browser)
        return "<h1>User: " + u.getName() + "</h1><p>Bio: " + u.getBio() + "</p>";
    }

    // 2) Unsafe file upload - no validation of content-type or file name
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File("uploads");
            if (!dir.exists()) dir.mkdirs();
            File out = new File(dir, file.getOriginalFilename());
            try (InputStream in = file.getInputStream(); FileOutputStream fos = new FileOutputStream(out)) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = in.read(buf)) != -1) fos.write(buf, 0, r);
            }
            return "Uploaded: " + file.getOriginalFilename();
        } catch (Exception e) {
            return "Upload failed";
        }
    }

    // 3) Unsafe command execution (RCE-style demo) — uses Runtime.exec on user input (VERY DANGEROUS)
    @GetMapping("/exec")
    public String execCmd(@RequestParam(value = "cmd", defaultValue = "echo hello") String cmd) {
        try {
            // DANGEROUS: executes user-provided command
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return "Executed: " + cmd;
        } catch (Exception e) {
            return "Failed to execute";
        }
    }

    // 4) Read uploaded file (demonstrates file read risk)
    @GetMapping("/read")
    public String readFile(@RequestParam("file") String file) {
        return userService.readFileFromDisk(file);
    }
}
