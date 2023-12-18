package com.mesanjay.admin.controller;

import com.mesanjay.admin.dto.AdminDto;
import com.mesanjay.admin.event.RegistrationCompleteEvent;
import com.mesanjay.admin.event.listner.RegistrationCompleteEventListener;
import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.model.VerificationToken;
import com.mesanjay.admin.securityconfig.email.EmailValidator;
import com.mesanjay.admin.service.AdminService;
import com.mesanjay.admin.service.PasswordResetTokenService;
import com.mesanjay.admin.service.VerificationTokenService;
import com.mesanjay.admin.utils.URLUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AdminService adminService;
    private final ApplicationEventPublisher publisher;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationTokenService tokenService;
    private final RegistrationCompleteEventListener eventListener;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailValidator emailValidator;

    @RequestMapping("/login")
    public String login(Model model, @RequestParam(value = "expired", required = false) String email) {

        model.addAttribute("title", "Login Page");
        model.addAttribute("email", email);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/";
    }

    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("title", "Home Page");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("adminDto", new AdminDto());
        return "register";
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult result,
                              HttpServletRequest request,
                              Model model) {

//        boolean isValidEmail = emailValidator.test(adminDto.getUsername());
//
//        if(isValidEmail){
//            throw new IllegalStateException("email not valid");
//        }

        try {

            if (result.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                return "register";
            }
            String username = adminDto.getUsername();

            Admin admin = adminService.findByUsername(username);

            if (admin != null) {
                model.addAttribute("adminDto", adminDto);
                System.out.println("admin not null");
                model.addAttribute("emailError", "Your email has been registered!");

                result.rejectValue("email", "409",
                        "There is already an account registered with that email");
            }

            if (adminDto.getPassword().equals(adminDto.getRepeatPassword())) {

                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                Admin savedAdmin = adminService.save(adminDto);

                // publish verification email event
                publisher.publishEvent(new RegistrationCompleteEvent(savedAdmin, URLUtil.getApplicationURL(request)));
                log.info("sending email to verify it first time");
                model.addAttribute("success", "Register successfully!");
                model.addAttribute("adminDto", adminDto);
            } else {
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "The server has been wrong!");
        }
        return "redirect:/register?success";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {

        Optional<VerificationToken> theToken = tokenService.findByToken(token);

        log.info("this email is already verified {}" , theToken);

        if (theToken.isPresent() && theToken.get().getAdmin().isEnabled()) {
            log.info("this email is already verified.");
            return "redirect:/login?verified";
        }

        String verificationResult = tokenService.validateToken(token);

        log.info("email token validated. {}", verificationResult);

        return switch (verificationResult.toLowerCase()) {
            case "expired" -> "redirect:/login?expired&token="+token;
            case "valid" -> "redirect:/login?valid";
            default -> "redirect:/login?invalid";
        };
    }

    // ########################################### forgot password

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String resetPasswordRequest(HttpServletRequest request, Model model){

        String email = request.getParameter("email");
        Admin admin = adminService.findByUsername(email);

        if (admin == null){
            return  "redirect:/forgot-password?not_fond";
        }

        System.out.println(email);
        System.out.println(admin.getFirstName() + "   <<< first name ");

        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForAdmin(admin, passwordResetToken);

        try {
            String url = URLUtil.getApplicationURL(request)+  "/password-reset?token=" + passwordResetToken;
            eventListener.sendPasswordResetVerificationEmail(admin, url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/forgot-password?success";
    }



    @GetMapping("/password-reset")
    public String passwordResetForm(@RequestParam("token") String token, Model model){
        model.addAttribute("token", token);
        return "reset-password";
    }

    @GetMapping("/password-reset-token")
    public String resetPassword(HttpServletRequest request, @RequestParam("token") String oldToken){

        String token = request.getParameter("token");
        String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(token);

        System.out.println("time to reset  password for token param > " + oldToken);
        System.out.println("time to reset  password for token > " + request.getParameter("token"));
        System.out.println("time to reset  password for password > " + request.getParameter("password"));

        if (!tokenVerificationResult.equalsIgnoreCase("valid")){
            return "redirect:/login?invalid_token";
        }

        Optional<Admin> theUser = passwordResetTokenService.findAdminByPasswordResetToken(token);

        System.out.println("time to reset  password for theUser > " + theUser);
        if (theUser.isPresent()){

            System.out.println("time to reset password > " + request.getParameter("password"));

            String password = passwordEncoder.encode(request.getParameter("password"));
            passwordResetTokenService.resetPassword(theUser.get(), password);
            return "redirect:/login?reset_success";
        }
        return "redirect:/login?not_found";
    }

    // Going to reset page without a token redirects to login page
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:/login");
    }

    // ###########################################################   Resend email for email verification

    @GetMapping("/resend-verification-token")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        System.out.println("oldToken  >>>>  " + oldToken);

        VerificationToken verificationToken = adminService.generateNewVerificationToken(oldToken);
        Admin theUser = verificationToken.getAdmin();
        resendRegistrationVerificationTokenEmail(theUser, URLUtil.getApplicationURL(request), verificationToken);

        return "redirect:/login?token_resent";
    }

    private void resendRegistrationVerificationTokenEmail(Admin admin, String applicationUrl,
                                                          VerificationToken verificationToken) throws MessagingException, UnsupportedEncodingException {

        // publish verification email event
        String url = applicationUrl+"/verifyEmail?token="+verificationToken.getToken();
        eventListener.sendVerificationEmail(admin, url);

        log.info("Click the link to verify your registration :  {}", url);
    }

}
