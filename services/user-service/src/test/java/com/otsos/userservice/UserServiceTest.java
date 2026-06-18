package com.otsos.userservice;

import com.otsos.userservice.dto.User;
import com.otsos.userservice.dto.UserDto;
import com.otsos.userservice.exceptions.IncorrectPasswordException;
import com.otsos.userservice.exceptions.UserNotFoundException;
import com.otsos.userservice.repositories.UserRepository;
import com.otsos.userservice.security.JwtTokenProvider;
import com.otsos.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
public class UserServiceTest {

     @Mock
     private UserRepository userRepository;
     @Mock
     private PasswordEncoder passwordEncoder;
     @Mock
     private JwtTokenProvider jwtTokenProvider;
     @InjectMocks
     private UserService userService;

     private static final Long ID = 1L;
     private User testUser;
     private UserDto testUserDto;
     private static final String JWT_TOKEN = "TEST_JWT_TOKEN";
     private static final String ENCODED_OLD_PASSWORD = "ENCODED_OLD_PASSWORD";
     private static final String NEW_JWT_TOKEN = "NEW_JWT_TOKEN";

     @BeforeEach
     void setUp(){
         testUser = new User(ID, "example@gmail.com",
                 "Mironov", "Mikhail", ENCODED_OLD_PASSWORD);
         testUserDto = new UserDto("example@gmail.com",
                 "Mironov", "Mikhail", JWT_TOKEN);
     }

     @Nested
     class GetUserTests{

          @Test
          void getUser_returnValidUserDto(){
              when(userRepository.findById(ID)).thenReturn(Optional.of(testUser));
              when(jwtTokenProvider.generateToken(testUser)).thenReturn(JWT_TOKEN);

              UserDto response = userService.getUser(ID);

              assertNotNull(response);
              assertEquals(response, testUserDto);
         }

         @Test
         void getUser_throwUserNotFoundException(){
              when(userRepository.findById(ID)).thenReturn(Optional.empty());

              UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                      () -> userService.getUser(ID));
              UserNotFoundException testEx = new UserNotFoundException(ID.toString());

              assertEquals(testEx.getStatusCode(), ex.getStatusCode());
              assertEquals(testEx.getMessage(), ex.getMessage());

              verify(jwtTokenProvider, never()).generateToken(any(User.class));
         }
     }

     @Nested
     class UpdateUserTests{

         private UserDto updatedUserDto;

         @BeforeEach
         void setUpForUpdate(){
             updatedUserDto = new UserDto("updated@gmail.com",
                     "Ivanov", "Mikhail", JWT_TOKEN);
         }

         @Test
         void updateUser_successUpdate(){
             when(userRepository.findById(ID)).thenReturn(Optional.of(testUser));
             when(userRepository.save(any(User.class))).thenAnswer(invocation ->
                     invocation.getArgument(0));
             when(jwtTokenProvider.generateToken(argThat(userArgs ->
                     userArgs.getEmail().equals(updatedUserDto.getEmail()) && userArgs.getSurname().equals(updatedUserDto.getSurname()))))
                     .thenReturn(NEW_JWT_TOKEN);

             UserDto user = userService.updateUser(ID, updatedUserDto);

             assertNotNull(user);
             assertEquals(updatedUserDto.getEmail(), user.getEmail());
             assertEquals(updatedUserDto.getSurname(), user.getSurname());
             assertEquals(NEW_JWT_TOKEN, user.getJwtToken());

             verify(userRepository).save(argThat(u ->
                     u.getEmail().equals(updatedUserDto.getEmail()) &&
                     u.getSurname().equals(updatedUserDto.getSurname())));
         }

         @Test
         void updateUser_throwsUserNotFoundException(){
             when(userRepository.findById(ID)).thenReturn(Optional.empty());

             UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                     () -> userService.updateUser(ID, updatedUserDto));
             UserNotFoundException testEx = new UserNotFoundException(ID.toString());

             assertEquals(ex.getStatusCode(), testEx.getStatusCode());
             assertEquals(testEx.getMessage(), ex.getMessage());

             verify(jwtTokenProvider, never()).generateToken(any(User.class));
             verify(userRepository, never()).save(any(User.class));
         }
     }

     @Nested
     class UpdatePasswordTest{

         private static final String OLD_PASSWORD = "TEST_OLD_PASSWORD";
         private static final String NEW_PASSWORD = "TEST_NEW_PASSWORD";
         private static final String ENCODED_NEW_PASSWORD = "ENCODED_NEW_PASSWORD";

         @Test
         void updatePassword_returnSuccessUpdate(){
             when(userRepository.findById(ID)).thenReturn(Optional.of(testUser));
             when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

             when(passwordEncoder.matches(OLD_PASSWORD, testUser.getPassword())).thenReturn(true);
             when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);

             when(jwtTokenProvider.generateToken(argThat(userArg ->
                     userArg.getPassword().equals(ENCODED_NEW_PASSWORD))))
                     .thenReturn(NEW_JWT_TOKEN);

             UserDto returnedUserDto = userService.updatePassword(ID, OLD_PASSWORD, NEW_PASSWORD);
             testUserDto.setJwtToken(NEW_JWT_TOKEN);

             assertNotNull(returnedUserDto);
             assertEquals(testUserDto, returnedUserDto);

             verify(userRepository, times(1)).save(argThat(u ->
                     u.getPassword().equals(ENCODED_NEW_PASSWORD)));
         }

         @Test
         void updatePassword_oldPasswordIsIncorrect(){
             when(userRepository.findById(ID)).thenReturn(Optional.of(testUser));
             when(passwordEncoder.matches(OLD_PASSWORD, testUser.getPassword())).thenReturn(false);

             IncorrectPasswordException ex = assertThrows(IncorrectPasswordException.class,
                     () -> userService.updatePassword(ID, OLD_PASSWORD, NEW_PASSWORD));

             assertEquals(ex.getStatusCode(), 401);
             assertEquals(ex.getMessage(), "Incorrect password, id/email: 1");

             verify(userRepository, never()).save(any(User.class));
         }

         @Test
         void updatePassword_throwsUserNotFoundException(){
             when(userRepository.findById(ID)).thenReturn(Optional.empty());

             UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                     () -> userService.updatePassword(ID, OLD_PASSWORD, NEW_PASSWORD));
             UserNotFoundException testEx = new UserNotFoundException(ID.toString());

             assertEquals(ex.getStatusCode(), testEx.getStatusCode());
             assertEquals(testEx.getMessage(), ex.getMessage());

             verify(jwtTokenProvider, never()).generateToken(any(User.class));
             verify(userRepository, never()).save(any(User.class));
             verify(passwordEncoder, never()).
                     matches(any(String.class), any(String.class));
         }
     }

     @Nested
     class deleteUserTest{

         @Test
         void deleteUser_returnNoContent(){
             when(userRepository.findById(ID)).thenReturn(Optional.ofNullable(testUser));
             doNothing().when(userRepository).delete(testUser);

             userService.deleteUser(ID);

             verify(userRepository, times(1)).findById(any(Long.class));
             verify(userRepository, times(1)).delete(testUser);
         }

         @Test
         void deleteUser_throwsUserNotFoundException(){
             when(userRepository.findById(ID)).thenReturn(Optional.empty());

             assertThatThrownBy(() -> userService.deleteUser(ID))
                     .isInstanceOf(UserNotFoundException.class)
                     .hasMessageContaining("user with id/email: " + ID + " not found")
                     .hasFieldOrPropertyWithValue("statusCode", 404);

             verify(userRepository, times(1)).findById(any(Long.class));
         }
     }
}
