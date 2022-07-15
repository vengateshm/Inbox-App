package dev.vengateshm.messagingapp.inbox.controllers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItem;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItemRepository;
import dev.vengateshm.messagingapp.inbox.folders.Folder;
import dev.vengateshm.messagingapp.inbox.folders.FolderRepository;
import dev.vengateshm.messagingapp.inbox.folders.FoldersService;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private FoldersService foldersService;

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal,
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
        //model.addAttribute("userFolders", defaultFolders);

        // Fetch messages
        String folderLabel = "Inbox";
        List<EmailListItem> emailList = emailListItemRepository.findAllByKey_IdAndKey_Label(userId, folderLabel);

        PrettyTime p = new PrettyTime();
        emailList.stream().forEach(emailItem -> {
            UUID timeUUID = emailItem.getKey().getTimeUUID();
            Date emailDateTime = new Date(Uuids.unixTimestamp(timeUUID));
            emailItem.setAgoTimeString(p.format(emailDateTime));
        });

        model.addAttribute("emailList", emailList);

        return "inbox-page";
    }
}
