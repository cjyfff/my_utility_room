package com.cjyfff.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

/**
 * Created by jiashen on 18-8-15.
 */
public class LeaderSelectorTest {

    private static final String PATH = "/leader";
    private static final int COUNT = 5;

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.43.241:2181",
            new ExponentialBackoffRetry(1000, 3));
        client.start();

        //List<LeaderLatch> list = new ArrayList<>();
        //
        //for (int i = 1; i <= COUNT; i++) {
        //    LeaderLatch example = new LeaderLatch(client, PATH, "Client #" + i);
        //    example.start();
        //    list.add(example);
        //}
        //
        //TimeUnit.SECONDS.sleep(10);
        //
        //LeaderLatch leader = null;
        //for (LeaderLatch ll : list) {
        //    if (ll.hasLeadership()) {
        //        leader = ll;
        //    }
        //    System.out.println(ll.getId() + "\t" + ll.hasLeadership());
        //}
        //
        //TimeUnit.SECONDS.sleep(2);
        //
        //list.remove(leader);
        //leader.close();
        //
        //TimeUnit.SECONDS.sleep(2);
        //
        //System.out.println("========================");
        //
        //for (LeaderLatch ll : list) {
        //    System.out.println(ll.getId() + "\t" + ll.hasLeadership());
        //}
        //
        //for (LeaderLatch ll : list) {
        //    ll.close();
        //}

        LeaderLatch example = new LeaderLatch(client, PATH, "Client # 1");
        example.start();

        example.await();
        client.close();

    }
}
