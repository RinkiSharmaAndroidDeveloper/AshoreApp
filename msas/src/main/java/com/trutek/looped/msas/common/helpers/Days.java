package com.trutek.looped.msas.common.helpers;

public enum Days {

    Sunday(1),
    Monday(2),
    Tuesday(3),
    Wednesday(4),
    Thursday(5),
    Friday(6),
    Saturday(7);

    private final int day;

    Days(int day){
        this.day = day;
    }

    public int getValue(){
        return this.day;
    }

    public static Days fromInt(int i){
        switch (i){
            case 1:
                return Sunday;
            case 2:
                return Monday;
            case 3:
                return Tuesday;
            case 4:
                return Wednesday;
            case 5:
                return Thursday;
            case 6:
                return Friday;
            case 7:
                return Saturday;
            default:
                return Sunday;
        }
    }

}
