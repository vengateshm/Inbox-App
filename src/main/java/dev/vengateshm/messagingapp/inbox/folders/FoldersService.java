package dev.vengateshm.messagingapp.inbox.folders;

import jnr.ffi.annotations.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoldersService {

    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    public List<Folder> init(String userId) {
        var defaultFolders = Arrays.asList(
                new Folder(userId, "Inbox", "blue"),
                new Folder(userId, "Sent", "purple")/*,
                new Folder(userId, "Important", "red"),
                new Folder(userId, "Done", "green")*/
        );
        // for (int i = 0; i < defaultFolders.size(); i++) {
        //     defaultFolders.get(i).setCreatedTimeUuid(Uuids.timeBased());
        // }
        return defaultFolders;
    }

    public Map<String, Integer> mapCountToLabels(String userId) {
        List<UnreadEmailStats> stats = unreadEmailStatsRepository.findAllById(userId);
        return stats.stream()
                .collect(Collectors.toMap(UnreadEmailStats::getLabel,
                        UnreadEmailStats::getUnreadCount));
    }
}
