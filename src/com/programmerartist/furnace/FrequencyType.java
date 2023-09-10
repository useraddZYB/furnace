package com.programmerartist.furnace;

import com.programmerartist.artist.util.date.DateFormat;
import com.programmerartist.artist.util.date.EnumDate;

/**
 * Created by 程序员Artist on 2017/4/21.
 */
public enum FrequencyType {

    MONTH(DateFormat.YM_NONE),
    DAY(DateFormat.YMD_NONE),
    HOUR(DateFormat.YMDH_NONE),
    MINUTE(DateFormat.YMDHM_NONE),
    SECOND(DateFormat.YMDHMS_NONE);

    private DateFormat enumDateFormat;
    private String name;

    FrequencyType(DateFormat enumDateFormat) {
        this.enumDateFormat = enumDateFormat;
    }

    public DateFormat getEnumDateFormat() {
        return enumDateFormat;
    }

    /**
     *
     * @return
     */
    public String getName() {
        String name = "";
        if(this.equals(MONTH)) {
            name = "月";
        }else if(this.equals(DAY)) {
            name = "天";
        }else if(this.equals(HOUR)) {
            name = "小时";
        }else if(this.equals(MINUTE)) {
            name = "分钟";
        }else if(this.equals(SECOND)) {
            name = "秒";
        }

        return name;
    }

    /**
     *
     * @return
     */
    public int getSeconds() {
        int seconds = 1;

        switch (this) {
            case MONTH:
                seconds = 60 * 60 * 24 * 30;
                break;
            case DAY:
                seconds = 60 * 60 * 24;
                break;
            case HOUR:
                seconds = 60 * 60;
                break;
            case MINUTE:
                seconds = 60;
                break;
            case SECOND:
                seconds = 1;
                break;
        }

        return seconds;
    }

    /**
     *
     * @return
     */
    public EnumDate getEnumDate() {
        EnumDate enumDate = EnumDate.MINUTES;

        switch (this) {
            case MONTH:
                enumDate = EnumDate.MONTH;
                break;
            case DAY:
                enumDate = EnumDate.DAY;
                break;
            case HOUR:
                enumDate = EnumDate.HOUR;
                break;
            case MINUTE:
                enumDate = EnumDate.MINUTES;
                break;
            case SECOND:
                enumDate = EnumDate.SECOND;
                break;
        }

        return enumDate;
    }
}
