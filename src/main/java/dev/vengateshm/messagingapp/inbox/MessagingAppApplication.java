package dev.vengateshm.messagingapp.inbox;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItem;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItemKey;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItemRepository;
import dev.vengateshm.messagingapp.inbox.folders.Folder;
import dev.vengateshm.messagingapp.inbox.folders.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
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

    FolderRepository folderRepository;
    EmailListItemRepository emailListItemRepository;

    @Autowired
    public void setFolderRepository(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Autowired
    public void setEmailListItemRepository(EmailListItemRepository emailListItemRepository) {
        this.emailListItemRepository = emailListItemRepository;
    }

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
        folderRepository.save(new Folder("vengateshm", "Inbox", "blue"));
        folderRepository.save(new Folder("vengateshm", "Sent", "green"));
        folderRepository.save(new Folder("vengateshm", "Important", "yellow"));

        for (int i = 0; i < 10; i++) {
            EmailListItemKey key = new EmailListItemKey();
            key.setId("vengateshm");
            key.setLabel("Inbox");
            key.setTimeUUID(Uuids.timeBased());

            EmailListItem item = new EmailListItem();
            item.setKey(key);
            item.setTo(Arrays.asList("vengateshm"));
            item.setSubject("Subject " + i);
            item.setUnread(true);

            emailListItemRepository.save(item);
        }
    }
}
