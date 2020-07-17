package com.php25.zookeeper.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * @author penghuiping
 * @date 2020/7/9 14:47
 */
@Configuration
public class ZookeeperConfig {

    private Long nodeId = null;

    @Configuration
    public class ZookeeperStart implements ApplicationListener<ApplicationStartedEvent> {
        @Override
        public void onApplicationEvent(ApplicationStartedEvent event) {
            try {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                CuratorFramework client = CuratorFrameworkFactory.newClient("zoo1:2181,zoo2:2181,zoo3:2181", retryPolicy);
                client.start();

                DistributedAtomicLong distributedAtomicLong = new DistributedAtomicLong(client, "/tmp/zookeeper_nodes", retryPolicy);
                AtomicValue<Long> value = distributedAtomicLong.increment();
                if (!value.succeeded()) {
                    throw new RuntimeException("无法获取nodeId");
                }
                nodeId = value.postValue();
                System.out.println("========================>" + "nodeId:" + nodeId);


                LeaderSelector leaderSelector = new LeaderSelector(client, "/zookeeper_selector", new LeaderSelectorListenerAdapter() {
                    @Override
                    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                        System.out.println("========================>" + nodeId + " isLeader");
                        while (true) {
                            Thread.sleep(1000);
                        }
                    }
                });
                leaderSelector.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
