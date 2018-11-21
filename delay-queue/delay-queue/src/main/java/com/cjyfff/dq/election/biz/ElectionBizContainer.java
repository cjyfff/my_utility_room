package com.cjyfff.dq.election.biz;

/**
 * Created by jiashen on 18-11-21.
 */
public class ElectionBizContainer {
    public static ElectionBiz masterBeforeUpdateElectionFinishBiz = new NoneBiz();

    public static ElectionBiz masterAfterUpdateElectionFinishBiz = new NoneBiz();

    public static ElectionBiz masterBeforeUpdateElectionNotYetBiz = new NoneBiz();

    public static ElectionBiz masterAfterUpdateElectionNotYetBiz = new NoneBiz();
}
