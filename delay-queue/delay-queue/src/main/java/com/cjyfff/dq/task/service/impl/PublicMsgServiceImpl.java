package com.cjyfff.dq.task.service.impl;

import com.cjyfff.dq.election.info.ElectionStatus;
import com.cjyfff.dq.election.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.dq.task.common.ApiException;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/9/23.
 */
@Service
public class PublicMsgServiceImpl implements PublicMsgService {

    @Autowired
    private ElectionStatus electionStatus;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(AcceptMsgDto reqDto) throws Exception {
        checkElectionStatus();
    }

    private void checkElectionStatus() throws ApiException {
        if (! ElectionStatusType.FINISH.equals(electionStatus.getElectionStatus())) {
            throw new ApiException("101", "选举未完成，不接受请求");
        }
    }
}
