package rs.xfinity.observer;

import org.reflections.Reflections;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rs.xfinity.observer.anotations.Topic;
import rs.xfinity.observer.interfaces.Message;
import rs.xfinity.observer.interfaces.Publisher;
import rs.xfinity.observer.interfaces.Subscriber;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class StartupService implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationContext context;

    public StartupService(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        List<Publisher<Message>> publishers = new ArrayList<>();
        List<Subscriber> subscribers = new ArrayList<>();
        context.getBeansWithAnnotation(Topic.class).forEach((name, object) -> {
            if (object instanceof Publisher<?>) {
                publishers.add((Publisher) object);
                return;
            }

            if (object instanceof Subscriber) {
                subscribers.add((Subscriber) object);
            }
        });

        publishers.forEach(messagePublisher -> Arrays
                .asList(messagePublisher.getClass().getAnnotation(Topic.class).name())
                .forEach(t -> messagePublisher.addAll(findAllSubscribedToTopic(subscribers, t))));
        System.out.println("asd");

    }

    private List<Subscriber> findAllSubscribedToTopic(List<Subscriber> subscribers, String topic) {
        return subscribers.stream()
                .filter(subscriber -> Arrays.asList(subscriber.getClass().getAnnotation(Topic.class).name())
                        .contains(topic)).collect(Collectors.toList());
    }
}
