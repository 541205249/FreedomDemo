package com.jiazy.freedomdemo.excel;

/**
 * 作者： jiazy
 * 日期： 2018/3/22.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class UnderstandingResultInfo extends UnderstandingInfo {
    private int result = -1;

    public UnderstandingResultInfo(String skillId, String intentName, String data, int result) {
        super(skillId, intentName, data);
        this.result = result;
    }

    public UnderstandingResultInfo(UnderstandingInfo understandingInfo, int result) {
        super(understandingInfo.getData(), understandingInfo.getSkillId(), understandingInfo.getIntentName());
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
