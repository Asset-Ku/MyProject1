package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${hostname}")
    private String hostname;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username);
    }

    public boolean addUser(Users users) {
        Users userFromDb = usersRepository.findByUsername(users.getUsername());
        if (userFromDb != null) {
            return false;
        }
        users.setActive(false);
        users.setRoles(Collections.singleton(Role.USER));
        users.setActivationCode(UUID.randomUUID().toString());
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        usersRepository.save(users);
//        sendMessage(users);
        return true;
    }

    private void sendMessage(Users users) {
        if (!StringUtils.isEmpty(users.getEmail())) {
            String message = String.format(
                    "Hello," + users.getUsername() +" %s! \n" +
                    "Your activation code: http://%s/activate/%s",
                    users.getUsername(),
                    hostname,
                    users.getActivationCode()
            );

            mailSender.send(users.getEmail(), "Activation code", message);
        }
    }

    public boolean activateUser(String code) {
        Users user = usersRepository.findByActivationCode(code);
        if (user == null) {
            return false;
        }
        user.setActivationCode(null);
        user.setActive(true);
        usersRepository.save(user);
        return true;
    }

    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    public void saveUser(String username, Map<String, String> form, Users users) {
        users.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        users.getRoles().clear();
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                users.getRoles().add(Role.valueOf(key));
            }
        }
        usersRepository.save(users);
    }

    public void updateProfile(Users users, String password, String email) {
        String userEmail = users.getEmail();

        boolean isEmailChanged = (email != null && !email.isEmpty() && !email.equals(userEmail));

        if (isEmailChanged) {
            users.setEmail(email);
            if (!StringUtils.isEmpty(email))
                users.setActivationCode(UUID.randomUUID().toString());
        }
        if (!StringUtils.isEmpty(password)) {
            users.setPassword(passwordEncoder.encode(password));
        }
        usersRepository.save(users);
        if (isEmailChanged)
            sendMessage(users);
    }

    public void subscribe(Users currentUser, Users users) {
        users.getSubscribers().add(currentUser);
        usersRepository.save(users);
    }

    public void unsubscribe(Users currentUser, Users users) {
        users.getSubscribers().remove(currentUser);
        usersRepository.save(users);
    }
}
