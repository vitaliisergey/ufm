package by.intexsoft.ufm.tests;

import by.intexsoft.ufm.config.ApplicationConfig;
import by.intexsoft.ufm.model.Client;
import by.intexsoft.ufm.model.OutputResult;
import by.intexsoft.ufm.service.ClientFileService;
import by.intexsoft.ufm.service.ClientProcessorService;
import by.intexsoft.ufm.utils.DataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class, loader = AnnotationConfigContextLoader.class)
public class ClientProcessorServiceTest {
    private static final int COUNT_CLIENTS = 2;
    private static final int COUNT_SUBSCRIBERS_BIG = 101;
    private static final int COUNT_SUBSCRIBERS_SMALL = 100;
    private static final long DEFAULT_SPENT = 100L;

    @Value("${inbox.filename.template:{0}.json}")
    private String fileNameTemplate;
    @Value("${inbox.folder.name:inbox}")
    private String inboxFolder;
    @Value("${outbox.folder.name:outbox}")
    private String outboxFolder;

    @Autowired
    private ClientProcessorService clientProcessorService;
    @Autowired
    private ClientFileService clientFileService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testBigClients() throws Exception {
        BlockingQueue<String> clientIds = new LinkedBlockingQueue<>();
        for (long i = 1; i <= COUNT_CLIENTS; i++) {
            Client client = DataUtils.createClient(i, COUNT_SUBSCRIBERS_BIG, DEFAULT_SPENT);
            String fileName = DataUtils.createClientFile(client, inboxFolder, fileNameTemplate, objectMapper);
            clientIds.add(fileName);
        }
        HashSet<Client> clients = new HashSet<>();
        BlockingQueue<String> cacheClientIds = new LinkedBlockingQueue<>();
        cacheClientIds.addAll(clientIds);
        for (long i = 1; i <= cacheClientIds.size(); i++) {
            clientProcessorService.oneStart(clientIds, clients);
        }
        for (String cacheClientId : cacheClientIds) {
            OutputResult outputResult = DataUtils.getOutputResult(cacheClientId, outboxFolder, objectMapper);
            assertThat(outputResult.spentTotal).isEqualTo(COUNT_SUBSCRIBERS_BIG * DEFAULT_SPENT);
            assertThat(outputResult.isBig).isEqualTo(true);
        }
    }

    @Test
    public void testSmallClients() throws Exception {
        BlockingQueue<String> clientIds = new LinkedBlockingQueue<>();
        for (long i = 1; i <= COUNT_CLIENTS; i++) {
            Client client = DataUtils.createClient(i, COUNT_SUBSCRIBERS_SMALL, DEFAULT_SPENT);
            String fileName = DataUtils.createClientFile(client, inboxFolder, fileNameTemplate, objectMapper);
            clientIds.add(fileName);
        }

        HashSet<Client> clients = new HashSet<>();
        BlockingQueue<String> cacheClientIds = new LinkedBlockingQueue<>();
        cacheClientIds.addAll(clientIds);
        for (long i = 1; i <= cacheClientIds.size(); i++) {
            clientProcessorService.oneStart(clientIds, clients);
        }
        for (String cacheClientId : cacheClientIds) {
            OutputResult outputResult = DataUtils.getOutputResult(cacheClientId, outboxFolder, objectMapper);
            assertThat(outputResult.spentTotal).isEqualTo(COUNT_SUBSCRIBERS_SMALL * DEFAULT_SPENT);
            assertThat(outputResult.isBig).isEqualTo(false);
        }
    }
}
