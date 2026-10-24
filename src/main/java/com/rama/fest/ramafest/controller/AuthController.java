package com.rama.fest.ramafest.controller;

import com.rama.fest.ramafest.dto.LoginRequest;
import com.rama.fest.ramafest.dto.SignupRequest;
import com.rama.fest.ramafest.entity.User;

import com.rama.fest.ramafest.reposatory.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Session-based authentication endpoints")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates a user and creates a server-side session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.getUsername(), request.getPassword()));
            httpRequest.getSession(true);
            httpRequest.changeSessionId();

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
            HttpSession session = httpRequest.getSession(false);
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("USERNAME", user.getUsername());
            session.setMaxInactiveInterval(30 * 60);

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "authenticated", true
            ));
        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid username or password"
                    ));
        }
    }








    @PostMapping("/logout")
    @Operation(summary = "Log out", description = "Invalidates the current server-side session.")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public ResponseEntity<?> logout(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(
                Map.of("message", "Logout successful")
        );
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the authenticated user from the current session.")
    @SecurityRequirement(name = "sessionCookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated user returned"),
            @ApiResponse(responseCode = "401", description = "No authenticated session")
    })
    public ResponseEntity<?> currentUser(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("USER_ID") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Not authenticated"
                    ));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", session.getAttribute("USER_ID"));
        response.put("username", session.getAttribute("USERNAME"));
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }

    @PostMapping("secure/signup")
    @Operation(summary = "Create user", description = "Creates a new user account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Username or password is missing"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {

        // Basic validation
        if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Username and password are required"
                    ));
        }

        // Check if username already exists
        boolean userExists = userRepository
                .findByUsername(request.getUsername())
                .isPresent();

        if (userExists) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", "Username already taken"
                    ));
        }

        // Hash password before saving
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEnabled(true);
        newUser.setRole("USER");
        // newUser.setEmail(request.getEmail()); // agar entity mein email field hai to uncomment karein

        User savedUser = userRepository.save(newUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Signup successful",
                        "userId", savedUser.getId(),
                        "username", savedUser.getUsername()
                ));
    }




}