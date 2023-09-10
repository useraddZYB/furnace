package com.programmerartist.furnace;

import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 程序员Artist on 2017/4/21.
 */
public abstract class AbstractFurnace implements Furnace {

    private SwitchParam switchParam;
    private AlarmParam alarmParam;

    /**
     * 频次计数器名称-计数器
     *
     * 一个进程含有多个频次计数器时,互相隔离,分别计数
     *
     * business 2 date 2 count
     *
     */
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> totalCounter = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> failCounter  = new ConcurrentHashMap<>();

    protected Throwable e;        // 可选参数
    protected Boolean manual;     // 可选参数

    private boolean canDo = true;


    /**
     * 业务调用：请求当前熔炉状态，是否已熔断
     *
     * @return true ? 业务可正常通行 : 业务需要阻断
     */
    @Override
    public boolean canDo() {
        return this.canDo;
    }

    /**
     * 初始化: 每个熔炉只调用一次，打开熔炉（启动定时任务做检查：失败率，以判断是否设置开关）
     *
     * @param switchParam
     * @param alarmParam
     */
    protected void initParam(SwitchParam switchParam, AlarmParam alarmParam) {
        this.switchParam = switchParam;
        this.alarmParam = alarmParam;
    }

    /**
     * 人为开关不设置的时候，才走自动熔炉处理
     *
     * @return
     */
    protected boolean auto() {
        return null == this.manual;
    }

    /**
     * 降级业务：关闭开关
     */
    protected void close() {
        if(this.canDo()) this.canDo = false;
    }

    /**
     * 恢复业务：打开开关
     */
    protected void reOpen() {
        try {
            if(!auto()) return ;

            if(!this.canDo()) {
                this.canDo = true;

                getLog().info("reOpen finish; totalCounter={}, failCounter={}, manual={}", totalCounter, failCounter, this.manual);

                AlarmParam notice = this.getAlarmParam();
                String title     = notice.getBusiName() + " Furnace auto reOpen";
                String content   = "[" + notice.getBusiName() + "] " + " Furnace has been open, then business work again. (manual=" + this.manual + ")";

                getLog().warn("Furnace Notice: title={}, content={}, notice={}", title, content, notice);
            }
        } catch (Exception ex) {
            getLog().error("reOpen error: ", ex);
        }
    }



    public abstract Logger getLog();



    public SwitchParam getSwitchParam() {
        return switchParam;
    }
    public AlarmParam getAlarmParam() {
        return alarmParam;
    }
    public ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> getTotalCounter() {
        return totalCounter;
    }
    public ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> getFailCounter() {
        return failCounter;
    }
}
