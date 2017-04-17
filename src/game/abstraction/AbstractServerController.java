package game.abstraction;

import framework.interfaces.Controller;
import framework.interfaces.Messagable;
import framework.interfaces.Networkable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Created by SJongeJongeJonge on 11-4-2017.
 */
public abstract class AbstractServerController implements Networkable, Messagable, Controller {
    private HashMap<String, BiConsumer<String, Object[]>> handlers;

    protected AbstractServerController() {
        handlers = new HashMap<>();
    }

    @Override
    public void call(String message, Object[] args) {
        handlers.keySet()
                .stream()
                .filter(message::startsWith)
                .forEach(key -> handlers.get(key).accept(message, args));
    }

    @Override
    public void putData(ArrayList<String> messages) {
        // Wordt nog niet afgevangen
    }

    protected void registerHandler(String prefix, BiConsumer<String, Object[]> handler) {
        handlers.put(prefix, handler);
    }

    protected void unregisterHandler(String prefix) {
        handlers.remove(prefix);
    }

    public abstract String getLocation();
}