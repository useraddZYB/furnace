# furnace
熔断组件。  
  
当依赖异常率达到阈值后，依赖熔断；定时恢复执行后，再判断是否熔断  

进程内支持创建多个"熔断器"  

  
## 一，集成使用

#### 1 maven引入

jar包已上传到github package仓库中：  
https://github.com/useraddZYB?tab=packages   

```
<dependency>
    <groupId>com.programmerartist.furnace</groupId>
    <artifactId>furnace</artifactId>
    <version>1.0.0</version>
</dependency>
```  

#### 2 代码示范  

```
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
```

## 二，效果示范

```
21:11:17.460 [main] INFO  c.p.furnace.DefaultFurnace - DefaultFurnace start. switchParam=SwitchParam{timeUnit=MINUTE, cutPercent=20, reOpenPeriod=5, closePeriod=1, minTotal=10}, alarmParam=AlarmParam{busiName='DemoTest', recievers='zyb;ybz', mail=true, rtx=false, sms=false}
success i=0, result=name-0
success i=1, result=name-1
success i=3, result=name-3
success i=4, result=name-4
success i=6, result=name-6
success i=7, result=name-7
21:11:27.464 [T_CLOSE_DemoTest_1] INFO  c.p.furnace.DefaultFurnace - judge total(1) < minTotal(10), refuse judge
success i=9, result=name-9
success i=11, result=name-11
success i=13, result=name-13
success i=15, result=name-15
...
success i=59, result=name-59
success i=62, result=name-62
success i=64, result=name-64
21:12:27.456 [T_CLOSE_DemoTest_1] WARN  c.p.furnace.DefaultFurnace - Furnace Alarm: title=DemoTest Furnace auto close, content=[DemoTest]  Furnace has been closed, then business refuse work; detail is: failPercent=51, fail=22, total=43, timeUnit=MINUTE (manual=null), alarm=AlarmParam{busiName='DemoTest', recievers='zyb;ybz', mail=true, rtx=false, sms=false}
21:12:27.456 [T_CLOSE_DemoTest_1] INFO  c.p.furnace.DefaultFurnace - judge failed=true, businessName=DemoTest, failPercent=51, fail=22.0, total=43, manual=null
------------- 调用 getUser 熔断了，直接返回：i=70
------------- 调用 getUser 熔断了，直接返回：i=71
------------- 调用 getUser 熔断了，直接返回：i=72
------------- 调用 getUser 熔断了，直接返回：i=73
------------- 调用 getUser 熔断了，直接返回：i=74
------------- 调用 getUser 熔断了，直接返回：i=75
------------- 调用 getUser 熔断了，直接返回：i=76
------------- 调用 getUser 熔断了，直接返回：i=77
...
------------- 调用 getUser 熔断了，直接返回：i=299
------------- 调用 getUser 熔断了，直接返回：i=300
------------- 调用 getUser 熔断了，直接返回：i=301
------------- 调用 getUser 熔断了，直接返回：i=302
------------- 调用 getUser 熔断了，直接返回：i=303
21:16:22.447 [T_OPEN_DemoTest_1] INFO  c.p.furnace.DefaultFurnace - reOpen finish; totalCounter={DemoTest={202309102111=43, 202309102112=60, 202309102115=60, 202309102116=23, 202309102113=60, 202309102114=59}}, failCounter={DemoTest={202309102111=22, 202309102112=10}}, manual=null
21:16:22.448 [T_OPEN_DemoTest_1] WARN  c.p.furnace.DefaultFurnace - Furnace Alarm: title=DemoTest Furnace auto reOpen, content=[DemoTest]  Furnace has been open, then business work again. (manual=null), alarm=AlarmParam{busiName='DemoTest', recievers='zyb;ybz', mail=true, rtx=false, sms=false}
success i=305, result=name-305
success i=306, result=name-306
success i=307, result=name-307
success i=308, result=name-308
...
```

