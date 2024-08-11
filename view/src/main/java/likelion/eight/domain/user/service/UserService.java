package likelion.eight.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion.eight.common.domain.exception.ResourceNotFoundException;
import likelion.eight.common.service.CookieService;
import likelion.eight.common.service.port.ClockHolder;
import likelion.eight.common.service.port.UuidHolder;
import likelion.eight.domain.user.controller.model.*;
import likelion.eight.domain.user.converter.UserConverter;
import likelion.eight.domain.user.model.User;
import likelion.eight.domain.user.service.port.UserRepository;
import likelion.eight.user.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UuidHolder uuidHolder;
    private final CertificationService certificationService;
    private final CookieService cookieService;
    private final ClockHolder clockHolder;

    public UserResponse createUser(UserCreateRequest request){
        User createUser = UserConverter.toUser(request, uuidHolder);
        createUser = userRepository.save(createUser);
        certificationService.sendCode(
                createUser.getEmail(),
                createUser.getCertificationCode()
        );
        return UserConverter.toResponse(createUser);
    }

    public UserResponse getById(long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
        return UserConverter.toResponse(user);
    }

    public UserResponse login(HttpServletResponse response, UserLoginRequest loginUser){ //TODO 메서드이름수정

        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("존재 하지 않는 이메일 입니다."));

        user = user.login(clockHolder, loginUser.getPassword());
        userRepository.save(user);

        if(Objects.equals(user.getRoleType(), RoleType.ADMIN)){
            cookieService.createAdminCookie(response, user);
        }else{
            cookieService.createUserCookie(response, user);
        }

        return UserConverter.toResponse(user);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response){
        cookieService.expiredCookie(request, response);
    }

    public void verifyEmail(long id, String certificationCode){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        user =  user.certificate(certificationCode);
        userRepository.save(user);
    }

    public UserResponse editUser(Long id, UserEditRequest userEditRequest){
        User user = userRepository.getById(id);
        user = user.edit(userEditRequest);
        userRepository.save(user);

        return UserConverter.toResponse(user);
    }

    public void findPassword(UserFindPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("존재 하지 않는 이메일 입니다."));

        if(user.checkNameAndPhoneNumber(request)){
            certificationService.sendPassword(user.getEmail(), user.getPassword());
        }else{
            throw new ResourceNotFoundException("입력하신 핸드폰 번호, 이름 정보가 일치 하지 않습니다");
        }
    }

    public UserResponse findEmail(UserFindEmailRequest request){
        User user = userRepository.findByPhoneAndName(request.getPhoneNumber(), request.getName())
                .orElseThrow(() -> new ResourceNotFoundException("입력하신 정보와 일치하는 이메일이 존재 하지 않습니다."));

        return UserConverter.toResponse(user);
    }
}
