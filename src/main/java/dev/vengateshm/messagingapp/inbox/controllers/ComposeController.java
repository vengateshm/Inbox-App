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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private FoldersService foldersService;

    @GetMapping(value = "/compose")
    public String getComposePage(
            @RequestParam(required = false) String to, // exact variable name must be specified
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }

        String userId = principal.getAttribute("login");

        // Fetch folders
        // Default folders
        List<Folder> defaultFolders = foldersService.init(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        // Folders from cassandra db
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);

        if (StringUtils.hasText(to)) {
            String[] splitToIds = to.split(",");
            List<String> uniqueToIds = Arrays.stream(splitToIds)
                    .map(StringUtils::trimWhitespace)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .collect(Collectors.toList());
            model.addAttribute("toIds", String.join(", ", uniqueToIds));
        }

        return "compose-page";
    }
}
