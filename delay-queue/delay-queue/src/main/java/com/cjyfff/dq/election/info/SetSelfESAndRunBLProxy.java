package com.cjyfff.dq.election.info;

import com.cjyfff.dq.election.biz.ElectionBiz;
import com.cjyfff.dq.election.exception.ElectionException;
import com.cjyfff.dq.election.info.ElectionStatus.ElectionStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 设置本机选举状态，并运行相关业务逻辑代理类
 * Created by jiashen on 2018/9/9.
 */
@Component
public class SetSelfESAndRunBLProxy {

    @Autowired
    private ElectionStatus electionStatus;

    public void setFinish(ElectionBiz before, ElectionBiz after) throws Exception {
        checkBeforeAndAfterMethod(before, after);

        before.run();
        electionStatus.setElectionStatus(ElectionStatusType.FINISH);
        after.run();
    }

    public void setNotYet(ElectionBiz before, ElectionBiz after) throws Exception {
        checkBeforeAndAfterMethod(before, after);

        before.run();
        electionStatus.setElectionStatus(ElectionStatusType.NOT_YET);
        after.run();
    }

    private void checkBeforeAndAfterMethod(ElectionBiz before, ElectionBiz after) throws ElectionException {
        if (before == null || after == null) {
            throw new ElectionException("Before Method or After Method can neither be null.");
        }
    }
}
