package dev.vengateshm.messagingapp.inbox.controllers;

import dev.vengateshm.messagingapp.inbox.folders.Folder;
import dev.vengateshm.messagingapp.inbox.folders.FolderRepository;
import dev.vengateshm.messagingapp.inbox.folders.FoldersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FoldersService foldersService;

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal,
                           Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";

        String loginId = principal.getAttribute("login");

        // Default folders
        List<Folder> defaultFolders = foldersService.init(loginId);
        model.addAttribute("defaultFolders", defaultFolders);

        // Folders from cassandra db
        List<Folder> userFolders = folderRepository.findAllById(loginId);
        if (userFolders.size() > 0) {
            model.addAttribute("userFolders", userFolders);
        }
        model.addAttribute("userFolders", defaultFolders);
        return "inbox-page";
    }
}
