package rs.xfinity.observer.interfaces;


import rs.xfinity.observer.anotations.HandleMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Publisher<T extends Message> {

    private final List<Subscriber> subscribers = new ArrayList<>();

    public final void addSubscriber(Subscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    public final void addAll(List<Subscriber> collection) {
        collection.forEach(this::addSubscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);

    }

    public void publish(T message) {
        subscribers.forEach(subscriber -> {
            Arrays.stream(subscriber.getClass().getMethods())
                    .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                            .anyMatch(annotation -> annotation.annotationType() == HandleMessage.class))
                    .filter(method -> Arrays.stream(method.getParameterTypes()).anyMatch(type -> type.isInstance(message)))
                    .forEach(method -> {
                        try {
                            method.invoke(subscriber, message);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }
}
