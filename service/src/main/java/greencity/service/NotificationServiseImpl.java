package greencity.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationServiseImpl implements NotificationServise {
    @Override
    public void notify(String notification) {
        System.out.println(notification);
    }
}
