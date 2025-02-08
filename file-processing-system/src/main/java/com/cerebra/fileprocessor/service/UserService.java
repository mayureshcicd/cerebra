package com.cerebra.fileprocessor.service;


import com.cerebra.fileprocessor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


public interface UserService {


     UserDetailsService userDetailsService() ;


}