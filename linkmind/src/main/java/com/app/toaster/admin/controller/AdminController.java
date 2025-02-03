package com.app.toaster.admin.controller;

import com.app.toaster.admin.common.RedirectResponse;
import com.app.toaster.admin.controller.dto.command.VerifyNewAdminCommand;
import com.app.toaster.admin.controller.dto.request.AdminTotpDto;
import com.app.toaster.admin.controller.dto.request.LinkDto;
import com.app.toaster.admin.controller.dto.request.SignInDto;
import com.app.toaster.admin.controller.dto.response.AdminResponse;
import com.app.toaster.admin.domain.AdminStatus;
import com.app.toaster.admin.entity.ToasterAdmin;
import com.app.toaster.admin.service.AdminService;
import com.app.toaster.admin.config.QrMfaAuthenticator;
//import com.app.toaster.admin.service.AdminLinkService;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.Success;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.external.client.aws.S3Service;
import com.app.toaster.external.client.discord.DiscordMessageProvider;

import com.app.toaster.external.client.discord.NotificationDto;
import com.app.toaster.external.client.discord.NotificationType;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
class AdminController {

    private final DiscordMessageProvider discordMessageProvider;
    private final S3Service s3Service;
    private final QrMfaAuthenticator qrMfaAuthenticator;
    private final AdminService adminService;
//    private final AdminLinkService adminLinkService;

    @PostMapping("/register")
    @ResponseBody
    public RedirectResponse<?> registerAdmin(@RequestBody SignInDto signInDto, HttpSession session) {
        VerifyNewAdminCommand res = adminService.registerAdmin(signInDto.username(), signInDto.password());

        String key = res.key();
        Long adminId = res.id();
        AdminStatus adminStatus = res.adminStatus();

        if (adminStatus.equals(AdminStatus.FIRST_REGISTER)){
            key = executeDiscordQrOperation(key);
        }

        session.setAttribute("VerifyId", adminId);
        session.setAttribute("QrUrl", key);

        return RedirectResponse.success(Success.LOGIN_SUCCESS, "verify",null);
    }

    @GetMapping("/register")
    public String getRegisterAdmin(Model model, HttpServletResponse response) throws IOException {
        return "basic/register";
    }

    @GetMapping("/verify")
    public String responseIsAdminCodeView(Model model) {
        return "basic/qrForm";
    }

    @GetMapping("/main")
    public String adminMain(Model model) {
//        model.addAttribute("imageUrl", imageUrl); // imageUrl을 모델에 추가
        return "basic/admin";
    }

    @PostMapping("/verify-code")
    @ResponseBody
    public RedirectResponse<AdminResponse> responseIsAdminView(HttpSession session, @RequestBody AdminTotpDto request) throws IOException {
        //admin인지 판단
        Long verifyId = (Long) session.getAttribute("VerifyId");

        if (verifyId == null) {
            throw new CustomException(Error.BAD_REQUEST_ID, "세션에 VerifyId가 없습니다.");
        }

        ToasterAdmin toasterAdmin = qrMfaAuthenticator.verifyGoogleTotpCode(Integer.valueOf(request.code()), verifyId);

        if (toasterAdmin == null){
            throw new CustomException(Error.BAD_REQUEST_ID, "잘못된 유저 입니다.");
        }
        AdminResponse result = new AdminResponse(toasterAdmin.getUsername());
        s3Service.deleteImage((String) session.getAttribute("QrUrl"));
        return RedirectResponse.success(Success.LOGIN_SUCCESS,"main", result);
    }

//    @PostMapping("/link")
//    public void addRecommendLink(@RequestBody LinkDto linkDto){
//        adminLinkService.addNewRecommendLink(linkDto);
//    }
//
//    @GetMapping("/link")
//    public String getAllLink(Model model){
//        model.addAttribute("links", adminLinkService.findAllLink());
//        return "basic/linkListPage";
//    }

    private String executeDiscordQrOperation(String key){
        MultipartFile qrImage = qrMfaAuthenticator.generateQrCode(key);
        String imageKey = s3Service.uploadImage(qrImage, "admin/");
        String qrUrl = s3Service.getURL(imageKey);
        discordMessageProvider.sendAdmin(new NotificationDto(NotificationType.ADMIN,null, qrUrl));
        return qrUrl;
    }
}
