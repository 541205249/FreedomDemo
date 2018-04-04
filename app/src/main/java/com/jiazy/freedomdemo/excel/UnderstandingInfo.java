package com.jiazy.freedomdemo.excel;

/**
 * 作者： jiazy
 * 日期： 2018/3/22.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class UnderstandingInfo {
    private String skillId;
    private String intentName;
    private String data;

    public UnderstandingInfo(String skillId, String intentName, String data) {
        this.skillId = skillId;
        this.intentName = intentName;
        this.data = data;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getIntentName() {
        return intentName;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return skillId + "," +
                intentName + "," +
                data;
    }
}
