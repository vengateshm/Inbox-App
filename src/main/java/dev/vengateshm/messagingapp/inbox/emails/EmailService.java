package dev.vengateshm.messagingapp.inbox.emails;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItem;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItemKey;
import dev.vengateshm.messagingapp.inbox.emailList.EmailListItemRepository;
import dev.vengateshm.messagingapp.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    EmailRepository emailRepository;
    @Autowired
    EmailListItemRepository emailListItemRepository;
    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from, List<String> to, String subject, String body) {
        // Email table
        Email email = new Email();
        email.setId(Uuids.timeBased());
        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subject);
        email.setBody(body);
        emailRepository.save(email);

        // Email in inbox folder - email list item
        to.forEach(toId -> {
            EmailListItem item = createEmailListItem(to, subject, email, toId, "Inbox");
            emailListItemRepository.save(item);
            unreadEmailStatsRepository.incrementUnreadCount(toId, "Inbox");
        });

        EmailListItem sent = createEmailListItem(to, subject, email, from, "Sent");
        sent.setUnread(false); // In his own sent item
        emailListItemRepository.save(sent);
    }

    private EmailListItem createEmailListItem(List<String> to, String subject, Email email, String itemOwner, String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setId(itemOwner); // Id of the receiving user
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        EmailListItem item = new EmailListItem();
        item.setKey(key);
        item.setTo(to);
        item.setSubject(subject);
        item.setUnread(true);
        return item;
    }
}
