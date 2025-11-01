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
