package com.programmerartist.furnace;

/**
 * Created by 程序员Artist on 2017/4/21.
 */
public interface Furnace {

    /**
     * 启动定时任务，只调用一次
     */
    void _start();



    /**
     * 业务调用：总请求数上报
     */
    void total();

    /**
     * 业务调用：失败请求数上报
     */
    void fail();

    /**
     * 业务调用：请求当前熔炉状态，是否已熔断
     *
     * @return true ? 业务可正常通行 : 业务需要阻断
     */
    boolean canDo();

    /**
     * 业务调用：失败请求数上报
     *
     * @param e
     */
    void fail(Throwable e);

    /**
     *
     * @param manual null ? 自动开关 : 手动开关
     * @return
     */
    boolean canDo(Boolean manual);


}
