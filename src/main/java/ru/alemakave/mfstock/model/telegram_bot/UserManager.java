package ru.alemakave.mfstock.model.telegram_bot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class UserManager {
    public Set<TGUserJson> registeredUsers = new HashSet<>();
    @JsonIgnore
    @Value("${mfstock.users.filepath:./Users.json}")
    private String usersJson;

    @JsonCreator
    UserManager(@JsonProperty("registeredUsers") List<TGUserJson> registeredUsers) {
        this.registeredUsers.addAll(registeredUsers);
    }

    public void registryUser(TGUserJson tgUserJson) {
        registeredUsers.add(tgUserJson);
        saveUsers();
    }

    public boolean isRegisteredUserByChatId(Long chatId) {
        return registeredUsers.stream().anyMatch(tgUserJson -> tgUserJson.getTgUserID() == chatId);
    }

    public Set<TGUserJson> getRegisteredUsers() {
        return registeredUsers;
    }

    private void saveUsers() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(new File(usersJson), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void loadUsers() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (Files.notExists(Path.of(usersJson))) {
                saveUsers();
                return;
            }

            registeredUsers.addAll(objectMapper.readValue(new File(usersJson), this.getClass()).getRegisteredUsers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
