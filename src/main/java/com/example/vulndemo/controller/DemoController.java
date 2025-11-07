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

@RestController
public class DemoController {

    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/")
    public String home() {
        return "<html><body>"
             + "<h1>VulnDemo Java App is Running</h1>"
             + "<p>Useful endpoints (demo only):</p>"
             + "<ul>"
             + "<li><a href=\"/health\">/health</a></li>"
             + "<li><a href=\"/user?name=Ravi\">/user?name=Ravi</a></li>"
             + "<li><a href=\"/users\">/users</a></li>"
             + "<li><code>/upload</code> (POST)</li>"
             + "<li><code>/exec?cmd=echo hello</code> (RCE demo — do not expose)</li>"
             + "<li><code>/read?file=filename</code> (unsafe file read)</li>"
             + "</ul>"
             + "<p><strong>Warning:</strong> This application contains deliberate vulnerabilities for demo purposes. Do not expose publicly.</p>"
             + "</body></html>";
    }

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

    @GetMapping("/user")
    public String getUser(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                          HttpServletResponse resp) {
        User u = userService.findByName(name);
        if (u == null) {
            resp.setStatus(404);
            return "User not found";
        }
        return "<h1>User: " + u.getName() + "</h1><p>Bio: " + u.getBio() + "</p>";
    }

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

    @GetMapping("/exec")
    public String execCmd(@RequestParam(value = "cmd", defaultValue = "echo hello") String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return "Executed: " + cmd;
        } catch (Exception e) {
            return "Failed to execute";
        }
    }

    @GetMapping("/read")
    public String readFile(@RequestParam("file") String file) {
        return userService.readFileFromDisk(file);
    }
}
