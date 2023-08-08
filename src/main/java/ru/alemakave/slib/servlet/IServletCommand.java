package ru.alemakave.slib.servlet;

import org.eclipse.jetty.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IServletCommand {
    String getCommand();
    void call(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
