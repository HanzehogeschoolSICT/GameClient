package framework.interfaces;

/**
 * Created by SJongeJongeJonge on 11-4-2017.
 */
@FunctionalInterface
public interface MessageHandler {
    void accept(String message, Object[] args);
}