package com.cjyfff;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jiashen on 2018/8/5.
 */
public class TestLeaderLatch {
    //会话超时时间
    private final int SESSION_TIMEOUT = 30 * 1000;

    //连接超时时间
    private final int CONNECTION_TIMEOUT = 3 * 1000;

    //客户端数量
    private final int CLIENT_NUMBER = 10;

    //ZooKeeper服务地址
    private static final String SERVER = "192.168.43.48:2181";

    private final String PATH = "/curator/latchPath";

    //创建连接实例
    private CuratorFramework client = null;

    /**
     * baseSleepTimeMs：初始的重试等待时间
     * maxRetries：最多重试次数
     */
    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    //LeaderLatch实例集合
    private List<LeaderLatch> leaderLatchList = new ArrayList<>(CLIENT_NUMBER);

    /**
     * 初始化
     * @throws Exception
     */
    @Before
    public void init() throws Exception{
        //创建 CuratorFrameworkImpl实例
        client = CuratorFrameworkFactory.newClient(SERVER, SESSION_TIMEOUT, CONNECTION_TIMEOUT, retryPolicy);
        client.start();

        for(int i=0; i<CLIENT_NUMBER; i++){
            //创建LeaderLatch实例
            LeaderLatch leaderLatch = new LeaderLatch(client, PATH, "Client #" + i);
            leaderLatchList.add(leaderLatch);
            leaderLatch.start();
        }

        //等待Leader选举完成
        TimeUnit.SECONDS.sleep(5);
        System.out.println("**********LeaderLatch初始化完成**********");
    }

    /**
     * 测试获取当前选举出来的leader，以及手动尝试获取领导权
     * @throws Exception
     */
    @Test
    public void testCheckLeader() throws Exception{
        LeaderLatch currentLeader = null;

        for(LeaderLatch tmp : leaderLatchList){
            if(tmp.hasLeadership()){ //判断是否是leader
                currentLeader = tmp;
                break;
            }
        }

        System.out.println("当前leader是： " + currentLeader.getId());

        // 从List中移除当前主节点，并从剩下的节点中继续选举leader
        currentLeader.close(); //关闭当前主节点
        leaderLatchList.remove(currentLeader); //从List中移除
        TimeUnit.SECONDS.sleep(5); //等待再次选举

        //再次获取当前leader
        for(LeaderLatch tmp : leaderLatchList){
            if(tmp.hasLeadership()){
                currentLeader = tmp;
                break;
            }
        }

        System.out.println("新leader是： " + currentLeader.getId());
        currentLeader.close(); //关闭当前主节点
        leaderLatchList.remove(currentLeader); //从List中移除

        LeaderLatch firstNode = leaderLatchList.get(0); //获取此时第一个节点
        System.out.println("删除leader后，当前第一个节点： " + firstNode.getId());

        firstNode.await(10, TimeUnit.SECONDS); //阻塞并尝试获取领导权，可能失败

        //再次获取当前leader
        for(LeaderLatch tmp : leaderLatchList){
            if(tmp.hasLeadership()){
                currentLeader = tmp;
                break;
            }
        }

        System.out.println("最终实际leader是： " + currentLeader.getId());

    }

    /**
     * 测试完毕关闭连接
     */
    @After
    public void close(){
        for(LeaderLatch tmp : leaderLatchList){
            CloseableUtils.closeQuietly(tmp);
        }

        CloseableUtils.closeQuietly(client);
    }

}
