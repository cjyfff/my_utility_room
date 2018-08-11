//package com.cjyfff;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.curator.RetryPolicy;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.framework.recipes.leader.LeaderSelector;
//import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
//import org.apache.curator.framework.state.ConnectionState;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//import org.apache.curator.utils.CloseableUtils;
//
///**
// * Created by jiashen on 2018/8/7.
// */
//public class LeaderSelectorTest {
//
//    private static final String PATH = "/demo/leader";
//
//    public static void main(String[] args) {
//
//        List<LeaderSelector> selectors = new ArrayList<>();
//        List<CuratorFramework> clients = new ArrayList<>();
//        try {
//            for (int i = 0; i < 10; i++) {
//                CuratorFramework client = getClient();
//                clients.add(client);
//
//                final String name = "client#" + i;
//                LeaderSelector leaderSelector = new LeaderSelector(client, PATH, new LeaderSelectorListener() {
//                    @Override
//                    public void takeLeadership(CuratorFramework client) throws Exception {
//
//                        System.out.println(name + ":I am leader.");
//                        Thread.sleep(2000);
//                    }
//
//                    @Override
//                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
//
//                    }
//                });
//
//                leaderSelector.autoRequeue();
//                leaderSelector.start();
//                selectors.add(leaderSelector);
//
//            }
//            Thread.sleep(Integer.MAX_VALUE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            for(CuratorFramework client : clients){
//                CloseableUtils.closeQuietly(client);
//            }
//
//            for(LeaderSelector selector : selectors){
//                CloseableUtils.closeQuietly(selector);
//            }
//
//        }
//    }
//
//    private static CuratorFramework getClient() {
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//        CuratorFramework client = CuratorFrameworkFactory.builder()
//            .connectString("192.168.43.48:2181")
//            .retryPolicy(retryPolicy)
//            .sessionTimeoutMs(6000)
//            .connectionTimeoutMs(3000)
//            .namespace("demo")
//            .build();
//        client.start();
//        return client;
//    }
//}
