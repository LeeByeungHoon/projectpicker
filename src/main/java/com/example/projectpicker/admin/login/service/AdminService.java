package com.example.projectpicker.admin.login.service;

import com.example.projectpicker.admin.login.dto.AdminResponseLoginDTO;
import com.example.projectpicker.admin.login.dto.AdminSignUpDTO;
import com.example.projectpicker.security.TokenProvider;
import com.example.projectpicker.admin.login.dto.AdminPostDTO;
import com.example.projectpicker.admin.login.entity.AdminPostEntity;
import com.example.projectpicker.admin.login.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service //스프링 빈 등록
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    @Autowired
    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AdminResponseLoginDTO adminLogin(AdminPostDTO adminPostDTO){
        AdminPostEntity findEmail = adminRepository.findByAdminEmail(adminPostDTO.getAdminEmail());


        if(findEmail == null){
            throw new RuntimeException("가입된 회원이 아닙니다.");
        }
        String adminPassword = findEmail.getAdminPassword();
        String inputPassword = adminPostDTO.getAdminPassword();

            if(!passwordEncoder.matches(inputPassword,adminPassword)){
                throw  new RuntimeException("비밀번호가 일치하지 않습니다.");
            }

        String token = tokenProvider.createToken(findEmail);

        return new AdminResponseLoginDTO(findEmail, token);
    }

    public AdminPostDTO signUp(AdminSignUpDTO adminSignUpDTO){

        String rawPassword = adminSignUpDTO.getAdminPassword(); // 평문 암호
        String encodedPassword = passwordEncoder.encode(rawPassword);
        adminSignUpDTO.setAdminPassword(encodedPassword);

        AdminPostEntity save = adminRepository.save(adminSignUpDTO.toEntity());

        return new AdminPostDTO(save);
    }
}
