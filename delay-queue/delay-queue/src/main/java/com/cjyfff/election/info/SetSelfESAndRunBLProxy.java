package com.cjyfff.election.info;

import com.cjyfff.bl.BusinessLogic;
import com.cjyfff.election.exception.ElectionException;
import com.cjyfff.election.info.ElectionStatus.ElectionStatusType;
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

    public void setFinish(BusinessLogic before, BusinessLogic after) throws Exception {
        checkBeforeAndAfter(before, after);

        before.run();
        electionStatus.setElectionStatus(ElectionStatusType.FINISH);
        after.run();
    }

    public void setNotYet(BusinessLogic before, BusinessLogic after) throws Exception {
        checkBeforeAndAfter(before, after);

        before.run();
        electionStatus.setElectionStatus(ElectionStatusType.NOT_YET);
        after.run();
    }

    private void checkBeforeAndAfter(BusinessLogic before, BusinessLogic after) throws ElectionException {
        if (before == null || after == null) {
            throw new ElectionException("Before Method or After Method can neither be null.");
        }
    }
}
