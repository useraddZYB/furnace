package com.programmerartist.furnace;

/**
 * 熔炉参数：满足什么以下条件，则自动熔断
 *
 * Created by 程序员Artist on 2017/4/21.
 */
public class SwitchParam {
    private static final FrequencyType DEFAULT_TIME_UNIT = FrequencyType.MINUTE;    // 定时任务单位
    private static final int DEFAULT_CUT_PERCENT         = 20;                      // 失败率超过此百分比的时候，开关自动关闭
    private static final int DEFAULT_REOPEN_PERIOD       = 5;                       // 自动恢复开关为打开状态的时间
    private static final int DEFAULT_CLOSE_PERIOD        = 1;                       // 自动判断是否该关闭开关的时间

    private static final int DEFAULT_MIN_TOTAL           = 10;                     // 最小的总请求数，避免刚启服几次失败的误报问题

    /**
     * 时间单位，每多长时间
     */
    private FrequencyType timeUnit;
    /**
     * 当异常或失败出现百分之多少的时候，做熔断
     */
    private int cutPercent;
    /**
     * 熔断时候，最多需要多长时间自动恢复
     */
    private int reOpenPeriod;
    /**
     * 多长时间判断一次是否该熔断了
     */
    private int closePeriod;

    /**
     * 最小的总请求数，避免刚启服几次失败的误报问题
     */
    private int minTotal;

    /**
     *
     * @param timeUnit
     * @return
     */
    public SwitchParam timeUnit(FrequencyType timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     *
     * @param cutPercent
     * @return
     */
    public SwitchParam cutPercent(int cutPercent) {
        this.cutPercent = cutPercent;
        return this;
    }

    /**
     *
     * @param reOpenPeriod
     * @return
     */
    public SwitchParam reOpenPeriod(int reOpenPeriod) {
        this.reOpenPeriod = reOpenPeriod;
        return this;
    }

    /**
     *
     * @param closePeriod
     * @return
     */
    public SwitchParam closePeriod(int closePeriod) {
        this.closePeriod = closePeriod;
        return this;
    }

    /**
     *
     * @param minTotal
     * @return
     */
    public SwitchParam minTotal(int minTotal) {
        this.minTotal = minTotal;
        return this;
    }

    /**
     *
     * @return
     */
    public static SwitchParam newBuilder(){
        return new SwitchParam();
    }

    /**
     * 使用默认参数设置
     *
     * @return
     */
    public static SwitchParam newBuilderDefault(){
        return new SwitchParam().timeUnit(DEFAULT_TIME_UNIT).cutPercent(DEFAULT_CUT_PERCENT).reOpenPeriod(DEFAULT_REOPEN_PERIOD).closePeriod(DEFAULT_CLOSE_PERIOD).minTotal(DEFAULT_MIN_TOTAL);
    }

    /**
     *
     * @return
     */
    public SwitchParam build() {
        return new SwitchParam().timeUnit(this.timeUnit).cutPercent(this.cutPercent).reOpenPeriod(this.reOpenPeriod).closePeriod(this.closePeriod).minTotal(this.minTotal);
    }


    public FrequencyType getTimeUnit() {
        return timeUnit;
    }
    public int getCutPercent() {
        return cutPercent;
    }
    public int getReOpenPeriod() {
        return reOpenPeriod;
    }
    public int getClosePeriod() {
        return closePeriod;
    }
    public int getMinTotal() {
        return minTotal;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "SwitchParam{" +
                "timeUnit=" + timeUnit +
                ", cutPercent=" + cutPercent +
                ", reOpenPeriod=" + reOpenPeriod +
                ", closePeriod=" + closePeriod +
                ", minTotal=" + minTotal +
                '}';
    }
}
