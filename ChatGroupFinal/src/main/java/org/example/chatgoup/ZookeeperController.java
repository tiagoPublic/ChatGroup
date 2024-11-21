import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/zookeeper")
public class ZookeeperController {

    private final ServiceDiscovery<Void> serviceDiscovery;
    private final CuratorFramework curatorFramework;

    public ZookeeperController(ServiceDiscovery<Void> serviceDiscovery, CuratorFramework curatorFramework) {
        this.serviceDiscovery = serviceDiscovery;
        this.curatorFramework = curatorFramework;
    }

    @GetMapping("/nodes")
    public Collection<ServiceInstance<Void>> getZNodes() {
        List<ServiceInstance<Void>> allInstances = new ArrayList<>();

        try {
            // Tenta consultar o primeiro nó
            Collection<ServiceInstance<Void>> instances = serviceDiscovery.queryForInstances("chat-service");
            allInstances.addAll(instances);
        } catch (Exception e) {
            // Em caso de falha, tenta consultar todos os nós do Zookeeper
            try {
                List<String> nodeList = curatorFramework.getChildren().forPath("/");
                for (String node : nodeList) {
                    Collection<ServiceInstance<Void>> instances = serviceDiscovery.queryForInstances(node);
                    allInstances.addAll(instances);
                }
            } catch (Exception ex) {
                // Lida com erros ao consultar todos os nós do Zookeeper
                ex.printStackTrace();
            }
        }

        return allInstances;
    }
}