package com.cjyfff.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by jiashen on 18-8-15.
 */
public class LeaderSelectorTest2 {

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

            LeaderLatch example = new LeaderLatch(client, PATH, "Client # 2");
            example.start();

            example.await();
            client.close();

        }
    }

