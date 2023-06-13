package ru.alemakave.mfstock.server.servlet;

import ru.alemakave.slib.servlet.ServletCommandManager;
import ru.alemakave.slib.servlet.IServletCommand;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.alemakave.mfstock.server.utils.PageUtils.*;

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
                    servletCommand.call(req, resp);
                }
            }
        }
    }
}
