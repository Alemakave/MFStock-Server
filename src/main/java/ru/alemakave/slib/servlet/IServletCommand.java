package ru.alemakave.slib.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IServletCommand {
    String getCommand();
    void call(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
