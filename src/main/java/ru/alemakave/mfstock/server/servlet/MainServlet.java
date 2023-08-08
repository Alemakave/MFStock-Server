package ru.alemakave.mfstock.server.servlet;

import org.eclipse.jetty.http.HttpMethod;
import ru.alemakave.slib.servlet.IServletCommand;
import ru.alemakave.slib.servlet.ServletCommandManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.alemakave.mfstock.server.utils.PageUtils.getCommands;

public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        for (String command : getCommands(req)) {
            if (command != null) {
                IServletCommand servletCommand;

                if (command.contains("=")) {
                    servletCommand = ServletCommandManager.getManager().getServletCommand(command.substring(0, command.indexOf("=")));
                } else {
                    servletCommand = ServletCommandManager.getManager().getServletCommand(command);
                }

                if (servletCommand != null) {
                    servletCommand.call(HttpMethod.GET, req, resp);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        for (String command : getCommands(req)) {
            if (command != null) {
                IServletCommand servletCommand;

                if (command.contains("=")) {
                    servletCommand = ServletCommandManager.getManager().getServletCommand(command.substring(0, command.indexOf("=")));
                } else {
                    servletCommand = ServletCommandManager.getManager().getServletCommand(command);
                }

                if (servletCommand != null) {
                    servletCommand.call(HttpMethod.POST, req, resp);
                }
            }
        }
    }
}
