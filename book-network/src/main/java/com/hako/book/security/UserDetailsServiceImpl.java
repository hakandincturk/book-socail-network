package com.hako.book.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hako.book.user.User;
import com.hako.book.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    System.out.println("userEmail -->" + userEmail);
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    System.out.println(1);
    System.out.println("UserDetailsServiceImpl.loadUserByUsername() user: " + user.getFullName());
    return user;
  }
  
}
