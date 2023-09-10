package com.programmerartist.furnace;

import com.programmerartist.artist.util.date.DateFormat;
import com.programmerartist.artist.util.date.DateUtill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 程序员Artist on 2017/4/24.
 */
public class DefaultFurnace extends AbstractFurnace {
    public static final Logger log = LoggerFactory.getLogger(DefaultFurnace.class);

    private static final int DELAY_OPEN  = 5;
    private static final int DELAY_CLOSE = 10;


    /**
     *
     * @param switchParam
     * @param alarmParam
     */
    public DefaultFurnace(SwitchParam switchParam, AlarmParam alarmParam) {
        this.initParam(switchParam, alarmParam);
    }

    /**
     * 启动定时任务
     */
    @Override
    public void _start() {
        int periodSeconds = this.getSwitchParam().getTimeUnit().getSeconds();

        /**
         * 启动关闭任务：条件满足，则熔断，关闭业务开关
         */
        Executors.newScheduledThreadPool(
                1,
                new ThreadFactoryInfo("T_CLOSE_" + this.getAlarmParam().getBusiName() + "_", new AtomicInteger(1))

        ).scheduleAtFixedRate(
                () -> this.judge(),
                DELAY_CLOSE,
                this.getSwitchParam().getClosePeriod() * periodSeconds,
                TimeUnit.SECONDS);


        /**
         * 启动恢复任务：到时间，自动恢复，打开业务开关
         */
        Executors.newScheduledThreadPool(
                1,
                new ThreadFactoryInfo("T_OPEN_" + this.getAlarmParam().getBusiName() + "_", new AtomicInteger(1))

        ).scheduleAtFixedRate(
                () -> this.reOpen(),
                DELAY_OPEN,
                this.getSwitchParam().getReOpenPeriod() * periodSeconds,
                TimeUnit.SECONDS);


        log.info("DefaultFurnace start. switchParam={}, alarmParam={}", this.getSwitchParam(), this.getAlarmParam());
    }

    /**
     * 业务调用：总请求数上报
     */
    @Override
    public void total() {
        if(auto()) this.incrOrGetLast(this.getTotalCounter(), false);
    }

    /**
     * 业务调用：失败请求数上报
     */
    @Override
    public void fail() {
        if(auto()) this.incrOrGetLast(this.getFailCounter(), false);
    }

    /**
     * 业务调用：失败请求数上报
     */
    @Override
    public void fail(Throwable e) {
        if(auto()) {
            this.incrOrGetLast(this.getFailCounter(), false);
            super.e = e;
        }

    }

    /**
     *
     * @param manual null ? 自动开关 : 手动开关
     * @return
     */
    @Override
    public boolean canDo(Boolean manual) {
        if(this.manual != manual) this.manual = manual;

        return null!=manual ? manual : super.canDo();
    }

    @Override
    public Logger getLog() {
        return log;
    }

    /**
     * 判断是否需要熔断
     *
     */
    private void judge() {
        int total       = 0;
        float fail      = 0;
        int failPercent = 0;
        boolean hasFail = false;

        try {
            if(!auto()) {
                log.info("judge auto is false by manual");
                return ;
            }

            if(!canDo()) {
                log.info("judge skip, because canDo={}, manual={}", canDo(), this.manual);
                return ;
            }

            // 避免误判
            total        = this.incrOrGetLast(this.getTotalCounter(), true);
            int minTotal = this.getSwitchParam().getMinTotal();
            if(total <= minTotal) {
                log.info("judge total({}) < minTotal({}), refuse judge", total, minTotal);
                return;
            }

            // 计算失败率
            fail = this.incrOrGetLast(this.getFailCounter(), true) * 1.0f;

            failPercent = (int)((fail / total) * 100);

            // 如果失败率超过预期，则熔断
            hasFail = false;
            if(failPercent >= this.getSwitchParam().getCutPercent()) {
                this.closeAndAlarm((int)fail, total, failPercent);

                hasFail = true;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        log.info("judge failed={}, businessName={}, failPercent={}, fail={}, total={}, manual={}", hasFail, this.getAlarmParam().getBusiName(), failPercent, fail, total, this.manual);
    }


    /**
     * 熔断处理：关闭开关，并告警
     *
     * @param fail
     * @param total
     * @param failPercent
     */
    private void closeAndAlarm(int fail, int total, int failPercent) {

        // 关闭
        this.close();

        // 告警
        AlarmParam alarm = this.getAlarmParam();
        String title     = alarm.getBusiName() + " Furnace auto close";
        String content   = "[" + alarm.getBusiName() + "] " + " Furnace has been closed, then business refuse work; detail is: failPercent=" + failPercent + ", fail=" + fail + ", total=" + total + ", timeUnit=" + this.getSwitchParam().getTimeUnit() + " (manual=" + this.manual + ")";

        log.warn("Furnace Alarm: title={}, content={}, alarm={}", title, content, alarm);
    }


    /**
     *
     * @param counter
     * @param getLast
     * @return
     */
    private int incrOrGetLast(ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> counter, boolean getLast) {
        String counterName = this.getAlarmParam().getBusiName();

        DateFormat edf = this.getSwitchParam().getTimeUnit().getEnumDateFormat();
        Date now           = new Date();
        String dateFormat  = DateUtill.format(now, edf);

        try {
            // 判断锁 counterName 是否存在,不存在则暴力加持
            ConcurrentHashMap<String, AtomicInteger> date2Times = counter.get(counterName);
            if(null == date2Times) {
                synchronized (counterName) {

                    date2Times = counter.get(counterName);

                    if(null == date2Times) {
                        date2Times = new ConcurrentHashMap<>();
                        date2Times.put(dateFormat, new AtomicInteger(0));
                        counter.put(counterName, date2Times);
                    }
                }
            }

            // 如果是取上次的计数，则直接取
            if(getLast) {
                String dateFormatLast = DateUtill.format(DateUtill.add(now, this.getSwitchParam().getTimeUnit().getEnumDate(), -1), edf);
                AtomicInteger aiLast  = date2Times.get(dateFormatLast);

                return null!=aiLast ? aiLast.get() : 1;
            }

            // 判断次数
            AtomicInteger count = date2Times.get(dateFormat);
            if(null != count){
                return count.incrementAndGet();
            }else {
                // 到一定量的时候，则清理下计数器；需要保留上一单位时间的数据，因为计算百分比的时候，用的是上一单位时间的数据
                if(date2Times.size() == 10) {
                    String dateFormatLast = DateUtill.format(DateUtill.add(now, this.getSwitchParam().getTimeUnit().getEnumDate(), -1), edf);
                    AtomicInteger aiLast  = date2Times.get(dateFormatLast);

                    date2Times.clear();

                    if(null != aiLast) date2Times.put(dateFormatLast, aiLast);
                }

                date2Times.put(dateFormat, new AtomicInteger(1));

                return 1;
            }
        } catch (Throwable e) {
            log.error("incrOrGetLast error, counterName={}, getLast={}, dateFormat={}", counterName, getLast, dateFormat, e);
        }

        return 1;
    }


}
