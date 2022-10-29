package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Badge;
import pl.bratosz.smartlockers.repository.BadgesRepository;

import java.util.Set;

@Service
public class BadgeService {
    private BadgesRepository badgesRepository;

    public BadgeService(BadgesRepository badgesRepository) {
        this.badgesRepository = badgesRepository;
    }

    public Badge getByNumber(int badgeNumber) {
        Badge badge = badgesRepository.getByNumber(badgeNumber);
        if(badge == null) {
            return createDefault(badgeNumber);
        } else {
            return badge;
        }
    }

    private Badge createDefault(int badgeNumber) {
        Badge badge = new Badge("Emblemat " + badgeNumber, badgeNumber);
        return badgesRepository.save(badge);
    }

    public Set<Badge> getBadges() {
        return badgesRepository.getAll();
    }
}
