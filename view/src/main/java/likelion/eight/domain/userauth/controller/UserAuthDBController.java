package likelion.eight.domain.userauth.controller;

import jakarta.validation.Valid;
import likelion.eight.certificationirequest.enums.AuthRequestType;
import likelion.eight.domain.course.model.Course;
import likelion.eight.domain.course.service.CourseService;
import likelion.eight.domain.userauth.controller.model.UserAuthCreateRequest;
import likelion.eight.domain.userauth.model.UserAuth;
import likelion.eight.domain.userauth.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@RequestMapping("/userAuth")
@RequiredArgsConstructor
@Slf4j
//@Controller
public class UserAuthDBController {

    private final UserAuthService userAuthService;
    private final CourseService courseService;


    @GetMapping("{id}/upload")
    public String uploadForm(
            @PathVariable long id,
            Model model
    ){
        List<Course> courses = courseService.getAllCourse();

        model.addAttribute("courses", courses);
        model.addAttribute("id", id);
        return "user/uploadForm";
    }

    @PostMapping("{id}/upload")
    public String uploadImage(
            @Valid @ModelAttribute("request")
            UserAuthCreateRequest request
    ){
//        request.setAuthRequestType(AuthRequestType.BOOTCAMP); // TODO 추후 화면에서 인증요청이 회사 or 부트캠프 선택 해야함
        UserAuth userAuth = userAuthService.create(request);
        return "redirect:/myPage";
    }

/*    @GetMapping("{id}/image")
    public ResponseEntity<Resource> getAuthImage(
            @PathVariable long id
    ){
        byte[] userAuthImage = userAuthService.getUserAuthImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // 이미지 타입에 맞게 설정 TODO 확장자 필터링
                .body(new ByteArrayResource(userAuthImage));
    }*/

    @GetMapping("/list")
    public String getAuthImages(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ){
        Page<UserAuth> userAuthPage = userAuthService.getUserAuthPage(page, size);
        int totalPages = userAuthPage.getTotalPages();
        if(totalPages > 0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("userAuthPage", userAuthPage);
        return "user/images";
    }

    @PostMapping("/{id}/approve")
    public String approveUserAuth(@PathVariable long id) {

        userAuthService.approve(id);
        return "redirect:/userAuth/list";
    }

    @PostMapping("/{id}/deny")
    public String denyUserAuth(@PathVariable long id) {
        userAuthService.deny(id);
        return "redirect:/userAuth/list";
    }

}
