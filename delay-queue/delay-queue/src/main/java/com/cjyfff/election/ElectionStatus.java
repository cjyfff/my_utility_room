package com.cjyfff.election;

import lombok.Getter;
import lombok.Setter;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-8-25.
 */
@Getter
@Setter
@Component
public class ElectionStatus {

    private volatile ElectionStatusType electionFinish = ElectionStatusType.NOT_YET;

    private volatile LeaderLatch leaderLatch;

    public enum ElectionStatusType {

        /**
         * 未完成
         */
        NOT_YET("NOT YET", 0),

        /**
         * 已完成
         */
        FINISH("FINISH", 1);

        private String desc;

        private Integer value;

        ElectionStatusType(String desc, Integer value) {
            this.desc = desc;
            this.value = value;
        }

        public static String getDesc(Integer value) {
            for (ElectionStatusType electionStatusType : ElectionStatusType.values()) {
                if (electionStatusType.getValue().equals(value)) {
                    return electionStatusType.getDesc();
                }
            }
            return null;
        }

        public static Integer getValue(String desc) {
            for (ElectionStatusType electionStatusType : ElectionStatusType.values()) {
                if (electionStatusType.getDesc().equals(desc)) {
                    return electionStatusType.getValue();
                }
            }
            return null;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}
