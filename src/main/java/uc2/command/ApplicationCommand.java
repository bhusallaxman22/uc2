package uc2.command;

public interface ApplicationCommand<R> {
    R execute() throws Exception;
}
