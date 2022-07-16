package dev.vengateshm.messagingapp.inbox.controllers;

import dev.vengateshm.messagingapp.inbox.emails.Email;
import dev.vengateshm.messagingapp.inbox.emails.EmailRepository;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private FoldersService foldersService;

    @GetMapping("/emails/{id}")
    public String emailView(
            @PathVariable String id,
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";

        String userId = principal.getAttribute("login");

        // Fetch folders
        // Default folders
        List<Folder> defaultFolders = foldersService.init(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        // Folders from cassandra db
        List<Folder> userFolders = folderRepository.findAllById(userId);
        if (userFolders.size() > 0) {
            model.addAttribute("userFolders", userFolders);
        }
        model.addAttribute("stats", foldersService.mapCountToLabels(userId));

        Optional<Email> optionalEmail = emailRepository.findById(UUID.fromString(id));
        if (optionalEmail.isEmpty()) {
            return "inbox-page";
        }
        Email email = optionalEmail.get();
        String toIds = String.join(", ",email.getTo());
        model.addAttribute("email", email);
        model.addAttribute("toIds", toIds);
        return "email-page";
    }
}
