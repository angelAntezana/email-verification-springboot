package com.angel.antezana.emailverificationdemo.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.angel.antezana.emailverificationdemo.exception.UserAlreadyExistsException;
import com.angel.antezana.emailverificationdemo.registration.RegistrationRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Override
    public List<User> getUsers() {
      return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        
        Optional<User> usuario = userRepository.findByEmail(request.email());
        if(usuario.isPresent()){
            throw new UserAlreadyExistsException("User with email" + request.email()+" already exists");
        }

        var newUser = new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        return userRepository.save(newUser);
        //VIDEO 20:26
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    

}
