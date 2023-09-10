package com.programmerartist.furnace;

/**
 * Created by 程序员Artist on 2017/4/21.
 */
public class AlarmParam {

    /**
     * 报警相关设置
     */
    private String busiName = "";
    private String recievers = "";
    private boolean mail = false;
    private boolean rtx = false;
    private boolean sms = false;

    /**
     * 设置报警接收人： 英文名，分号相隔多个
     *
     * @param recievers 英文名，分号相隔多个；举例：yubozhang;sampsonzhu
     * @return
     */
    public AlarmParam recievers(String recievers) {
        this.recievers = recievers;
        return this;
    }

    /**
     * 设置报警标题，也作为计数器名称
     *
     * @param title
     * @return
     */
    public AlarmParam busiName(String title) {
        this.busiName = title;
        return this;
    }

    /**
     *  设置使用哪种报警器
     *
     * @return
     */
    public AlarmParam mail() {
        this.mail = true;
        return this;
    }
    public AlarmParam rtx() {
        this.rtx = true;
        return this;
    }
    public AlarmParam sms() {
        this.sms = true;
        return this;
    }


    /**
     *
     * @return
     */
    public static AlarmParam newBuilder(){
        return new AlarmParam();
    }

    /**
     *
     * @return
     */
    public AlarmParam build() { return this; }


    public String getBusiName() {
        return busiName;
    }
    public String getRecievers() {
        return recievers;
    }
    public boolean isMail() {
        return mail;
    }
    public boolean isRtx() {
        return rtx;
    }
    public boolean isSms() {
        return sms;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "AlarmParam{" +
                "busiName='" + busiName + '\'' +
                ", recievers='" + recievers + '\'' +
                ", mail=" + mail +
                ", rtx=" + rtx +
                ", sms=" + sms +
                '}';
    }
}
