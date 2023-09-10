package com.programmerartist.furnace;

import com.programmerartist.artist.util.sample.LocalSample;
import com.programmerartist.artist.util.sample.Sample;

/**
 * @author 程序员Artist
 * @date 2023-09-10
 */
public class DemoTest {
    /**
     * 可选，手工降级开关
     *
     * 外部可在线热修改此开关
     */
    public static Boolean manualRemote = null;

    /**
     * 自动降级开关：熔炉，自动熔断
     *
     * 默认1分钟检查一次，若判断为该熔断，则默认熔断5分钟；通过此初始化代码可修改默认值
     */
    private static Furnace furnace = new DefaultFurnace(
            SwitchParam.newBuilderDefault().cutPercent(20).build(),
            AlarmParam.newBuilder().busiName("DemoTest").recievers("zyb;ybz").mail().build()
    );
    static {
        furnace._start();
    }

    /**
     * 测试代码
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        // 测试程序执行
        for(int i=0; i<10000; i++) {

            // 1、每次请求远程都要上报一次
            furnace.total();

            // 2、每次执行前判断当前是否已熔断
            if(furnace.canDo(manualRemote)) {

                try {
                    String result = getUser(i + "");
                    System.out.println("success i=" + i + ", result=" + result);
                } catch (Exception e) {

                    // 3、如果异常则上报fail
                    furnace.fail(e);
                }
            }else {
                Thread.sleep(1000);
                System.out.println("------------- 调用 getUser 熔断了，直接返回：i=" + i);
            }
        }
    }


    private static final Sample errorSample = new LocalSample(50);
    /**
     *
     * @param userId
     * @return
     */
    private static String getUser(String userId) throws Exception {
        // 查数据库 or 调用远程服务
        String userName = "name-" + userId;
        Thread.sleep(1000);

        // 模拟出错次数
        if(errorSample.yes()) {
            throw new RuntimeException("error");
        }
        return userName;
    }

}
