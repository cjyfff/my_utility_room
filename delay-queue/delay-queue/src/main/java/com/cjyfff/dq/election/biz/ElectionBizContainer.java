package com.cjyfff.dq.election.biz;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-11-21.
 */
@Getter
@Component
public class ElectionBizContainer {
    @Qualifier(value = "masterBeforeUpdateElectionFinishBiz")
    @Autowired
    private ElectionBiz masterBeforeUpdateElectionFinishBiz;

    @Qualifier(value = "masterAfterUpdateElectionFinishBiz")
    @Autowired
    private ElectionBiz masterAfterUpdateElectionFinishBiz;

    @Qualifier(value = "masterBeforeUpdateElectionNotYetBiz")
    @Autowired
    private ElectionBiz masterBeforeUpdateElectionNotYetBiz;

    @Qualifier(value = "masterAfterUpdateElectionNotYetBiz")
    @Autowired
    private ElectionBiz masterAfterUpdateElectionNotYetBiz;
}
