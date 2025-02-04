package greencity.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void notify(String notification) {
        System.out.println(notification);
    }
}
