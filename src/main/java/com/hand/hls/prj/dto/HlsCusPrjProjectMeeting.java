package com.hand.hls.prj.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "prj_project_meeting"
)
@Getter
@Setter
public class HlsCusPrjProjectMeeting extends PrjProjectMeeting {
    public HlsCusPrjProjectMeeting() {
    }

    private String meetingType;

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getMeetingType() {
        return meetingType;
    }

    private String reviewResults;

    private String reviewOpinions;

    private String riskClass;

    private String approveComments;
    private String meetingTime;
    private String dataClass;

}
