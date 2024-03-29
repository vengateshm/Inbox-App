package dev.vengateshm.messagingapp.inbox;

import dev.vengateshm.messagingapp.inbox.emailList.EmailListItemRepository;
import dev.vengateshm.messagingapp.inbox.emails.EmailRepository;
import dev.vengateshm.messagingapp.inbox.emails.EmailService;
import dev.vengateshm.messagingapp.inbox.folders.Folder;
import dev.vengateshm.messagingapp.inbox.folders.FolderRepository;
import dev.vengateshm.messagingapp.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class MessagingAppApplication {

    @Autowired
    FolderRepository folderRepository;
    @Autowired
    EmailListItemRepository emailListItemRepository;
    @Autowired
    EmailRepository emailRepository;
    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;
    @Autowired
    EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(MessagingAppApplication.class, args);
    }

    @RequestMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println(principal);
        return principal.getAttribute("login");
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path path = astraProperties.getSecureConnectBundle().toPath();
        return cqlSessionBuilder -> cqlSessionBuilder.withCloudSecureConnectBundle(path);
    }

    @PostConstruct
    public void init() {
        /*folderRepository.save(new Folder("vengateshm", "Family", "blue"));
        folderRepository.save(new Folder("vengateshm", "Home", "green"));*/
        folderRepository.save(new Folder("vengateshm", "Work", "yellow"));

        /*unreadEmailStatsRepository.incrementUnreadCount("vengateshm", "Inbox");
        unreadEmailStatsRepository.incrementUnreadCount("vengateshm", "Inbox");
        unreadEmailStatsRepository.incrementUnreadCount("vengateshm", "Inbox");*/

        for (int i = 0; i < 10; i++) {
            emailService.sendEmail("vengateshm", Arrays.asList("vengateshm", "abc"), "Bonjour! " + i, "Body " + i);
            /*EmailListItemKey key = new EmailListItemKey();
            key.setId("vengateshm");
            key.setLabel("Inbox");
            key.setTimeUUID(Uuids.timeBased());

            EmailListItem item = new EmailListItem();
            item.setKey(key);
            item.setTo(Arrays.asList("vengateshm", "abc", "def"));
            item.setSubject("Subject " + i);
            item.setUnread(true);

            emailListItemRepository.save(item);

            Email email = new Email();
            email.setId(key.getTimeUUID());
            email.setFrom("vengateshm");
            email.setSubject(item.getSubject());
            email.setBody("Body " + i);
            email.setTo(item.getTo());

            emailRepository.save(email);*/
        }
        emailService.sendEmail("abc", Arrays.asList("def", "abc"), "Bonjour! ", "Body ");
    }
}
