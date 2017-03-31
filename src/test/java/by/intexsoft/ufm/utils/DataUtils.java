package by.intexsoft.ufm.utils;

import by.intexsoft.ufm.model.Client;
import by.intexsoft.ufm.model.OutputResult;
import by.intexsoft.ufm.model.Subscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;

public class DataUtils {


    static public String createClientFile(Client client, String inboxFolder, String fileNameTemplate, ObjectMapper objectMapper) throws IOException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("clientId", client.clientId);
        ArrayNode array = objectMapper.valueToTree(objectMapper.valueToTree(client.subscribers));
        objectNode.putArray("subscribers").addAll(array);
        File file = createFile(client.clientId, inboxFolder, fileNameTemplate);
        objectMapper.writeValue(file, objectNode);
        return file.getName();
    }

    static public Client createClient(long id, long subscribersCount, long spent) {
        Client client = new Client();
        client.clientId = id;
        client.subscribers = new ArrayList<>();
        for (long i = 1; i <= subscribersCount; i++) {
            Subscriber subscriber = createSubscriber(client, i, spent);
            client.subscribers.add(subscriber);
        }
        return client;
    }

    static public Subscriber createSubscriber(Client client, long id, long spent) {
        Subscriber subscriber = new Subscriber();
        subscriber.id = id;
        subscriber.spent = spent;
        subscriber.client = client;
        return subscriber;
    }

    static public File createFile(Long clientId, String inboxFolder, String fileNameTempalate) throws IOException {
        File file = Paths.get(inboxFolder, MessageFormat.format(fileNameTempalate, clientId)).toFile();
        file.createNewFile();
        return file;
    }

    static public OutputResult getOutputResult(String name, String outboxFolder, ObjectMapper mapper) throws IOException {
        Path file = Paths.get(outboxFolder, name);
        return mapper.readValue(file.toFile(), OutputResult.class);
    }
}