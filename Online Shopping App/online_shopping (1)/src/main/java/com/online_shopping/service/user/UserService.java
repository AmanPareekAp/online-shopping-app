package com.online_shopping.service.user;

import com.online_shopping.entity.product.Product;
import com.online_shopping.entity.user.*;
import com.online_shopping.exception.InvalidTokenException;
import com.online_shopping.exception.UserInactiveException;
import com.online_shopping.exception.UserNotFoundException;
import com.online_shopping.repository.AdminRepository;
import com.online_shopping.repository.RoleRepository;
import com.online_shopping.repository.TokenRepository;
import com.online_shopping.repository.UserRepository;
import com.online_shopping.token.ConfirmationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    private EmailSenderService emailSenderService;
    private TokenRepository tokenRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private AdminRepository adminRepository;

    @Autowired
    public UserService(EmailSenderService emailSenderService, TokenRepository tokenRepository,
                       UserRepository userRepository, PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository, AdminRepository adminRepository)  {
        this.emailSenderService = emailSenderService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.adminRepository=adminRepository;
    }

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    public static final long MAX_FAILED_ATTEMPTS = 3;

    public User getUserByEmail(String email){

        User user = userRepository.findByEmail(email);
        return user;
    }



    public User addUser(User user){

        return userRepository.save(user);

    }

    public Role getRole(UserRole userRole) {
        return roleRepository.findByAuthority("ROLE_" + userRole.name());
    }

    public void sendActivationLinkToCustomer(Customer customer) {
        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        tokenRepository.save(confirmationToken);
        customer.setConfirmationToken(confirmationToken);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/customer/confirm-account?token=" + confirmationToken.getToken());
        emailSenderService.sendEmail(mailMessage);
    }

    public void sendActivationLinkToSeller(Seller seller) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(seller.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("Your account has been created,waiting for Approval.");
        emailSenderService.sendEmail(mailMessage);
    }


    public void sendActivationMessage(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Activated");
        mailMessage.setText("Congratulation Your Account is activated");
        emailSenderService.sendEmail(mailMessage);
    }
    public void sendDeactivationMessage(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Deactivated");
        mailMessage.setText("Your Account is Deactivated");
        emailSenderService.sendEmail(mailMessage);
    }

    public void sendResetPasswordMessage(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        tokenRepository.save(confirmationToken);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Reset Your Password!");
        mailMessage.setText("To reset your password, please click here : "
                + "http://localhost:8080/confirm-reset?token="
                + confirmationToken.getToken());
        emailSenderService.sendEmail(mailMessage);
    }

    public String verifyToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token);
        if(confirmationToken.getUser().isActive())
        {

        }
        if (confirmationToken == null) {
            return "invalidToken";
        }

        final User user = confirmationToken.getUser();

        if (confirmationToken.getExpiryDate().compareTo(LocalDateTime.now()) < 0) {
            throw new InvalidTokenException("Token is expired");
        }
        return "tokenValid";
    }


    public ResponseEntity<Object> sendResetPasswordLinkToUser(String username) {

        //System.out.println(username);
        User user = getUserByEmail(username);

        if(user!=null)
        {
            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            tokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(username);

            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setText("To complete the password reset process," +
                    " please click here and enter new password \n: "
                    + "http://localhost:8080/confirm-reset?token="+confirmationToken.getToken());

            emailSenderService.sendEmail(mailMessage);

            return new ResponseEntity("Password Link sent, please check your email",
                    HttpStatus.OK);
        }
        else
        {
            throw new UserNotFoundException("User not found");
        }
    }

    public ResponseEntity<Object> resetPasswordIfTokenIsValid(String token, String newPassword,
                                                              String confirmNewPassword) {

        //System.out.println(token);
        String tokenValidationResponse=verifyToken(token);
        if (tokenValidationResponse.equals("tokenValid")) {

            ConfirmationToken confirmationToken=tokenRepository.findByToken(token);
            System.out.println(confirmationToken.getUser());
            User user = confirmationToken.getUser();
            if(user.isActive()) {

                if(!(newPassword.equals(confirmNewPassword)))
                {
                    throw new ConstraintViolationException(
                            "Enter same password in both password and confirm password field",
                            new HashSet<>());
                }

                user.setPassword(passwordEncoder.encode(newPassword));
                addUser(user);

                return new ResponseEntity<Object>("Password Changed Successfully"
                        ,HttpStatus.OK);
            }
            else {
                user.setActive(true);
                throw new UserInactiveException("User activated, try again");
            }

        }
        else {
            throw new InvalidTokenException("Token is invalid");
        }

    }


    @Transactional
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.isUnlocked(),user.getEmail());
    }

    @Transactional
    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(0, true ,email);
    }

    public void lock(User user) {
        user.setUnlocked(false);
        user.setLockTime(new Date());

        userRepository.save(user);
    }

    @Transactional
    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setUnlocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    public ResponseEntity<Object> sendProductActivationReminderToAdmin(Product product) {

        Role role = roleRepository.findByAuthority("ROLE_ADMIN");
        List<User> adminList = role.getUserList();
        if(adminList==null)
        {
            throw new UserNotFoundException("No Admin found");
        }

        User admin = adminList.get(0);

        String adminEmail=admin.getEmail();


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(adminEmail);

        mailMessage.setSubject("Activate Product!!");
        mailMessage.setText("New product with " + product.toString() + "\n" + " Added. " +
                "\n" + "To complete the product activation process," +
                " please click here \n: "
                + "http://localhost:8080/admin/activate-product?productId="
                + product.getId()
        );

        emailSenderService.sendEmail(mailMessage);

        return new ResponseEntity<Object> ("Email to notify admin about the product has been sent" ,
                HttpStatus.OK);

    }


    public boolean checkUserStatus(User user) {

        return (!user.isDeleted()) && user.isActive();
    }

}
