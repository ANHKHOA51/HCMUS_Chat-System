package chatapp.utils;

import javafx.concurrent.Task;
import java.util.concurrent.Callable;

public class DbTask<T> extends Task<T> {
    private final Callable<T> callable;

    public DbTask(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    protected T call() throws Exception {
        return callable.call();
    }
}
