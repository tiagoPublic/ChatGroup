import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.connection-string}")
    private String zookeeperConnectionString;

    private static final int SESSION_TIMEOUT_MS = 5000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final String SERVICE_PATH = "/chat-service"; // Caminho do serviço no Zookeeper

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zookeeperConnectionString)
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        return curatorFramework;
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public ServiceDiscovery<Void> serviceDiscovery(CuratorFramework curatorFramework) throws Exception {
        return ServiceDiscoveryBuilder.builder(Void.class)
                .client(curatorFramework)
                .basePath(SERVICE_PATH)
                .watchInstances(true) // Adiciona um observador para assistir a mudanças nas instâncias
                .serializer(new JsonInstanceSerializer<>(Void.class))
                .build();
    }
}